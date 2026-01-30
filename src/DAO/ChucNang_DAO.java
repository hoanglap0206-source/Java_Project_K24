package DAO;

import DataBase.DBConnection;
import Model.ChucNang;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class ChucNang_DAO {
    public ArrayList<ChucNang> getAllChucNang(){
        ArrayList<ChucNang> list =new ArrayList<>();
        String sql=
                "SELECT * FROM DM_CHUC_NANG";
        try(
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery();
        ){
            while ((rs.next())){
                ChucNang cn =new ChucNang();
                cn.setMaCN(rs.getString("ma_chuc_nang"));
                cn.setTenCN(rs.getString("ten_chuc_nang"));
                list.add(cn);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    public boolean insert(ChucNang cn){
        String sql=
                "INSERT INTO DM_CHUC_NANG(ma_chuc_nang,ten_chuc_nang) VALUES (?, ?)";
        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1,cn.getMaCN());
            ps.setString(2,cn.getTenCN());
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean update(ChucNang cn){
        String sql=
                "UPDATE DM_CHUC_NANG SET ten_chuc_nang=? WHERE ma_chuc_nang=?";
        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1,cn.getTenCN());
            ps.setString(2,cn.getMaCN());
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean delete(String maCN){
        String sql=
                "DELETE FROM DM_CHUC_NANG WHERE ma_chuc_nang=?";
        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1,maCN);
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
