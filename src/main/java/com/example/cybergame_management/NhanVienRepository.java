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
        checkAndAddCalamColumn();
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
                       TO_CHAR(nv.NGAYTHOIVIEC, 'YYYY-MM-DD') AS NGAYTHOIVIEC,
                       nv.CALAM
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
                String cl = resultSet.getString("CALAM");
                if (cl == null || cl.trim().isEmpty()) {
                    cl = "Ca Sáng";
                }

                result.add(new NhanVien(
                        resultSet.getString("MANV"),
                        resultSet.getString("HOTEN"),
                        valueOrEmpty(resultSet.getString("CHUCVU")),
                        valueOrEmpty(resultSet.getString("SDT")),
                        luongStr,
                        cl,
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
        checkAndAddCalamColumn();
        String sqlUser = """
                INSERT INTO APP_USER (USER_ID, HOTEN, QUYENHAN, IS_DELETE)
                VALUES (?, ?, 'STAFF', 0)
                """;

        String sqlNhanVien = """
                INSERT INTO NHAN_VIEN (MANV, USER_ID, MALNV, LUONG_CB, TRANGTHAI, NGAYVAOLAM, MASOTHUECN, SOBHYT, NGAYTHOIVIEC, CALAM, IS_DELETE)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 0)
                """;

        String userId = "US_" + nv.getMaNV();

        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);
            try {
                // Lookup MALNV and MUCLUONG dynamically
                String maLNV = "LNV003";
                long luongCB = 7000000;
                String sqlLookup = "SELECT MALNV, MUCLUONG FROM LOAI_NHAN_VIEN WHERE UPPER(VITRI) = UPPER(?) AND NVL(IS_DELETE, 0) = 0";
                try (PreparedStatement stmtL = connection.prepareStatement(sqlLookup)) {
                    stmtL.setString(1, nv.getChucVu());
                    try (ResultSet rsL = stmtL.executeQuery()) {
                        if (rsL.next()) {
                            maLNV = rsL.getString("MALNV");
                            luongCB = rsL.getLong("MUCLUONG");
                        }
                    }
                }

                try (PreparedStatement stmtUser = connection.prepareStatement(sqlUser)) {
                    stmtUser.setString(1, userId);
                    stmtUser.setString(2, nv.getHoTen());
                    stmtUser.executeUpdate();
                }

                try (PreparedStatement stmtNV = connection.prepareStatement(sqlNhanVien)) {
                    stmtNV.setString(1, nv.getMaNV());
                    stmtNV.setString(2, userId);
                    stmtNV.setString(3, maLNV);
                    stmtNV.setLong(4, luongCB);
                    stmtNV.setString(5, nv.getTrangThai());
                    stmtNV.setDate(6, parseDateSafely(nv.getNgayVao()));
                    stmtNV.setString(7, nv.getMaSoThue());
                    stmtNV.setString(8, nv.getSoBHYT());
                    stmtNV.setDate(9, parseDateSafely(nv.getNgayThoiViec()));
                    stmtNV.setString(10, nv.getCaLam());
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
        checkAndAddCalamColumn();
        String sqlUser = """
                UPDATE APP_USER
                SET HOTEN = ?
                WHERE USER_ID = (SELECT USER_ID FROM NHAN_VIEN WHERE MANV = ?)
                """;

        String sqlNhanVien = """
                UPDATE NHAN_VIEN
                SET MASOTHUECN = ?, SOBHYT = ?, NGAYVAOLAM = ?, NGAYTHOIVIEC = ?, TRANGTHAI = ?, MALNV = ?, LUONG_CB = ?, CALAM = ?
                WHERE MANV = ?
                """;

        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);
            try {
                // Lookup MALNV and MUCLUONG dynamically
                String maLNV = "LNV003";
                long luongCB = 7000000;
                String sqlLookup = "SELECT MALNV, MUCLUONG FROM LOAI_NHAN_VIEN WHERE UPPER(VITRI) = UPPER(?) AND NVL(IS_DELETE, 0) = 0";
                try (PreparedStatement stmtL = connection.prepareStatement(sqlLookup)) {
                    stmtL.setString(1, nv.getChucVu());
                    try (ResultSet rsL = stmtL.executeQuery()) {
                        if (rsL.next()) {
                            maLNV = rsL.getString("MALNV");
                            luongCB = rsL.getLong("MUCLUONG");
                        }
                    }
                }

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
                    stmtNV.setString(6, maLNV);
                    stmtNV.setLong(7, luongCB);
                    stmtNV.setString(8, nv.getCaLam());
                    stmtNV.setString(9, nv.getMaNV());
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

    private static void checkAndAddCalamColumn() {
        if (!DatabaseConnection.isConfigured()) {
            return;
        }
        String checkSql = "SELECT COUNT(*) FROM user_tab_cols WHERE UPPER(table_name) = 'NHAN_VIEN' AND UPPER(column_name) = 'CALAM'";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(checkSql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next() && rs.getInt(1) == 0) {
                String alterSql = "ALTER TABLE NHAN_VIEN ADD CALAM VARCHAR2(100) DEFAULT 'Ca Sáng'";
                try (PreparedStatement alterStmt = connection.prepareStatement(alterSql)) {
                    alterStmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
