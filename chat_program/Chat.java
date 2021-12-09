package chat_program;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class Chat {

    // array to store local message log
    ArrayList<String> messages = new ArrayList<>();

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
        nickname.setText("Type Nickname..");

        JTextField server = new JTextField();
        server.setText("Type Server..");

        // login button
        JButton login = new JButton("Login");

        // on button click event
        login.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                usr_name = nickname.getText();
                usr_server = server.getText();
                mainWindow(frame, nickname, server, login); // pass frame and elements to be able to remove elements on new view
            }
        });

        // add elements to pane
        // TODO: make view bigger, change border layouts
        frame.getContentPane().add(BorderLayout.NORTH, nickname);
        frame.getContentPane().add(BorderLayout.CENTER, server);
        frame.getContentPane().add(BorderLayout.SOUTH, login);
        frame.setVisible(true);
    }

    public void mainWindow(JFrame frame, JTextField nickname, JTextField server, JButton login) {

        // remove elements from prior window
        frame.remove(nickname);
        frame.remove(server);
        frame.remove(login);

        // creating main frame
        frame.setTitle("Chat");
        frame.setSize(400, 400);

        // creating menu bar, adding elements | may be redundant
        JMenuBar mb = new JMenuBar();
        JMenu m1 = new JMenu("menu1");
        JMenu m2 = new JMenu("menu2");
        mb.add(m1);
        mb.add(m2);
        JMenuItem m11 = new JMenuItem("sub1");
        JMenuItem m22 = new JMenuItem("sub2");
        m1.add(m11);
        m1.add(m22);

        // set text area in the middle
        JTextArea ta = new JTextArea();
        ta.setEditable(false);

        // creating bottom panel
        JPanel panel = new JPanel(); // the panel is not visible in output
        JLabel label = new JLabel("~$");
        JTextField tf = new JTextField(25); // accepts upto 25 characters | also sets textbox length
        JButton send = new JButton("Send");

        // send button click event
        send.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String textFieldValue = usr_name + ": " + tf.getText(); // get text value from textbox
                if (!tf.getText().equals("")) {

                    messages.add(textFieldValue); // add message to log array
                    String tmp = String.join("\n", messages); // joins array
                    ta.setText(tmp); // prints log to central text area

                    tf.setText(""); // empties textbox after message sent

                    // calls network class
                    NetworkIO nio = new NetworkIO(usr_name, textFieldValue);
                    nio.sendMessage();

                    // receives new message log (in case other user sent a message)

                }
            }
        });

        // flow layout to add elements to panel
        panel.add(label);
        panel.add(tf);
        panel.add(send);

        //adding components to main frame
        frame.getContentPane().add(BorderLayout.SOUTH, panel);
        frame.getContentPane().add(BorderLayout.NORTH, mb);
        frame.getContentPane().add(BorderLayout.CENTER, ta);
        frame.setVisible(true);
    }
}
