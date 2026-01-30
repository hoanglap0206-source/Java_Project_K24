package DAO;

import DataBase.DBConnection;
import Model.ChiTiet_PhieuNhap;
import Model.PhieuNhap;
import Model.SanPham;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class ChiTietPN_DAO {
    public ArrayList<ChiTiet_PhieuNhap> getAllCtPN(){
        ArrayList<ChiTiet_PhieuNhap> list = new ArrayList<>();
        String sql = "SELECT * FROM CHITIET_PHIEU_NHAP";
        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery();
        ) {
            while(rs.next()){
                PhieuNhap pn =new PhieuNhap();
                SanPham sp =new SanPham();
                ChiTiet_PhieuNhap ct =new ChiTiet_PhieuNhap();
                pn.setMaPN(rs.getString("ma_pn"));
                sp.setMaSP(rs.getString("ma_sku"));
                ct.setPhieuNhap(pn);
                ct.setSanPham(sp);
                ct.setSoLuong(rs.getInt("sl"));
                ct.setDonGia(rs.getFloat("don_gia"));
                ct.setThanhTien(rs.getFloat("thanh_tien"));
                list.add(ct);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    public boolean insert(ChiTiet_PhieuNhap ct){
        String sql ="INSERT INTO CHITIET_PHIEU_NHAP(ma_pn,ma_sku,sl,don_gia,thanh_tien) VALUES (?, ?, ?,?,?)";
        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1,ct.getPhieuNhap().getMaPN());
            ps.setString(2,ct.getSanPham().getMaSP());
            ps.setInt(3,ct.getSoLuong());
            ps.setFloat(4,ct.getDonGia());
            ps.setFloat(5,ct.getThanhTien());
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean update(ChiTiet_PhieuNhap ct){
        String sql =
                "UPDATE CHITIET_PHIEU_NHAP SET sl=?,don_gia=?,thanh_tien=? WHERE ma_pn=? AND ma_sku=?";
        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setInt(1,ct.getSoLuong());
            ps.setFloat(2,ct.getDonGia());
            ps.setFloat(3,ct.getThanhTien());
            ps.setString(4,ct.getPhieuNhap().getMaPN());
            ps.setString(5,ct.getSanPham().getMaSP());
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean delete(String maPN,String maSP) {
        String sql = "DELETE FROM CHITIET_PHIEU_NHAP WHERE ma_pn=? AND ma_sku=?";
        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, maPN);
            ps.setString(2, maSP);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
