package DAO;

import DataBase.DBConnection;
import Model.ChiTietBaoCao;
import Model.SanPham;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class BaoCao_DAO {


    public ArrayList<ChiTietBaoCao> getBaoCaoNhap(java.sql.Date tuNgay, java.sql.Date denNgay) {
        ArrayList<ChiTietBaoCao> list = new ArrayList<>();


        String sql = "SELECT sp.ma_sku, sp.ten_sp, sp.dvt, sp.gia, SUM(ct.sl) as tong_nhap " +
                "FROM chitiet_phieu_nhap ct " +
                "JOIN phieu_nhap pn ON ct.ma_pn = pn.ma_pn " +
                "JOIN san_pham sp ON ct.ma_sku = sp.ma_sku " +
                "WHERE pn.ngay_ct BETWEEN ? AND ? " +
                "GROUP BY sp.ma_sku, sp.ten_sp, sp.dvt, sp.gia";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDate(1, tuNgay);
            ps.setDate(2, denNgay);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                SanPham sp = new SanPham();
                sp.setMaSP(rs.getString("ma_sku"));
                sp.setTenSP(rs.getString("ten_sp"));
                sp.setDonViTinh(rs.getString("dvt"));
                sp.setGiaTien(rs.getFloat("gia"));

                ChiTietBaoCao ct = new ChiTietBaoCao();
                ct.setSanPham(sp);
                ct.setSoLuongNhap(rs.getInt("tong_nhap"));
                ct.setSoLuongXuat(0);

                list.add(ct);
            }
        } catch (Exception e) {
            System.err.println("Lỗi lấy báo cáo nhập: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }


    public ArrayList<ChiTietBaoCao> getBaoCaoXuat(java.sql.Date tuNgay, java.sql.Date denNgay) {
        ArrayList<ChiTietBaoCao> list = new ArrayList<>();


        String sql = "SELECT sp.ma_sku, sp.ten_sp, sp.dvt, sp.gia, SUM(ctx.sl) as tong_xuat " +
                "FROM chitiet_phieu_xuat ctx " +
                "JOIN phieu_xuat px ON ctx.ma_px = px.ma_px " +
                "JOIN san_pham sp ON ctx.ma_sku = sp.ma_sku " +
                "WHERE px.ngay_ct BETWEEN ? AND ? " +
                "GROUP BY sp.ma_sku, sp.ten_sp, sp.dvt, sp.gia";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDate(1, tuNgay);
            ps.setDate(2, denNgay);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                SanPham sp = new SanPham();
                sp.setMaSP(rs.getString("ma_sku"));
                sp.setTenSP(rs.getString("ten_sp"));
                sp.setDonViTinh(rs.getString("dvt"));
                sp.setGiaTien(rs.getFloat("gia"));

                ChiTietBaoCao ct = new ChiTietBaoCao();
                ct.setSanPham(sp);
                ct.setSoLuongNhap(0); // Báo cáo xuất thì nhập = 0
                ct.setSoLuongXuat(rs.getInt("tong_xuat"));

                list.add(ct);
            }
        } catch (Exception e) {
            System.err.println("Lỗi lấy báo cáo xuất: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }
    public ArrayList<Object[]> getLichSuGiaoDidh(String maSP,String loaiBaoCao){
        ArrayList<Object[]> list = new ArrayList<>();
        Connection conn=null;
        PreparedStatement ps=null;
        ResultSet rs=null;

        try{
            conn=DBConnection.getConnection();
            if(loaiBaoCao.equalsIgnoreCase("Nhập hàng")){

                String sql = "SELECT pn.ma_pn, pn.ngay_ct, pn.ma_ncc, pn.ma_nhan_vien, ct.sl, ct.don_gia, ct.thanh_tien " +
                        "FROM phieu_nhap pn JOIN chitiet_phieu_nhap ct ON pn.ma_pn = ct.ma_pn " +
                        "WHERE ct.ma_sku = ? ORDER BY pn.ngay_ct DESC";

                ps = conn.prepareStatement(sql);
                ps.setString(1,maSP);
                rs=ps.executeQuery();

                while (rs.next()){
                    list.add(new Object[]{
                            rs.getString("ma_pn"), rs.getTimestamp("ngay_ct"),
                            rs.getString("ma_ncc"), rs.getString("ma_nhan_vien"),
                            rs.getInt("sl"), rs.getFloat("don_gia"), rs.getFloat("thanh_tien")
                    });
                }
            }
            else {

                String sql = "SELECT px.ma_px, px.ngay_ct, px.ma_kh, px.ma_nhan_vien, ctx.sl, ctx.don_gia, ctx.thanh_tien "+
                        "FROM phieu_xuat px JOIN chitiet_phieu_xuat ctx ON px.ma_px = ctx.ma_px " +
                        "WHERE ctx.ma_sku=? ORDER BY px.ngay_ct DESC";

                ps= conn.prepareStatement(sql);
                ps.setString(1,maSP);
                rs=ps.executeQuery();

                while (rs.next()){
                    list.add(new Object[]{
                            rs.getString("ma_px"), rs.getTimestamp("ngay_ct"),
                            rs.getString("ma_kh"), rs.getString("ma_nhan_vien"),
                            rs.getInt("sl"), rs.getFloat("don_gia"), rs.getFloat("thanh_tien")
                    });
                }
            }

        } catch (Exception e) {
            System.err.println("Lỗi lấy lịch sử: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }
}