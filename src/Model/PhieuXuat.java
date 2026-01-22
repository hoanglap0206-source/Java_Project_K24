package Model;

import java.time.LocalDateTime;

public class PhieuXuat {
    private String maPX;
    private LocalDateTime ngay_ct;
    private KhachHang khachHang;
    private NhanVien nhanVien;

    public PhieuXuat() {
    }

    public PhieuXuat(String maPX, LocalDateTime ngay_ct, KhachHang khachHang, NhanVien nhanVien) {
        this.maPX = maPX;
        this.ngay_ct = ngay_ct;
        this.khachHang = khachHang;
        this.nhanVien = nhanVien;
    }

    public String getMaPX() {
        return maPX;
    }

    public void setMaPX(String maPX) {
        this.maPX = maPX;
    }

    public LocalDateTime getNgay_ct() {
        return ngay_ct;
    }

    public void setNgay_ct(LocalDateTime ngay_ct) {
        this.ngay_ct = ngay_ct;
    }

    public KhachHang getKhachHang() {
        return khachHang;
    }

    public void setKhachHang(KhachHang khachHang) {
        this.khachHang = khachHang;
    }

    public NhanVien getNhanVien() {
        return nhanVien;
    }

    public void setNhanVien(NhanVien nhanVien) {
        this.nhanVien = nhanVien;
    }
}
