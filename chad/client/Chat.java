import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Chat {

    // array to store local message log
    String messageCache = "";

    // store login name and ip / localhost
    String usr_name;
    String usr_server;

    public Chat() {
        // creating login frame
        JFrame frame = new JFrame("Login");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 105);

        // text boxes for server and nickname
        // TODO: make text ghost text / onclick empty textboxes
        JTextField nickname = new JTextField();
        nickname.setText("user");

        JTextField server = new JTextField();
        server.setText("127.0.0.1");

        // login button
        JButton login = new JButton("Login");

        // on button click event
        login.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                usr_name = nickname.getText();
                usr_server = server.getText();

                // TODO: enter drücken

                // network io shit
                Client nio = new Client(usr_server, 6000, usr_name);
                nio.connect();
                if (nio.getStatus()) {
                    nio.start();
                    mainWindow(frame, nickname, server, login, nio); // pass frame and elements to be able to remove elements on new view
                }
            }
        });

        // add elements to pane
        // TODO: make view bigger, change border layouts
        frame.getContentPane().add(BorderLayout.NORTH, nickname);
        frame.getContentPane().add(BorderLayout.CENTER, server);
        frame.getContentPane().add(BorderLayout.SOUTH, login);
        frame.setVisible(true);
    }

    public void mainWindow(JFrame frame, JTextField nickname, JTextField server, JButton login, Client nio) {
        // remove elements from prior window
        frame.remove(nickname);
        frame.remove(server);
        frame.remove(login);

        // creating main frame
        frame.setTitle("Chat | v0.6 | User: " + usr_name);
        frame.setSize(450, 400);

        // set text area in the middle
        JTextArea ta = new JTextArea();
        ta.setEditable(false);

        // creating bottom panel
        JPanel panel = new JPanel(); // the panel is not visible in output
        JLabel label = new JLabel("Message:");
        JTextField tf = new JTextField(25); // accepts upto 25 characters | also sets textbox length
        JButton send = new JButton("Send");

        // send button click event
        send.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String textFieldValue = usr_name + ": " + tf.getText(); // get text value from textbox
                if (!tf.getText().equals("")) {
                    // calls network class | sends msg
                    nio.send(tf.getText());
                    // empties textbox after message sent
                    tf.setText("");
                    // TODO: Enter drücken
                }
            }
        });


        // flow layout to add elements to panel
        panel.add(label);
        panel.add(tf);
        panel.add(send);

        // adding components to main frame
        frame.getContentPane().add(BorderLayout.SOUTH, panel);
        frame.getContentPane().add(BorderLayout.CENTER, ta);
        frame.setVisible(true);

        // check for updates
        checkForUpdates(nio, ta);
    }

    public void checkForUpdates(Client nio, JTextArea ta) {
        Thread updater = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("THREAD STARTET");
                while (nio.getStatus()) {
                    String revMessage = nio.getMessage();
                    if (revMessage != null) {
                        messageManipulator(revMessage, ta);
                    }
                }
            }
        });
        updater.start();
    }

    public void messageManipulator(String lastMessageReceivedFromServer, JTextArea textArea) {
        if(!lastMessageReceivedFromServer.equals(messageCache)) {
            messageCache = lastMessageReceivedFromServer;
            textArea.setText(textArea.getText() + lastMessageReceivedFromServer + "\n");
            System.out.println("Updated Output: " + textArea.getText() + " " + lastMessageReceivedFromServer);
        }
    }
}