package com.example.cybergame_management;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;

final class KhuyenMaiRepository {
    private KhuyenMaiRepository() {
    }

    static boolean isDatabaseEnabled() {
        return DatabaseConnection.isConfigured();
    }

    static ObservableList<KhuyenMai> findAll() throws SQLException {
        String sql = """
                SELECT MACTR, TENCTR, NGBD, NGKT, CHIETKHAU, LOAICTR, TRANGTHAI
                FROM CHUONG_TRINH_KHUYEN_MAI
                WHERE NVL(IS_DELETE, 0) = 0
                ORDER BY MACTR
                """;

        ObservableList<KhuyenMai> result = FXCollections.observableArrayList();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                String loaiCTR = valueOrEmpty(resultSet.getString("LOAICTR"));
                double chietKhauVal = resultSet.getDouble("CHIETKHAU");
                
                result.add(new KhuyenMai(
                        resultSet.getString("MACTR"),
                        valueOrEmpty(resultSet.getString("TENCTR")),
                        loaiCTR,
                        formatChietKhau(chietKhauVal, loaiCTR),
                        resultSet.getDate("NGBD") != null ? resultSet.getDate("NGBD").toString() : "",
                        resultSet.getDate("NGKT") != null ? resultSet.getDate("NGKT").toString() : "",
                        valueOrEmpty(resultSet.getString("TRANGTHAI"))
                ));
            }
        }
        return result;
    }

    static void insert(KhuyenMai km) throws SQLException {
        String sql = """
                INSERT INTO CHUONG_TRINH_KHUYEN_MAI (MACTR, TENCTR, NGBD, NGKT, CHIETKHAU, LOAICTR, TRANGTHAI, IS_DELETE)
                VALUES (?, ?, ?, ?, ?, ?, ?, 0)
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, km.getMaCTR());
            statement.setString(2, km.getTenCTR());
            statement.setDate(3, parseDate(km.getNgayBD()));
            statement.setDate(4, parseDate(km.getNgayKT()));
            statement.setDouble(5, parseChietKhau(km.getChietKhau()));
            statement.setString(6, km.getLoaiCTR());
            statement.setString(7, km.getTrangThai());
            statement.executeUpdate();
        }
    }

    static void update(KhuyenMai km) throws SQLException {
        String sql = """
                UPDATE CHUONG_TRINH_KHUYEN_MAI
                SET TENCTR = ?, NGBD = ?, NGKT = ?, CHIETKHAU = ?, LOAICTR = ?, TRANGTHAI = ?
                WHERE MACTR = ?
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, km.getTenCTR());
            statement.setDate(2, parseDate(km.getNgayBD()));
            statement.setDate(3, parseDate(km.getNgayKT()));
            statement.setDouble(4, parseChietKhau(km.getChietKhau()));
            statement.setString(5, km.getLoaiCTR());
            statement.setString(6, km.getTrangThai());
            statement.setString(7, km.getMaCTR());
            statement.executeUpdate();
        }
    }

    static void softDelete(String maCTR) throws SQLException {
        String sql = "UPDATE CHUONG_TRINH_KHUYEN_MAI SET IS_DELETE = 1 WHERE MACTR = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, maCTR);
            statement.executeUpdate();
        }
    }

    static String getNextMaCTR() {
        if (!isDatabaseEnabled()) {
            return "CTR" + String.format("%03d", (int)(Math.random() * 900) + 100);
        }
        String sql = "SELECT MACTR FROM CHUONG_TRINH_KHUYEN_MAI ORDER BY MACTR DESC FETCH FIRST 1 ROWS ONLY";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                String lastMa = resultSet.getString("MACTR");
                if (lastMa != null && lastMa.startsWith("CTR")) {
                    try {
                        int num = Integer.parseInt(lastMa.substring(3));
                        return "CTR" + String.format("%03d", num + 1);
                    } catch (NumberFormatException e) {
                        // ignore
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "CTR001";
    }

    private static String valueOrEmpty(String value) {
        return value == null ? "" : value;
    }

    private static Date parseDate(String dateStr) {
        try {
            if (dateStr == null || dateStr.trim().isEmpty()) return null;
            return Date.valueOf(dateStr.trim());
        } catch (Exception e) {
            return new Date(System.currentTimeMillis());
        }
    }

    private static double parseChietKhau(String chietKhau) {
        if (chietKhau == null) return 0.0;
        String clean = chietKhau.replaceAll("[^0-9.-]", "").trim();
        try {
            return Double.parseDouble(clean);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    private static String formatChietKhau(double chietKhau, String loaiCTR) {
        if ("GIAM_GIA".equals(loaiCTR)) {
            return (long) chietKhau + "%";
        } else if ("TANG_QUA".equals(loaiCTR)) {
            return DisplayFormat.money((long) chietKhau);
        } else if ("TANG_GIO".equals(loaiCTR)) {
            return (long) chietKhau + "h";
        }
        return String.valueOf(chietKhau);
    }
}
