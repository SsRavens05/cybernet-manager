package com.example.cybergame_management;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

final class PCRepository {
    private PCRepository() {
    }

    static boolean isDatabaseEnabled() {
        return DatabaseConnection.isConfigured();
    }

    static ObservableList<PC> findAll() throws SQLException {
        String sql = """
                SELECT MAPC, MAKV, CPU, RAM, VGA, ROM, SOMAY, LOAIPC, TRANGTHAI, CREATE_AT
                FROM PC
                WHERE NVL(IS_DELETE, 0) = 0
                ORDER BY MAPC
                """;

        ObservableList<PC> result = FXCollections.observableArrayList();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                result.add(new PC(
                        resultSet.getString("MAPC"),
                        valueOrEmpty(resultSet.getString("MAKV")),
                        valueOrEmpty(resultSet.getString("CPU")),
                        valueOrEmpty(resultSet.getString("RAM")),
                        valueOrEmpty(resultSet.getString("VGA")),
                        valueOrEmpty(resultSet.getString("ROM")),
                        String.valueOf(resultSet.getLong("SOMAY")),
                        valueOrEmpty(resultSet.getString("LOAIPC")),
                        valueOrEmpty(resultSet.getString("TRANGTHAI")),
                        valueOrEmpty(resultSet.getString("CREATE_AT"))
                ));
            }
        }
        return result;
    }

    static void insert(PC pc) throws SQLException {
        String sql = """
                INSERT INTO PC (MAPC, MAKV, CPU, RAM, VGA, ROM, SOMAY, LOAIPC, TRANGTHAI, IS_DELETE, CREATE_AT)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, 0, CURRENT_TIMESTAMP)
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, pc.getMaPC());
            statement.setString(2, pc.getMaKV().isEmpty() ? null : pc.getMaKV());
            statement.setString(3, pc.getCpu());
            statement.setString(4, pc.getRam());
            statement.setString(5, pc.getVga());
            statement.setString(6, pc.getRom());
            statement.setLong(7, Long.parseLong(pc.getSoMay()));
            statement.setString(8, pc.getLoaiPC());
            statement.setString(9, pc.getTrangThai());
            statement.executeUpdate();
        }
    }

    static void update(PC pc) throws SQLException {
        String sql = """
                UPDATE PC
                SET MAKV = ?, CPU = ?, RAM = ?, VGA = ?, ROM = ?, SOMAY = ?, LOAIPC = ?, TRANGTHAI = ?
                WHERE MAPC = ?
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, pc.getMaKV().isEmpty() ? null : pc.getMaKV());
            statement.setString(2, pc.getCpu());
            statement.setString(3, pc.getRam());
            statement.setString(4, pc.getVga());
            statement.setString(5, pc.getRom());
            statement.setLong(6, Long.parseLong(pc.getSoMay()));
            statement.setString(7, pc.getLoaiPC());
            statement.setString(8, pc.getTrangThai());
            statement.setString(9, pc.getMaPC());
            statement.executeUpdate();
        }
    }

    static void softDelete(String maPC) throws SQLException {
        String sql = "UPDATE PC SET IS_DELETE = 1 WHERE MAPC = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, maPC);
            statement.executeUpdate();
        }
    }

    private static String valueOrEmpty(String value) {
        return value == null ? "" : value;
    }
}
