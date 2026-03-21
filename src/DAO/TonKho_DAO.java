package DAO;

import DataBase.DBConnection;
import Model.BaoCaoTonKho;
import Model.ChiTietKe;
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

        String sql = "SELECT tk.ma_bc, tk.canh_bao_hh, " +
                "sp.ma_sku, sp.ten_sp, sp.dvt, sp.gia, " +
                "GROUP_CONCAT(DISTINCT ctk.ma_ke SEPARATOR ',') AS danh_sach_ke, "+
                "SUM(ctk.so_luong) AS tong_ton "+
                "FROM bao_cao_ton_kho tk " +
                "JOIN SAN_PHAM sp ON tk.ma_sku = sp.ma_sku " +
                "LEFT JOIN chitiet_ke ctk ON sp.ma_sku=ctk.ma_sku "+
                "WHERE sp.trang_thai = 1 "+
                "GROUP BY sp.ma_sku, tk.ma_bc, tk.canh_bao_hh, sp.ten_sp, sp.dvt, sp.gia";

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

                String dsmaKe=rs.getString("danh_sach_ke");
                sp.setMaKe(dsmaKe!=null? dsmaKe:"Chưa xếp kệ");


                BaoCaoTonKho tk = new BaoCaoTonKho();

                tk.setMaBC(rs.getString("ma_bc"));

                tk.setCanhBaoHH(rs.getInt("canh_bao_hh"));
                tk.setsLTon(rs.getInt("tong_ton"));

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
// 1. Ghi vào bảng báo cáo (Chỉ lấy cảnh báo)
        String sqlBC = "INSERT INTO bao_cao_ton_kho (ma_bc, canh_bao_hh, ma_sku) VALUES (?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE canh_bao_hh = ?";
        // 2. Ghi Số lượng và Mã kệ vào bảng trung gian (Dùng UPSERT để lỡ Kệ đó có hàng rồi thì cộng dồn hoặc ghi đè)
        String sqlCTK = "INSERT INTO chitiet_ke (ma_ke, ma_sku, so_luong) VALUES (?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE so_luong = ?";


        String sqlSP = "UPDATE san_pham SET trang_thai = 1 WHERE ma_sku = ?";

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();

            conn.setAutoCommit(false);
            try (PreparedStatement psBC = conn.prepareStatement(sqlBC)) {

                psBC.setString(1, bc.getMaTonKho());
                psBC.setInt(2, bc.getCanhBaoHH());
                psBC.setString(3,bc.getSanPham().getMaSP());
                psBC.setInt(4, bc.getCanhBaoHH());




                psBC.executeUpdate();
            }
            try (PreparedStatement psCTK = conn.prepareStatement(sqlCTK)) {
                psCTK.setString(1, bc.getSanPham().getMaKe());
                psCTK.setString(2, bc.getSanPham().getMaSP());
                psCTK.setInt(3, bc.getsLTon());
                psCTK.setInt(4, bc.getsLTon()); // Cho phần ON DUPLICATE
                psCTK.executeUpdate();
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

        String sqlBC="UPDATE bao_cao_ton_kho SET canh_bao_hh=? WHERE ma_bc=?";
        String sqlCTK="UPDATE chitiet_ke SET so_luong=?  WHERE ma_sku=? AND ma_ke=?";

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);


           try(PreparedStatement psBC= conn.prepareStatement(sqlBC)){
               psBC.setInt(1,bc.getCanhBaoHH());
               psBC.setString(2,bc.getMaTonKho());
               psBC.executeUpdate();
           }
           try(PreparedStatement psCTK=conn.prepareStatement(sqlCTK)){
               psCTK.setInt(1, bc.getsLTon());
               psCTK.setString(2,bc.getSanPham().getMaSP());
               psCTK.setString(3,bc.getSanPham().getMaKe());
               psCTK.executeUpdate();
           }

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