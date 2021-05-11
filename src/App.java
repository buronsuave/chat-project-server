import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import connection.Client;

public class App 
{
    public static void main(String[] args) throws Exception 
    {
        boolean run = true;

        try 
        {
            ServerSocket server;
            Socket session;
            Thread client;

            server = new ServerSocket(1234);

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
