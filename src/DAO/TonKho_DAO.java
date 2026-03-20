package DAO;

import DataBase.DBConnection;
import Model.BaoCaoTonKho;
import Model.SanPham;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class TonKho_DAO {

    // 1. HÀM LẤY DANH SÁCH
    public ArrayList<BaoCaoTonKho> getDanhSachTonKho() {
        ArrayList<BaoCaoTonKho> list = new ArrayList<>();

        String sql = "SELECT tk.ma_bc, tk.ton, tk.canh_bao_hh, " +
                "sp.ma_sku, sp.ten_sp, sp.dvt, sp.gia, sp.ma_ke " +
                "FROM bao_cao_ton_kho tk " +
                "JOIN SAN_PHAM sp ON tk.ma_sku = sp.ma_sku " +
                "WHERE sp.trang_thai = 1";

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery();
        ) {
            while (rs.next()) {
                SanPham sp = new SanPham();
                sp.setMaSP(rs.getString("ma_sku"));
                sp.setTenSP(rs.getString("ten_sp"));
                sp.setDonViTinh(rs.getString("dvt"));
                sp.setGiaTien(rs.getFloat("gia"));
                sp.setMaKe(rs.getString("ma_ke"));


                BaoCaoTonKho tk = new BaoCaoTonKho();

                tk.setMaBC(rs.getString("ma_bc"));
                tk.setsLTon(rs.getInt("ton"));
                tk.setCanhBaoHH(rs.getInt("canh_bao_hh"));

                tk.setSanPham(sp);
                list.add(tk);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi lấy dữ liệu tồn kho: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    // 2. HÀM THÊM MỚI (INSERT)
    public boolean insert(BaoCaoTonKho bc) {
        boolean ketQua = false;

        String sqlBC = "INSERT INTO bao_cao_ton_kho (ma_bc, ton, canh_bao_hh, ma_sku) VALUES (?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE ton = ?, canh_bao_hh = ?";

        String sqlSP = "UPDATE san_pham SET trang_thai = 1 WHERE ma_sku = ?";

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();

            conn.setAutoCommit(false);
            try (PreparedStatement psBC = conn.prepareStatement(sqlBC)) {
                // Tham số cho phần INSERT
                psBC.setString(1, bc.getMaTonKho());
                psBC.setInt(2, bc.getsLTon());
                psBC.setInt(3, bc.getCanhBaoHH());
                psBC.setString(4, bc.getSanPham().getMaSP());

                // Tham số cho phần ON DUPLICATE KEY UPDATE
                psBC.setInt(5, bc.getsLTon());
                psBC.setInt(6, bc.getCanhBaoHH());

                psBC.executeUpdate();
            }




            try (PreparedStatement psSP = conn.prepareStatement(sqlSP)) {
                psSP.setString(1, bc.getSanPham().getMaSP());
                psSP.executeUpdate();
            }

            conn.commit();
            ketQua = true;

        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            System.err.println("Lỗi Insert: " + e.getMessage());
        } finally {
            try {
                if (conn != null) conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return ketQua;
    }


    public boolean update(BaoCaoTonKho bc) {
        boolean ketQua = false;

        String sqlSP = "UPDATE SAN_PHAM SET ten_sp = ?, dvt = ?, sl = ?, gia = ?, ma_ke = ? WHERE ma_sku = ?";
        String sqlBC = "UPDATE bao_cao_ton_kho SET ton = ?, canh_bao_hh = ? WHERE ma_bc = ?";

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);


            try (PreparedStatement psSP = conn.prepareStatement(sqlSP)) {
                psSP.setString(1, bc.getSanPham().getTenSP());
                psSP.setString(2, bc.getSanPham().getDonViTinh());
                psSP.setInt(3, bc.getsLTon());
                psSP.setFloat(4, bc.getSanPham().getGiaTien());
               psSP.setString(5, bc.getSanPham().getMaKe());
                psSP.setString(6, bc.getMaTonKho()); // Khóa WHERE
                psSP.executeUpdate();
            }


            try (PreparedStatement psBC = conn.prepareStatement(sqlBC)) {
                psBC.setInt(1, bc.getsLTon());
                psBC.setInt(2, bc.getCanhBaoHH());
                psBC.setString(3, bc.getMaTonKho()); // Khóa WHERE
                psBC.executeUpdate();
            }

            conn.commit();
            ketQua = true;

        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            System.err.println("Lỗi Update: " + e.getMessage());
        } finally {
            try {
                if (conn != null) conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return ketQua;
    }


    public boolean delete(String maSP) {
        boolean ketQua = false;

        // --- ĐÃ SỬA: Chuyển sang lệnh UPDATE để ẩn sản phẩm ---
        String sql = "UPDATE san_pham SET trang_thai = 0 WHERE ma_sku = ?";
        // -----------------------------------------------------

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maSP);

            int rows = ps.executeUpdate();
            if (rows > 0) ketQua = true;

        } catch (SQLException e) {
            System.err.println("Lỗi Xóa ảo Tồn Kho: " + e.getMessage());
            e.printStackTrace();
        }
        return ketQua;
    }
}