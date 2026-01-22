package Model;

public class ChiTiet_PhieuXuat {
    private PhieuXuat phieuXuat;
    private SanPham sanPham;
    private int soLuong;
    private float donGia;
    private float thanhTien;
    private float thueVAT;

    public ChiTiet_PhieuXuat() {
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

    public float getDonGia() {
        return donGia;
    }

    public void setDonGia(float donGia) {
        this.donGia = donGia;
    }

    public float getThanhTien() {
        return thanhTien;
    }

    public void setThanhTien(float thanhTien) {
        this.thanhTien = thanhTien;
    }

    public float getThueVAT() {
        return thueVAT;
    }

    public void setThueVAT(float thueVAT) {
        this.thueVAT = thueVAT;
    }

    public ChiTiet_PhieuXuat(PhieuXuat phieuXuat, SanPham sanPham, int soLuong, float donGia, float thanhTien, float thueVAT) {
        this.phieuXuat = phieuXuat;
        this.sanPham = sanPham;
        this.soLuong = soLuong;
        this.donGia = donGia;
        this.thanhTien = thanhTien;
        this.thueVAT = thueVAT;
    }
}
