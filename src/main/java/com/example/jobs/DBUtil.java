package com.example.jobs;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBUtil {
    public static Connection getConnection() throws Exception {
        // Load H2 driver
        Class.forName("org.h2.Driver");

        // In-memory database "jobsdb"
        // DB_CLOSE_DELAY=-1 keeps it alive until app stops
        return DriverManager.getConnection("jdbc:h2:mem:jobsdb;DB_CLOSE_DELAY=-1", "sa", "");
    }
}
