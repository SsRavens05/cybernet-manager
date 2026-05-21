package com.example.cybergame_management;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

final class AuthRepository {
    private AuthRepository() {
    }

    static boolean isDatabaseLoginEnabled() {
        return DatabaseConnection.isConfigured();
    }

    static LoginResult authenticate(String username, String password) throws SQLException {
        String sql = """
                SELECT u.HOTEN
                FROM APP_ACCOUNT a
                JOIN APP_USER u ON u.USER_ID = a.USER_ID
                WHERE a.USERNAME = ?
                  AND a.PASS = ?
                  AND NVL(a.IS_DELETE, 0) = 0
                  AND NVL(u.IS_DELETE, 0) = 0
                  AND (a.TRANGTHAI IS NULL OR UPPER(a.TRANGTHAI) NOT IN ('LOCKED', 'KHOA', 'BI_KHOA'))
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            statement.setString(2, password);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new LoginResult(true, resultSet.getString("HOTEN"));
                }
                return new LoginResult(false, null);
            }
        }
    }

    record LoginResult(boolean success, String displayName) {
    }
}
