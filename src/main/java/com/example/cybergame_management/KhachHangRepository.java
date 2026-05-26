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
                       kh.SODU,
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
                        String.valueOf(resultSet.getLong("SODU")),
                        String.valueOf(resultSet.getLong("SODIEMTICHLUY")),
                        resultSet.getInt("IS_DELETE") == 0 ? "ACTIVE" : "INACTIVE",
                        valueOrEmpty(resultSet.getString("CREATE_AT"))
                ));
            }
        }
        return result;
    }

    static void insert(KhachHang kh) throws SQLException {
        String userId = "USER_" + kh.getMaKH();
        
        // 1. Đảm bảo APP_USER tồn tại
        String sqlUser = "MERGE INTO APP_USER USING DUAL ON (USER_ID = ?) WHEN NOT MATCHED THEN INSERT (USER_ID, HOTEN, QUYENHAN) VALUES (?, ?, 'CUSTOMER')";
        
        // 2. Thêm mới KHACHHANG
        String sqlKh = "INSERT INTO KHACHHANG (MAKH, USER_ID, SODU, SODIEMTICHLUY, IS_DELETE) VALUES (?, ?, ?, ?, 0)";
        
        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);
            try {
                try (PreparedStatement stmtUser = connection.prepareStatement(sqlUser)) {
                    stmtUser.setString(1, userId);
                    stmtUser.setString(2, userId);
                    stmtUser.setString(3, kh.getHoTen());
                    stmtUser.executeUpdate();
                }
                
                try (PreparedStatement stmtKh = connection.prepareStatement(sqlKh)) {
                    stmtKh.setString(1, kh.getMaKH());
                    stmtKh.setString(2, userId);
                    stmtKh.setLong(3, DisplayFormat.parseMoney(kh.getSoDu()));
                    stmtKh.setLong(4, DisplayFormat.parseMoney(kh.getSoDiemTichLuy()));
                    stmtKh.executeUpdate();
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

    static void update(KhachHang khachHang) throws SQLException {
        String sqlKh = """
                UPDATE KHACHHANG
                SET SODIEMTICHLUY = ?
                WHERE MAKH = ?
                """;

        String sqlUser = """
                UPDATE APP_USER
                SET HOTEN = ?
                WHERE USER_ID = (SELECT USER_ID FROM KHACHHANG WHERE MAKH = ?)
                """;

        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);
            try {
                try (PreparedStatement statement = connection.prepareStatement(sqlKh)) {
                    statement.setLong(1, DisplayFormat.parseMoney(khachHang.getSoDiemTichLuy()));
                    statement.setString(2, khachHang.getMaKH());
                    statement.executeUpdate();
                }

                try (PreparedStatement statement = connection.prepareStatement(sqlUser)) {
                    statement.setString(1, khachHang.getHoTen());
                    statement.setString(2, khachHang.getMaKH());
                    statement.executeUpdate();
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

    static void deposit(String maKH, long soTien, long diemCong) throws SQLException {
        String sqlUpdate = """
                UPDATE KHACHHANG
                SET SODU = NVL(SODU, 0) + ?,
                    SODIEMTICHLUY = NVL(SODIEMTICHLUY, 0) + ?
                WHERE MAKH = ?
                """;

        String sqlInsert = """
                INSERT INTO PHIEUNAPTIEN (MAPN, MAKH, SOTIEN, DIEMCONG, NGAYNAP)
                VALUES (?, ?, ?, ?, SYSDATE)
                """;

        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);
            try {
                try (PreparedStatement statement = connection.prepareStatement(sqlUpdate)) {
                    statement.setLong(1, soTien);
                    statement.setLong(2, diemCong);
                    statement.setString(3, maKH);
                    statement.executeUpdate();
                }

                String mapn = "NT" + System.currentTimeMillis() % 1000000;
                try (PreparedStatement statement = connection.prepareStatement(sqlInsert)) {
                    statement.setString(1, mapn);
                    statement.setString(2, maKH);
                    statement.setLong(3, soTien);
                    statement.setLong(4, diemCong);
                    statement.executeUpdate();
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

    static void softDelete(String maKH) throws SQLException {
        String sql = "UPDATE KHACHHANG SET IS_DELETE = 1 WHERE MAKH = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, maKH);
            statement.executeUpdate();
        }
    }

    static String getNextMaKH() {
        if (!isDatabaseEnabled()) {
            return "KH" + String.format("%03d", (int)(Math.random() * 900) + 100);
        }
        String sql = "SELECT MAKH FROM KHACHHANG ORDER BY MAKH DESC FETCH FIRST 1 ROWS ONLY";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                String lastMa = resultSet.getString("MAKH");
                if (lastMa != null && lastMa.startsWith("KH")) {
                    try {
                        int num = Integer.parseInt(lastMa.substring(2));
                        return "KH" + String.format("%03d", num + 1);
                    } catch (NumberFormatException e) {
                        // ignore
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "KH001";
    }

    private static String valueOrEmpty(String value) {
        return value == null ? "" : value;
    }
}
