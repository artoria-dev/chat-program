import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Chat {
    // string to store local message log
    String messageCache = "";

    // store login name and ip / localhost
    String usr_name;
    String usr_server;

    public Chat() {
        // creating login frame
        JFrame frame = new JFrame("Login");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 105);

        // add nickname text box
        JTextField nickname = new JTextField();
        // add ghost text
        nickname.setText("Type your username..");

        // add mouse on click listener to empty ghost text
        nickname.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                nickname.setText("");
            }
        });

        // add server text box
        JTextField server = new JTextField();
        // put standard server (localhost)
        server.setText("127.0.0.1");

        // login button
        JButton login = new JButton("Login");

        // on button click event
        login.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // save inputs from text boxes
                usr_name = nickname.getText();
                usr_server = server.getText();
                // network io shit
                // instantiate client object
                Client nio = new Client(usr_server, 6000, usr_name);
                // connecting socket to server
                nio.connect();
                // checks if connection resolves
                if (nio.getStatus()) {
                    nio.listen();
                    // calls mainWindow methode
                    mainWindow(frame, nickname, server, login, nio); // pass frame and elements to be able to remove elements on new view
                }
            }
        });

        // add elements to pane
        frame.getContentPane().add(BorderLayout.NORTH, nickname);
        frame.getContentPane().add(BorderLayout.CENTER, server);
        frame.getContentPane().add(BorderLayout.SOUTH, login);
        // set visibility
        frame.setVisible(true);
    }

    public void mainWindow(JFrame frame, JTextField nickname, JTextField server, JButton login, Client nio) {
        // remove elements from prior window
        frame.remove(nickname);
        frame.remove(server);
        frame.remove(login);

        // changing frame attributes
        frame.setTitle("Chad | v0.7 | User: " + usr_name);
        frame.setSize(450, 400);

        // set text area in the middle
        JTextArea textArea = new JTextArea();
        textArea.setEditable(false); // make textarea non edible
        textArea.setWrapStyleWord(true); // make textarea wrap by word
        textArea.setLineWrap(true); // make textarea wrap lines

        // scroll pane
        JScrollPane scrollableTextArea = new JScrollPane(textArea);
        scrollableTextArea.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        // creating bottom panel
        JPanel panel = new JPanel(); // the panel is not visible in output
        JLabel label = new JLabel("Message:");
        JTextField tf = new JTextField(25); // accepts upto 25 characters | also sets textbox length
        JButton send = new JButton("Send"); // send button

        // send button click event
        send.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // if input !empty
                if (!tf.getText().equals("")) {
                    // calls network class | sends msg
                    nio.send(tf.getText());
                    // empties textbox after message sent
                    tf.setText("");
                }
            }
        });


        // flow layout to add elements to panel
        panel.add(label);
        panel.add(tf);
        panel.add(send);

        // adding components to main frame
        frame.getContentPane().add(BorderLayout.SOUTH, panel);
        frame.getContentPane().add(BorderLayout.CENTER, scrollableTextArea);
        frame.setVisible(true);

        // check for updates
        checkForUpdates(nio, textArea);
    }

    // if client is connected to server, receive last sent message
    public void checkForUpdates(Client nio, JTextArea textArea) {
        Thread updater = new Thread(new Runnable() {
            @Override
            public void run() {
                while (nio.getStatus()) {
                    String revMessage = nio.getMessage();
                    if (revMessage != null) {
                        // if received msg is not null
                        messageManipulator(revMessage, textArea);
                    }
                }
            }
        });
        updater.start();
    }

    public void messageManipulator(String lastMessageReceivedFromServer, JTextArea textArea) {
        if(!lastMessageReceivedFromServer.equals(messageCache)) {
            // save last received message
            messageCache = lastMessageReceivedFromServer;
            // set textArea text to (already given text + new message from server)
            textArea.setText(textArea.getText() + lastMessageReceivedFromServer + "\n");
            // debug terminal output
            System.out.println("Updated Output: " + textArea.getText() + " " + lastMessageReceivedFromServer);
        }
    }
}
