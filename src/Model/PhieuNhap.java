package Model;

import java.time.LocalDateTime;

public class PhieuNhap {
    private String maPN;
    private LocalDateTime ngay_ct;
    private NhaCungCap nhaCC;
    private NhanVien nhanVien;
    private long TongTien;

    public PhieuNhap() {
    }

    public PhieuNhap(String maPN, LocalDateTime ngay_ct, NhaCungCap nhaCC, NhanVien nhanVien) {
        this.maPN = maPN;
        this.ngay_ct = ngay_ct;
        this.nhaCC = nhaCC;
        this.nhanVien = nhanVien;
    }

    public String getMaPN() {
        return maPN;
    }

    public void setMaPN(String maPN) {
        this.maPN = maPN;
    }

    public LocalDateTime getNgay_ct() {
        return ngay_ct;
    }

    public void setNgay_ct(LocalDateTime ngay_ct) {
        this.ngay_ct = ngay_ct;
    }

    public NhaCungCap getNhaCC() {
        return nhaCC;
    }

    public void setNhaCC(NhaCungCap nhaCC) {
        this.nhaCC = nhaCC;
    }

    public NhanVien getNhanVien() {
        return nhanVien;
    }

    public void setNhanVien(NhanVien nhanVien) {
        this.nhanVien = nhanVien;
    }

    public long getTongTien() {
        return TongTien;
    }

    public void setTongTien(long TongTien) {
        this.TongTien = TongTien;
    }
}
