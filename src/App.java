import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import connector.Client;

public class App 
{
    private static final int SERVER_PORT = 1234;

    public static void main(String[] args) throws Exception 
    {
        boolean run = true;

        try 
        {
            ServerSocket server;
            Socket session;
            Thread client;

            server = new ServerSocket(SERVER_PORT);

            while (run)
            {
                session = server.accept();
                client = new Client(session);
                client.start();
            }

            server.close();
        } 
        catch (IOException e)
        {

        }
    }
}
