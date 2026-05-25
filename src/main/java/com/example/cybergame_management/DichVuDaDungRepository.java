package com.example.cybergame_management;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

final class DichVuDaDungRepository {
    private DichVuDaDungRepository() {
    }

    static ObservableList<DichVuDaDung> findAll() throws SQLException {
        String sql = """
                SELECT MADVDD, MASP, SL, TRANGTHAI, TO_CHAR(CREATE_AT, 'YYYY-MM-DD HH24:MI:SS') AS THOIGIAN
                FROM DICH_VU_DA_DUNG
                WHERE NVL(IS_DELETE, 0) = 0
                ORDER BY MADVDD
                """;

        ObservableList<DichVuDaDung> result = FXCollections.observableArrayList();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                result.add(new DichVuDaDung(
                        resultSet.getString("MADVDD"),
                        resultSet.getString("MASP"),
                        String.valueOf(resultSet.getLong("SL")),
                        valueOrEmpty(resultSet.getString("TRANGTHAI")),
                        valueOrEmpty(resultSet.getString("THOIGIAN"))
                ));
            }
        }
        return result;
    }

    static void insert(DichVuDaDung dv) throws SQLException {
        String mals = getOrCreatePlaySession();
        ensureProductExists(dv.getMaSP());

        String sql = """
                INSERT INTO DICH_VU_DA_DUNG (MADVDD, MALS, MASP, SL, TRANGTHAI, IS_DELETE)
                VALUES (?, ?, ?, ?, ?, 0)
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, dv.getMaDVDD());
            statement.setString(2, mals);
            statement.setString(3, dv.getMaSP());
            statement.setLong(4, Long.parseLong(dv.getSoLuong().trim()));
            statement.setString(5, dv.getTrangThai());
            statement.executeUpdate();
        }
    }

    private static String getOrCreatePlaySession() throws SQLException {
        String sqlCheck = "SELECT MALS FROM LICHSUCHOI WHERE NVL(IS_DELETE, 0) = 0 FETCH FIRST 1 ROWS ONLY";
        String sqlInsert = "INSERT INTO LICHSUCHOI (MALS, MAPC, MAKH, NGAYBD, TRANGTHAI, IS_DELETE) VALUES ('LSC001', 'PC001', 'KH001', CURRENT_TIMESTAMP, 'Đang chơi', 0)";
        
        // Đảm bảo PC001 và KH001 tồn tại để tránh vi phạm khóa ngoại của LICHSUCHOI
        String sqlPc = "MERGE INTO PC USING DUAL ON (MAPC = 'PC001') WHEN NOT MATCHED THEN INSERT (MAPC, SOMAY, TRANGTHAI, IS_DELETE) VALUES ('PC001', 1, 'HOATDONG', 0)";
        String sqlKh = "MERGE INTO KHACHHANG USING DUAL ON (MAKH = 'KH001') WHEN NOT MATCHED THEN INSERT (MAKH, SODIEMTICHLUY, IS_DELETE) VALUES ('KH001', 1000, 0)";

        try (Connection connection = DatabaseConnection.getConnection()) {
            try (PreparedStatement stmtPc = connection.prepareStatement(sqlPc)) {
                stmtPc.executeUpdate();
            }
            try (PreparedStatement stmtKh = connection.prepareStatement(sqlKh)) {
                stmtKh.executeUpdate();
            }

            try (PreparedStatement stmtCheck = connection.prepareStatement(sqlCheck);
                 ResultSet rs = stmtCheck.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("MALS");
                }
            }

            try (PreparedStatement stmtIns = connection.prepareStatement(sqlInsert)) {
                stmtIns.executeUpdate();
            }
            return "LSC001";
        }
    }

    private static void ensureProductExists(String masp) throws SQLException {
        String sqlCheck = "SELECT MASP FROM SAN_PHAM WHERE MASP = ?";
        String sqlInsert = "INSERT INTO SAN_PHAM (MASP, TENSP, DVT, LOAISP, SOLUONGTK, DONGIABQ, SODIEMTICHLUY, IS_DELETE) VALUES (?, ?, 'Cai', 'Do an', 100, 10000, 10, 0)";
        
        try (Connection connection = DatabaseConnection.getConnection()) {
            try (PreparedStatement stmtCheck = connection.prepareStatement(sqlCheck)) {
                stmtCheck.setString(1, masp);
                try (ResultSet rs = stmtCheck.executeQuery()) {
                    if (rs.next()) {
                        return; // Sản phẩm đã tồn tại
                    }
                }
            }

            try (PreparedStatement stmtIns = connection.prepareStatement(sqlInsert)) {
                stmtIns.setString(1, masp);
                stmtIns.setString(2, masp.equals("SP001") ? "Mi Hao Hao" : "Pepsi Lon");
                stmtIns.executeUpdate();
            }
        }
    }

    private static String valueOrEmpty(String value) {
        return value == null ? "" : value;
    }
}
