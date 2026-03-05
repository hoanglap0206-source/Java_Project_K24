package Model;

public class BaoCaoTonKho {
    private String maTonKho;
    private int sLTon;
    private int canhBaoHH;
    private SanPham sanPham;

    public BaoCaoTonKho() {
    }

    public BaoCaoTonKho(String maTonKho, int sLTon, int canhBaoHH, SanPham sanPham) {
        this.maTonKho = maTonKho;
        this.sLTon = sLTon;
        this.canhBaoHH = canhBaoHH;
        this.sanPham = sanPham;
    }

    public String getMaTonKho() {
        return maTonKho;
    }

    public void setMaBC(String maTonKho) {
        this.maTonKho = maTonKho;
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
