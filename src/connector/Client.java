package connector;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import data.Connections;
import database.UserDAO;
import exceptions.LevelEmitException;
import exceptions.WCException;
import models.Message;
import models.User;

public class Client extends Thread
{
    private Socket client;
    private User user;
    private UserDAO dbManager;

    public Client(Socket client)
    {
        this.client = client;
        this.dbManager = new UserDAO();
        this.user = null;
    }

    @Override
    public void run()
    {
        System.out.println("New connection");
        byte[] data = new byte[2048];

        while (true)
        {
            try 
            {    
                client.getInputStream().read(data);
                String input = new String(data);
                int code = getCode(input);

                switch(code)
                {
                    // Login
                    case 1:
                    {
                        user = signIn(input);
                        sendUser(user);
                        break;
                    }

                    // Signup
                    case 2:
                    {
                        user = signUp(input);
                        sendUser(user);
                        break;
                    }

                    // Sending message
                    case 3:
                    {
                        Message message = getMessage(input);
                        sendMessage(message);
                        break;
                    }
                }   
            } 
            catch (IOException e)
            {
                Connections.get().removeConnection(this);
                ArrayList<User> onlineUsers = Connections.get().getOnlineUsers();
                try 
                {
                    sendUsers(onlineUsers);
                } catch (IOException e1) 
                {

                }
                break;
            } 
            catch (JSONException e) 
            {
                System.out.println(e.getMessage());
                try 
                {
                    sendException(e.getMessage());
                } catch (IOException ex) 
                {
                    
                }
            } 
            catch (WCException e) 
            {
                System.out.println(e.getMessage());
                try 
                {
                    sendException(e.getMessage());
                } catch (IOException ex) 
                {
                    
                }
            } 
            catch (LevelEmitException e) 
            {
                System.out.println(e.getMessage());
                try 
                {
                    sendException(e.getMessage());
                } catch (IOException ex) 
                {
                    
                }
            }
        }
    }

    private void sendException(String message) throws IOException
    {
        Map<String, String> map = new HashMap<>();
        map.put("code", "1");
        map.put("error", message);

        JSONObject json = new JSONObject(map);
        String jsonString = json.toString();
        client.getOutputStream().write(jsonString.getBytes());
    }

    private void sendUser(User auxUser) throws IOException 
    {
        Map<String, String> map = new HashMap<>();
        map.put("code", "2");
        map.put("id", auxUser.getId()+"");
        map.put("name", auxUser.getName());
        map.put("pass", auxUser.getPass());

        JSONObject json = new JSONObject(map);
        String jsonString = json.toString();
        client.getOutputStream().write(jsonString.getBytes());
        Connections.get().addConnection(this);
        ArrayList<User> onlineUsers = Connections.get().getOnlineUsers();
        sendUsers(onlineUsers);
    }

    private void sendUsers(ArrayList<User> users) throws IOException
    {
        JSONArray array = new JSONArray();
        for (int i = 0; i < users.size(); i++)
        {
            User auxUser = users.get(i);
            Map<String, String> mapUser = new HashMap<>();
            mapUser.put("name", auxUser.getName());
            JSONObject jsonUser = new JSONObject(mapUser);
            array.put(jsonUser);
        }    

        Map<String, String> map = new HashMap<>();
        map.put("code", "4");
        map.put("array", array.toString());
        JSONObject json = new JSONObject(map);
        String jsonString = json.toString();
        System.out.println(jsonString);

        for (int i = 0; i < Connections.get().getCount(); i++)
        {
            Socket socket = Connections.get().getConnections().get(i).getClient();
            socket.getOutputStream().write(jsonString.getBytes());
        }        
    }

    private void sendMessage(Message message) throws IOException
    {
        System.out.println("Sending message");
        Map<String,String> map = new HashMap<>();
        map.put("code", "5");
        map.put("sender", message.getSender());
        map.put("receiver", message.getReceiver());
        map.put("content", message.getContent());

        JSONObject json = new JSONObject(map);
        String jsonString = json.toString();
        Client receiver = Connections.get().getClient(message.getReceiver());
        receiver.getClient().getOutputStream().write(jsonString.getBytes());
    }

    private int getCode(String input) throws JSONException
    {
        JSONObject json = new JSONObject(input);
        return Integer.parseInt(json.getString("op"));        
    }

    private Message getMessage(String input) throws JSONException
    {
        JSONObject json = new JSONObject(input);
        String content = json.getString("content");
        String sender = json.getString("sender");
        String receiver = json.getString("receiver");
        Message message = new Message();
        message.setContent(content);
        message.setSender(sender);
        message.setReceiver(receiver);
        return message;
    }

    private User signIn(String input) throws WCException, JSONException, LevelEmitException, IOException
    {
        if (user != null)
        {
            throw new LevelEmitException("This run has already logged in");
        }

        JSONObject json = new JSONObject(input);
        String name = json.getString("name");
        String pass = json.getString("pass");

        User checkUser = dbManager.getUser(name);
        if (checkUser == null)
        {
            throw new WCException("Username does not exist");
        }

        if (!checkUser.getPass().equals(pass))
        {
            throw new WCException("Password is incorrect");
        }

        if (isOnline(name))
        {
            Client checkClient = Connections.get().getClient(name);
            Socket prevSocket = checkClient.getClient();
            if (!prevSocket.equals(client))
            {
                Map<String, String> map = new HashMap<>();
                map.put("code", "3");
                map.put("message", "Another app has just logged in with this account");
                JSONObject jsonPrev = new JSONObject(map);
                String jsonStringPrev = jsonPrev.toString();
                prevSocket.getOutputStream().write(jsonStringPrev.getBytes());
                Connections.get().removeConnection(checkClient);
                checkClient.setUser(null);
            }
        }

        return checkUser;
    }

    private User signUp(String input) throws LevelEmitException, JSONException, WCException
    {
        if (user != null)
        {
            throw new LevelEmitException("This run has already logged in");
        }

        JSONObject json = new JSONObject(input);
        String name = json.getString("name");
        String pass = json.getString("pass");

        User checkuUser = dbManager.getUser(name);
        if (checkuUser != null)
        {
            throw new WCException("Username already exist");
        }

        User auxUser = new User();
        auxUser.setName(name);
        auxUser.setPass(pass);
        dbManager.add(auxUser);

        return dbManager.getUser(name);
    }

    private boolean isOnline(String name)
    {
        Client checkClient = Connections.get().getClient(name);
        return checkClient != null;        
    }

    public User getUser() 
    {
        return user;
    }

    public void setUser(User user)
    {
        this.user = user;
    }

    public Socket getClient() 
    {
        return client;
    }
}
