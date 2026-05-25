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
        // Thực hiện JOIN để lấy giá thuê GIA từ bảng LOAIKHUVUC
        String sql = """
                SELECT kv.MAKV, kv.TENKV, kv.SOMAYKV, lkv.GIA, kv.TRANGTHAI, nvl(kv.MALOAIKV, 'LKV_DEFAULT') AS MALOAIKV
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
                result.add(new KhuVuc(
                        resultSet.getString("MAKV"),
                        resultSet.getString("TENKV"),
                        String.valueOf(resultSet.getLong("SOMAYKV")),
                        String.valueOf(resultSet.getLong("GIA")) + "đ",
                        resultSet.getString("TRANGTHAI"),
                        resultSet.getString("MALOAIKV")
                ));
            }
        }
        return result;
    }

    static void insert(KhuVuc kv) throws SQLException {
        String maloaikv = "LKV_" + kv.getMaKV();

        // 1. Thêm/Merge loại khu vực mới chứa giá tiền
        String sqlLkv = "MERGE INTO LOAIKHUVUC USING DUAL ON (MALOAIKV = ?) " +
                "WHEN MATCHED THEN UPDATE SET GIA = ?, TENLOAIKV = ? " +
                "WHEN NOT MATCHED THEN INSERT (MALOAIKV, TENLOAIKV, GIA) VALUES (?, ?, ?)";

        // 2. Thêm mới khu vực
        String sqlKv = """
                INSERT INTO KHUVUC (MAKV, TENKV, SOMAYKV, TRANGTHAI, MALOAIKV, IS_DELETE)
                VALUES (?, ?, ?, ?, ?, 0)
                """;

        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);
            try {
                try (PreparedStatement stmtLkv = connection.prepareStatement(sqlLkv)) {
                    stmtLkv.setString(1, maloaikv);
                    stmtLkv.setLong(2, DisplayFormat.parseMoney(kv.getGiaThue()));
                    stmtLkv.setString(3, kv.getTenKV() + " Type");
                    stmtLkv.setString(4, maloaikv);
                    stmtLkv.setString(5, kv.getTenKV() + " Type");
                    stmtLkv.setLong(6, DisplayFormat.parseMoney(kv.getGiaThue()));
                    stmtLkv.executeUpdate();
                }

                try (PreparedStatement stmtKv = connection.prepareStatement(sqlKv)) {
                    stmtKv.setString(1, kv.getMaKV());
                    stmtKv.setString(2, kv.getTenKV());
                    stmtKv.setLong(3, DisplayFormat.parseInt(kv.getSoMay()));
                    stmtKv.setString(4, kv.getTrangThai());
                    stmtKv.setString(5, maloaikv);
                    stmtKv.executeUpdate();
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

    static void update(KhuVuc kv) throws SQLException {
        String maloaikv = "LKV_" + kv.getMaKV();

        // 1. Cập nhật giá tiền trong LOAIKHUVUC
        String sqlLkv = "MERGE INTO LOAIKHUVUC USING DUAL ON (MALOAIKV = ?) " +
                "WHEN MATCHED THEN UPDATE SET GIA = ?, TENLOAIKV = ? " +
                "WHEN NOT MATCHED THEN INSERT (MALOAIKV, TENLOAIKV, GIA) VALUES (?, ?, ?)";

        // 2. Cập nhật thông tin trong KHUVUC
        String sqlKv = """
                UPDATE KHUVUC
                SET TENKV = ?, SOMAYKV = ?, TRANGTHAI = ?, MALOAIKV = ?
                WHERE MAKV = ?
                """;

        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);
            try {
                try (PreparedStatement stmtLkv = connection.prepareStatement(sqlLkv)) {
                    stmtLkv.setString(1, maloaikv);
                    stmtLkv.setLong(2, DisplayFormat.parseMoney(kv.getGiaThue()));
                    stmtLkv.setString(3, kv.getTenKV() + " Type");
                    stmtLkv.setString(4, maloaikv);
                    stmtLkv.setString(5, kv.getTenKV() + " Type");
                    stmtLkv.setLong(6, DisplayFormat.parseMoney(kv.getGiaThue()));
                    stmtLkv.executeUpdate();
                }

                try (PreparedStatement stmtKv = connection.prepareStatement(sqlKv)) {
                    stmtKv.setString(1, kv.getTenKV());
                    stmtKv.setLong(2, DisplayFormat.parseInt(kv.getSoMay()));
                    stmtKv.setString(3, kv.getTrangThai());
                    stmtKv.setString(4, maloaikv);
                    stmtKv.setString(5, kv.getMaKV());
                    stmtKv.executeUpdate();
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

    static void delete(String maKV) throws SQLException {
        String sql = "UPDATE KHUVUC SET IS_DELETE = 1 WHERE MAKV = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, maKV);
            statement.executeUpdate();
        }
    }
}
