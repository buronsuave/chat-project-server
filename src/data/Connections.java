package data;

import java.util.ArrayList;

import connector.Client;
import models.User;

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

    public void removeConnection(Client client)
    {
        connections.remove(client);
    }

    public int getCount()
    {
        return connections.size();
    }

    public Client getClient(int id)
    {
        for (int i = 0; i < getCount(); i++)
        {
            if (connections.get(i).getUser().getId() == id)
            {
                return connections.get(i);
            }
        }

        return null;
    }

    public Client getClient(String name)
    {
        for (int i = 0; i < getCount(); i++)
        {
            if (connections.get(i).getUser().getName().equals(name))
            {
                return connections.get(i);
            }
        }

        return null;
    }

    public ArrayList<Client> getConnections() 
    {
        return connections;
    }

    public ArrayList<User> getOnlineUsers()
    {
        ArrayList<User> users = new ArrayList<>();
        for (int i = 0; i < getCount(); i++)
        {
            users.add(connections.get(i).getUser());
        }
        
        return users;
    }
}
