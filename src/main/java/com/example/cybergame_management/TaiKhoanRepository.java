package com.example.cybergame_management;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

final class TaiKhoanRepository {
    private TaiKhoanRepository() {
    }

    static ObservableList<TaiKhoan> findAll() throws SQLException {
        String sql = """
                SELECT USERNAME,
                       PASS,
                       QUYENHANG,
                       TRANGTHAI,
                       TO_CHAR(CREATE_AT, 'YYYY-MM-DD') AS NGAYTAO
                FROM APP_ACCOUNT
                WHERE NVL(IS_DELETE, 0) = 0
                ORDER BY USERNAME
                """;

        ObservableList<TaiKhoan> result = FXCollections.observableArrayList();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                result.add(new TaiKhoan(
                        resultSet.getString("USERNAME"),
                        resultSet.getString("PASS"),
                        valueOrEmpty(resultSet.getString("QUYENHANG")),
                        valueOrEmpty(resultSet.getString("TRANGTHAI")),
                        valueOrEmpty(resultSet.getString("NGAYTAO"))
                ));
            }
        }
        return result;
    }

    static void insert(TaiKhoan tk) throws SQLException {
        String sqlUser = """
                INSERT INTO APP_USER (USER_ID, HOTEN, QUYENHAN, IS_DELETE)
                VALUES (?, ?, ?, 0)
                """;

        String sqlAccount = """
                INSERT INTO APP_ACCOUNT (ACCOUNT_ID, USER_ID, USERNAME, PASS, TRANGTHAI, QUYENHANG, IS_DELETE)
                VALUES (?, ?, ?, ?, ?, ?, 0)
                """;

        String username = tk.getTenDangNhap().toLowerCase().trim();
        String userId = "US_ACC_" + username;
        String accountId = "ACC_" + username;

        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);
            try {
                try (PreparedStatement stmtUser = connection.prepareStatement(sqlUser)) {
                    stmtUser.setString(1, userId);
                    stmtUser.setString(2, tk.getTenDangNhap()); // Sử dụng username làm họ tên giả định
                    stmtUser.setString(3, tk.getVaiTro().equalsIgnoreCase("Admin") ? "ADMIN" : "STAFF");
                    stmtUser.executeUpdate();
                }

                try (PreparedStatement stmtAcc = connection.prepareStatement(sqlAccount)) {
                    stmtAcc.setString(1, accountId);
                    stmtAcc.setString(2, userId);
                    stmtAcc.setString(3, username);
                    stmtAcc.setString(4, tk.getMatKhau());
                    stmtAcc.setString(5, tk.getTrangThai());
                    stmtAcc.setString(6, tk.getVaiTro());
                    stmtAcc.executeUpdate();
                }
                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    static void update(TaiKhoan tk) throws SQLException {
        String sql = """
                UPDATE APP_ACCOUNT
                SET PASS = ?,
                    TRANGTHAI = ?,
                    QUYENHANG = ?
                WHERE USERNAME = ?
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, tk.getMatKhau());
            statement.setString(2, tk.getTrangThai());
            statement.setString(3, tk.getVaiTro());
            statement.setString(4, tk.getTenDangNhap().toLowerCase().trim());
            statement.executeUpdate();
        }
    }

    static void softDelete(String username) throws SQLException {
        String sql = "UPDATE APP_ACCOUNT SET IS_DELETE = 1 WHERE USERNAME = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username.toLowerCase().trim());
            statement.executeUpdate();
        }
    }

    private static String valueOrEmpty(String value) {
        return value == null ? "" : value;
    }
}
