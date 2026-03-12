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
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            //Xóa chi tiết phiếu nhập trước bắt buộc trước khi xóa phiếu nhập
            PreparedStatement ps1 = conn.prepareStatement(
                    "DELETE FROM CHITIET_PHIEU_NHAP WHERE ma_pn=?");
            ps1.setString(1, maPN);
            ps1.executeUpdate();
            ps1.close();

            //Sau khi chi tiết đã xóa xong mới xóa phiếu nhập
            PreparedStatement ps2 = conn.prepareStatement(
                    "DELETE FROM PHIEU_NHAP WHERE ma_pn=?");
            ps2.setString(1, maPN);
            int rows = ps2.executeUpdate();
            ps2.close();

            if (rows == 0) {
                conn.rollback();
                return false;
            }
            conn.commit();
            return true;

        } catch (Exception e) {
            try { if (conn != null) conn.rollback(); } catch (Exception ignored) {}
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (Exception ignored) {}
        }
    }

    public ArrayList<PhieuNhap> getDanhSachPhieuNhapTongKet() {
        ArrayList<PhieuNhap> list = new ArrayList<>();
        String sql = "SELECT pn.ma_pn, pn.ngay_ct, ncc.ma_ncc, ncc.ten_ncc, SUM(ct.thanh_tien) AS tong_tien " +
                "FROM PHIEU_NHAP pn " +
                "JOIN NHA_CUNG_CAP ncc ON pn.ma_ncc = ncc.ma_ncc " +
                "JOIN CHITIET_PHIEU_NHAP ct ON pn.ma_pn = ct.ma_pn " +
                "GROUP BY pn.ma_pn, pn.ngay_ct, ncc.ma_ncc, ncc.ten_ncc";

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery();
        ) {
            while (rs.next()) {
                PhieuNhap pn = new PhieuNhap();
                NhaCungCap ncc = new NhaCungCap();

                // Lấy thông tin phiếu nhập
                pn.setMaPN(rs.getString("ma_pn"));
                pn.setNgay_ct(rs.getTimestamp("ngay_ct").toLocalDateTime());

                // Lấy thông tin nhà cung cấp
                ncc.setMaNCC(rs.getString("ma_ncc"));
                // Lấy tên nhà cung cấp
                ncc.setTenNCC(rs.getString("ten_ncc"));
                pn.setNhaCC(ncc);

                // Lấy tổng tiền đã được SUM từ SQL
                pn.setTongTien(rs.getLong("tong_tien"));

                list.add(pn);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
