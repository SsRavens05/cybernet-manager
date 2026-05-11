package com.mycompany.test5;

import java.sql.Connection;
import java.sql.SQLException;
import com.microsoft.sqlserver.jdbc.SQLServerDataSource;

/**
 *
 * @author THAO
 */
public class ketnoicsdl {
    
    // Tạo một phương thức public static để có thể gọi từ bất kỳ đâu
    public static Connection getConnection() {
        var server = "LAPTOP-C1AAQSUA";
        var user = "sa";
        var password = "12345"; 
        var db = "quan ly phong net"; // Tên CSDL Quản lý phòng nét của bạn
        var port = 1433;

        SQLServerDataSource ds = new SQLServerDataSource();
        ds.setUser(user);
        ds.setPassword(password);
        ds.setDatabaseName(db);
        ds.setServerName(server);
        ds.setPortNumber(port);
        
        // Cấu hình bắt buộc cho JDBC Driver mới
        ds.setEncrypt("true");
        ds.setTrustServerCertificate(true);

        try {
            // Trả về đối tượng Connection nếu thành công
            return ds.getConnection();
        } catch (SQLException ex) {
            System.out.println("Lỗi kết nối CSDL: " + ex.getMessage());
            ex.printStackTrace();
            return null; // Trả về null nếu kết nối thất bại
        }
    }
}
