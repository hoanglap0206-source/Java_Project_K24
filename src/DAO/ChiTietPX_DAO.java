package DAO;

import DataBase.DBConnection;
import Model.ChiTiet_PhieuXuat;
import Model.PhieuXuat;
import Model.SanPham;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class ChiTietPX_DAO {
    public ArrayList<ChiTiet_PhieuXuat> getAllCtPX(){
        ArrayList<ChiTiet_PhieuXuat> list =new ArrayList<>();
        String sql ="SELECT * FROM CHITIET_PHIEU_XUAT ";
        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery();
        ) {
            while(rs.next()){
                PhieuXuat px =new PhieuXuat();
                SanPham sp =new SanPham();
                ChiTiet_PhieuXuat ct =new ChiTiet_PhieuXuat();
                px.setMaPX(rs.getString("ma_px"));
                sp.setMaSP(rs.getString("ma_sku"));
                ct.setPhieuXuat(px);
                ct.setSanPham(sp);
                ct.setSoLuong(rs.getInt("sl"));
                ct.setDonGia(rs.getDouble("don_gia"));
                ct.setThanhTien(rs.getLong("thanh_tien"));
                ct.setThueVAT(rs.getFloat("thue_vat"));
                list.add(ct);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    public boolean insert(ChiTiet_PhieuXuat ct){
        String sql=
                "INSERT INTO CHITIET_PHIEU_XUAT(ma_px,ma_sku,sl,don_gia,thanh_tien,thue_vat) VALUES (?, ?, ?,?,?,?)";
        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1,ct.getPhieuXuat().getMaPX());
            ps.setString(2,ct.getSanPham().getMaSP());
            ps.setInt(3,ct.getSoLuong());
            ps.setDouble(4,ct.getDonGia());
            ps.setLong(5,ct.getThanhTien());
            ps.setFloat(6,ct.getThueVAT());
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean inSert(Connection conn,ChiTiet_PhieuXuat ct){
        String sql=
                "INSERT INTO CHITIET_PHIEU_XUAT(ma_px,ma_sku,sl,don_gia,thanh_tien,thue_vat) VALUES (?, ?, ?,?,?,?)";
        try (
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1,ct.getPhieuXuat().getMaPX());
            ps.setString(2,ct.getSanPham().getMaSP());
            ps.setInt(3,ct.getSoLuong());
            ps.setDouble(4,ct.getDonGia());
            ps.setLong(5,ct.getThanhTien());
            ps.setFloat(6,ct.getThueVAT());
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(ChiTiet_PhieuXuat ct){
        String sql=
                "UPDATE CHITIET_PHIEU_XUAT SET sl=?,don_gia=?,thanh_tien=?,thue_vat=? WHERE ma_px=? AND ma_sku=?";
        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setInt(1,ct.getSoLuong());
            ps.setDouble(2,ct.getDonGia());
            ps.setLong(3,ct.getThanhTien());
            ps.setFloat(4,ct.getThueVAT());
            ps.setString(5,ct.getPhieuXuat().getMaPX());
            ps.setString(6,ct.getSanPham().getMaSP());
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean delete(String maPX,String maSP){
        String sql=
                "DELETE FROM CHITIET_PHIEU_XUAT WHERE ma_px=? AND ma_sku=?";
        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, maPX);
            ps.setString(2,maSP);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
