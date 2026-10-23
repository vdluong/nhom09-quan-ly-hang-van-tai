package model;

import java.sql.Date;

public class TaiXe {
    private String maTaiXe;
    private String soCmt;
    private String hoTen;
    private String maBangLai;
    private String loaiBangLai; // 'D', 'E', 'FC'
    private String diaChi;
    private Date ngaySinh;
    private int thamNien;

    public TaiXe() {
    }

    public TaiXe(String maTaiXe, String soCmt, String hoTen, String maBangLai,
                 String loaiBangLai, String diaChi, Date ngaySinh, int thamNien) {
        this.maTaiXe = maTaiXe;
        this.soCmt = soCmt;
        this.hoTen = hoTen;
        this.maBangLai = maBangLai;
        this.loaiBangLai = loaiBangLai;
        this.diaChi = diaChi;
        this.ngaySinh = ngaySinh;
        this.thamNien = thamNien;
    }

    public String getMaTaiXe() { return maTaiXe; }
    public void setMaTaiXe(String maTaiXe) { this.maTaiXe = maTaiXe; }

    public String getSoCmt() { return soCmt; }
    public void setSoCmt(String soCmt) { this.soCmt = soCmt; }

    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }

    public String getMaBangLai() { return maBangLai; }
    public void setMaBangLai(String maBangLai) { this.maBangLai = maBangLai; }

    public String getLoaiBangLai() { return loaiBangLai; }
    public void setLoaiBangLai(String loaiBangLai) { this.loaiBangLai = loaiBangLai; }

    public String getDiaChi() { return diaChi; }
    public void setDiaChi(String diaChi) { this.diaChi = diaChi; }

    public Date getNgaySinh() { return ngaySinh; }
    public void setNgaySinh(Date ngaySinh) { this.ngaySinh = ngaySinh; }

    public int getThamNien() { return thamNien; }
    public void setThamNien(int thamNien) { this.thamNien = thamNien; }

    @Override
    public String toString() {
        return maTaiXe + " - " + hoTen;
    }
}
