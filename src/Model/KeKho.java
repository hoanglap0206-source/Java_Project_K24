package Model;

public class KeKho {
    private String maKe;
    private int sucChua;
    private String viTri;
    private int khoangTrong;

    public KeKho() {
    }

    public KeKho(String maKe, int sucChua, String viTri,int khoangTrong) {
        this.maKe = maKe;
        this.sucChua = sucChua;
        this.viTri = viTri;
        this.khoangTrong = khoangTrong;
    }

    public int getKhoangTrong() {
        return khoangTrong;
    }

    public void setKhoangTrong(int khoangTrong) {
        this.khoangTrong = khoangTrong;
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
