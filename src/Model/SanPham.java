package Model;

public class SanPham {
    private String maSP;
    private String tenSP;
    private String donViTinh;
    private int soLuong;
    private float giaTien;
    private KeKho keKho;

    public SanPham() {
        this.keKho=new KeKho();
    }
    public SanPham(String maSP, String tenSP, String donViTinh, int soLuong, float giaTien, KeKho keKho) {
        this.maSP = maSP;
        this.tenSP = tenSP;
        this.donViTinh = donViTinh;
        this.soLuong = soLuong;
        this.giaTien = giaTien;
        this.keKho = keKho;
    }

    public String getMaSP() {
        return maSP;
    }

    public void setMaSP(String maSP) {
        this.maSP = maSP;
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

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }

    public float getGiaTien() {
        return giaTien;
    }

    public void setGiaTien(float giaTien) {
        this.giaTien = giaTien;
    }

    public KeKho getKeKho() {
        return keKho;
    }

    public void setKeKho(KeKho keKho) {
        this.keKho = keKho;
    }
    public String getMaKe() {
        if (this.keKho != null) {
            return this.keKho.getMaKe();
        }
        return "";
    }
    public void setMaKe(String maKe) {
        if (this.keKho == null) {
            this.keKho = new KeKho();
        }
        this.keKho.setMaKe(maKe);
    }
}

