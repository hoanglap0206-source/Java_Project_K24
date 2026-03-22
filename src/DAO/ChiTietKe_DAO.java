package DAO;

import DataBase.DBConnection;
import Model.ChiTietKe;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class ChiTietKe_DAO {
    public boolean insertOrUpdate(ChiTietKe ct) {
        String sql = "INSERT INTO CHITIET_KE (ma_ke, ma_sku, so_luong) " +
                "VALUES (?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE so_luong = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ct.getMaKe());
            ps.setString(2, ct.getMaSP());
            ps.setInt(3, ct.getSoLuong());
            ps.setInt(4, ct.getSoLuong());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public ArrayList<ChiTietKe> getByMaKe(String maKe) {
        ArrayList<ChiTietKe> list = new ArrayList<>();
        String sql = "SELECT ma_ke, ma_sku, so_luong FROM CHITIET_KE WHERE ma_ke=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maKe);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ChiTietKe ct = new ChiTietKe(
                        rs.getString("ma_ke"),
                        rs.getString("ma_sku"),
                        rs.getInt("so_luong")
                );
                list.add(ct);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public ArrayList<ChiTietKe> getByMaSP(String maSP) {
        ArrayList<ChiTietKe> list = new ArrayList<>();
        String sql = "SELECT ma_ke, so_luong FROM CHITIET_KE WHERE ma_sku=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maSP);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                ChiTietKe ct = new ChiTietKe(
                        rs.getString("ma_ke"),
                        maSP,
                        rs.getInt("so_luong")
                );
                list.add(ct);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public ArrayList<ChiTietKe> getAll() {
        ArrayList<ChiTietKe> list = new ArrayList<>();
        String sql = "SELECT ma_ke, ma_sku, so_luong FROM CHITIET_KE";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new ChiTietKe(
                        rs.getString("ma_ke"),
                        rs.getString("ma_sku"),
                        rs.getInt("so_luong")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean deleteByMaKe(String maKe) {
        String sql = "DELETE FROM CHITIET_KE WHERE ma_ke=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maKe);
            return ps.executeUpdate() >= 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(String maKe, String maSP) {
        String sql = "DELETE FROM CHITIET_KE WHERE ma_ke=? AND ma_sku=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maKe);
            ps.setString(2, maSP);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Lấy tổng số lượng của sản phẩm (tổng từ tất cả các kệ)
    public int getTongSoLuongByMaSP(String maSP) {
        int tong = 0;
        String sql = "SELECT SUM(so_luong) FROM CHITIET_KE WHERE ma_sku=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maSP);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                tong = rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tong;
    }
}