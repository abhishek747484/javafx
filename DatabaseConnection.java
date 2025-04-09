package database;

import java.sql.*;

public class DatabaseConnection {
    // Database connection details
    private static final String HOST = "localhost";  // Database host
    private static final String USER = "root";  // Database username
    private static final String PASS = "Iamfinethankyou96";  // Database password
    private static final int PORT = 3306;  // Port for MySQL (default: 3306)
    private static final String DBNAME = "Assingment2";  // Database name
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";  // MySQL JDBC driver
    private static final String URL = "jdbc:mysql://" + HOST + ":" + PORT + "/" + DBNAME;  // Connection URL

    // Singleton instance of the DatabaseConnection
    private static DatabaseConnection instance;
    
    // The actual database connection object
    private Connection connection;

    // Private constructor to initialize the database connection
    private DatabaseConnection() {
        try {
            // Try to establish a connection using the provided details
            connection = DriverManager.getConnection(URL, USER, PASS);
        } catch (SQLException e) {
            // Handle any SQLException that occurs during the connection attempt
            System.err.println("Failed to initialize database connection: " + e.getMessage());
        }
    }

    // Method to get the singleton instance of the DatabaseConnection
    public static DatabaseConnection getInstance() {
        // If the instance does not exist, create a new one
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;  // Return the singleton instance
    }

    // Method to get the current connection to the database
    public Connection getConnection() {
        try {
            // If the connection is null or closed, create a new one
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL, USER, PASS);
            }
        } catch (SQLException e) {
            // Handle any SQLException that occurs while getting the connection
            System.err.println("Failed to get database connection: " + e.getMessage());
        }
        return connection;  // Return the established connection
    }
}
