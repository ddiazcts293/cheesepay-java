package com.axolutions.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbConnectionWrapper 
{
    private String string;
    private Connection connection = null;

    public DbConnectionWrapper(String string)
    {
        this.string = string;
    }

    public String getString() 
    {
        return string;
    }

    public Connection getConnection() 
    {
        return connection;
    }

    public Connection create(String user, String password) throws SQLException
    {
        connection = DriverManager.getConnection(string, user, password);
        return connection;
    }

    public void close()
    {
        if (connection != null)
        {
            try 
            {
                connection.close();
            } 
            catch (SQLException e) 
            {
            }
            finally
            {
                connection = null;
            }
        }
    }
}
