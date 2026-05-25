package com.example.cybergame_management;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class DbCheckTriggers {
    public static void check() {
        System.out.println("=== COMPILATION ERRORS IN USER_ERRORS ===");
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            String errQuery = "SELECT NAME, TYPE, LINE, POSITION, TEXT FROM USER_ERRORS ORDER BY TYPE, NAME, LINE, POSITION";
            try (ResultSet rs = stmt.executeQuery(errQuery)) {
                boolean hasErrors = false;
                while (rs.next()) {
                    hasErrors = true;
                    System.out.printf("[%s] %s (Line %d:%d) -> %s\n",
                            rs.getString("TYPE"),
                            rs.getString("NAME"),
                            rs.getInt("LINE"),
                            rs.getInt("POSITION"),
                            rs.getString("TEXT")
                    );
                }
                if (!hasErrors) {
                    System.out.println("No compilation errors found in any triggers, functions, or procedures!");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        check();
    }
}
