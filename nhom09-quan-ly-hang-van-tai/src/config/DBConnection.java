package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/** Quản lý kết nối MySQL. Hãy cập nhật thông tin kết nối trước khi chạy. */
public final class DBConnection {
    private static final String URL =
            "jdbc:mysql://localhost:3306/quan_ly_hang_van_tai?useSSL=false&serverTimezone=Asia/Ho_Chi_Minh";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    private DBConnection() {}

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
