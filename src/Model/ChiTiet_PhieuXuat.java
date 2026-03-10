package Model;

public class ChiTiet_PhieuXuat {
    private PhieuXuat phieuXuat;
    private SanPham sanPham;
    private int soLuong;
    private double donGia;
    private long thanhTien;
    private float thueVAT = 10/100;

    public ChiTiet_PhieuXuat() {
    }

    public ChiTiet_PhieuXuat(PhieuXuat phieuXuat, SanPham sanPham, int soLuong, double donGia, long thanhTien, float thueVAT) {
        this.phieuXuat = phieuXuat;
        this.sanPham = sanPham;
        this.soLuong = soLuong;
        this.donGia = donGia;
        this.thanhTien = thanhTien;
        this.thueVAT = thueVAT;
    }

    public PhieuXuat getPhieuXuat() {
        return phieuXuat;
    }

    public void setPhieuXuat(PhieuXuat phieuXuat) {
        this.phieuXuat = phieuXuat;
    }

    public SanPham getSanPham() {
        return sanPham;
    }

    public void setSanPham(SanPham sanPham) {
        this.sanPham = sanPham;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }

    public double getDonGia() {
        return donGia;
    }

    public void setDonGia(double donGia) {
        this.donGia = donGia;
    }

    public long getThanhTien() {
        return thanhTien;
    }

    public void setThanhTien(long thanhTien) {
        this.thanhTien = thanhTien;
    }

    public float getThueVAT() {
        return thueVAT;
    }

    public void setThueVAT(float thueVAT) {
        this.thueVAT = thueVAT;
    }
}
