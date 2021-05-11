package database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;

import models.User;

public class UserDAO extends Database
{
    public UserDAO()
    {
        super();    
    }

    public User getUser(int id)
    {
        try 
        {
            PreparedStatement sql;
            ResultSet result = null;
            
            sql = getConnection().prepareStatement("SELECT * FROM Users WHERE id = ?");
            sql.setInt(1, id);
            result = sql.executeQuery();
            
            if (result.next())
            {
                System.out.println(result.toString());
                User user;
                user = new User();
                user.setId(result.getInt("id"));
                user.setName(result.getString("name"));
                user.setPass(result.getString("pass"));
                return user;
            }
            
            return null;
        } 
        catch (SQLException e)
        {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public User getUser(String name)
    {
        try 
        {
            PreparedStatement sql;
            ResultSet result;
            
            sql = getConnection().prepareStatement("SELECT * FROM Users WHERE name = ?");
            sql.setString(1, name);
            result = sql.executeQuery();

            if (result.next())
            {
                User user;
                user = new User();
                user.setId(result.getInt("id"));
                user.setName(result.getString("name"));
                user.setPass(result.getString("pass"));
                return user;
            }

            return null;
        } 
        catch (SQLException e)
        {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public ArrayList<User> getUsers()
    {
        try 
        {
            PreparedStatement sql ;
            ResultSet result;
            User user;
            ArrayList<User> list;
            
            sql = getConnection().prepareStatement("SELECT * FROM Users");
            result = sql.executeQuery();
            
            list = new ArrayList<>();
            
            while (result.next())
            {
                user = new User();
                user.setId(result.getInt("id"));
                user.setName(result.getString("name"));
                user.setPass(result.getString("pass"));
                list.add(user);
            }
                
            return list;
        }
        catch (SQLException e)
        {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public void add(User user)
    {
        PreparedStatement sql;
        
        String sqlInsert;
        try
        {
            sqlInsert = "INSERT INTO Users (name, pass) VALUES (?,?)";
            
            sql = getConnection().prepareStatement(sqlInsert);
            sql.setString(1, user.getName());
            sql.setString(2, user.getPass());
            sql.executeUpdate();
        }
        catch (SQLException e)
        {
            System.out.println(e.getMessage());
            System.out.println(e.getMessage());
        }
    }

    public void update(User user)
    {
        PreparedStatement sql;
        try
        {
            sql = getConnection().prepareStatement("UPDATE Users SET name = ?, pass = ? WHERE Users.id = ?");
            sql.setString(1, user.getName());
            sql.setString(2, user.getPass());
            sql.setInt(3, user.getId());
            sql.executeUpdate();
        }
        catch (SQLException e)
        {
            System.out.println(e.getMessage());
        }      
    }

    public void delete(int id)
    {
        PreparedStatement sql;
        try
        {
            sql = getConnection().prepareStatement("DELETE FROM Users WHERE id = ? ");
            sql.setInt(1, id);
            sql.executeUpdate();
        }
        catch (SQLException e)
        {
            System.out.println(e.getMessage());
        }   
    }
    
    public void close()
    {
        try 
        {
            getConnection().close();
        } catch (SQLException ex) 
        {
            System.out.println(ex.getMessage());
        }
    }

}
