package model;

import java.math.BigDecimal;

public class TuyenXe {
    private String maTuyen;
    private String diemDau;
    private String diemCuoi;
    private BigDecimal doDaiKm;
    private int doPhucTap; // 1, 2, 3

    public TuyenXe() {
    }

    public TuyenXe(String maTuyen, String diemDau, String diemCuoi, BigDecimal doDaiKm, int doPhucTap) {
        this.maTuyen = maTuyen;
        this.diemDau = diemDau;
        this.diemCuoi = diemCuoi;
        this.doDaiKm = doDaiKm;
        this.doPhucTap = doPhucTap;
    }

    public String getMaTuyen() { return maTuyen; }
    public void setMaTuyen(String maTuyen) { this.maTuyen = maTuyen; }

    public String getDiemDau() { return diemDau; }
    public void setDiemDau(String diemDau) { this.diemDau = diemDau; }

    public String getDiemCuoi() { return diemCuoi; }
    public void setDiemCuoi(String diemCuoi) { this.diemCuoi = diemCuoi; }

    public BigDecimal getDoDaiKm() { return doDaiKm; }
    public void setDoDaiKm(BigDecimal doDaiKm) { this.doDaiKm = doDaiKm; }

    public int getDoPhucTap() { return doPhucTap; }
    public void setDoPhucTap(int doPhucTap) { this.doPhucTap = doPhucTap; }

    @Override
    public String toString() {
        return maTuyen + " (" + diemDau + " - " + diemCuoi + ")";
    }
}
