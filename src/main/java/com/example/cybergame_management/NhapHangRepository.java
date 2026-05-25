package com.example.cybergame_management;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;

final class NhapHangRepository {
    private NhapHangRepository() {
    }

    static ObservableList<NhapHang> findAll() throws SQLException {
        String sql = """
                SELECT pnk.MAPNK,
                       ctnk.LOAIHANG,
                       nvl(ncc.TENNCC, 'NCC001 - Suntory PepsiCo') AS TENNCC,
                       ctnk.SOLUONGNHAP,
                       ctnk.GIANHAP,
                       pnk.TONGTIENNHAP,
                       TO_CHAR(pnk.NGAYNHAP, 'YYYY-MM-DD') AS NGAYNHAP,
                       nvl(u.HOTEN, pnk.MANV) AS NGUOINHAP
                FROM PHIEU_NHAP_KHO pnk
                LEFT JOIN CHI_TIET_NHAP_KHO ctnk ON ctnk.MAPNK = pnk.MAPNK
                LEFT JOIN NHAN_VIEN nv ON nv.MANV = pnk.MANV
                LEFT JOIN APP_USER u ON u.USER_ID = nv.USER_ID
                LEFT JOIN NHA_CUNG_CAP ncc ON ncc.MANCC = 'NCC001'
                ORDER BY pnk.MAPNK
                """;

        ObservableList<NhapHang> result = FXCollections.observableArrayList();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                long sl = resultSet.getLong("SOLUONGNHAP");
                long gia = resultSet.getLong("GIANHAP");
                long tong = resultSet.getLong("TONGTIENNHAP");

                result.add(new NhapHang(
                        resultSet.getString("MAPNK"),
                        valueOrEmpty(resultSet.getString("LOAIHANG")),
                        valueOrEmpty(resultSet.getString("TENNCC")),
                        String.valueOf(sl),
                        DisplayFormat.money(gia) + "đ",
                        DisplayFormat.money(tong) + "đ",
                        valueOrEmpty(resultSet.getString("NGAYNHAP")),
                        valueOrEmpty(resultSet.getString("NGUOINHAP")),
                        "DA_NHAP"
                ));
            }
        }
        return result;
    }

    static void insert(NhapHang nh) throws SQLException {
        String sqlPNK = """
                INSERT INTO PHIEU_NHAP_KHO (MAPNK, MAKHO, MANV, NGAYNHAP, TONGTIENNHAP)
                VALUES (?, 'KHO001', ?, ?, ?)
                """;

        String sqlCTNK = """
                INSERT INTO CHI_TIET_NHAP_KHO (MAPNK, MASP, LOAIHANG, SOLUONGNHAP, GIANHAP)
                VALUES (?, ?, ?, ?, ?)
                """;

        long sl = Long.parseLong(nh.getSoLuong().trim());
        long gia = DisplayFormat.parseMoney(nh.getDonGia());
        long tong = sl * gia;

        String masp = getOrInsertProduct(nh.getLoaiHang());

        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);
            try {
                // Đảm bảo kho, nhà cung cấp và nhân viên nhập hàng tồn tại
                ensureRequiredDataExist(connection);
                ensureEmployeeExists(nh.getNguoiNhap(), connection);

                try (PreparedStatement stmtPNK = connection.prepareStatement(sqlPNK)) {
                    stmtPNK.setString(1, nh.getMaPN());
                    stmtPNK.setString(2, nh.getNguoiNhap() != null && !nh.getNguoiNhap().trim().isEmpty() ? nh.getNguoiNhap().trim() : "NV001");
                    stmtPNK.setDate(3, new Date(System.currentTimeMillis()));
                    stmtPNK.setLong(4, tong);
                    stmtPNK.executeUpdate();
                }

                try (PreparedStatement stmtCTNK = connection.prepareStatement(sqlCTNK)) {
                    stmtCTNK.setString(1, nh.getMaPN());
                    stmtCTNK.setString(2, masp);
                    stmtCTNK.setString(3, nh.getLoaiHang());
                    stmtCTNK.setLong(4, sl);
                    stmtCTNK.setLong(5, gia);
                    stmtCTNK.executeUpdate();
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

    private static void ensureRequiredDataExist(Connection connection) throws SQLException {
        // Đảm bảo KHO001 tồn tại
        String sqlKho = "MERGE INTO KHO USING DUAL ON (MAKHO = 'KHO001') WHEN NOT MATCHED THEN INSERT (MAKHO, TENKHO) VALUES ('KHO001', 'Kho Chinh')";
        // Đảm bảo NCC001 tồn tại
        String sqlNcc = "MERGE INTO NHA_CUNG_CAP USING DUAL ON (MANCC = 'NCC001') WHEN NOT MATCHED THEN INSERT (MANCC, TENNCC) VALUES ('NCC001', 'NCC001 - Suntory PepsiCo')";
        
        try (PreparedStatement stmtKho = connection.prepareStatement(sqlKho)) {
            stmtKho.executeUpdate();
        }
        try (PreparedStatement stmtNcc = connection.prepareStatement(sqlNcc)) {
            stmtNcc.executeUpdate();
        }
    }

    private static void ensureEmployeeExists(String manv, Connection connection) throws SQLException {
        String employeeId = (manv == null || manv.trim().isEmpty()) ? "NV001" : manv.trim();

        // 1. Kiểm tra xem MANV đã tồn tại trong NHAN_VIEN chưa
        String checkSql = "SELECT 1 FROM NHAN_VIEN WHERE MANV = ?";
        try (PreparedStatement stmt = connection.prepareStatement(checkSql)) {
            stmt.setString(1, employeeId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return; // Nhân viên đã tồn tại
                }
            }
        }

        // 2. Đảm bảo LOAI_NHAN_VIEN 'LNV001' tồn tại
        String sqlLnv = "MERGE INTO LOAI_NHAN_VIEN USING DUAL ON (MALNV = 'LNV001') WHEN NOT MATCHED THEN INSERT (MALNV, VITRI, MUCLUONG) VALUES ('LNV001', 'Nhan Vien', 5000000)";
        try (PreparedStatement stmtLnv = connection.prepareStatement(sqlLnv)) {
            stmtLnv.executeUpdate();
        }

        // 3. Đảm bảo APP_USER tương ứng tồn tại
        String userId = "USER_" + employeeId;
        String sqlUser = "MERGE INTO APP_USER USING DUAL ON (USER_ID = ?) WHEN NOT MATCHED THEN INSERT (USER_ID, HOTEN, QUYENHAN) VALUES (?, ?, 'STAFF')";
        try (PreparedStatement stmtUser = connection.prepareStatement(sqlUser)) {
            stmtUser.setString(1, userId);
            stmtUser.setString(2, userId);
            stmtUser.setString(3, "Nhan vien " + employeeId);
            stmtUser.executeUpdate();
        }

        // 4. Thêm nhân viên mới vào NHAN_VIEN
        String sqlNv = "INSERT INTO NHAN_VIEN (MANV, USER_ID, MALNV, LUONG_CB, TRANGTHAI, NGAYVAOLAM) VALUES (?, ?, 'LNV001', 5000000, 'Dang lam viec', SYSDATE)";
        try (PreparedStatement stmtNv = connection.prepareStatement(sqlNv)) {
            stmtNv.setString(1, employeeId);
            stmtNv.setString(2, userId);
            stmtNv.executeUpdate();
        }
    }

    private static String getOrInsertProduct(String loaiHang) throws SQLException {
        String query = "SELECT MASP FROM SAN_PHAM WHERE TENSP = ? OR LOAISP = ?";
        String insert = "INSERT INTO SAN_PHAM (MASP, TENSP, DVT, LOAISP, SOLUONGTK, DONGIABQ, SODIEMTICHLUY, IS_DELETE) VALUES (?, ?, 'Cai', 'Khac', 100, 10000, 10, 0)";
        
        try (Connection connection = DatabaseConnection.getConnection()) {
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, loaiHang);
                stmt.setString(2, loaiHang);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getString("MASP");
                    }
                }
            }

            // Nếu không tìm thấy, tạo mới sản phẩm SP_xxx để trigger hoạt động
            String masp = "SP_" + System.currentTimeMillis() % 100000;
            try (PreparedStatement stmtIns = connection.prepareStatement(insert)) {
                stmtIns.setString(1, masp);
                stmtIns.setString(2, loaiHang);
                stmtIns.executeUpdate();
            }
            return masp;
        }
    }

    static void delete(String mapnk) throws SQLException {
        String sqlCT = "DELETE FROM CHI_TIET_NHAP_KHO WHERE MAPNK = ?";
        String sqlPNK = "DELETE FROM PHIEU_NHAP_KHO WHERE MAPNK = ?";
        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);
            try {
                try (PreparedStatement stmtCT = connection.prepareStatement(sqlCT)) {
                    stmtCT.setString(1, mapnk);
                    stmtCT.executeUpdate();
                }
                try (PreparedStatement stmtPN = connection.prepareStatement(sqlPNK)) {
                    stmtPN.setString(1, mapnk);
                    stmtPN.executeUpdate();
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

    static String getNextMaPN() throws SQLException {
        String sql = "SELECT MAPNK FROM PHIEU_NHAP_KHO WHERE MAPNK LIKE 'PN%'";
        long maxNum = 0;
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                String mapnk = rs.getString("MAPNK");
                try {
                    long num = Long.parseLong(mapnk.substring(2).trim());
                    if (num > maxNum) {
                        maxNum = num;
                    }
                } catch (Exception ignored) {}
            }
        }
        return "PN" + String.format("%03d", maxNum + 1);
    }

    static void update(NhapHang nh) throws SQLException {
        String sqlPNK = """
                UPDATE PHIEU_NHAP_KHO 
                SET TONGTIENNHAP = ?, MANV = ?, NGAYNHAP = ?
                WHERE MAPNK = ?
                """;

        String sqlCTNK = """
                UPDATE CHI_TIET_NHAP_KHO 
                SET MASP = ?, LOAIHANG = ?, SOLUONGNHAP = ?, GIANHAP = ?
                WHERE MAPNK = ?
                """;

        long sl = Long.parseLong(nh.getSoLuong().trim());
        long gia = DisplayFormat.parseMoney(nh.getDonGia());
        long tong = sl * gia;

        String masp = getOrInsertProduct(nh.getLoaiHang());

        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);
            try {
                // Đảm bảo nhân viên nhập hàng tồn tại
                ensureEmployeeExists(nh.getNguoiNhap(), connection);

                try (PreparedStatement stmtPNK = connection.prepareStatement(sqlPNK)) {
                    stmtPNK.setLong(1, tong);
                    stmtPNK.setString(2, nh.getNguoiNhap() != null && !nh.getNguoiNhap().trim().isEmpty() ? nh.getNguoiNhap().trim() : "NV001");
                    
                    // Parse Date
                    Date ngayNhap;
                    try {
                        ngayNhap = Date.valueOf(nh.getNgayNhap().split(" ")[0].trim());
                    } catch (Exception e) {
                        ngayNhap = new Date(System.currentTimeMillis());
                    }
                    stmtPNK.setDate(3, ngayNhap);
                    stmtPNK.setString(4, nh.getMaPN());
                    stmtPNK.executeUpdate();
                }

                try (PreparedStatement stmtCTNK = connection.prepareStatement(sqlCTNK)) {
                    stmtCTNK.setString(1, masp);
                    stmtCTNK.setString(2, nh.getLoaiHang());
                    stmtCTNK.setLong(3, sl);
                    stmtCTNK.setLong(4, gia);
                    stmtCTNK.setString(5, nh.getMaPN());
                    stmtCTNK.executeUpdate();
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

    private static String valueOrEmpty(String value) {
        return value == null ? "" : value;
    }
}
