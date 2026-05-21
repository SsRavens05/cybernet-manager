package com.example.cybergame_management;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

final class DatabaseConfig {
    private static final String CONFIG_FILE = "database.properties";
    private static final Properties PROPERTIES = loadProperties();

    private DatabaseConfig() {
    }

    static boolean isEnabled() {
        return Boolean.parseBoolean(value("CYBERGAME_DB_ENABLED", "cybergame.db.enabled", "false"));
    }

    static String url() {
        return value("CYBERGAME_DB_URL", "cybergame.db.url", "jdbc:oracle:thin:@localhost:1521/XEPDB1");
    }

    static String user() {
        return value("CYBERGAME_DB_USER", "cybergame.db.user", "");
    }

    static String password() {
        return value("CYBERGAME_DB_PASSWORD", "cybergame.db.password", "");
    }

    static boolean hasCredentials() {
        return !user().isBlank();
    }

    private static String value(String envName, String propertyName, String fallback) {
        String envValue = System.getenv(envName);
        if (envValue != null && !envValue.isBlank()) {
            return envValue;
        }
        String propertyValue = System.getProperty(propertyName);
        if (propertyValue != null && !propertyValue.isBlank()) {
            return propertyValue;
        }
        return PROPERTIES.getProperty(propertyName, fallback).trim();
    }

    private static Properties loadProperties() {
        Properties properties = new Properties();
        try (InputStream input = DatabaseConfig.class.getResourceAsStream(CONFIG_FILE)) {
            if (input != null) {
                properties.load(input);
            }
        } catch (IOException e) {
            System.err.println("Khong doc duoc database.properties: " + e.getMessage());
        }
        return properties;
    }
}
