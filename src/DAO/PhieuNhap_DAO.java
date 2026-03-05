package DAO;

import DataBase.DBConnection;
import Model.NhaCungCap;
import Model.NhanVien;
import Model.PhieuNhap;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;

public class PhieuNhap_DAO {
    public ArrayList<PhieuNhap> getAllPhieuNhap(){
        ArrayList<PhieuNhap> list = new ArrayList<>();
        String sql = "SELECT * FROM PHIEU_NHAP";
        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery();
        ) {
            while(rs.next()){
                PhieuNhap pn =new PhieuNhap();
                NhanVien nv =new NhanVien();
                NhaCungCap ncc =new NhaCungCap();
                pn.setMaPN(rs.getString("ma_pn"));
                pn.setNgay_ct(
                        rs.getTimestamp("ngay_ct").toLocalDateTime()
                );
                ncc.setMaNCC(rs.getString("ma_ncc"));
                pn.setNhaCC(ncc);
                nv.setMaNV(rs.getString("ma_nhan_vien"));
                pn.setNhanVien(nv);
                list.add(pn);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    public boolean insert(PhieuNhap pn){
        String sql ="INSERT INTO PHIEU_NHAP(ma_pn,ngay_ct,ma_ncc,ma_nhan_vien) VALUES (?, ?, ?,?)";
        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1,pn.getMaPN());
            ps.setTimestamp(2, Timestamp.valueOf(pn.getNgay_ct()));
            ps.setString(3,pn.getNhaCC().getMaNCC());
            ps.setString(4,pn.getNhanVien().getMaNV());
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean inSert(Connection conn,PhieuNhap pn){
        String sql ="INSERT INTO PHIEU_NHAP(ma_pn,ngay_ct,ma_ncc,ma_nhan_vien) VALUES (?, ?, ?,?)";
        try (
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1,pn.getMaPN());
            ps.setTimestamp(2, Timestamp.valueOf(pn.getNgay_ct()));
            ps.setString(3,pn.getNhaCC().getMaNCC());
            ps.setString(4,pn.getNhanVien().getMaNV());
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(PhieuNhap pn){
        String sql =
                "UPDATE PHIEU_NHAP SET ngay_ct=?,ma_ncc=?,ma_nhan_vien=? WHERE ma_pn=?";
        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setTimestamp(1, Timestamp.valueOf(pn.getNgay_ct()));
            ps.setString(2,pn.getNhaCC().getMaNCC());
            ps.setString(3,pn.getNhanVien().getMaNV());
            ps.setString(4,pn.getMaPN());
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean delete(String maPN){
        String sql = "DELETE FROM PHIEU_NHAP WHERE ma_pn=?";
        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, maPN);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
