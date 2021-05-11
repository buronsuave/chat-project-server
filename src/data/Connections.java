package data;

import java.util.ArrayList;

import connection.Client;

public class Connections 
{
    private static Connections instance = null;
    private ArrayList<Client> connections; 
    
    public static Connections get()
    {
        if (instance == null)
        {
            instance = new Connections();
        }
        return instance;
    }

    private Connections()
    {
        connections = new ArrayList<>();
    }

    public void addConnection(Client client)
    {
        connections.add(client);
    }

    public int getCount()
    {
        return connections.size();
    }
}
