package Model;

import java.util.ArrayList;
import java.util.Date;

public class BaoCao {
    private String maBaoCao;
    private String loaiBaoCao; // Ví dụ: "Nhập hàng", "Xuất hàng"
    private Date tuNgay;
    private Date denNgay;
    private float tongTien;


    private ArrayList<ChiTietBaoCao> danhSachChiTiet;

    public BaoCao() {
        this.danhSachChiTiet = new ArrayList<>();
    }

    public BaoCao(String maBaoCao, String loaiBaoCao, Date tuNgay, Date denNgay, float tongTien, ArrayList<ChiTietBaoCao> danhSachChiTiet) {
        this.maBaoCao = maBaoCao;
        this.loaiBaoCao = loaiBaoCao;
        this.tuNgay = tuNgay;
        this.denNgay = denNgay;
        this.tongTien = tongTien;
        this.danhSachChiTiet = danhSachChiTiet;
    }

    public String getMaBaoCao() {
        return maBaoCao;
    }

    public void setMaBaoCao(String maBaoCao) {
        this.maBaoCao = maBaoCao;
    }

    public String getLoaiBaoCao() {
        return loaiBaoCao;
    }

    public void setLoaiBaoCao(String loaiBaoCao) {
        this.loaiBaoCao = loaiBaoCao;
    }

    public Date getTuNgay() {
        return tuNgay;
    }

    public void setTuNgay(Date tuNgay) {
        this.tuNgay = tuNgay;
    }

    public Date getDenNgay() {
        return denNgay;
    }

    public void setDenNgay(Date denNgay) {
        this.denNgay = denNgay;
    }

    public float getTongTien() {
        return tongTien;
    }

    public void setTongTien(float tongTien) {
        this.tongTien = tongTien;
    }

    public ArrayList<ChiTietBaoCao> getDanhSachChiTiet() {
        return danhSachChiTiet;
    }

    public void setDanhSachChiTiet(ArrayList<ChiTietBaoCao> danhSachChiTiet) {
        this.danhSachChiTiet = danhSachChiTiet;
    }
}