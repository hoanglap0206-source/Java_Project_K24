package Model;

public class SanPhamDTO {
    private String ma_sku;
    private String tenSP;
    private String donViTinh;
    private int sl;
    private float giaTien;
    private int trangthai;

    public SanPhamDTO() {
    }

    public SanPhamDTO(int trangthai, float giaTien, int sl, String donViTinh, String tenSP, String ma_sku) {
        this.trangthai = trangthai;
        this.giaTien = giaTien;
        this.sl = sl;
        this.donViTinh = donViTinh;
        this.tenSP = tenSP;
        this.ma_sku = ma_sku;
    }

    public String getMa_sku() {
        return ma_sku;
    }

    public void setMa_sku(String ma_sku) {
        this.ma_sku = ma_sku;
    }

    public String getTenSP() {
        return tenSP;
    }

    public void setTenSP(String tenSP) {
        this.tenSP = tenSP;
    }

    public String getDonViTinh() {
        return donViTinh;
    }

    public void setDonViTinh(String donViTinh) {
        this.donViTinh = donViTinh;
    }

    public int getSl() {
        return sl;
    }

    public void setSl(int sl) {
        this.sl = sl;
    }

    public float getGiaTien() {
        return giaTien;
    }

    public void setGiaTien(float giaTien) {
        this.giaTien = giaTien;
    }

    public int getTrangthai() {
        return trangthai;
    }

    public void setTrangthai(int trangthai) {
        this.trangthai = trangthai;
    }
}
