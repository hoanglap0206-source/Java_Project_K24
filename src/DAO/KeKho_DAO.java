package DAO;

import DataBase.DBConnection;
import Model.KeKho;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class KeKho_DAO {
    public ArrayList<KeKho> getAllKeKho(){
        ArrayList<KeKho> list = new ArrayList<>();
        String sql = "SELECT * FROM KE_KHO";
        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery();
        ) {
            while(rs.next()){
                KeKho kk = new KeKho();
                kk.setMaKe(rs.getString("ma_ke"));
                kk.setSucChua(rs.getInt("suc_chua"));
                kk.setViTri(rs.getString("vi_tri"));
                kk.setDangChua(rs.getString("dang_chua"));
                list.add(kk);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    public boolean insert(KeKho kk){
        String sql ="INSERT INTO KE_KHO(ma_ke,suc_chua,vi_tri,dang_chua) VALUES (?, ?, ?,?)";
        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1,kk.getMaKe());
            ps.setInt(2,kk.getSucChua());
            ps.setString(3,kk.getViTri());
            ps.setString(4,kk.getDangChua());
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean update(KeKho kk){
        String sql =
                "UPDATE KE_KHO SET suc_chua=?,vi_tri=?,dang_chua=? WHERE ma_ke=?";
        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setInt(1,kk.getSucChua());
            ps.setString(2,kk.getViTri());
            ps.setString(3,kk.getDangChua());
            ps.setString(4,kk.getMaKe());
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean delete(String maKe){
        String sql = "DELETE FROM KE_KHO WHERE ma_ke=?";
        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, maKe);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
