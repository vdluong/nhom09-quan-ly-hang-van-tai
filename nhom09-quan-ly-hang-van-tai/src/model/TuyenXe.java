package model;

public class TuyenXe {
    private int maTuyen;
    private String diemDi;
    private String diemDen;

    public TuyenXe() {}

    public TuyenXe(int maTuyen, String diemDi, String diemDen) {
        this.maTuyen = maTuyen;
        this.diemDi = diemDi;
        this.diemDen = diemDen;
    }

    public int getMaTuyen() { return maTuyen; }
    public void setMaTuyen(int maTuyen) { this.maTuyen = maTuyen; }
    public String getDiemDi() { return diemDi; }
    public void setDiemDi(String diemDi) { this.diemDi = diemDi; }
    public String getDiemDen() { return diemDen; }
    public void setDiemDen(String diemDen) { this.diemDen = diemDen; }
}
