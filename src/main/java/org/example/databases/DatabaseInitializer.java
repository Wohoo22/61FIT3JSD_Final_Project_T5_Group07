package org.example.databases;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Stebenc0912@))@"; // Replace with your DB password
    private static final String DB_NAME = "events_scheduler";

    public DatabaseInitializer() {
        initializeDatabase();
    }

    public void initializeDatabase() {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {

            // Check if the database exists, if not create it
            if (!isDatabaseExists(connection, DB_NAME)) {
                createDatabase(connection, DB_NAME);
            }

            // Use the events_scheduler database for further operations
            try (Connection dbConnection = DriverManager.getConnection(DB_URL + DB_NAME, DB_USER, DB_PASSWORD)) {

                // Ensure InitializationStatus table exists
                createInitializationStatusTable(dbConnection);

                // Check if the script has already been executed
                if (!isScriptExecuted(dbConnection)) {
                    // Run the initialization script
                    runInitializationScript(dbConnection);

                    // Mark the script as executed
                    markScriptAsExecuted(dbConnection);
                } else {
                    System.out.println("Initialization script has already been executed. Skipping...");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Check if the database exists
    private boolean isDatabaseExists(Connection connection, String dbName) throws Exception {
        String query = "SHOW DATABASES LIKE ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, dbName);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    // Create the database
    private void createDatabase(Connection connection, String dbName) throws Exception {
        String createDbQuery = "CREATE DATABASE " + dbName;
        try (Statement statement = connection.createStatement()) {
            statement.execute(createDbQuery);
            System.out.println("Database '" + dbName + "' created successfully.");
        }
    }

    // Create the InitializationStatus table
    private void createInitializationStatusTable(Connection connection) throws Exception {
        String createTableQuery = "CREATE TABLE IF NOT EXISTS InitializationStatus ("
                + "id INT PRIMARY KEY," + "script_executed BOOLEAN NOT NULL)";
        try (Statement statement = connection.createStatement()) {
            statement.execute(createTableQuery);

            // Insert initial record with script_executed set to FALSE
            String insertQuery = "INSERT INTO InitializationStatus (id, script_executed) VALUES (1, FALSE)"
                    + "ON DUPLICATE KEY UPDATE script_executed = script_executed";
            try (PreparedStatement ps = connection.prepareStatement(insertQuery)) {
                ps.executeUpdate();
                System.out.println("InitializationStatus table created/checked successfully.");
            }
        }
    }

    private boolean isScriptExecuted(Connection connection) throws Exception {
        String query = "SELECT script_executed FROM InitializationStatus WHERE id = 1";
        try (PreparedStatement ps = connection.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getBoolean("script_executed");
            }
        }
        return false; // If no record exists, assume not executed
    }

    private void runInitializationScript(Connection connection) throws Exception {
        String sqlScript = "CREATE TABLE IF NOT EXISTS Event ("
                + "id INT AUTO_INCREMENT PRIMARY KEY,"
                + "title VARCHAR(255) NOT NULL,"
                + "description TEXT,"
                + "location VARCHAR(255),"
                + "color VARCHAR(50),"
                + "isRepeated BOOLEAN NOT NULL,"
                + "repeatType VARCHAR(50),"
                + "startDate DATE,"
                + "endDate DATE,"
                + "startTime TIME NOT NULL,"
                + "endTime TIME NOT NULL"
                + ");";

        try (Statement statement = connection.createStatement()) {
            statement.execute(sqlScript);
            System.out.println("Initialization script executed successfully.");
        } catch (SQLException e) {
            System.err.println("SQL error during initialization: " + e.getMessage());
        }
    }

    private void markScriptAsExecuted(Connection connection) throws Exception {
        String query = "UPDATE InitializationStatus SET script_executed = TRUE WHERE id = 1";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.executeUpdate();
            System.out.println("Marked the script as executed.");
        }
    }

}