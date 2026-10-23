package model;

import java.sql.Date;

public class XeKhach {
    private String bienSo;
    private String mauXe;
    private String hangSanXuat;
    private int doiXe;
    private String model;
    private int soGhe;
    private int soNamSuDung;
    private Date ngayBaoDuongCuoi;

    public XeKhach() {
    }

    public XeKhach(String bienSo, String mauXe, String hangSanXuat, int doiXe,
                   String model, int soGhe, int soNamSuDung, Date ngayBaoDuongCuoi) {
        this.bienSo = bienSo;
        this.mauXe = mauXe;
        this.hangSanXuat = hangSanXuat;
        this.doiXe = doiXe;
        this.model = model;
        this.soGhe = soGhe;
        this.soNamSuDung = soNamSuDung;
        this.ngayBaoDuongCuoi = ngayBaoDuongCuoi;
    }

    /**
     * Phục vụ RB01: Số chỗ tối đa cho hành khách = soGhe - 2 (trừ 1 lái xe chính và 1 phụ xe)
     */
    public int getSoChoToiDa() {
        return Math.max(0, this.soGhe - 2);
    }

    public String getBienSo() { return bienSo; }
    public void setBienSo(String bienSo) { this.bienSo = bienSo; }

    public String getMauXe() { return mauXe; }
    public void setMauXe(String mauXe) { this.mauXe = mauXe; }

    public String getHangSanXuat() { return hangSanXuat; }
    public void setHangSanXuat(String hangSanXuat) { this.hangSanXuat = hangSanXuat; }

    public int getDoiXe() { return doiXe; }
    public void setDoiXe(int doiXe) { this.doiXe = doiXe; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public int getSoGhe() { return soGhe; }
    public void setSoGhe(int soGhe) { this.soGhe = soGhe; }

    public int getSoNamSuDung() { return soNamSuDung; }
    public void setSoNamSuDung(int soNamSuDung) { this.soNamSuDung = soNamSuDung; }

    public Date getNgayBaoDuongCuoi() { return ngayBaoDuongCuoi; }
    public void setNgayBaoDuongCuoi(Date ngayBaoDuongCuoi) { this.ngayBaoDuongCuoi = ngayBaoDuongCuoi; }

    @Override
    public String toString() {
        return bienSo + " (" + hangSanXuat + " " + model + " - " + soGhe + " ghế)";
    }
}
