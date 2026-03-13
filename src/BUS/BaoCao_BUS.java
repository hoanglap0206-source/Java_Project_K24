package BUS;

import DAO.BaoCao_DAO;
import Model.BaoCao;
import Model.ChiTietBaoCao;

import java.sql.Date;
import java.util.ArrayList;

public class BaoCao_BUS {
    private BaoCao_DAO baoCaoDAO;

    public BaoCao_BUS() {
        baoCaoDAO = new BaoCao_DAO();
    }


    public BaoCao taoBaoCao(String loaiBaoCao, java.util.Date tuNgay, java.util.Date denNgay) {
        BaoCao bc = new BaoCao();
        bc.setLoaiBaoCao(loaiBaoCao);
        bc.setTuNgay(tuNgay);
        bc.setDenNgay(denNgay);


        java.sql.Date sqlTuNgay = new java.sql.Date(tuNgay.getTime());
        java.sql.Date sqlDenNgay = new java.sql.Date(denNgay.getTime());

        ArrayList<ChiTietBaoCao> danhSach = new ArrayList<>();
        float tongTien = 0;


        if (loaiBaoCao.equalsIgnoreCase("Nhập hàng")) {
            danhSach = baoCaoDAO.getBaoCaoNhap(sqlTuNgay, sqlDenNgay);

            for (ChiTietBaoCao ct : danhSach) {
                tongTien += ct.tinhThanhTienNhap();
            }
        } else if (loaiBaoCao.equalsIgnoreCase("Xuất hàng")) {
            danhSach = baoCaoDAO.getBaoCaoXuat(sqlTuNgay, sqlDenNgay);

            for (ChiTietBaoCao ct : danhSach) {
                tongTien += ct.tinhThanhTienXuat();
            }
        }


        bc.setDanhSachChiTiet(danhSach);
        bc.setTongTien(tongTien);

        return bc;
    }
    public ArrayList<Object[]> getLichSuGiaoDidh(String maSP, String loaiBaoCao) {
        return baoCaoDAO.getLichSuGiaoDidh(maSP, loaiBaoCao);
    }
}