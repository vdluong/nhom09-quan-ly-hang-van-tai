package dao;

import config.DBConnection;

import javax.swing.JOptionPane;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ThongKeDAO {

    /**
     * TRUY VẤN 1: Bảng lương tài xế trong tháng m, năm y (tính đến ngày hiện tại CURDATE()).
     * Đơn giá gốc: 300.000/chuyến; lái chính x2.0; phụ xe x1.0.
     * Hệ số độ phức tạp tuyến: cấp 1 x1.0; cấp 2 x1.5; cấp 3 x2.0.
     * Cột trả về trong Object[]: [ma_tai_xe, ho_ten, luong_thang_vnd]
     */
    public List<Object[]> getBangLuong(int m, int y) {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT "
                + "    tx.ma_tai_xe, "
                + "    tx.ho_ten, "
                + "    COALESCE(SUM( "
                + "        300000 * "
                + "        CASE vt.vai_tro WHEN 'LAI_CHINH' THEN 2.0 ELSE 1.0 END * "
                + "        CASE txe.do_phuc_tap WHEN 1 THEN 1.0 WHEN 2 THEN 1.5 WHEN 3 THEN 2.0 END "
                + "    ), 0) AS luong_thang_vnd "
                + "FROM TAI_XE tx "
                + "LEFT JOIN ( "
                + "    SELECT ma_chuyen, ma_lai_xe AS ma_tai_xe, 'LAI_CHINH' AS vai_tro "
                + "    FROM CHUYEN_XE "
                + "    WHERE MONTH(ngay_khoi_hanh) = ? AND YEAR(ngay_khoi_hanh) = ? "
                + "      AND DATE(ngay_khoi_hanh) <= CURDATE() "
                + "    UNION ALL "
                + "    SELECT ma_chuyen, ma_phu_xe AS ma_tai_xe, 'PHU_XE' AS vai_tro "
                + "    FROM CHUYEN_XE "
                + "    WHERE MONTH(ngay_khoi_hanh) = ? AND YEAR(ngay_khoi_hanh) = ? "
                + "      AND DATE(ngay_khoi_hanh) <= CURDATE() "
                + ") vt ON vt.ma_tai_xe = tx.ma_tai_xe "
                + "LEFT JOIN CHUYEN_XE cx ON cx.ma_chuyen = vt.ma_chuyen "
                + "LEFT JOIN TUYEN_XE txe ON txe.ma_tuyen = cx.ma_tuyen "
                + "GROUP BY tx.ma_tai_xe, tx.ho_ten "
                + "ORDER BY luong_thang_vnd DESC, tx.ma_tai_xe";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, m);
            ps.setInt(2, y);
            ps.setInt(3, m);
            ps.setInt(4, y);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Object[]{
                            rs.getString("ma_tai_xe"),
                            rs.getString("ho_ten"),
                            rs.getBigDecimal("luong_thang_vnd")
                    });
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Lỗi thống kê bảng lương tài xế: " + e.getMessage(),
                    "Lỗi CSDL", JOptionPane.ERROR_MESSAGE);
        }
        return list;
    }

    /**
     * TRUY VẤN 2: Doanh thu của mỗi xe trong khoảng ngày [d1, d2].
     * Cột trả về trong Object[]: [bien_so, hang_san_xuat, model, doanh_thu_vnd]
     */
    public List<Object[]> getDoanhThuXe(Date d1, Date d2) {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT "
                + "    xk.bien_so, "
                + "    xk.hang_san_xuat, "
                + "    xk.model, "
                + "    COALESCE(SUM(cx.so_khach * cx.gia_ve), 0) AS doanh_thu_vnd "
                + "FROM XE_KHACH xk "
                + "LEFT JOIN CHUYEN_XE cx "
                + "    ON cx.bien_so = xk.bien_so "
                + "   AND DATE(cx.ngay_khoi_hanh) BETWEEN ? AND ? "
                + "GROUP BY xk.bien_so, xk.hang_san_xuat, xk.model "
                + "ORDER BY doanh_thu_vnd DESC, xk.bien_so";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, d1);
            ps.setDate(2, d2);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Object[]{
                            rs.getString("bien_so"),
                            rs.getString("hang_san_xuat"),
                            rs.getString("model"),
                            rs.getBigDecimal("doanh_thu_vnd")
                    });
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Lỗi thống kê doanh thu xe: " + e.getMessage(),
                    "Lỗi CSDL", JOptionPane.ERROR_MESSAGE);
        }
        return list;
    }

    /**
     * TRUY VẤN 3A: Danh sách các xe trong hạn bảo dưỡng (hoặc lịch bảo dưỡng tiếp theo của xe còn hạn).
     * Cột trả về trong Object[]: [bien_so, hang_san_xuat, model, ngay_bao_duong_cuoi, so_ngay_bi_giam, ngay_bao_duong_tiep]
     */
    public List<Object[]> getXeTrongHanBD() {
        List<Object[]> list = new ArrayList<>();
        String sql = "WITH km_quy_doi AS ( "
                + "    SELECT "
                + "        cx.bien_so, "
                + "        SUM(tx.do_dai_km * "
                + "            CASE tx.do_phuc_tap "
                + "                WHEN 1 THEN 1.0 "
                + "                WHEN 2 THEN 1.2 "
                + "                WHEN 3 THEN 1.5 "
                + "            END) AS tong_km "
                + "    FROM CHUYEN_XE cx "
                + "    JOIN TUYEN_XE tx ON tx.ma_tuyen = cx.ma_tuyen "
                + "    JOIN XE_KHACH xk ON xk.bien_so = cx.bien_so "
                + "    WHERE DATE(cx.ngay_khoi_hanh) > xk.ngay_bao_duong_cuoi "
                + "    GROUP BY cx.bien_so "
                + "), "
                + "lich_bao_duong AS ( "
                + "    SELECT "
                + "        xk.bien_so, "
                + "        xk.hang_san_xuat, "
                + "        xk.model, "
                + "        xk.ngay_bao_duong_cuoi, "
                + "        COALESCE(FLOOR(k.tong_km / 100), 0) AS so_ngay_bi_giam, "
                + "        DATE_ADD( "
                + "            xk.ngay_bao_duong_cuoi, "
                + "            INTERVAL (360 - COALESCE(FLOOR(k.tong_km / 100), 0)) DAY "
                + "        ) AS ngay_bao_duong_tiep "
                + "    FROM XE_KHACH xk "
                + "    LEFT JOIN km_quy_doi k ON k.bien_so = xk.bien_so "
                + ") "
                + "SELECT * FROM lich_bao_duong "
                + "WHERE ngay_bao_duong_tiep >= CURDATE() "
                + "ORDER BY ngay_bao_duong_tiep, bien_so";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Object[]{
                        rs.getString("bien_so"),
                        rs.getString("hang_san_xuat"),
                        rs.getString("model"),
                        rs.getDate("ngay_bao_duong_cuoi"),
                        rs.getInt("so_ngay_bi_giam"),
                        rs.getDate("ngay_bao_duong_tiep")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Lỗi tải danh sách xe trong hạn bảo dưỡng: " + e.getMessage(),
                    "Lỗi CSDL", JOptionPane.ERROR_MESSAGE);
        }
        return list;
    }

    /**
     * TRUY VẤN 3B: Danh sách riêng các xe đã quá hạn bảo dưỡng (ngay_bao_duong_tiep < CURDATE()).
     * Cột trả về trong Object[]: [bien_so, hang_san_xuat, model, ngay_bao_duong_cuoi, ngay_bao_duong_tiep]
     */
    public List<Object[]> getXeQuaHanBD() {
        List<Object[]> list = new ArrayList<>();
        String sql = "WITH km_quy_doi AS ( "
                + "    SELECT "
                + "        cx.bien_so, "
                + "        SUM(tx.do_dai_km * "
                + "            CASE tx.do_phuc_tap "
                + "                WHEN 1 THEN 1.0 "
                + "                WHEN 2 THEN 1.2 "
                + "                WHEN 3 THEN 1.5 "
                + "            END) AS tong_km "
                + "    FROM CHUYEN_XE cx "
                + "    JOIN TUYEN_XE tx ON tx.ma_tuyen = cx.ma_tuyen "
                + "    JOIN XE_KHACH xk ON xk.bien_so = cx.bien_so "
                + "    WHERE DATE(cx.ngay_khoi_hanh) > xk.ngay_bao_duong_cuoi "
                + "    GROUP BY cx.bien_so "
                + "), "
                + "lich_bao_duong AS ( "
                + "    SELECT "
                + "        xk.bien_so, "
                + "        xk.hang_san_xuat, "
                + "        xk.model, "
                + "        xk.ngay_bao_duong_cuoi, "
                + "        DATE_ADD( "
                + "            xk.ngay_bao_duong_cuoi, "
                + "            INTERVAL (360 - COALESCE(FLOOR(k.tong_km / 100), 0)) DAY "
                + "        ) AS ngay_bao_duong_tiep "
                + "    FROM XE_KHACH xk "
                + "    LEFT JOIN km_quy_doi k ON k.bien_so = xk.bien_so "
                + ") "
                + "SELECT * FROM lich_bao_duong "
                + "WHERE ngay_bao_duong_tiep < CURDATE() "
                + "ORDER BY ngay_bao_duong_tiep, bien_so";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Object[]{
                        rs.getString("bien_so"),
                        rs.getString("hang_san_xuat"),
                        rs.getString("model"),
                        rs.getDate("ngay_bao_duong_cuoi"),
                        rs.getDate("ngay_bao_duong_tiep")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Lỗi tải danh sách xe quá hạn bảo dưỡng: " + e.getMessage(),
                    "Lỗi CSDL", JOptionPane.ERROR_MESSAGE);
        }
        return list;
    }

    /**
     * Hàm phụ trợ kiểm tra nhanh 1 biển số có nằm trong danh sách quá hạn bảo dưỡng không (Phục vụ RB04).
     */
    public boolean isXeQuaHanBaoDuong(String bienSo) {
        List<Object[]> dsQuaHan = getXeQuaHanBD();
        for (Object[] row : dsQuaHan) {
            if (row[0] != null && row[0].toString().equalsIgnoreCase(bienSo)) {
                return true;
            }
        }
        return false;
    }
}
