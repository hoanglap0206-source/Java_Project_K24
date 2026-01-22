package Model;

public class PhanQuyen {
    private NhanVien nhanVien;
    private ChucNang chucNang;
    private boolean xem = false;
    private boolean xoa = false;
    private boolean sua = false;
    private boolean them = false;

    public PhanQuyen() {
    }

    public PhanQuyen(NhanVien nhanVien, ChucNang chucNang, boolean xem, boolean xoa, boolean sua, boolean them) {
        this.nhanVien = nhanVien;
        this.chucNang = chucNang;
        this.xem = xem;
        this.xoa = xoa;
        this.sua = sua;
        this.them = them;
    }

    public NhanVien getNhanVien() {
        return nhanVien;
    }

    public void setNhanVien(NhanVien nhanVien) {
        this.nhanVien = nhanVien;
    }

    public ChucNang getChucNang() {
        return chucNang;
    }

    public void setChucNang(ChucNang chucNang) {
        this.chucNang = chucNang;
    }

    public boolean isXem() {
        return xem;
    }

    public void setXem(boolean xem) {
        this.xem = xem;
    }

    public boolean isXoa() {
        return xoa;
    }

    public void setXoa(boolean xoa) {
        this.xoa = xoa;
    }

    public boolean isSua() {
        return sua;
    }

    public void setSua(boolean sua) {
        this.sua = sua;
    }

    public boolean isThem() {
        return them;
    }

    public void setThem(boolean them) {
        this.them = them;
    }
}
