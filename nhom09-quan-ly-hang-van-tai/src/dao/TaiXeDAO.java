package dao;

import config.DBConnection;
import model.TaiXe;

import javax.swing.JOptionPane;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TaiXeDAO {

    public List<TaiXe> getAll() {
        List<TaiXe> list = new ArrayList<>();
        String sql = "SELECT ma_tai_xe, so_cmt, ho_ten, ma_bang_lai, loai_bang_lai, dia_chi, ngay_sinh, tham_nien "
                + "FROM TAI_XE ORDER BY ma_tai_xe";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new TaiXe(
                        rs.getString("ma_tai_xe"),
                        rs.getString("so_cmt"),
                        rs.getString("ho_ten"),
                        rs.getString("ma_bang_lai"),
                        rs.getString("loai_bang_lai"),
                        rs.getString("dia_chi"),
                        rs.getDate("ngay_sinh"),
                        rs.getInt("tham_nien")
                ));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Lỗi tải danh sách tài xế: " + e.getMessage(),
                    "Lỗi CSDL", JOptionPane.ERROR_MESSAGE);
        }
        return list;
    }

    public boolean insert(TaiXe tx) {
        String loiRB05 = DBConnection.kiemTraGioiHanBanGhi("TAI_XE");
        if (loiRB05 != null) {
            JOptionPane.showMessageDialog(null, loiRB05, "Lỗi Ràng Buộc RB05", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        String sql = "INSERT INTO TAI_XE (ma_tai_xe, so_cmt, ho_ten, ma_bang_lai, loai_bang_lai, dia_chi, ngay_sinh, tham_nien) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tx.getMaTaiXe());
            ps.setString(2, tx.getSoCmt());
            ps.setString(3, tx.getHoTen());
            ps.setString(4, tx.getMaBangLai());
            ps.setString(5, tx.getLoaiBangLai());
            ps.setString(6, tx.getDiaChi());
            ps.setDate(7, tx.getNgaySinh());
            ps.setInt(8, tx.getThamNien());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            xuLyNgoaiLeTaiXe(e);
            return false;
        }
    }

    public boolean update(TaiXe tx) {
        String sql = "UPDATE TAI_XE SET so_cmt = ?, ho_ten = ?, ma_bang_lai = ?, loai_bang_lai = ?, "
                + "dia_chi = ?, ngay_sinh = ?, tham_nien = ? WHERE ma_tai_xe = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tx.getSoCmt());
            ps.setString(2, tx.getHoTen());
            ps.setString(3, tx.getMaBangLai());
            ps.setString(4, tx.getLoaiBangLai());
            ps.setString(5, tx.getDiaChi());
            ps.setDate(6, tx.getNgaySinh());
            ps.setInt(7, tx.getThamNien());
            ps.setString(8, tx.getMaTaiXe());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            xuLyNgoaiLeTaiXe(e);
            return false;
        }
    }

    public boolean delete(String maTX) {
        String sql = "DELETE FROM TAI_XE WHERE ma_tai_xe = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maTX);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            if (e.getErrorCode() == 1451) {
                JOptionPane.showMessageDialog(null,
                        "Không thể xóa tài xế " + maTX + " vì tài xế này đang được phân công trong bảng CHUYEN_XE!",
                        "Lỗi Khóa Ngoại (1451)", JOptionPane.ERROR_MESSAGE);
            } else {
                xuLyNgoaiLeTaiXe(e);
            }
            return false;
        }
    }

    private void xuLyNgoaiLeTaiXe(SQLException e) {
        if (e.getErrorCode() == 1062) {
            String msg = e.getMessage() != null ? e.getMessage().toLowerCase() : "";
            if (msg.contains("so_cmt")) {
                JOptionPane.showMessageDialog(null, "Số CMT/CCCD đã bị trùng với tài xế khác!",
                        "Lỗi Trùng Dữ Liệu (1062)", JOptionPane.ERROR_MESSAGE);
            } else if (msg.contains("ma_bang_lai")) {
                JOptionPane.showMessageDialog(null, "Mã bằng lái đã bị trùng với tài xế khác!",
                        "Lỗi Trùng Dữ Liệu (1062)", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Mã tài xế, Số CMT hoặc Mã bằng lái đã tồn tại!",
                        "Lỗi Trùng Dữ Liệu (1062)", JOptionPane.ERROR_MESSAGE);
            }
        } else if ("45000".equals(e.getSQLState())) {
            JOptionPane.showMessageDialog(null, e.getMessage(),
                    "Lỗi Ràng Buộc Trigger", JOptionPane.ERROR_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, "Lỗi CSDL: " + e.getMessage(),
                    "Lỗi SQL", JOptionPane.ERROR_MESSAGE);
        }
    }
}
