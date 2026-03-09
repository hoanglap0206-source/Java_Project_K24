package DAO;

import DataBase.DBConnection;
import Model.KhachHang;
import Model.NhanVien;
import Model.PhieuXuat;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;

public class PhieuXuat_DAO {
    public ArrayList<PhieuXuat> getAllPhieuXuat(){
        ArrayList<PhieuXuat> list = new ArrayList<>();
        String sql = "SELECT * FROM PHIEU_XUAT";
        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery();
        ) {
            while(rs.next()){
                PhieuXuat px =new PhieuXuat();
                px.setMaPX(rs.getString("ma_px"));
                px.setNgay_ct(
                        rs.getTimestamp("ngay_ct").toLocalDateTime()
                );
                KhachHang kh =new KhachHang();
                kh.setMaKH(rs.getString("ma_kh"));
                px.setKhachHang(kh);
                NhanVien nv =new NhanVien();
                nv.setMaNV(rs.getString("ma_nhan_vien"));
                px.setNhanVien(nv);
                list.add(px);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    public boolean insert(PhieuXuat px){
        String sql =
                "INSERT INTO PHIEU_XUAT(ma_px,ngay_ct,ma_kh,ma_nhan_vien) VALUES (?, ?, ?,?)";
        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1,px.getMaPX());
            ps.setTimestamp(2, Timestamp.valueOf(px.getNgay_ct()));
            ps.setString(3,px.getKhachHang().getMaKH());
            ps.setString(4,px.getNhanVien().getMaNV());
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean inSert(Connection conn,PhieuXuat px){
        String sql =
                "INSERT INTO PHIEU_XUAT(ma_px,ngay_ct,ma_kh,ma_nhan_vien) VALUES (?, ?, ?,?)";
        try (
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1,px.getMaPX());
            ps.setTimestamp(2, Timestamp.valueOf(px.getNgay_ct()));
            ps.setString(3,px.getKhachHang().getMaKH());
            ps.setString(4,px.getNhanVien().getMaNV());
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(PhieuXuat px){
        String sql =
                "UPDATE PHIEU_XUAT SET ngay_ct=?,ma_kh=?,ma_nhan_vien=? WHERE ma_px=?";
        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setTimestamp(1, Timestamp.valueOf(px.getNgay_ct()));
            ps.setString(2,px.getKhachHang().getMaKH());
            ps.setString(3,px.getNhanVien().getMaNV());
            ps.setString(4,px.getMaPX());
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean delete(String maPX){
        String sql=
                "DELETE FROM PHIEU_XUAT WHERE ma_px=?";
        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1,maPX);
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
