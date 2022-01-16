import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

// Version 0.6 Client
// Written by Erik & Tom 2022

public class Client // sends/receives messages to/from server
{
    private final String ip; // ipv4 address of the server
    private final int port; // port of the server
    private final String username; // username to use

    public String lastReceivedMessage; // last message received

    private Socket clientSocket; // TCP-Socket to handle connection with
    private BufferedReader input; // variable to store InputStream from server
    private PrintWriter output; // variable to store OutputStream to server

    public boolean getStatus() // returns the current status of the connection to the server
    {
        if (clientSocket != null) // checks if a Client-Socket exists
        {
            if (clientSocket.isConnected() && !clientSocket.isClosed()) // checks if the socket is connected and open
            {
                return true;
            }
            else
            {
                System.out.println("System: No connection to server!");
                return false;
            }
        }
        System.out.println("System: No connection to server!");
        return false;
    }

    public Client(String ip, int port, String username) // constructor instates client
    {
        System.out.println("System: Client is initialising ...");

        this.ip = ip;
        this.port = port;
        this.username = username;
    }

    public void listen() // handles receiving messages
    {
        try
        {
            System.out.println("System: Starting listener ...");
            Thread receiver = new Thread(new Runnable() // create a thread that handles receiving messages for the server
            {
                String messageToReceive; // save message in a locale variable

                @Override // override method "run" of class Runnable
                public void run() // method "run" of class Runnable
                {
                    try
                    {
                        messageToReceive = input.readLine(); // read message from InputStream
                        while (messageToReceive != null) // repeat as long as the InputStream/connection is open
                        {
                            System.out.println("System: Received message: " + messageToReceive);
                            transferMessage(messageToReceive); // prepare transfer message to UI
                            messageToReceive = input.readLine(); // read message from InputStream
                        }
                        System.out.println("System: Connection to server lost!");
                        output.close(); // when connection is interrupted close the stream
                        clientSocket.close(); // close the TCP-Socket
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace(); // print errors
                    }
                }
            });
            receiver.start(); // start the thread
        }
        catch (Exception e)
        {
            e.printStackTrace(); // print errors
        }
    }

    public void send(String messageToSend) // send the message to the server
    {
        System.out.println("System: Message to send: " + messageToSend);
        output.println(username + ": " + messageToSend); // print the message into the OutputStream to the server
        output.flush(); // clear stream
    }

    public void connect() // handles connection to the server
    {
        System.out.println("System: Connecting to server: " + ip + ":" + port + " ...");

        try
        {
            clientSocket = new Socket(ip, port); // open TCP-Socket to Server IP & Port
            output = new PrintWriter(clientSocket.getOutputStream()); // create an OutputStream to the server
            input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())); // create an InputStream from the server
            System.out.println("System: Connected!");
        }
        catch (IOException e)
        {
            System.out.println("System: Could not connect to server: " + ip + ":" + port + "!"); // inform about connection problems
        }
    }

    private void transferMessage(String message) // prepares message for UI (mostly outdated)
    {
        lastReceivedMessage = message; // just save it in the variable
    }

    public String getMessage() // getter for lastReceivedMessage
    {
        return lastReceivedMessage;
    }
}
