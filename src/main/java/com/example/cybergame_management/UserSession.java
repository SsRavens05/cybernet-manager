package com.example.cybergame_management;

public final class UserSession {
    private static String username = "Admin";
    private static String role = "Admin"; // "Admin" or "Nhân viên"

    private UserSession() {
    }

    public static String getUsername() {
        return username;
    }

    public static void setUsername(String name) {
        username = name;
        // Determine role automatically based on username:
        if (name != null && (name.trim().equalsIgnoreCase("admin") || name.trim().equalsIgnoreCase("ad"))) {
            role = "Admin";
        } else {
            role = "Nhân viên";
        }
    }

    public static String getRole() {
        return role;
    }

    public static void setRole(String r) {
        role = r;
    }

    public static boolean isAdmin() {
        return "Admin".equalsIgnoreCase(role);
    }
}
