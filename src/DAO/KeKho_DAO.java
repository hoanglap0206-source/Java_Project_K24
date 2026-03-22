package DAO;

import DataBase.DBConnection;
import Model.KeKho;
import Model.KeKhoDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class KeKho_DAO {
    public ArrayList<KeKho> getAllKeKho(){
        ArrayList<KeKho> list = new ArrayList<>();
        String sql = "SELECT ma_ke, suc_chua, vi_tri FROM KE_KHO";
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
                list.add(kk);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public ArrayList<KeKho> getAllKeKhoB(Connection conn){
        ArrayList<KeKho> list = new ArrayList<>();
        String sql = "SELECT ma_ke, suc_chua, vi_tri FROM KE_KHO";
        try (
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery();
        ) {
            while(rs.next()){
                KeKho kk = new KeKho();
                kk.setMaKe(rs.getString("ma_ke"));
                kk.setSucChua(rs.getInt("suc_chua"));
                kk.setViTri(rs.getString("vi_tri"));
                list.add(kk);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean insert(KeKho kk){
        String sql ="INSERT INTO KE_KHO(ma_ke,suc_chua,vi_tri) VALUES (?, ?,?)";
        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1,kk.getMaKe());
            ps.setInt(2,kk.getSucChua());
            ps.setString(3,kk.getViTri());
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(KeKho kk){
        String sql =
                "UPDATE KE_KHO SET suc_chua=?,vi_tri=? WHERE ma_ke=?";
        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setInt(1,kk.getSucChua());
            ps.setString(2,kk.getViTri());
            ps.setString(3,kk.getMaKe());
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
    public int getSucChua(Connection conn,String maKe){
        String sql = "SELECT suc_chua FROM ke_kho WHERE ma_ke = ?";
        int sucChua = 0;
        try(
                PreparedStatement ps = conn.prepareStatement(sql);)
        {
            ps.setString(1,maKe);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                sucChua = rs.getInt("suc_chua");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sucChua;
    }
    public ArrayList<KeKhoDTO> getDSKeKho_Nhap(Connection conn){
        String sql = """
                SELECT ma_ke, SUM(so_luong) AS tongSL
                FROM CHITIET_KE
                GROUP BY ma_ke
                ORDER BY tongSL ASC
                """;
        ArrayList<KeKhoDTO> list = new ArrayList<>();
        try(
                PreparedStatement ps = conn.prepareStatement(sql);
                )
        {
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                KeKhoDTO kk = new KeKhoDTO();
                kk.setMa_ke(rs.getString("ma_ke"));
                kk.setTongSL(rs.getInt("tongSL"));
                list.add(kk);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
