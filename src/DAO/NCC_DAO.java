package DAO;

import DataBase.DBConnection;
import Model.NhaCungCap;
import Model.PhieuNhap;
import Model.ThongKeNCCDTO;
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
                ncc.setEmail(rs.getString("email"));
                list.add(ncc);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    public boolean insert(NhaCungCap ncc){
        String sql ="INSERT INTO NHA_CUNG_CAP(ma_ncc,ten_ncc,dia_chi,sdt,email) VALUES (?, ?, ?,?,?)";
        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ){
            ps.setString(1,ncc.getMaNCC());
            ps.setString(2,ncc.getTenNCC());
            ps.setString(3,ncc.getDiaChi());
            ps.setString(4,ncc.getSdt());
            ps.setString(5,ncc.getEmail());
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean update(NhaCungCap ncc){
        String sql =
                "UPDATE NHA_CUNG_CAP SET ten_ncc=?,dia_chi=?,sdt=?,email=? WHERE ma_ncc=?";
        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ){
            ps.setString(1,ncc.getTenNCC());
            ps.setString(2,ncc.getDiaChi());
            ps.setString(3,ncc.getSdt());
            ps.setString(4,ncc.getEmail());
            ps.setString(5,ncc.getMaNCC());
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
    public ThongKeNCCDTO getThongKeTuSQL(String maNCC) {
        String sql = "SELECT COUNT(DISTINCT pn.ma_pn) AS so_don, SUM(ct.thanh_tien) AS tong_tien " +
                "FROM phieu_nhap pn " +
                "JOIN chitiet_phieu_nhap ct ON pn.ma_pn = ct.ma_pn " +
                "WHERE pn.ma_ncc = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maNCC);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new ThongKeNCCDTO(rs.getInt("so_don"), rs.getDouble("tong_tien"));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return new ThongKeNCCDTO(0, 0);
    }

    // Lấy danh sách phiếu nhập kèm tổng tiền từng phiếu
    public ArrayList<Object[]> getLichSuPhieu(String maNCC) {
        ArrayList<Object[]> list = new ArrayList<>();
        String sql = "SELECT pn.ma_pn, pn.ngay_ct, SUM(ct.thanh_tien) AS tong_phieu " +
                "FROM phieu_nhap pn " +
                "JOIN chitiet_phieu_nhap ct ON pn.ma_pn = ct.ma_pn " +
                "WHERE pn.ma_ncc = ? " +
                "GROUP BY pn.ma_pn, pn.ngay_ct";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maNCC);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Object[]{
                        rs.getString("ma_pn"),
                        rs.getTimestamp("ngay_ct"),
                        rs.getDouble("tong_phieu")
                });
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
}
