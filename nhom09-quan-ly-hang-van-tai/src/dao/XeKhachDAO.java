package dao;

import config.DBConnection;
import model.XeKhach;

import javax.swing.JOptionPane;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class XeKhachDAO {

    public List<XeKhach> getAll() {
        List<XeKhach> list = new ArrayList<>();
        String sql = "SELECT bien_so, mau_xe, hang_san_xuat, doi_xe, model, so_ghe, so_nam_su_dung, ngay_bao_duong_cuoi "
                + "FROM XE_KHACH ORDER BY bien_so";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new XeKhach(
                        rs.getString("bien_so"),
                        rs.getString("mau_xe"),
                        rs.getString("hang_san_xuat"),
                        rs.getInt("doi_xe"),
                        rs.getString("model"),
                        rs.getInt("so_ghe"),
                        rs.getInt("so_nam_su_dung"),
                        rs.getDate("ngay_bao_duong_cuoi")
                ));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Lỗi tải danh sách xe khách: " + e.getMessage(),
                    "Lỗi CSDL", JOptionPane.ERROR_MESSAGE);
        }
        return list;
    }

    public XeKhach getByBienSo(String bs) {
        String sql = "SELECT bien_so, mau_xe, hang_san_xuat, doi_xe, model, so_ghe, so_nam_su_dung, ngay_bao_duong_cuoi "
                + "FROM XE_KHACH WHERE bien_so = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, bs);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new XeKhach(
                            rs.getString("bien_so"),
                            rs.getString("mau_xe"),
                            rs.getString("hang_san_xuat"),
                            rs.getInt("doi_xe"),
                            rs.getString("model"),
                            rs.getInt("so_ghe"),
                            rs.getInt("so_nam_su_dung"),
                            rs.getDate("ngay_bao_duong_cuoi")
                    );
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Lỗi tra cứu xe theo biển số: " + e.getMessage(),
                    "Lỗi CSDL", JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }

    public boolean insert(XeKhach xk) {
        String loiRB05 = DBConnection.kiemTraGioiHanBanGhi("XE_KHACH");
        if (loiRB05 != null) {
            JOptionPane.showMessageDialog(null, loiRB05, "Lỗi Ràng Buộc RB05", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (xk.getSoGhe() < 4) {
            JOptionPane.showMessageDialog(null, "Số ghế của xe khách phải từ 4 ghế trở lên!",
                    "Lỗi Dữ Liệu", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        String sql = "INSERT INTO XE_KHACH (bien_so, mau_xe, hang_san_xuat, doi_xe, model, so_ghe, so_nam_su_dung, ngay_bao_duong_cuoi) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, xk.getBienSo());
            ps.setString(2, xk.getMauXe());
            ps.setString(3, xk.getHangSanXuat());
            ps.setInt(4, xk.getDoiXe());
            ps.setString(5, xk.getModel());
            ps.setInt(6, xk.getSoGhe());
            ps.setInt(7, xk.getSoNamSuDung());
            ps.setDate(8, xk.getNgayBaoDuongCuoi());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            xuLyNgoaiLeXeKhach(e);
            return false;
        }
    }

    public boolean update(XeKhach xk) {
        if (xk.getSoGhe() < 4) {
            JOptionPane.showMessageDialog(null, "Số ghế của xe khách phải từ 4 ghế trở lên!",
                    "Lỗi Dữ Liệu", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        String sql = "UPDATE XE_KHACH SET mau_xe = ?, hang_san_xuat = ?, doi_xe = ?, model = ?, "
                + "so_ghe = ?, so_nam_su_dung = ?, ngay_bao_duong_cuoi = ? WHERE bien_so = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, xk.getMauXe());
            ps.setString(2, xk.getHangSanXuat());
            ps.setInt(3, xk.getDoiXe());
            ps.setString(4, xk.getModel());
            ps.setInt(5, xk.getSoGhe());
            ps.setInt(6, xk.getSoNamSuDung());
            ps.setDate(7, xk.getNgayBaoDuongCuoi());
            ps.setString(8, xk.getBienSo());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            xuLyNgoaiLeXeKhach(e);
            return false;
        }
    }

    public boolean delete(String bienSo) {
        String sql = "DELETE FROM XE_KHACH WHERE bien_so = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, bienSo);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            if (e.getErrorCode() == 1451) {
                JOptionPane.showMessageDialog(null,
                        "Không thể xóa xe mang biển số " + bienSo + " vì xe này đã có lịch sử chuyến chạy trong CSDL!",
                        "Lỗi Ràng Buộc Khóa Ngoại (1451)", JOptionPane.ERROR_MESSAGE);
            } else {
                xuLyNgoaiLeXeKhach(e);
            }
            return false;
        }
    }

    /**
     * Phục vụ RB04: Cập nhật ngày bảo dưỡng cuối của xe thành ngày hiện tại (CURDATE()).
     */
    public boolean xacNhanBaoDuong(String bs) {
        String sql = "UPDATE XE_KHACH SET ngay_bao_duong_cuoi = CURDATE() WHERE bien_so = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, bs);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Lỗi khi xác nhận bảo dưỡng xe: " + e.getMessage(),
                    "Lỗi CSDL", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private void xuLyNgoaiLeXeKhach(SQLException e) {
        if ("45000".equals(e.getSQLState())) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Lỗi Ràng Buộc Trigger", JOptionPane.ERROR_MESSAGE);
        } else if (e.getErrorCode() == 1062) {
            JOptionPane.showMessageDialog(null, "Biển số xe đã tồn tại trong hệ thống!",
                    "Lỗi Trùng Dữ Liệu (1062)", JOptionPane.ERROR_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, "Lỗi CSDL: " + e.getMessage(),
                    "Lỗi SQL", JOptionPane.ERROR_MESSAGE);
        }
    }
}
