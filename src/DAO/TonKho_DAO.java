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
                "sp.ma_sku, sp.ten_sp, sp.dvt, sp.sl, sp.gia, sp.ma_ke " +
                "FROM bao_cao_ton_kho tk JOIN SAN_PHAM sp ON tk.ma_bc = sp.ma_sku";

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
                // ĐÃ SỬA: Đổi "ma_ton_kho" thành "ma_bc" cho khớp với lệnh SELECT
                tk.setMaBC(rs.getString("ma_bc"));
                tk.setsLTon(rs.getInt("sl"));
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
        // Vì dữ liệu nằm ở 2 bảng, ta cần 2 lệnh INSERT
        String sqlSP = "INSERT INTO SAN_PHAM (ma_sku, ten_sp, dvt, sl, gia, ma_ke) VALUES (?, ?, ?, ?, ?, ?)";
        String sqlBC = "INSERT INTO bao_cao_ton_kho (ma_bc, ton, canh_bao_hh) VALUES (?, ?, ?)";

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            // Tắt auto commit để đảm bảo phải chèn thành công cả 2 bảng mới lưu
            conn.setAutoCommit(false);

            // Chèn vào bảng SAN_PHAM trước
            try (PreparedStatement psSP = conn.prepareStatement(sqlSP)) {
                psSP.setString(1, bc.getSanPham().getMaSP());
                psSP.setString(2, bc.getSanPham().getTenSP());
                psSP.setString(3, bc.getSanPham().getDonViTinh());
                psSP.setInt(4, bc.getsLTon()); // Lưu số lượng
                psSP.setFloat(5, bc.getSanPham().getGiaTien());
                psSP.setString(6, bc.getSanPham().getMaKe());

                psSP.executeUpdate();
            }

            // Chèn vào bảng bao_cao_ton_kho
            try (PreparedStatement psBC = conn.prepareStatement(sqlBC)) {
                psBC.setString(1, bc.getMaTonKho());
                psBC.setInt(2, bc.getsLTon());
                psBC.setInt(3, bc.getCanhBaoHH());
                psBC.executeUpdate();
            }

            conn.commit(); // Lưu chính thức vào CSDL
            ketQua = true;

        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback(); // Nếu lỗi thì hoàn tác lại toàn bộ
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

    // 3. HÀM CẬP NHẬT (UPDATE)
    public boolean update(BaoCaoTonKho bc) {
        boolean ketQua = false;
        // Cập nhật cả 2 bảng để đồng bộ
        String sqlSP = "UPDATE SAN_PHAM SET ten_sp = ?, dvt = ?, sl = ?, gia = ?, ma_ke = ? WHERE ma_sku = ?";
        String sqlBC = "UPDATE bao_cao_ton_kho SET ton = ?, canh_bao_hh = ? WHERE ma_bc = ?";

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            // Cập nhật SAN_PHAM
            try (PreparedStatement psSP = conn.prepareStatement(sqlSP)) {
                psSP.setString(1, bc.getSanPham().getTenSP());
                psSP.setString(2, bc.getSanPham().getDonViTinh());
                psSP.setInt(3, bc.getsLTon());
                psSP.setFloat(4, bc.getSanPham().getGiaTien());
               psSP.setString(5, bc.getSanPham().getMaKe());
                psSP.setString(6, bc.getMaTonKho()); // Khóa WHERE
                psSP.executeUpdate();
            }

            // Cập nhật bao_cao_ton_kho
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

    // 4. HÀM XÓA (DELETE)
    public boolean delete(String maBC) {
        boolean ketQua = false;
        // Chú ý: Cần xóa khóa phụ (bảng bao_cao_ton_kho) trước, sau đó mới xóa bảng chính (SAN_PHAM)
        String sqlBC = "DELETE FROM bao_cao_ton_kho WHERE ma_bc = ?";
        String sqlSP = "DELETE FROM SAN_PHAM WHERE ma_sku = ?";

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            // Xóa ở bao_cao_ton_kho trước
            try (PreparedStatement psBC = conn.prepareStatement(sqlBC)) {
                psBC.setString(1, maBC);
                psBC.executeUpdate();
            }

            // Xóa ở SAN_PHAM sau
            try (PreparedStatement psSP = conn.prepareStatement(sqlSP)) {
                psSP.setString(1, maBC);
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
            System.err.println("Lỗi Delete: " + e.getMessage());
        } finally {
            try {
                if (conn != null) conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return ketQua;
    }
}