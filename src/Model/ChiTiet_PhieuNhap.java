package Model;

public class ChiTiet_PhieuNhap {
    private PhieuNhap phieuNhap;
    private SanPham sanPham;
    private int soLuong;
    private float donGia;
    private float thanhTien;

    public ChiTiet_PhieuNhap() {
    }

    public ChiTiet_PhieuNhap(PhieuNhap phieuNhap, SanPham sanPham, int soLuong, float donGia, float thanhTien) {
        this.phieuNhap = phieuNhap;
        this.sanPham = sanPham;
        this.soLuong = soLuong;
        this.donGia = donGia;
        this.thanhTien = thanhTien;
    }

    public PhieuNhap getPhieuNhap() {
        return phieuNhap;
    }

    public void setPhieuNhap(PhieuNhap phieuNhap) {
        this.phieuNhap = phieuNhap;
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
}
