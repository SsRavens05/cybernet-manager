package com.example.cybergame_management;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

final class LoaiKhuVucRepository {
    private LoaiKhuVucRepository() {
    }

    static ObservableList<LoaiKhuVuc> findAll() throws SQLException {
        String sql = "SELECT MALOAIKV, TENLOAIKV, SOLUONG, GIA FROM LOAIKHUVUC WHERE NVL(IS_DELETE, 0) = 0 ORDER BY MALOAIKV";
        ObservableList<LoaiKhuVuc> result = FXCollections.observableArrayList();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                result.add(new LoaiKhuVuc(
                        resultSet.getString("MALOAIKV"),
                        resultSet.getString("TENLOAIKV"),
                        String.valueOf(resultSet.getLong("SOLUONG")),
                        String.valueOf(resultSet.getLong("GIA")) + "đ",
                        "—"
                ));
            }
        }
        
        // Cơ chế tự vá lỗi (Self-Healing): Nếu bảng LOAIKHUVUC trống, tự động nạp seed data vào Database Oracle
        if (result.isEmpty()) {
            try {
                seedInitialData();
                try (Connection connection = DatabaseConnection.getConnection();
                     PreparedStatement statement = connection.prepareStatement(sql);
                     ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        result.add(new LoaiKhuVuc(
                                resultSet.getString("MALOAIKV"),
                                resultSet.getString("TENLOAIKV"),
                                String.valueOf(resultSet.getLong("SOLUONG")),
                                String.valueOf(resultSet.getLong("GIA")) + "đ",
                                "—"
                        ));
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }

    private static void seedInitialData() throws SQLException {
        String sql = "INSERT INTO LOAIKHUVUC (MALOAIKV, TENLOAIKV, SOLUONG, GIA, IS_DELETE) VALUES (?, ?, ?, ?, 0)";
        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                // VIP
                stmt.setString(1, "LKV001");
                stmt.setString(2, "VIP");
                stmt.setLong(3, 10);
                stmt.setLong(4, 15000);
                stmt.addBatch();

                // Thường
                stmt.setString(1, "LKV002");
                stmt.setString(2, "Thường");
                stmt.setLong(3, 20);
                stmt.setLong(4, 10000);
                stmt.addBatch();

                // Esport
                stmt.setString(1, "LKV003");
                stmt.setString(2, "Esport");
                stmt.setLong(3, 8);
                stmt.setLong(4, 20000);
                stmt.addBatch();

                // Offline
                stmt.setString(1, "LKV004");
                stmt.setString(2, "Offline");
                stmt.setLong(3, 5);
                stmt.setLong(4, 8000);
                stmt.addBatch();

                stmt.executeBatch();
                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }
}
