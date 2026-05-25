package com.example.cybergame_management;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

final class SanPhamRepository {
    private SanPhamRepository() {
    }

    static boolean isDatabaseEnabled() {
        return DatabaseConnection.isConfigured();
    }

    static ObservableList<SanPham> findAll() throws SQLException {
        String sql = """
                SELECT MASP, TENSP, DVT, LOAISP, SOLUONGTK, DONGIABQ, SODIEMTICHLUY
                FROM SAN_PHAM
                WHERE NVL(IS_DELETE, 0) = 0
                ORDER BY MASP
                """;

        ObservableList<SanPham> result = FXCollections.observableArrayList();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                result.add(new SanPham(
                        resultSet.getString("MASP"),
                        valueOrEmpty(resultSet.getString("TENSP")),
                        valueOrEmpty(resultSet.getString("LOAISP")),
                        String.valueOf(resultSet.getLong("DONGIABQ")),
                        String.valueOf(resultSet.getLong("SOLUONGTK")),
                        valueOrEmpty(resultSet.getString("DVT")),
                        String.valueOf(resultSet.getLong("SODIEMTICHLUY"))
                ));
            }
        }
        return result;
    }

    static void insert(SanPham sp) throws SQLException {
        String sql = """
                INSERT INTO SAN_PHAM (MASP, TENSP, DVT, LOAISP, SOLUONGTK, DONGIABQ, SODIEMTICHLUY, IS_DELETE)
                VALUES (?, ?, ?, ?, ?, ?, ?, 0)
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, sp.maSPProperty().get());
            statement.setString(2, sp.tenSPProperty().get());
            statement.setString(3, sp.donViProperty().get());
            statement.setString(4, sp.loaiProperty().get());
            statement.setLong(5, DisplayFormat.parseMoney(sp.soLuongProperty().get()));
            statement.setLong(6, DisplayFormat.parseMoney(sp.giaProperty().get()));
            statement.setLong(7, DisplayFormat.parseMoney(sp.soDiemTichLuyProperty().get()));
            statement.executeUpdate();
        }
    }

    static void update(SanPham sp) throws SQLException {
        String sql = """
                UPDATE SAN_PHAM
                SET TENSP = ?, DVT = ?, LOAISP = ?, SOLUONGTK = ?, DONGIABQ = ?, SODIEMTICHLUY = ?
                WHERE MASP = ?
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, sp.tenSPProperty().get());
            statement.setString(2, sp.donViProperty().get());
            statement.setString(3, sp.loaiProperty().get());
            statement.setLong(4, DisplayFormat.parseMoney(sp.soLuongProperty().get()));
            statement.setLong(5, DisplayFormat.parseMoney(sp.giaProperty().get()));
            statement.setLong(6, DisplayFormat.parseMoney(sp.soDiemTichLuyProperty().get()));
            statement.setString(7, sp.maSPProperty().get());
            statement.executeUpdate();
        }
    }

    static void softDelete(String maSP) throws SQLException {
        String sql = "UPDATE SAN_PHAM SET IS_DELETE = 1 WHERE MASP = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, maSP);
            statement.executeUpdate();
        }
    }

    static String getNextMaSP() {
        if (!isDatabaseEnabled()) {
            return "SP" + String.format("%03d", (int)(Math.random() * 900) + 100);
        }
        String sql = "SELECT MASP FROM SAN_PHAM ORDER BY MASP DESC FETCH FIRST 1 ROWS ONLY";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                String lastMa = resultSet.getString("MASP");
                if (lastMa != null && lastMa.startsWith("SP")) {
                    try {
                        int num = Integer.parseInt(lastMa.substring(2));
                        return "SP" + String.format("%03d", num + 1);
                    } catch (NumberFormatException e) {
                        // ignore
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "SP001";
    }

    private static String valueOrEmpty(String value) {
        return value == null ? "" : value;
    }
}
