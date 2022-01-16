import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

// Version 0.5 Server
// Written by Erik 2022

public class Server // forwards received messages to all connected clients
{
    private int port; // port for the Server-Socket
    private ServerSocket serverSocket; // TCP-Socket to handle connections over Transmission-Control-Protocol
    private ArrayList <Socket> clientList = new ArrayList<Socket>(); // list of all connected clients to handle broadcast

    public Server(int port) // Constructor for Server class
    {
        System.out.println("Service is starting...");
        this.port = port; // saves port in variable
    }

    public void start() // initiates the Server
    {
        System.out.println("Service is initializing...");
        listen(); // start listening on port to read messages
    }

    private void listen() // handles receiving messages
    {
        try
        {
            System.out.println("Service is creating new socket ...");
            serverSocket = new ServerSocket(port); // open TCP-Socket on given port
            System.out.println("Service is ready for new connections ...");

            while(true) // repeat as long as the server is running
            {
                Socket clientSocket = serverSocket.accept(); // accept new connections and save it as a separate client connection
                if(clientSocket.isConnected()) // if connection is successful established ...
                {
                    System.out.println("Service accepted new connection ...");
                    clientList.add(clientSocket); // add the client belonging to that connection to our list of connected clients
                    System.out.println("Service added new client to the list ...");

                    Thread receiver = new Thread(new Runnable() // create a thread that handles receiving messages for the new client
                    {
                        @Override // override method "run" of class Runnable
                        public void run() // method "run" of class Runnable
                        {
                            try
                            {
                                BufferedReader input; // variable to store InputStream to client
                                input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())); // generate InputStream from the client-socket

                                String messageToReceive = input.readLine(); // read received messages
                                while (messageToReceive != null) // repeat as long as the InputStream/connection is open
                                {
                                    System.out.println("Service received: " + messageToReceive);
                                    broadcast(messageToReceive); // broadcast messages to all connected clients
                                    messageToReceive = input.readLine(); // read from InputStream again
                                }
                                removeClient(clientSocket); // close client connection after the InputStream/connection is interrupted
                            }
                            catch (IOException e)
                            {
                                e.printStackTrace(); // print errors
                            }
                        }
                    });
                    receiver.start(); // start the thread
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace(); // print errors
        }
    }

    public void removeClient(Socket client) // handle disconnected client
    {
        try
        {
            System.out.println("Warning! Service lost connection to a client!");
            client.close(); // close the socket of the client
        }
        catch(IOException e)
        {
            e.printStackTrace(); // print errors
        }
        clientList.remove(client); // remove client from the list of connected clients
        System.out.println("Service removed inactive client from the list ...");
    }

    public void broadcast (String messageToSend) // broadcast messages to all connected clients
    {
        for (Socket client: clientList) // for every client in the list
        {
            try
            {
                PrintWriter outp = new PrintWriter(client.getOutputStream()); // store and open OutputStream to client
                outp.println(messageToSend); // print message into the stream
                System.out.println("Service send: " + messageToSend);
                outp.flush(); // clear stream
            }
            catch (IOException e)
            {
                e.printStackTrace(); // print errors
            }
        }
    }
}