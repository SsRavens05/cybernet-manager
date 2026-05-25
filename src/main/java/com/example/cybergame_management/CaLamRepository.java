package com.example.cybergame_management;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

final class CaLamRepository {
    private CaLamRepository() {
    }

    static boolean isDatabaseEnabled() {
        return DatabaseConnection.isConfigured();
    }

    static ObservableList<CaLam> findAll() throws SQLException {
        String sql = """
                SELECT MACA, TGBD, TGKT, SOGIOLAM, THOIGIANTANGCA, TRANGTHAI
                FROM CA_LAM
                WHERE NVL(IS_DELETE, 0) = 0
                ORDER BY MACA
                """;

        ObservableList<CaLam> result = FXCollections.observableArrayList();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                double sogio = resultSet.getDouble("SOGIOLAM");
                double tangca = resultSet.getDouble("THOIGIANTANGCA");
                
                result.add(new CaLam(
                        resultSet.getString("MACA"),
                        formatTime(resultSet.getTimestamp("TGBD")),
                        formatTime(resultSet.getTimestamp("TGKT")),
                        (long)sogio + "h",
                        valueOrEmpty(resultSet.getString("TRANGTHAI")),
                        formatTangCa(tangca)
                ));
            }
        }
        return result;
    }

    static void insert(CaLam cl) throws SQLException {
        String sql = """
                INSERT INTO CA_LAM (MACA, TGBD, TGKT, NGAYCC, SOGIOLAM, THOIGIANTANGCA, TRANGTHAI, IS_DELETE)
                VALUES (?, ?, ?, ?, ?, ?, ?, 0)
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, cl.getMaCa());
            statement.setTimestamp(2, parseTime(cl.getThoiGianBD()));
            statement.setTimestamp(3, parseTime(cl.getThoiGianKT()));
            statement.setDate(4, Date.valueOf(LocalDate.now()));
            statement.setDouble(5, parseDoubleClean(cl.getSoGioLam()));
            statement.setDouble(6, parseTangCa(cl.getSoGioTangCa()));
            statement.setString(7, cl.getTrangThai());
            statement.executeUpdate();
        }
    }

    static void update(CaLam cl) throws SQLException {
        String sql = """
                UPDATE CA_LAM
                SET TGBD = ?, TGKT = ?, SOGIOLAM = ?, THOIGIANTANGCA = ?, TRANGTHAI = ?
                WHERE MACA = ?
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setTimestamp(1, parseTime(cl.getThoiGianBD()));
            statement.setTimestamp(2, parseTime(cl.getThoiGianKT()));
            statement.setDouble(3, parseDoubleClean(cl.getSoGioLam()));
            statement.setDouble(4, parseTangCa(cl.getSoGioTangCa()));
            statement.setString(5, cl.getTrangThai());
            statement.setString(6, cl.getMaCa());
            statement.executeUpdate();
        }
    }

    static void softDelete(String maCa) throws SQLException {
        String sql = "UPDATE CA_LAM SET IS_DELETE = 1 WHERE MACA = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, maCa);
            statement.executeUpdate();
        }
    }

    static String getNextMaCa() {
        if (!isDatabaseEnabled()) {
            return "CA" + String.format("%03d", (int)(Math.random() * 900) + 100);
        }
        String sql = "SELECT MACA FROM CA_LAM ORDER BY MACA DESC FETCH FIRST 1 ROWS ONLY";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                String lastMa = resultSet.getString("MACA");
                if (lastMa != null && lastMa.startsWith("CA")) {
                    try {
                        int num = Integer.parseInt(lastMa.substring(2));
                        return "CA" + String.format("%03d", num + 1);
                    } catch (NumberFormatException e) {
                        // ignore
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "CA001";
    }

    private static String valueOrEmpty(String value) {
        return value == null ? "" : value;
    }

    private static String formatTime(Timestamp ts) {
        if (ts == null) return "";
        return ts.toLocalDateTime().format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    private static Timestamp parseTime(String timeStr) {
        try {
            if (timeStr == null || timeStr.trim().isEmpty()) return null;
            String clean = timeStr.trim();
            if (clean.length() <= 5) {
                String today = LocalDate.now().toString();
                clean = today + " " + clean + ":00";
            } else if (clean.length() <= 8) {
                String today = LocalDate.now().toString();
                clean = today + " " + clean;
            }
            return Timestamp.valueOf(clean);
        } catch (Exception e) {
            return new Timestamp(System.currentTimeMillis());
        }
    }

    private static double parseDoubleClean(String str) {
        if (str == null) return 0.0;
        String clean = str.replaceAll("[^0-9.-]", "").trim();
        try {
            return Double.parseDouble(clean);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    private static double parseTangCa(String str) {
        if (str == null || str.equals("—")) return 0.0;
        String clean = str.replaceAll("[^0-9.-]", "").trim();
        try {
            return Double.parseDouble(clean);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    private static String formatTangCa(double val) {
        if (val <= 0.0) return "—";
        return "+" + (long) val + "h";
    }
}
