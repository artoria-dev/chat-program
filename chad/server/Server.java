import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

//Version 0.5 Server

public class Server
{
    private int port;
    private ServerSocket serverSocket;
    private ArrayList <Socket> clientList = new ArrayList<Socket>();

    public Server(int port)
    {
        System.out.println("Service is starting...");
        this.port = port;
    }

    public void start()
    {
        System.out.println("Service is initializing...");

        listen();
    }

    private void listen()
    {
        try
        {
            System.out.println("Service is creating new socket ...");

            serverSocket = new ServerSocket(port);

            System.out.println("Service is ready for new connections ...");

            while(true)
            {
                Socket clientSocket = serverSocket.accept();
                if(clientSocket.isConnected())
                {
                    System.out.println("Service accepted new connection ...");

                    clientList.add(clientSocket);

                    System.out.println("Service added new client to the list ...");

                    Thread receiver = new Thread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            try
                            {
                                BufferedReader input;

                                input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                                String messageToReceive = input.readLine();
                                while (messageToReceive != null)
                                {
                                    System.out.println("Service received: " + messageToReceive);

                                    broadcast(messageToReceive);

                                    messageToReceive = input.readLine();
                                }
                                checkForClient(clientSocket);
                            }
                            catch (IOException e)
                            {
                                e.printStackTrace();
                            }
                        }
                    });
                    receiver.start();
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void checkForClient(Socket client)
    {
        try
        {
            System.out.println("Warning! Service lost connection to a client!");
            client.close();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        clientList.remove(client);
        System.out.println("Service removed inactive client from the list ...");
    }

    public void broadcast (String messageToSend)
    {
        for (Socket client: clientList)
        {
            try
            {
                PrintWriter outp = new PrintWriter(client.getOutputStream());
                outp.println(messageToSend);
                System.out.println("Service send: " + messageToSend);
                outp.flush();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
}