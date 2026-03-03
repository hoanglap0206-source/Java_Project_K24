package Model;

public class KeKho {
    private String maKe;
    private int sucChua;
    private String viTri;

    public KeKho() {
    }

    public KeKho(String maKe, int sucChua, String viTri) {
        this.maKe = maKe;
        this.sucChua = sucChua;
        this.viTri = viTri;
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
}
