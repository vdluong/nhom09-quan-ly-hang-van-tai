package model;

public class TaiXe {
    private int maTaiXe;
    private String hoTen;
    private String soDienThoai;

    public TaiXe() {}

    public TaiXe(int maTaiXe, String hoTen, String soDienThoai) {
        this.maTaiXe = maTaiXe;
        this.hoTen = hoTen;
        this.soDienThoai = soDienThoai;
    }

    public int getMaTaiXe() { return maTaiXe; }
    public void setMaTaiXe(int maTaiXe) { this.maTaiXe = maTaiXe; }
    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }
    public String getSoDienThoai() { return soDienThoai; }
    public void setSoDienThoai(String soDienThoai) { this.soDienThoai = soDienThoai; }
}
