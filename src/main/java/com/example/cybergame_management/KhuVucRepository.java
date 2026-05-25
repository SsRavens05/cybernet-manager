package com.example.cybergame_management;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

final class KhuVucRepository {
    private KhuVucRepository() {
    }

    static boolean isDatabaseEnabled() {
        return DatabaseConnection.isConfigured();
    }

    static ObservableList<KhuVuc> findAll() throws SQLException {
        if (DatabaseConnection.isConfigured()) {
            LoaiKhuVucRepository.findAll();
        }
        // Thực hiện JOIN để lấy giá thuê GIA và tên loại TENLOAIKV từ bảng LOAIKHUVUC
        String sql = """
                SELECT kv.MAKV, kv.TENKV, kv.SOMAYKV, lkv.GIA, kv.TRANGTHAI, nvl(kv.MALOAIKV, 'LKV_DEFAULT') AS MALOAIKV, lkv.TENLOAIKV
                FROM KHUVUC kv
                LEFT JOIN LOAIKHUVUC lkv ON lkv.MALOAIKV = kv.MALOAIKV
                WHERE NVL(kv.IS_DELETE, 0) = 0
                ORDER BY kv.MAKV
                """;

        ObservableList<KhuVuc> result = FXCollections.observableArrayList();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                String tenLkv = resultSet.getString("TENLOAIKV");
                if (tenLkv == null || tenLkv.trim().isEmpty()) {
                    tenLkv = resultSet.getString("MALOAIKV");
                }
                if ("LKV001".equalsIgnoreCase(tenLkv)) tenLkv = "VIP";
                else if ("LKV002".equalsIgnoreCase(tenLkv)) tenLkv = "Thường";
                else if ("LKV003".equalsIgnoreCase(tenLkv)) tenLkv = "Esport";
                else if ("LKV004".equalsIgnoreCase(tenLkv)) tenLkv = "Offline";

                result.add(new KhuVuc(
                        resultSet.getString("MAKV"),
                        resultSet.getString("TENKV"),
                        String.valueOf(resultSet.getLong("SOMAYKV")),
                        String.valueOf(resultSet.getLong("GIA")) + "đ",
                        resultSet.getString("TRANGTHAI"),
                        tenLkv
                ));
            }
        }
        return result;
    }

    static void insert(KhuVuc kv) throws SQLException {
        if (DatabaseConnection.isConfigured()) {
            LoaiKhuVucRepository.findAll();
        }
        String sqlKv = """
                INSERT INTO KHUVUC (MAKV, TENKV, SOMAYKV, TRANGTHAI, MALOAIKV, IS_DELETE)
                VALUES (?, ?, ?, ?, ?, 0)
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmtKv = connection.prepareStatement(sqlKv)) {
            stmtKv.setString(1, kv.getMaKV());
            stmtKv.setString(2, kv.getTenKV());
            stmtKv.setLong(3, DisplayFormat.parseInt(kv.getSoMay()));
            stmtKv.setString(4, kv.getTrangThai());

            String malnv = kv.getMoTa();
            if ("VIP".equalsIgnoreCase(malnv) || "LKV001".equalsIgnoreCase(malnv)) {
                stmtKv.setString(5, "LKV001");
            } else if ("Thường".equalsIgnoreCase(malnv) || "LKV002".equalsIgnoreCase(malnv)) {
                stmtKv.setString(5, "LKV002");
            } else if ("Esport".equalsIgnoreCase(malnv) || "LKV003".equalsIgnoreCase(malnv)) {
                stmtKv.setString(5, "LKV003");
            } else if ("Offline".equalsIgnoreCase(malnv) || "LKV004".equalsIgnoreCase(malnv)) {
                stmtKv.setString(5, "LKV004");
            } else {
                stmtKv.setString(5, "LKV002"); // default fallback
            }

            stmtKv.executeUpdate();
        }
    }

    static void update(KhuVuc kv) throws SQLException {
        if (DatabaseConnection.isConfigured()) {
            LoaiKhuVucRepository.findAll();
        }
        String sqlKv = """
                UPDATE KHUVUC
                SET TENKV = ?, SOMAYKV = ?, TRANGTHAI = ?, MALOAIKV = ?
                WHERE MAKV = ?
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmtKv = connection.prepareStatement(sqlKv)) {
            stmtKv.setString(1, kv.getTenKV());
            stmtKv.setLong(2, DisplayFormat.parseInt(kv.getSoMay()));
            stmtKv.setString(3, kv.getTrangThai());

            String malnv = kv.getMoTa();
            if ("VIP".equalsIgnoreCase(malnv) || "LKV001".equalsIgnoreCase(malnv)) {
                stmtKv.setString(4, "LKV001");
            } else if ("Thường".equalsIgnoreCase(malnv) || "LKV002".equalsIgnoreCase(malnv)) {
                stmtKv.setString(4, "LKV002");
            } else if ("Esport".equalsIgnoreCase(malnv) || "LKV003".equalsIgnoreCase(malnv)) {
                stmtKv.setString(4, "LKV003");
            } else if ("Offline".equalsIgnoreCase(malnv) || "LKV004".equalsIgnoreCase(malnv)) {
                stmtKv.setString(4, "LKV004");
            } else {
                stmtKv.setString(4, "LKV002"); // default fallback
            }

            stmtKv.setString(5, kv.getMaKV());
            stmtKv.executeUpdate();
        }
    }

    static void delete(String maKV) throws SQLException {
        String sql = "UPDATE KHUVUC SET IS_DELETE = 1 WHERE MAKV = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, maKV);
            statement.executeUpdate();
        }
    }
}
