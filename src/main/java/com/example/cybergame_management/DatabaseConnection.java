package com.example.cybergame_management;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

final class DatabaseConnection {
    private DatabaseConnection() {
    }

    static boolean isConfigured() {
        return DatabaseConfig.isEnabled() && DatabaseConfig.hasCredentials();
    }

    static Connection getConnection() throws SQLException {
        if (!isConfigured()) {
            throw new SQLException("Database is disabled or missing credentials.");
        }
        return DriverManager.getConnection(DatabaseConfig.url(), DatabaseConfig.user(), DatabaseConfig.password());
    }
}
