package com.example.cybergame_management;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

final class KhachHangRepository {
    private KhachHangRepository() {
    }

    static boolean isDatabaseEnabled() {
        return DatabaseConnection.isConfigured();
    }

    static ObservableList<KhachHang> findAll() throws SQLException {
        String sql = """
                SELECT kh.MAKH,
                       u.HOTEN,
                       kh.SODIEMTICHLUY,
                       kh.IS_DELETE,
                       kh.CREATE_AT
                FROM KHACHHANG kh
                LEFT JOIN APP_USER u ON u.USER_ID = kh.USER_ID
                WHERE NVL(kh.IS_DELETE, 0) = 0
                ORDER BY kh.MAKH
                """;

        ObservableList<KhachHang> result = FXCollections.observableArrayList();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                result.add(new KhachHang(
                        resultSet.getString("MAKH"),
                        valueOrEmpty(resultSet.getString("HOTEN")),
                        "0",
                        String.valueOf(resultSet.getLong("SODIEMTICHLUY")),
                        resultSet.getInt("IS_DELETE") == 0 ? "ACTIVE" : "INACTIVE",
                        valueOrEmpty(resultSet.getString("CREATE_AT"))
                ));
            }
        }
        return result;
    }

    static void update(KhachHang khachHang) throws SQLException {
        String sql = """
                UPDATE KHACHHANG
                SET SODIEMTICHLUY = ?
                WHERE MAKH = ?
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, DisplayFormat.parseMoney(khachHang.getSoDiemTichLuy()));
            statement.setString(2, khachHang.getMaKH());
            statement.executeUpdate();
        }
    }

    static void softDelete(String maKH) throws SQLException {
        String sql = "UPDATE KHACHHANG SET IS_DELETE = 1 WHERE MAKH = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, maKH);
            statement.executeUpdate();
        }
    }

    private static String valueOrEmpty(String value) {
        return value == null ? "" : value;
    }
}
