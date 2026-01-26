package DAO;

import DataBase.DBConnection;
import Model.KhachHang;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class KhachHangDAO {
    public ArrayList<KhachHang> gettAllKhachHang(){
        ArrayList<KhachHang> list = new ArrayList<>();
        String sql = "SELECT * FROM KHACH_HANG";
        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery();
        ) {
            while(rs.next()){
                KhachHang kh = new KhachHang();
                kh.setMaKH(rs.getString("ma_kh"));
                kh.setHoTenKH(rs.getString("ten_kh"));
                kh.setDiaChi(rs.getString("dia_chi"));
                kh.setSdt(rs.getString("sdt"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    public boolean insert(KhachHang kh){
        String sql ="INSERT INTO KHACH_HANG(ma_kh,ten_kh,dia_chi,sdt) VALUES (?, ?, ?,?)";
        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ){
            ps.setString(1,kh.getMaKH());
            ps.setString(2,kh.getHoTenKH());
            ps.setString(3,kh.getDiaChi());
            ps.setString(4,kh.getSdt());
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean update(KhachHang kh){
        String sql =
                "UPDATE KHACH_HANG SET ten_kh=?,dia_chi=?,sdt=? WHERE ma_kh=?";
        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ){
            ps.setString(1,kh.getHoTenKH());
            ps.setString(2,kh.getDiaChi());
            ps.setString(3,kh.getSdt());
            ps.setString(4,kh.getMaKH());
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean delete(String maKH){
        String sql = "DELETE FROM KHACH_HANG WHERE ma_kh=?";
        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, maKH);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
