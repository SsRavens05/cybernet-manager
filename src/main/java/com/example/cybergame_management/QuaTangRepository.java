package com.example.cybergame_management;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

final class QuaTangRepository {
    private QuaTangRepository() {
    }

    static ObservableList<QuaTang> findAll() throws SQLException {
        String sql = """
                SELECT MAQT, SODIEMTIEUHAO, NOIDUNG
                FROM QUA_TANG
                WHERE NVL(IS_DELETE, 0) = 0
                ORDER BY MAQT
                """;

        ObservableList<QuaTang> result = FXCollections.observableArrayList();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                result.add(new QuaTang(
                        resultSet.getString("MAQT"),
                        resultSet.getString("NOIDUNG") == null ? "" : resultSet.getString("NOIDUNG"),
                        String.valueOf(resultSet.getLong("SODIEMTIEUHAO"))
                ));
            }
        }
        return result;
    }

    static void insert(QuaTang qt) throws SQLException {
        String sql = """
                INSERT INTO QUA_TANG (MAQT, SODIEMTIEUHAO, NOIDUNG, IS_DELETE)
                VALUES (?, ?, ?, 0)
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, qt.getMaQT());
            statement.setLong(2, Long.parseLong(qt.getSoDiemTieuHao().trim()));
            statement.setString(3, qt.getNoiDung());
            statement.executeUpdate();
        }
    }

    static void update(QuaTang qt) throws SQLException {
        String sql = """
                UPDATE QUA_TANG
                SET SODIEMTIEUHAO = ?, NOIDUNG = ?
                WHERE MAQT = ? AND NVL(IS_DELETE, 0) = 0
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, Long.parseLong(qt.getSoDiemTieuHao().trim()));
            statement.setString(2, qt.getNoiDung());
            statement.setString(3, qt.getMaQT());
            statement.executeUpdate();
        }
    }

    static void delete(String maQT) throws SQLException {
        String sql = """
                UPDATE QUA_TANG
                SET IS_DELETE = 1
                WHERE MAQT = ?
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, maQT);
            statement.executeUpdate();
        }
    }
}
