package com.example.cybergame_management;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;

final class NhanVienRepository {
    private NhanVienRepository() {
    }

    static ObservableList<NhanVien> findAll() throws SQLException {
        String sql = """
                SELECT nv.MANV,
                       u.HOTEN,
                       lnv.VITRI AS CHUCVU,
                       u.SDT,
                       nv.LUONG_CB,
                       nv.TRANGTHAI,
                       TO_CHAR(nv.NGAYVAOLAM, 'YYYY-MM-DD') AS NGAYVAOLAM,
                       nv.MASOTHUECN,
                       nv.SOBHYT,
                       TO_CHAR(nv.NGAYTHOIVIEC, 'YYYY-MM-DD') AS NGAYTHOIVIEC
                FROM NHAN_VIEN nv
                LEFT JOIN APP_USER u ON u.USER_ID = nv.USER_ID
                LEFT JOIN LOAI_NHAN_VIEN lnv ON lnv.MALNV = nv.MALNV
                WHERE NVL(nv.IS_DELETE, 0) = 0
                ORDER BY nv.MANV
                """;

        ObservableList<NhanVien> result = FXCollections.observableArrayList();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                long luongCB = resultSet.getLong("LUONG_CB");
                String luongStr = DisplayFormat.money(luongCB) + "đ";

                result.add(new NhanVien(
                        resultSet.getString("MANV"),
                        resultSet.getString("HOTEN"),
                        valueOrEmpty(resultSet.getString("CHUCVU")),
                        valueOrEmpty(resultSet.getString("SDT")),
                        luongStr,
                        "Ca Sáng (6h-14h)",
                        valueOrEmpty(resultSet.getString("TRANGTHAI")),
                        valueOrEmpty(resultSet.getString("NGAYVAOLAM")),
                        valueOrEmpty(resultSet.getString("MASOTHUECN")),
                        valueOrEmpty(resultSet.getString("SOBHYT")),
                        valueOrEmpty(resultSet.getString("NGAYTHOIVIEC"))
                ));
            }
        }
        return result;
    }

    static void insert(NhanVien nv) throws SQLException {
        String sqlUser = """
                INSERT INTO APP_USER (USER_ID, HOTEN, QUYENHAN, IS_DELETE)
                VALUES (?, ?, 'STAFF', 0)
                """;

        String sqlNhanVien = """
                INSERT INTO NHAN_VIEN (MANV, USER_ID, MALNV, LUONG_CB, TRANGTHAI, NGAYVAOLAM, MASOTHUECN, SOBHYT, NGAYTHOIVIEC, IS_DELETE)
                VALUES (?, ?, 'LNV003', 7000000, ?, ?, ?, ?, ?, 0)
                """;

        String userId = "US_" + nv.getMaNV();

        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);
            try {
                try (PreparedStatement stmtUser = connection.prepareStatement(sqlUser)) {
                    stmtUser.setString(1, userId);
                    stmtUser.setString(2, nv.getHoTen());
                    stmtUser.executeUpdate();
                }

                try (PreparedStatement stmtNV = connection.prepareStatement(sqlNhanVien)) {
                    stmtNV.setString(1, nv.getMaNV());
                    stmtNV.setString(2, userId);
                    stmtNV.setString(3, nv.getTrangThai());
                    stmtNV.setDate(4, parseDateSafely(nv.getNgayVao()));
                    stmtNV.setString(5, nv.getMaSoThue());
                    stmtNV.setString(6, nv.getSoBHYT());
                    stmtNV.setDate(7, parseDateSafely(nv.getNgayThoiViec()));
                    stmtNV.executeUpdate();
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

    static void update(NhanVien nv) throws SQLException {
        String sqlUser = """
                UPDATE APP_USER
                SET HOTEN = ?
                WHERE USER_ID = (SELECT USER_ID FROM NHAN_VIEN WHERE MANV = ?)
                """;

        String sqlNhanVien = """
                UPDATE NHAN_VIEN
                SET MASOTHUECN = ?, SOBHYT = ?, NGAYVAOLAM = ?, NGAYTHOIVIEC = ?, TRANGTHAI = ?
                WHERE MANV = ?
                """;

        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);
            try {
                try (PreparedStatement stmtUser = connection.prepareStatement(sqlUser)) {
                    stmtUser.setString(1, nv.getHoTen());
                    stmtUser.setString(2, nv.getMaNV());
                    stmtUser.executeUpdate();
                }

                try (PreparedStatement stmtNV = connection.prepareStatement(sqlNhanVien)) {
                    stmtNV.setString(1, nv.getMaSoThue());
                    stmtNV.setString(2, nv.getSoBHYT());
                    stmtNV.setDate(3, parseDateSafely(nv.getNgayVao()));
                    stmtNV.setDate(4, parseDateSafely(nv.getNgayThoiViec()));
                    stmtNV.setString(5, nv.getTrangThai());
                    stmtNV.setString(6, nv.getMaNV());
                    stmtNV.executeUpdate();
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

    private static Date parseDateSafely(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty() || dateStr.equals("—")) {
            return null;
        }
        try {
            return Date.valueOf(dateStr.trim());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    static void softDelete(String maNV) throws SQLException {
        String sql = "UPDATE NHAN_VIEN SET IS_DELETE = 1 WHERE MANV = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, maNV);
            statement.executeUpdate();
        }
    }

    private static String valueOrEmpty(String value) {
        return value == null ? "" : value;
    }
}
