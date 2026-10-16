package model;

import java.time.LocalDateTime;

public class ChuyenXe {
    private int maChuyen;
    private int maXe;
    private int maTaiXe;
    private LocalDateTime thoiGianKhoiHanh;

    public ChuyenXe() {}

    public int getMaChuyen() { return maChuyen; }
    public void setMaChuyen(int maChuyen) { this.maChuyen = maChuyen; }
    public int getMaXe() { return maXe; }
    public void setMaXe(int maXe) { this.maXe = maXe; }
    public int getMaTaiXe() { return maTaiXe; }
    public void setMaTaiXe(int maTaiXe) { this.maTaiXe = maTaiXe; }
    public LocalDateTime getThoiGianKhoiHanh() { return thoiGianKhoiHanh; }
    public void setThoiGianKhoiHanh(LocalDateTime thoiGianKhoiHanh) { this.thoiGianKhoiHanh = thoiGianKhoiHanh; }
}
