package com.example.cybergame_management;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

final class ThietBiRepository {
    private ThietBiRepository() {
    }

    static ObservableList<ThietBi> findAll() throws SQLException {
        String sql = """
                SELECT MATB, TENTB, LOAITB, TRANGTHAI, nvl(MAPC, '—') AS MAPC
                FROM THIET_BI
                WHERE NVL(IS_DELETE, 0) = 0
                ORDER BY MATB
                """;

        ObservableList<ThietBi> result = FXCollections.observableArrayList();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                result.add(new ThietBi(
                        resultSet.getString("MATB"),
                        resultSet.getString("TENTB"),
                        valueOrEmpty(resultSet.getString("LOAITB")),
                        valueOrEmpty(resultSet.getString("TRANGTHAI")),
                        valueOrEmpty(resultSet.getString("MAPC"))
                ));
            }
        }
        return result;
    }

    static void insert(ThietBi tb) throws SQLException {
        ensurePCExists(tb.getNgayMua());

        String sql = """
                INSERT INTO THIET_BI (MATB, MAPC, TENTB, LOAITB, TRANGTHAI, IS_DELETE)
                VALUES (?, ?, ?, ?, ?, 0)
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, tb.getMaTB());
            
            String mapc = tb.getNgayMua();
            if (mapc == null || mapc.trim().isEmpty() || mapc.equals("—")) {
                statement.setNull(2, java.sql.Types.VARCHAR);
            } else {
                statement.setString(2, mapc);
            }
            
            statement.setString(3, tb.getTenTB());
            statement.setString(4, tb.getLoaiTB());
            statement.setString(5, tb.getTrangThai());
            statement.executeUpdate();
        }
    }

    static void update(ThietBi tb) throws SQLException {
        ensurePCExists(tb.getNgayMua());

        String sql = """
                UPDATE THIET_BI
                SET TENTB = ?,
                    LOAITB = ?,
                    TRANGTHAI = ?,
                    MAPC = ?
                WHERE MATB = ?
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, tb.getTenTB());
            statement.setString(2, tb.getLoaiTB());
            statement.setString(3, tb.getTrangThai());
            
            String mapc = tb.getNgayMua();
            if (mapc == null || mapc.trim().isEmpty() || mapc.equals("—")) {
                statement.setNull(4, java.sql.Types.VARCHAR);
            } else {
                statement.setString(4, mapc);
            }
            
            statement.setString(5, tb.getMaTB());
            statement.executeUpdate();
        }
    }

    static void softDelete(String matb) throws SQLException {
        String sql = "UPDATE THIET_BI SET IS_DELETE = 1 WHERE MATB = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, matb);
            statement.executeUpdate();
        }
    }

    private static void ensurePCExists(String mapc) throws SQLException {
        if (mapc == null || mapc.trim().isEmpty() || mapc.equals("—")) {
            return;
        }

        String sqlCheck = "SELECT MAPC FROM PC WHERE MAPC = ?";
        String sqlInsert = "INSERT INTO PC (MAPC, SOMAY, TRANGTHAI, IS_DELETE) VALUES (?, 1, 'HOATDONG', 0)";
        
        try (Connection connection = DatabaseConnection.getConnection()) {
            try (PreparedStatement stmtCheck = connection.prepareStatement(sqlCheck)) {
                stmtCheck.setString(1, mapc);
                try (ResultSet rs = stmtCheck.executeQuery()) {
                    if (rs.next()) {
                        return; // PC đã tồn tại
                    }
                }
            }

            // Nếu PC chưa tồn tại, chèn để tránh vi phạm khóa ngoại FK_TB_PC
            try (PreparedStatement stmtIns = connection.prepareStatement(sqlInsert)) {
                stmtIns.setString(1, mapc);
                stmtIns.executeUpdate();
            }
        }
    }

    private static String valueOrEmpty(String value) {
        return value == null ? "" : value;
    }
}
