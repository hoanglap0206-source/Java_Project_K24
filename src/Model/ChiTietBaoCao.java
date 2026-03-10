package Model;

public class ChiTietBaoCao {
    private SanPham sanPham;
    private int soLuongNhap;
    private int soLuongXuat;
    public ChiTietBaoCao() {
        this.sanPham = new SanPham();
    }
    public ChiTietBaoCao(SanPham sanPham, int soLuongNhap, int soLuongXuat) {
        this.sanPham = sanPham;
        this.soLuongNhap = soLuongNhap;
        this.soLuongXuat = soLuongXuat;
    }
    public SanPham getSanPham() {
        return sanPham;
    }
    public void setSanPham(SanPham sanPham) {
        this.sanPham = sanPham;
    }
    public int getSoLuongNhap() {
        return soLuongNhap;
    }
    public void setSoLuongNhap(int soLuongNhap) {
        this.soLuongNhap = soLuongNhap;
    }
    public int getSoLuongXuat() {
        return soLuongXuat;
    }
    public void setSoLuongXuat(int soLuongXuat) {
        this.soLuongXuat = soLuongXuat;
    }
    public float tinhThanhTienNhap() {
        if (this.sanPham != null) {
            return this.soLuongNhap * this.sanPham.getGiaTien();
        }
        return 0;
    }
    public float tinhThanhTienXuat() {
        if (this.sanPham != null) {
            return this.soLuongXuat * this.sanPham.getGiaTien();
        }
        return 0;
    }
}
