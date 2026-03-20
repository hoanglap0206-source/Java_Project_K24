package DAO;

import DataBase.DBConnection;
import Model.NhanVien;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class NV_DAO {

    public ArrayList<NhanVien> getAllNV() {
        ArrayList<NhanVien> list = new ArrayList<>();
        String sql =
                "SELECT nv.ma_nhan_vien, nv.ho_ten, nv.mat_khau, " +
                        "       nv.ma_nhom, nq.ten_nhom, nv.trang_thai, nv.sdt " +
                        "FROM NHAN_VIEN nv " +
                        "LEFT JOIN NHOM_QUYEN nq ON nv.ma_nhom = nq.ma_nhom";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                NhanVien nv = new NhanVien();
                nv.setMaNV(rs.getString("ma_nhan_vien"));
                nv.setHoTen(rs.getString("ho_ten"));
                nv.setMatKhau(rs.getString("mat_khau"));
                nv.setMaNhom(rs.getString("ma_nhom"));      // ← thay setChucVu
                nv.setTenNhom(rs.getString("ten_nhom"));    // ← tên nhóm từ JOIN
                nv.setTrangThai(rs.getString("trang_thai"));
                nv.setSDT(rs.getString("sdt"));
                list.add(nv);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean insert(NhanVien nv) {
        String sql =
                "INSERT INTO NHAN_VIEN(ma_nhan_vien, ho_ten, mat_khau, ma_nhom, trang_thai, sdt) " +
                        "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nv.getMaNV());
            ps.setString(2, nv.getHoTen());
            ps.setString(3, nv.getMatKhau());
            ps.setString(4, nv.getMaNhom());      // ← thay getChucVu
            ps.setString(5, nv.getTrangThai());
            ps.setString(6, nv.getSDT());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(NhanVien nv) {
        String sql =
                "UPDATE NHAN_VIEN " +
                        "SET ho_ten=?, mat_khau=?, ma_nhom=?, trang_thai=?, sdt=? " +
                        "WHERE ma_nhan_vien=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nv.getHoTen());
            ps.setString(2, nv.getMatKhau());
            ps.setString(3, nv.getMaNhom());      // ← thay getChucVu
            ps.setString(4, nv.getTrangThai());
            ps.setString(5, nv.getSDT());
            ps.setString(6, nv.getMaNV());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(String maNv) {
        String sql1 = "DELETE FROM PHAN_QUYEN WHERE ma_nhan_vien=?";
        String sql2 = "DELETE FROM NHAN_VIEN   WHERE ma_nhan_vien=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps1 = conn.prepareStatement(sql1);
             PreparedStatement ps2 = conn.prepareStatement(sql2)) {
            ps1.setString(1, maNv);
            ps1.executeUpdate();
            ps2.setString(1, maNv);
            return ps2.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public ArrayList<NhanVien> getAccoount() {
        String sql = "SELECT ma_nhan_vien, mat_khau FROM NHAN_VIEN";
        ArrayList<NhanVien> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                NhanVien nv = new NhanVien();
                nv.setMaNV(rs.getString("ma_nhan_vien"));
                nv.setMatKhau(rs.getString("mat_khau"));
                list.add(nv);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public String getTenNV_DAO(String maNV) {
        String sql = "SELECT ho_ten FROM NHAN_VIEN WHERE ma_nhan_vien=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maNV);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getString("ho_ten");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public ArrayList<NhanVien> getInfo_NV_DAO(String maNV) {
        String sql =
                "SELECT nv.ma_nhan_vien, nv.ho_ten, nv.sdt, " +
                        "       nv.ma_nhom, nq.ten_nhom " +
                        "FROM NHAN_VIEN nv " +
                        "LEFT JOIN NHOM_QUYEN nq ON nv.ma_nhom = nq.ma_nhom " +
                        "WHERE nv.ma_nhan_vien=?";
        ArrayList<NhanVien> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maNV);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    NhanVien nv = new NhanVien();
                    nv.setMaNV(rs.getString("ma_nhan_vien"));
                    nv.setHoTen(rs.getString("ho_ten"));
                    nv.setSDT(rs.getString("sdt"));
                    nv.setMaNhom(rs.getString("ma_nhom"));
                    nv.setTenNhom(rs.getString("ten_nhom"));
                    list.add(nv);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}