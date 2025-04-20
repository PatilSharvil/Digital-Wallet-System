package com.digitalwallet.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseUtil {
    // Database connection details
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String URL = "jdbc:mysql://localhost:3306/digital_wallet_db";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "osetyubwh5"; // Using a simple password for development

    static {
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(
                "Could not load MySQL JDBC driver. Please ensure mysql-connector-java is in the classpath.\n" +
                "Error: " + e.getMessage(), e
            );
        }
    }

    public static Connection getConnection() throws SQLException {
        try {
            return DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (SQLException e) {
            throw new SQLException("Failed to connect to database. Please check if MySQL is running and the credentials are correct.\n" +
                                 "URL: " + URL + "\n" +
                                 "Error: " + e.getMessage(), e);
        }
    }

    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
} 