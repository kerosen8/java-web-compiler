package com.application.util;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnectorManager {

    private static final String URL_KEY = "database.url";
    private static final String USERNAME_KEY = "database.username";
    private static final String PASSWORD_KEY = "database.password";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                PropertiesUtil.getProperty(URL_KEY),
                PropertiesUtil.getProperty(USERNAME_KEY),
                PropertiesUtil.getProperty(PASSWORD_KEY)
        );
    }

    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}
