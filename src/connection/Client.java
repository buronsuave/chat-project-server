package connection;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import database.UserDAO;
import exceptions.LevelEmitException;
import exceptions.WCException;
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
                        System.out.println("User: " + user.getName() + " Pass: " + user.getPass());
                        sendUser(user);
                        break;
                    }

                    // Signup
                    case 2:
                    {
                        user = signUp(input);
                        System.out.println("User: " + user.getName() + " Pass: " + user.getPass());
                        sendUser(user);
                        break;
                    }
                }   
            } 
            catch (IOException e)
            {
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
    }

    private int getCode(String input) throws JSONException
    {
        JSONObject json = new JSONObject(input);
        return Integer.parseInt(json.getString("op"));        
    }

    private User signIn(String input) throws WCException, JSONException, LevelEmitException
    {
        if (user != null)
        {
            throw new LevelEmitException("This run has already logged in");
        }

        JSONObject json = new JSONObject(input);
        String name = json.getString("name");
        String pass = json.getString("pass");

        User checkuUser = dbManager.getUser(name);
        if (checkuUser == null)
        {
            throw new WCException("Username does not exist");
        }

        if (!checkuUser.getPass().equals(pass))
        {
            throw new WCException("Password is incorrect");
        }

        return checkuUser;
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

    private void removeSession(int id)
    {

    }

    private boolean isOnline(int id)
    {
        return true;
    }
}
