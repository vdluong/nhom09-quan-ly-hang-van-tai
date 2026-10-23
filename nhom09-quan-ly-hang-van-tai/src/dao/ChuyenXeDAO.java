package dao;

import config.DBConnection;
import model.ChuyenXe;
import model.XeKhach;

import javax.swing.JOptionPane;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ChuyenXeDAO {
    private final XeKhachDAO xeKhachDAO = new XeKhachDAO();
    private final ThongKeDAO thongKeDAO = new ThongKeDAO();

    public List<ChuyenXe> getAll() {
        List<ChuyenXe> list = new ArrayList<>();
        String sql = "SELECT ma_chuyen, bien_so, ma_tuyen, ma_lai_xe, ma_phu_xe, ngay_khoi_hanh, so_khach, gia_ve, trang_thai "
                + "FROM CHUYEN_XE ORDER BY ngay_khoi_hanh DESC, ma_chuyen DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new ChuyenXe(
                        rs.getString("ma_chuyen"),
                        rs.getString("bien_so"),
                        rs.getString("ma_tuyen"),
                        rs.getString("ma_lai_xe"),
                        rs.getString("ma_phu_xe"),
                        rs.getTimestamp("ngay_khoi_hanh"),
                        rs.getInt("so_khach"),
                        rs.getBigDecimal("gia_ve"),
                        rs.getString("trang_thai")
                ));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Lỗi tải danh sách chuyến xe: " + e.getMessage(),
                    "Lỗi CSDL", JOptionPane.ERROR_MESSAGE);
        }
        return list;
    }

    /**
     * Kiểm tra các ràng buộc RB01, RB02, RB04 trên tầng Java trước khi thêm/sửa chuyến xe.
     */
    private boolean kiemTraRangBuocChuyenXe(ChuyenXe cx, boolean isInsert) {
        // 1. Kiểm tra RB02: Lái xe chính và phụ xe không được trùng nhau
        if (cx.getMaLaiXe() != null && cx.getMaLaiXe().equals(cx.getMaPhuXe())) {
            JOptionPane.showMessageDialog(null,
                    "Vi phạm RB02: Một tài xế không thể vừa làm Lái xe chính vừa làm Phụ xe trên cùng một chuyến!",
                    "Lỗi Ràng Buộc", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // 2. Kiểm tra RB01: soKhach <= soGhe - 2
        XeKhach xk = xeKhachDAO.getByBienSo(cx.getBienSo());
        if (xk == null) {
            JOptionPane.showMessageDialog(null,
                    "Không tìm thấy thông tin xe khách có biển số: " + cx.getBienSo(),
                    "Lỗi Ràng Buộc", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        int soGhe = xk.getSoGhe();
        if (cx.getSoKhach() > (soGhe - 2)) {
            JOptionPane.showMessageDialog(null,
                    "Vi phạm RB01: Xe " + cx.getBienSo() + " có " + soGhe
                            + " ghế (trừ 1 lái xe, 1 phụ xe), chỉ được chở tối đa "
                            + (soGhe - 2) + " hành khách!",
                    "Lỗi Ràng Buộc", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // 3. Kiểm tra RB04 (Khi thêm chuyến mới): Cảnh báo / Chặn nếu xe đã quá hạn bảo dưỡng
        if (isInsert && thongKeDAO.isXeQuaHanBaoDuong(cx.getBienSo())) {
            int confirm = JOptionPane.showConfirmDialog(null,
                    "Vi phạm RB04: Xe " + cx.getBienSo() + " đã quá hạn bảo dưỡng định kỳ!\n"
                            + "Bạn có muốn xác nhận đã bảo dưỡng xe hôm nay trước khi xếp lịch chạy không?",
                    "Cảnh Báo Quá Hạn Bảo Dưỡng (RB04)",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                boolean daCapNhat = xeKhachDAO.xacNhanBaoDuong(cx.getBienSo());
                if (!daCapNhat) {
                    return false;
                }
                JOptionPane.showMessageDialog(null,
                        "Đã cập nhật ngày bảo dưỡng mới cho xe " + cx.getBienSo() + ". Tiếp tục lưu chuyến xe!",
                        "Xác Nhận Bảo Dưỡng", JOptionPane.INFORMATION_MESSAGE);
            } else {
                return false; // Chặn không cho thêm chuyến nếu chưa bảo dưỡng
            }
        }

        return true;
    }

    public boolean insert(ChuyenXe cx) {
        // Kiểm tra RB05: Giới hạn số lượng bản ghi bảng CHUYEN_XE
        String loiRB05 = DBConnection.kiemTraGioiHanBanGhi("CHUYEN_XE");
        if (loiRB05 != null) {
            JOptionPane.showMessageDialog(null, loiRB05, "Lỗi Ràng Buộc RB05", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Kiểm tra RB01, RB02, RB04
        if (!kiemTraRangBuocChuyenXe(cx, true)) {
            return false;
        }

        String sql = "INSERT INTO CHUYEN_XE (ma_chuyen, bien_so, ma_tuyen, ma_lai_xe, ma_phu_xe, "
                + "ngay_khoi_hanh, so_khach, gia_ve, trang_thai) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, cx.getMaChuyen());
            ps.setString(2, cx.getBienSo());
            ps.setString(3, cx.getMaTuyen());
            ps.setString(4, cx.getMaLaiXe());
            ps.setString(5, cx.getMaPhuXe());
            ps.setTimestamp(6, cx.getNgayKhoiHanh());
            ps.setInt(7, cx.getSoKhach());
            ps.setBigDecimal(8, cx.getGiaVe());
            ps.setString(9, cx.getTrangThai() != null ? cx.getTrangThai() : "Đã lên lịch");
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            xuLyNgoaiLeChuyenXe(e);
            return false;
        }
    }

    public boolean update(ChuyenXe cx) {
        // Kiểm tra RB01, RB02
        if (!kiemTraRangBuocChuyenXe(cx, false)) {
            return false;
        }

        String sql = "UPDATE CHUYEN_XE SET bien_so = ?, ma_tuyen = ?, ma_lai_xe = ?, ma_phu_xe = ?, "
                + "ngay_khoi_hanh = ?, so_khach = ?, gia_ve = ?, trang_thai = ? WHERE ma_chuyen = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, cx.getBienSo());
            ps.setString(2, cx.getMaTuyen());
            ps.setString(3, cx.getMaLaiXe());
            ps.setString(4, cx.getMaPhuXe());
            ps.setTimestamp(5, cx.getNgayKhoiHanh());
            ps.setInt(6, cx.getSoKhach());
            ps.setBigDecimal(7, cx.getGiaVe());
            ps.setString(8, cx.getTrangThai());
            ps.setString(9, cx.getMaChuyen());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            xuLyNgoaiLeChuyenXe(e);
            return false;
        }
    }

    public boolean delete(String maChuyen) {
        String sql = "DELETE FROM CHUYEN_XE WHERE ma_chuyen = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maChuyen);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            xuLyNgoaiLeChuyenXe(e);
            return false;
        }
    }

    private void xuLyNgoaiLeChuyenXe(SQLException e) {
        if ("45000".equals(e.getSQLState())) {
            JOptionPane.showMessageDialog(null, e.getMessage(),
                    "Lỗi Ràng Buộc Trigger (SQLState 45000)", JOptionPane.ERROR_MESSAGE);
        } else if (e.getErrorCode() == 1062) {
            JOptionPane.showMessageDialog(null, "Mã chuyến xe đã tồn tại trong hệ thống!",
                    "Lỗi Trùng Khóa Chính (1062)", JOptionPane.ERROR_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, "Lỗi CSDL: " + e.getMessage(),
                    "Lỗi SQL", JOptionPane.ERROR_MESSAGE);
        }
    }
}
