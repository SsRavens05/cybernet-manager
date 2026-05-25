package com.example.cybergame_management;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

final class DoiQuaRepository {
    private DoiQuaRepository() {
    }

    static ObservableList<DoiQua> findAll() throws SQLException {
        String sql = """
                SELECT MADQ, MAKH, MAQT, TO_CHAR(CREATE_AT, 'YYYY-MM-DD') AS NGAYDOI, SL, TRANGTHAI
                FROM LICHSUDOIQUA
                WHERE NVL(IS_DELETE, 0) = 0
                ORDER BY MADQ
                """;

        ObservableList<DoiQua> result = FXCollections.observableArrayList();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                result.add(new DoiQua(
                        resultSet.getString("MADQ"),
                        resultSet.getString("MAKH"),
                        resultSet.getString("MAQT"),
                        valueOrEmpty(resultSet.getString("NGAYDOI")),
                        String.valueOf(resultSet.getLong("SL")),
                        valueOrEmpty(resultSet.getString("TRANGTHAI"))
                ));
            }
        }
        return result;
    }

    static void insert(DoiQua dq) throws SQLException {
        ensureRequiredDataExist(dq.getMaQT(), dq.getMaKH());

        String sql = """
                INSERT INTO LICHSUDOIQUA (MADQ, MAKH, MAQT, SL, TRANGTHAI, IS_DELETE)
                VALUES (?, ?, ?, ?, ?, 0)
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, dq.getMaDQ());
            statement.setString(2, dq.getMaKH());
            statement.setString(3, dq.getMaQT());
            statement.setLong(4, Long.parseLong(dq.getSoLuong().trim()));
            statement.setString(5, dq.getTrangThai());
            statement.executeUpdate();
        }
    }

    private static void ensureRequiredDataExist(String maqt, String makh) throws SQLException {
        // Đảm bảo quà tặng tồn tại để tránh vi phạm FK_LSDQ_QT
        String sqlQt = "MERGE INTO QUA_TANG USING DUAL ON (MAQT = ?) WHEN NOT MATCHED THEN INSERT (MAQT, SODIEMTIEUHAO, NOIDUNG) VALUES (?, 100, 'Qua tang mac dinh')";
        // Đảm bảo khách hàng tồn tại để tránh vi phạm FK_LSDQ_KH
        String sqlKh = "MERGE INTO KHACHHANG USING DUAL ON (MAKH = ?) WHEN NOT MATCHED THEN INSERT (MAKH, SODIEMTICHLUY, IS_DELETE) VALUES (?, 1000, 0)";

        try (Connection connection = DatabaseConnection.getConnection()) {
            try (PreparedStatement stmtQt = connection.prepareStatement(sqlQt)) {
                stmtQt.setString(1, maqt);
                stmtQt.setString(2, maqt);
                stmtQt.executeUpdate();
            }
            try (PreparedStatement stmtKh = connection.prepareStatement(sqlKh)) {
                stmtKh.setString(1, makh);
                stmtKh.setString(2, makh);
                stmtKh.executeUpdate();
            }
        }
    }

    private static String valueOrEmpty(String value) {
        return value == null ? "" : value;
    }
}
