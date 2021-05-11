package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database 
{
    private Connection connection;
    private static final String DATABASE_NAME = "ChatMe";

    public Database()
    {
        try
        {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(
                "jdbc:mysql://localhost/" + DATABASE_NAME,
                "root",
                ""
            );
        } 
        catch (ClassNotFoundException e)
        {

        }
        catch (SQLException e)
        {

        }
    }

    public Connection getConnection()
    {
        return connection;
    }
}
