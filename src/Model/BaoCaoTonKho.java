package Model;

public class BaoCaoTonKho {
    private String maBC;
    private int sLTon;
    private int canhBaoHH;
    private SanPham sanPham;

    public BaoCaoTonKho() {
    }

    public BaoCaoTonKho(String maBC, int sLTon, int canhBaoHH, SanPham sanPham) {
        this.maBC = maBC;
        this.sLTon = sLTon;
        this.canhBaoHH = canhBaoHH;
        this.sanPham = sanPham;
    }

    public String getMaBC() {
        return maBC;
    }

    public void setMaBC(String maBC) {
        this.maBC = maBC;
    }

    public int getsLTon() {
        return sLTon;
    }

    public void setsLTon(int sLTon) {
        this.sLTon = sLTon;
    }

    public int getCanhBaoHH() {
        return canhBaoHH;
    }

    public void setCanhBaoHH(int canhBaoHH) {
        this.canhBaoHH = canhBaoHH;
    }

    public SanPham getSanPham() {
        return sanPham;
    }

    public void setSanPham(SanPham sanPham) {
        this.sanPham = sanPham;
    }
}
