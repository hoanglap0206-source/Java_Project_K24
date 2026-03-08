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

    public ArrayList<BaoCaoTonKho> getDanhSachTonKho() {
        ArrayList<BaoCaoTonKho> list = new ArrayList<>();
        String sql = "SELECT tk.ma_ton_kho, tk.so_luong_ton, tk.canh_bao_hh, " +
                "sp.ma_sku, sp.ten_sp, sp.dvt " +
                "FROM TON_KHO tk JOIN SAN_PHAM sp ON tk.ma_sp = sp.ma_sku";

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery();
        ) {
            while (rs.next()) {
                SanPham sp = new SanPham();
                sp.setMaSP(rs.getString("ma_sp"));
                sp.setTenSP(rs.getString("ten_sp"));
                sp.setDonViTinh(rs.getString("don_vi_tinh"));

                BaoCaoTonKho tk = new BaoCaoTonKho();
                tk.setMaBC(rs.getString("ma_ton_kho"));
                tk.setsLTon(rs.getInt("so_luong_ton"));
                tk.setCanhBaoHH(rs.getInt("canh_bao_hh"));

                tk.setSanPham(sp);
                list.add(tk);
            }
        } catch (SQLException e) { // SỬA Ở ĐÂY: Phải là SQLException
            System.err.println("Lỗi lấy dữ liệu tồn kho: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }
}