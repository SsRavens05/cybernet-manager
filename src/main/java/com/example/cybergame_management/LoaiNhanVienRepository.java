package com.example.cybergame_management;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

final class LoaiNhanVienRepository {
    private LoaiNhanVienRepository() {
    }

    static ObservableList<LoaiNhanVien> findAll() throws SQLException {
        String sql = """
                SELECT MALNV, VITRI, MUCLUONG
                FROM LOAI_NHAN_VIEN
                WHERE NVL(IS_DELETE, 0) = 0
                ORDER BY MALNV
                """;

        ObservableList<LoaiNhanVien> result = FXCollections.observableArrayList();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                result.add(new LoaiNhanVien(
                        resultSet.getString("MALNV"),
                        resultSet.getString("VITRI") == null ? "" : resultSet.getString("VITRI"),
                        DisplayFormat.money(resultSet.getLong("MUCLUONG")) + "đ"
                ));
            }
        }

        // Cơ chế tự vá lỗi (Self-Healing): Nếu bảng LOAI_NHAN_VIEN trống, tự động nạp seed data vào Database Oracle
        if (result.isEmpty()) {
            try {
                seedInitialData();
                // Thực hiện truy vấn lại sau khi đã chèn dữ liệu
                try (Connection connection = DatabaseConnection.getConnection();
                     PreparedStatement statement = connection.prepareStatement(sql);
                     ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        result.add(new LoaiNhanVien(
                                resultSet.getString("MALNV"),
                                resultSet.getString("VITRI") == null ? "" : resultSet.getString("VITRI"),
                                DisplayFormat.money(resultSet.getLong("MUCLUONG")) + "đ"
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
        String sql = "INSERT INTO LOAI_NHAN_VIEN (MALNV, VITRI, MUCLUONG, IS_DELETE) VALUES (?, ?, ?, 0)";
        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                
                // LNV001 - Quản Lý - 12,000,000 đ
                stmt.setString(1, "LNV001");
                stmt.setString(2, "Quản Lý");
                stmt.setLong(3, 12000000);
                stmt.addBatch();

                // LNV002 - Kỹ Thuật - 8,000,000 đ
                stmt.setString(1, "LNV002");
                stmt.setString(2, "Kỹ Thuật");
                stmt.setLong(3, 8000000);
                stmt.addBatch();

                // LNV003 - Nhân Viên - 7,000,000 đ
                stmt.setString(1, "LNV003");
                stmt.setString(2, "Nhân Viên");
                stmt.setLong(3, 7000000);
                stmt.addBatch();

                // LNV004 - Bảo Vệ - 6,000,000 đ
                stmt.setString(1, "LNV004");
                stmt.setString(2, "Bảo Vệ");
                stmt.setLong(3, 6000000);
                stmt.addBatch();

                // LNV005 - Phục Vụ - 6,500,000 đ
                stmt.setString(1, "LNV005");
                stmt.setString(2, "Phục Vụ");
                stmt.setLong(3, 6500000);
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
