package Model;

public class ChiTietKe {
    private String maKe;      // Mã kệ
    private String maSP;      // Mã sản phẩm
    private int soLuong;      // Số lượng sản phẩm trong kệ

    // Constructor mặc định
    public ChiTietKe() {
    }

    // Constructor đầy đủ
    public ChiTietKe(String maKe, String maSP, int soLuong) {
        this.maKe = maKe;
        this.maSP = maSP;
        this.soLuong = soLuong;
    }

    // Getter & Setter
    public String getMaKe() {
        return maKe;
    }

    public void setMaKe(String maKe) {
        this.maKe = maKe;
    }

    public String getMaSP() {
        return maSP;
    }

    public void setMaSP(String maSP) {
        this.maSP = maSP;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }

    @Override
    public String toString() {
        return "ChiTietKe{" +
                "maKe='" + maKe + '\'' +
                ", maSP='" + maSP + '\'' +
                ", soLuong=" + soLuong +
                '}';
    }
}
