package Model;

public class ChucNang {
    private String maCN;
    private String tenCN;

    public ChucNang() {
    }

    public ChucNang(String maCN, String tenCN) {
        this.maCN = maCN;
        this.tenCN = tenCN;
    }

    public String getMaCN() {
        return maCN;
    }

    public void setMaCN(String maCN) {
        this.maCN = maCN;
    }

    public String getTenCN() {
        return tenCN;
    }

    public void setTenCN(String tenCN) {
        this.tenCN = tenCN;
    }
}
