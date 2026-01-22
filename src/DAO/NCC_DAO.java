package DAO;

import DataBase.DBConnection;
import Model.NhaCungCap;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class NCC_DAO {
    public ArrayList<NhaCungCap> getAllNCC(){
        ArrayList<NhaCungCap> list = new ArrayList<>();
        String sql = "SELECT * FROM NHA_CUNG_CAP";
        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery();
        ) {
            while(rs.next()){
                NhaCungCap ncc = new NhaCungCap();
                ncc.setMaNCC(rs.getString("ma_ncc"));
                ncc.setTenNCC(rs.getString("ten_ncc"));
                ncc.setDiaChi(rs.getString("dia_chi"));
                ncc.setSdt(rs.getString("sdt"));
                list.add(ncc);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    public boolean insert(NhaCungCap ncc){
        String sql ="INSERT INTO NHA_CUNG_CAP(ma_ncc,ten_ncc,dia_chi,sdt) VALUES (?, ?, ?,?)";
        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ){
            ps.setString(1,ncc.getMaNCC());
            ps.setString(2,ncc.getTenNCC());
            ps.setString(3,ncc.getDiaChi());
            ps.setString(4,ncc.getSdt());
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean update(NhaCungCap ncc){
        String sql =
                "UPDATE NHA_CUNG_CAP SET ten_ncc=?,dia_chi=?,sdt=? WHERE ma_ncc=?";
        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ){
            ps.setString(1,ncc.getTenNCC());
            ps.setString(2,ncc.getDiaChi());
            ps.setString(3,ncc.getSdt());
            ps.setString(4,ncc.getMaNCC());
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean delete(String maNCC){
        String sql = "DELETE FROM NHA_CUNG_CAP WHERE ma_ncc=?";
        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, maNCC);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
