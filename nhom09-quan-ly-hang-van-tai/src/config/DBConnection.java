package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/** Quản lý kết nối MySQL. Hãy cập nhật thông tin kết nối trước khi chạy. */
public final class DBConnection {
    private static final String URL =
            "jdbc:mysql://localhost:3306/quan_ly_van_tai_nhom09";
    private static final String USER = "root";
    private static final String PASSWORD = "06022006";

    private DBConnection() {}

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    /**
     * RB05: kiểm tra số bản ghi hiện có của bảng đã đạt giới hạn tối đa chưa.
     * Trả về null nếu còn được thêm, trả về câu thông báo lỗi nếu đã đầy.
     */
    public static String kiemTraGioiHanBanGhi(String tenBang) {
        String sqlGioiHan = "SELECT so_luong_toi_da FROM CAU_HINH_GIOI_HAN WHERE ten_bang = ?";
        String sqlDem = "SELECT COUNT(*) FROM " + tenBang;

        try (Connection conn = getConnection()) {
            int toiDa;
            try (java.sql.PreparedStatement ps = conn.prepareStatement(sqlGioiHan)) {
                ps.setString(1, tenBang);
                try (java.sql.ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        return null; // bảng không có cấu hình giới hạn
                    }
                    toiDa = rs.getInt(1);
                }
            }

            int hienCo;
            try (java.sql.PreparedStatement ps = conn.prepareStatement(sqlDem);
                 java.sql.ResultSet rs = ps.executeQuery()) {
                rs.next();
                hienCo = rs.getInt(1);
            }

            if (hienCo >= toiDa) {
                return "Vi phạm RB05: Bảng " + tenBang + " đã đạt giới hạn tối đa "
                        + toiDa + " bản ghi (hiện có " + hienCo + "). Không thể thêm mới!";
            }
            return null;

        } catch (SQLException e) {
            return "Không thể kiểm tra giới hạn bản ghi: " + e.getMessage();
        }
    }

    /** Trả về chuỗi dạng "12/100" để hiển thị trên giao diện. */
    public static String layThongTinGioiHan(String tenBang) {
        String sqlGioiHan = "SELECT so_luong_toi_da FROM CAU_HINH_GIOI_HAN WHERE ten_bang = ?";
        String sqlDem = "SELECT COUNT(*) FROM " + tenBang;
        try (Connection conn = getConnection()) {
            int toiDa = 0;
            try (java.sql.PreparedStatement ps = conn.prepareStatement(sqlGioiHan)) {
                ps.setString(1, tenBang);
                try (java.sql.ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) toiDa = rs.getInt(1);
                }
            }
            int hienCo = 0;
            try (java.sql.PreparedStatement ps = conn.prepareStatement(sqlDem);
                 java.sql.ResultSet rs = ps.executeQuery()) {
                if (rs.next()) hienCo = rs.getInt(1);
            }
            return hienCo + "/" + toiDa;
        } catch (SQLException e) {
            return "?";
        }
    }
}
