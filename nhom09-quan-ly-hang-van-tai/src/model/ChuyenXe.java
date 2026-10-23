package model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class ChuyenXe {
    private String maChuyen;
    private String bienSo;
    private String maTuyen;
    private String maLaiXe;
    private String maPhuXe;
    private Timestamp ngayKhoiHanh;
    private int soKhach;
    private BigDecimal giaVe;
    private String trangThai; // 'Đã lên lịch', 'Đang chạy', 'Hoàn thành'

    public ChuyenXe() {
    }

    public ChuyenXe(String maChuyen, String bienSo, String maTuyen, String maLaiXe,
                    String maPhuXe, Timestamp ngayKhoiHanh, int soKhach,
                    BigDecimal giaVe, String trangThai) {
        this.maChuyen = maChuyen;
        this.bienSo = bienSo;
        this.maTuyen = maTuyen;
        this.maLaiXe = maLaiXe;
        this.maPhuXe = maPhuXe;
        this.ngayKhoiHanh = ngayKhoiHanh;
        this.soKhach = soKhach;
        this.giaVe = giaVe;
        this.trangThai = trangThai;
    }

    public String getMaChuyen() { return maChuyen; }
    public void setMaChuyen(String maChuyen) { this.maChuyen = maChuyen; }

    public String getBienSo() { return bienSo; }
    public void setBienSo(String bienSo) { this.bienSo = bienSo; }

    public String getMaTuyen() { return maTuyen; }
    public void setMaTuyen(String maTuyen) { this.maTuyen = maTuyen; }

    public String getMaLaiXe() { return maLaiXe; }
    public void setMaLaiXe(String maLaiXe) { this.maLaiXe = maLaiXe; }

    public String getMaPhuXe() { return maPhuXe; }
    public void setMaPhuXe(String maPhuXe) { this.maPhuXe = maPhuXe; }

    public Timestamp getNgayKhoiHanh() { return ngayKhoiHanh; }
    public void setNgayKhoiHanh(Timestamp ngayKhoiHanh) { this.ngayKhoiHanh = ngayKhoiHanh; }

    public int getSoKhach() { return soKhach; }
    public void setSoKhach(int soKhach) { this.soKhach = soKhach; }

    public BigDecimal getGiaVe() { return giaVe; }
    public void setGiaVe(BigDecimal giaVe) { this.giaVe = giaVe; }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
}
