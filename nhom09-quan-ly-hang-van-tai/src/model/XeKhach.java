package model;

public class XeKhach {
    private int maXe;
    private String bienSo;
    private int sucChua;

    public XeKhach() {}

    public XeKhach(int maXe, String bienSo, int sucChua) {
        this.maXe = maXe;
        this.bienSo = bienSo;
        this.sucChua = sucChua;
    }

    public int getMaXe() { return maXe; }
    public void setMaXe(int maXe) { this.maXe = maXe; }
    public String getBienSo() { return bienSo; }
    public void setBienSo(String bienSo) { this.bienSo = bienSo; }
    public int getSucChua() { return sucChua; }
    public void setSucChua(int sucChua) { this.sucChua = sucChua; }
}
