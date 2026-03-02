package Model;

public class ChiTiet_PhieuNhap {
    private PhieuNhap phieuNhap;
    private SanPham sanPham;
    private int soLuong;
    private double donGia;
    private long thanhTien;

    public ChiTiet_PhieuNhap() {
    }

    public ChiTiet_PhieuNhap(PhieuNhap phieuNhap, SanPham sanPham, int soLuong, double donGia, long thanhTien) {
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
}
