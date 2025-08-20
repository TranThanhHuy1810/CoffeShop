package da.conn;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    // Thông tin kết nối MySQL
    private static final String URL = "jdbc:mysql://localhost:3306/CF?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root"; // Thay bằng user của bạn
    private static final String PASSWORD = ""; // Thay bằng password của bạn

    public static Connection getConnection() {
        Connection conn = null;
        try {
            // Load MySQL JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Kết nối MySQL
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("✅ Kết nối MySQL thành công!");
        } catch (ClassNotFoundException e) {
            System.err.println("❌ Không tìm thấy Driver MySQL: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("❌ Lỗi kết nối MySQL: " + e.getMessage());
        }
        return conn;
    }

    public static void main(String[] args) {
        Connection conn = getConnection();
        if (conn != null) {
            try {
                conn.close();
                System.out.println("🔌 Đã đóng kết nối.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
