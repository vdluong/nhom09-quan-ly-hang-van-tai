package dao;

import config.DBConnection;
import model.TuyenXe;

import javax.swing.JOptionPane;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TuyenXeDAO {

    public List<TuyenXe> getAll() {
        List<TuyenXe> list = new ArrayList<>();
        String sql = "SELECT ma_tuyen, diem_dau, diem_cuoi, do_dai_km, do_phuc_tap FROM TUYEN_XE ORDER BY ma_tuyen";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new TuyenXe(
                        rs.getString("ma_tuyen"),
                        rs.getString("diem_dau"),
                        rs.getString("diem_cuoi"),
                        rs.getBigDecimal("do_dai_km"),
                        rs.getInt("do_phuc_tap")
                ));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Lỗi tải danh sách tuyến xe: " + e.getMessage(),
                    "Lỗi CSDL", JOptionPane.ERROR_MESSAGE);
        }
        return list;
    }

    private boolean kiemTraHopLeTuyenXe(TuyenXe t) {
        if (t.getDoDaiKm() == null || t.getDoDaiKm().compareTo(BigDecimal.ZERO) <= 0) {
            JOptionPane.showMessageDialog(null, "Độ dài tuyến đường (km) phải lớn hơn 0!",
                    "Lỗi Ràng Buộc", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        // Ràng buộc 3 (RB03): Độ phức tạp chỉ nhận 1, 2, 3
        if (t.getDoPhucTap() < 1 || t.getDoPhucTap() > 3) {
            JOptionPane.showMessageDialog(null, "Vi phạm RB03: Độ phức tạp tuyến đường chỉ nhận 3 cấp (1, 2, 3)!",
                    "Lỗi Ràng Buộc RB03", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (t.getDiemDau() != null && t.getDiemDau().trim().equalsIgnoreCase(t.getDiemCuoi().trim())) {
            JOptionPane.showMessageDialog(null, "Điểm đầu và điểm cuối của tuyến xe không được trùng nhau!",
                    "Lỗi Ràng Buộc", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    public boolean insert(TuyenXe t) {
        String loiRB05 = DBConnection.kiemTraGioiHanBanGhi("TUYEN_XE");
        if (loiRB05 != null) {
            JOptionPane.showMessageDialog(null, loiRB05, "Lỗi Ràng Buộc RB05", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (!kiemTraHopLeTuyenXe(t)) {
            return false;
        }

        String sql = "INSERT INTO TUYEN_XE (ma_tuyen, diem_dau, diem_cuoi, do_dai_km, do_phuc_tap) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, t.getMaTuyen());
            ps.setString(2, t.getDiemDau());
            ps.setString(3, t.getDiemCuoi());
            ps.setBigDecimal(4, t.getDoDaiKm());
            ps.setInt(5, t.getDoPhucTap());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            xuLyNgoaiLeTuyenXe(e);
            return false;
        }
    }

    public boolean update(TuyenXe t) {
        if (!kiemTraHopLeTuyenXe(t)) {
            return false;
        }

        String sql = "UPDATE TUYEN_XE SET diem_dau = ?, diem_cuoi = ?, do_dai_km = ?, do_phuc_tap = ? WHERE ma_tuyen = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, t.getDiemDau());
            ps.setString(2, t.getDiemCuoi());
            ps.setBigDecimal(3, t.getDoDaiKm());
            ps.setInt(4, t.getDoPhucTap());
            ps.setString(5, t.getMaTuyen());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            xuLyNgoaiLeTuyenXe(e);
            return false;
        }
    }

    public boolean delete(String maTuyen) {
        String sql = "DELETE FROM TUYEN_XE WHERE ma_tuyen = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maTuyen);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            if (e.getErrorCode() == 1451) {
                JOptionPane.showMessageDialog(null,
                        "Không thể xóa tuyến xe " + maTuyen + " vì đã có chuyến xe chạy trên tuyến này!",
                        "Lỗi Khóa Ngoại (1451)", JOptionPane.ERROR_MESSAGE);
            } else {
                xuLyNgoaiLeTuyenXe(e);
            }
            return false;
        }
    }

    private void xuLyNgoaiLeTuyenXe(SQLException e) {
        if ("45000".equals(e.getSQLState())) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Lỗi Ràng Buộc Trigger", JOptionPane.ERROR_MESSAGE);
        } else if (e.getErrorCode() == 1062) {
            JOptionPane.showMessageDialog(null, "Mã tuyến xe đã tồn tại!",
                    "Lỗi Trùng Dữ Liệu (1062)", JOptionPane.ERROR_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, "Lỗi CSDL: " + e.getMessage(),
                    "Lỗi SQL", JOptionPane.ERROR_MESSAGE);
        }
    }
}

