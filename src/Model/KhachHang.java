package Model;

public class KhachHang {
    private String maKH;
    private String hoTenKH;
    private String diaChi;
    private String sdt;
    private String Chitieu;

    public KhachHang() {}

    public KhachHang(String maKH, String hoTenKH, String diaChi, String sdt,String CT) {
        this.maKH = maKH;
        this.hoTenKH = hoTenKH;
        this.diaChi = diaChi;
        this.sdt = sdt;
        this.Chitieu=CT;
    }

    public String getMaKH() {
        return maKH;
    }

    public void setMaKH(String maKH) {
        this.maKH = maKH;
    }

    public String getHoTenKH() {
        return hoTenKH;
    }

    public void setHoTenKH(String hoTenKH) {
        this.hoTenKH = hoTenKH;
    }

    public String getDiaChi() {
        return diaChi;
    }

    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }

    public String getSdt() {
        return sdt;
    }

    public void setSdt(String sdt) {
        this.sdt = sdt;
    }

    public String getCT(){return Chitieu;}

    public void setCT(String CT){this.Chitieu=CT;}
}
