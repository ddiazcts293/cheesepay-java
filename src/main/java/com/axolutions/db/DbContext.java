package com.axolutions.db;

import java.sql.*;

public class DbContext 
{
    private DbConnectionWrapper wrapper;
    
    public DbContext(DbConnectionWrapper dbConnectionWrapper)
    {
        this.wrapper = dbConnectionWrapper;
    }

    public boolean isConnected()
    {
        return wrapper.getConnection() != null; //&& dbConnection.isValid(0);
    }
}
