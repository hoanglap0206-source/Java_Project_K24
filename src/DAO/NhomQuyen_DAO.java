package DAO;

import DataBase.DBConnection;
import Model.ChucNang;
import Model.NhomQuyen;
import Model.NhomQuyenCT;

import java.sql.*;
import java.util.ArrayList;

public class NhomQuyen_DAO {

    public ArrayList<NhomQuyen> getAllNhomQuyen() {
        ArrayList<NhomQuyen> list = new ArrayList<>();
        String sql = "SELECT ma_nhom, ten_nhom FROM NHOM_QUYEN ORDER BY ma_nhom ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next())
                list.add(new NhomQuyen(rs.getString("ma_nhom"), rs.getString("ten_nhom")));
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public boolean insertNhomQuyen(NhomQuyen nhom) {
        String sql = "INSERT INTO NHOM_QUYEN(ma_nhom, ten_nhom) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nhom.getMaNhom());
            ps.setString(2, nhom.getTenNhom());
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    public boolean deleteNhomQuyen(String maNhom) {
        String sqlCT   = "DELETE FROM NHOM_QUYEN_CT WHERE ma_nhom = ?";
        String sqlNhom = "DELETE FROM NHOM_QUYEN    WHERE ma_nhom = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps1 = conn.prepareStatement(sqlCT);
             PreparedStatement ps2 = conn.prepareStatement(sqlNhom)) {
            ps1.setString(1, maNhom); ps1.executeUpdate();
            ps2.setString(1, maNhom);
            return ps2.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    public boolean isDuplicateMaNhom(String maNhom) {
        String sql = "SELECT 1 FROM NHOM_QUYEN WHERE ma_nhom = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maNhom);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    public String getMaxMaNhom() {
        String sql = "SELECT MAX(ma_nhom) FROM NHOM_QUYEN";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getString(1);
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    public ArrayList<NhomQuyenCT> getBangQuyenCuaNhom(String maNhom) {
        ArrayList<NhomQuyenCT> list = new ArrayList<>();
        String sql =
                "SELECT cn.ma_chuc_nang, cn.ten_chuc_nang, " +
                        "  IFNULL(ct.duoc_xem,  0) AS duoc_xem, " +
                        "  IFNULL(ct.duoc_xoa,  0) AS duoc_xoa, " +
                        "  IFNULL(ct.duoc_sua,  0) AS duoc_sua, " +
                        "  IFNULL(ct.duoc_them, 0) AS duoc_them " +
                        "FROM DM_CHUC_NANG cn " +
                        "LEFT JOIN NHOM_QUYEN_CT ct " +
                        "  ON cn.ma_chuc_nang = ct.ma_chuc_nang " +
                        "  AND ct.ma_nhom = ? " +
                        "ORDER BY cn.ma_chuc_nang ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maNhom);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    NhomQuyen nhom = new NhomQuyen();
                    nhom.setMaNhom(maNhom);
                    ChucNang cn = new ChucNang();
                    cn.setMaCN(rs.getString("ma_chuc_nang"));
                    cn.setTenCN(rs.getString("ten_chuc_nang"));
                    list.add(new NhomQuyenCT(
                            nhom, cn,
                            rs.getBoolean("duoc_xem"),
                            rs.getBoolean("duoc_xoa"),
                            rs.getBoolean("duoc_sua"),
                            rs.getBoolean("duoc_them")));
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public boolean checkExists(String maNhom, String maCN) {
        String sql = "SELECT 1 FROM NHOM_QUYEN_CT WHERE ma_nhom = ? AND ma_chuc_nang = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maNhom); ps.setString(2, maCN);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    public boolean insertCT(String maNhom, String maCN,
                            boolean xem, boolean xoa, boolean sua, boolean them) {
        String sql =
                "INSERT INTO NHOM_QUYEN_CT " +
                        "(ma_nhom, ma_chuc_nang, duoc_xem, duoc_xoa, duoc_sua, duoc_them) " +
                        "VALUES (?,?,?,?,?,?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maNhom);  ps.setString(2, maCN);
            ps.setBoolean(3, xem);    ps.setBoolean(4, xoa);
            ps.setBoolean(5, sua);    ps.setBoolean(6, them);
            boolean ok = ps.executeUpdate() > 0;
            if (ok)
                new PhanQuyen_DAO().syncNhomQuyenToNhanVien(maNhom, maCN, xem, xoa, sua, them);
            return ok;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    public boolean updateCT(String maNhom, String maCN,
                            boolean xem, boolean xoa, boolean sua, boolean them) {
        String sql =
                "UPDATE NHOM_QUYEN_CT " +
                        "SET duoc_xem=?, duoc_xoa=?, duoc_sua=?, duoc_them=? " +
                        "WHERE ma_nhom=? AND ma_chuc_nang=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, xem);   ps.setBoolean(2, xoa);
            ps.setBoolean(3, sua);   ps.setBoolean(4, them);
            ps.setString(5, maNhom); ps.setString(6, maCN);
            boolean ok = ps.executeUpdate() > 0;
            if (ok)
                new PhanQuyen_DAO().syncNhomQuyenToNhanVien(maNhom, maCN, xem, xoa, sua, them);
            return ok;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    /**
     * Xóa quyền chức năng khỏi nhóm (tất cả 4 bit = false).
     * Đồng thời xóa các dòng is_custom=FALSE tương ứng trong PHAN_QUYEN
     * của tất cả NV thuộc nhóm này.
     * Dòng is_custom=TRUE (tự tick tay) giữ nguyên.
     */
    public boolean deleteCT(String maNhom, String maCN) {
        String sqlDel = "DELETE FROM NHOM_QUYEN_CT WHERE ma_nhom=? AND ma_chuc_nang=?";
        String sqlSyncDel =
                "DELETE FROM PHAN_QUYEN " +
                        "WHERE ma_chuc_nang=? " +
                        "  AND is_custom=FALSE " +
                        "  AND ma_nhan_vien IN (SELECT ma_nhan_vien FROM NHAN_VIEN WHERE ma_nhom=?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement psDel  = conn.prepareStatement(sqlDel);
             PreparedStatement psSync = conn.prepareStatement(sqlSyncDel)) {

            psDel.setString(1, maNhom);
            psDel.setString(2, maCN);
            boolean ok = psDel.executeUpdate() > 0;

            if (ok) {
                // Xóa quyền kế thừa của các NV thuộc nhóm (is_custom=FALSE giữ nguyên TRUE)
                psSync.setString(1, maCN);
                psSync.setString(2, maNhom);
                psSync.executeUpdate();
            }
            return ok;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }
}