package Model;

public class NhanVien {
    private String maNV;
    private String hoTen;
    private String sdt;
    private String matKhau;
    private String maNhom;
    private String tenNhom;
    private String trangThai;

    public NhanVien() {}

    public NhanVien(String maNV, String hoTen, String sdt, String matKhau, String maNhom, String tenNhom, String trangThai) {
        this.maNV = maNV;
        this.hoTen = hoTen;
        this.sdt = sdt;
        this.matKhau = matKhau;
        this.maNhom = maNhom;
        this.tenNhom = tenNhom;
        this.trangThai = trangThai;
    }

    public String getMaNV(){ return maNV; }
    public void   setMaNV(String maNV){ this.maNV = maNV; }

    public String getHoTen(){ return hoTen; }
    public void   setHoTen(String hoTen){ this.hoTen = hoTen; }

    public String getSDT(){ return sdt; }
    public void   setSDT(String sdt){ this.sdt = sdt; }

    public String getMatKhau(){ return matKhau; }
    public void   setMatKhau(String matKhau){ this.matKhau = matKhau; }

    public String getMaNhom(){ return maNhom; }
    public void   setMaNhom(String maNhom){ this.maNhom = maNhom; }

    public String getTenNhom(){ return tenNhom; }
    public void   setTenNhom(String tenNhom){ this.tenNhom = tenNhom; }

    public String getTrangThai(){ return trangThai; }
    public void   setTrangThai(String trangThai){ this.trangThai = trangThai; }
}