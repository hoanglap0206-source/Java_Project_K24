package Model;

public class KeKho {
    private String maKe;
    private int sucChua;
    private String viTri;
    private String dangChua;

    public KeKho() {
    }

    public KeKho(String maKe, int sucChua, String viTri, String dangChua) {
        this.maKe = maKe;
        this.sucChua = sucChua;
        this.viTri = viTri;
        this.dangChua = dangChua;
    }

    public String getMaKe() {
        return maKe;
    }

    public void setMaKe(String maKe) {
        this.maKe = maKe;
    }

    public int getSucChua() {
        return sucChua;
    }

    public void setSucChua(int sucChua) {
        this.sucChua = sucChua;
    }

    public String getViTri() {
        return viTri;
    }

    public void setViTri(String viTri) {
        this.viTri = viTri;
    }

    public String getDangChua() {
        return dangChua;
    }

    public void setDangChua(String dangChua) {
        this.dangChua = dangChua;
    }
}
