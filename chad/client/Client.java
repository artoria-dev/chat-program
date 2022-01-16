// v0.6

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    private final String ip;
    private final int port;
    private final String username;

    public String lastReceivedMessage;

    private Socket clientSocket;
    private BufferedReader input;
    private PrintWriter output;

    public boolean getStatus() {
        if (clientSocket != null) {
            if (clientSocket.isConnected() && !clientSocket.isClosed()) {
                return true;
            } else {
                System.out.println("System: No connection to server!");
                return false;
            }
        }
        System.out.println("System: No connection to server!");
        return false;
    }

    public Client(String ip, int port, String username) {
        System.out.println("System: Client is initialising ...");

        this.ip = ip;
        this.port = port;
        this.username = username;
    }

    public void start() {
        try {
            System.out.println("System: Starting listener ...");
            Thread receiver = new Thread(new Runnable() {
                String messageToReceive;

                @Override
                public void run() {
                    try {
                        messageToReceive = input.readLine();
                        while (messageToReceive != null) {
                            System.out.println("System: Received message: " + messageToReceive);
                            transferMessage(messageToReceive);
                            messageToReceive = input.readLine();
                        }
                        System.out.println("System: Connection to server lost!");
                        output.close();
                        clientSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            receiver.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void send(String messageToSend) {
        System.out.println("msg to send: " + messageToSend); // db
        output.println(username + ": " + messageToSend);
        output.flush();
    }

    public void connect() {
        System.out.println("System: Connecting to server: " + ip + ":" + port + " ...");

        try {
            clientSocket = new Socket(ip, port);
            output = new PrintWriter(clientSocket.getOutputStream());
            input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            System.out.println("System: Connected!");
        } catch (IOException e) {
            System.out.println("System: Could not connect to server: " + ip + ":" + port + "!");
        }
    }

    private void transferMessage(String message) {
        lastReceivedMessage = message;
    }

    public String getMessage() {
        //System.out.println("System: last received message: " + lastReceivedMessage);
        return lastReceivedMessage;
    }
}
