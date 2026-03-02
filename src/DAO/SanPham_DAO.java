package DAO;

import DataBase.DBConnection;
import Model.KeKho;
import Model.SanPham;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class SanPham_DAO {
    public ArrayList<SanPham> getAllSanPham(){
        ArrayList<SanPham> list = new ArrayList<>();
        String sql = "SELECT * FROM SAN_PHAM";
        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery();
        ) {
            while(rs.next()){
                SanPham sp = new SanPham();
                KeKho kk = new KeKho();
                sp.setMaSP(rs.getString("ma_sku"));
                sp.setTenSP(rs.getString("ten_sp"));
                sp.setDonViTinh(rs.getString("dvt"));
                sp.setSoLuong(rs.getInt("sl"));
                sp.setGiaTien(rs.getFloat("gia"));
                kk.setMaKe(rs.getString("ma_ke"));
                sp.setKeKho(kk);
                list.add(sp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    public ArrayList<SanPham> getSpByKey(String input){
        ArrayList<SanPham> list = new ArrayList<>();
        String sql = """
        SELECT *
        FROM san_pham
        WHERE LOWER(ma_sku) = LOWER(?)
           OR LOWER(ma_sku) LIKE LOWER(CONCAT('%', ?, '%'))
           OR LOWER(ten_sp) LIKE LOWER(CONCAT('%', ?, '%'))
        ORDER BY (LOWER(ma_sku) = LOWER(?)) DESC
    """;
        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
        ) {
            String keyword = "%" + input + "%";
            ps.setString(1, input);
            ps.setString(2, keyword);
            ps.setString(3, keyword);
            ps.setString(4,input);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                SanPham sp = new SanPham();
                KeKho kk = new KeKho();
                sp.setMaSP(rs.getString("ma_sku"));
                sp.setTenSP(rs.getString("ten_sp"));
                sp.setDonViTinh(rs.getString("dvt"));
                sp.setSoLuong(rs.getInt("sl"));
                sp.setGiaTien(rs.getFloat("gia"));
                kk.setMaKe(rs.getString("ma_ke"));
                sp.setKeKho(kk);
                list.add(sp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    public boolean insert(SanPham sp){
//        Trước khi thêm sản phẩm cần kiểm tra sức chứa ở kệ chứa sản phẩm nếu vượt mức sức chứa thì báo lỗi
        String sql ="INSERT INTO SAN_PHAM(ma_sku,ten_sp,dvt,sl,gia,ma_ke) VALUES (?, ?, ?,?,?,?)";
        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1,sp.getMaSP());
            ps.setString(2,sp.getTenSP());
            ps.setString(3,sp.getDonViTinh());
            ps.setInt(4,sp.getSoLuong());
            ps.setFloat(5,sp.getGiaTien());
            ps.setString(6,sp.getKeKho().getMaKe());
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean update(SanPham sp){
//        Nếu muốn thay đổi mã kệ,cần kiểm tra số sức chứa còn lại của kệ xem có chứa đc số lượng sản phẩm mới này ko?
        String sql =
                "UPDATE SAN_PHAM SET ten_sp=?,dvt=?,sl=?,gia=?,ma_ke=? WHERE ma_sku=?";
        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1,sp.getTenSP());
            ps.setString(2,sp.getDonViTinh());
            ps.setInt(3,sp.getSoLuong());
            ps.setFloat(4,sp.getGiaTien());
            ps.setString(5,sp.getKeKho().getMaKe());
            ps.setString(6,sp.getMaSP());
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean delete(String maSP){
        String sql = "DELETE FROM SAN_PHAM WHERE ma_sku=?";
        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, maSP);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public int countByMaKe(String maKe){ // trả về số lượng sản phẩm trong kệ
        String sql = "SELECT COUNT(*) FROM SAN_PHAM WHERE ma_ke = ?";
        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, maKe);
            ResultSet rs = ps.executeQuery();

            if(rs.next()){
                return rs.getInt(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public ArrayList<SanPham> laySanPhamTheoKe(String maKe){
        ArrayList<SanPham> list = new ArrayList<>();
        String sql = "SELECT * FROM SAN_PHAM WHERE ma_ke = ?";

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, maKe);
            ResultSet rs = ps.executeQuery();

            while(rs.next()){
                SanPham sp = new SanPham();
                KeKho kk = new KeKho();

                sp.setMaSP(rs.getString("ma_sku"));
                sp.setTenSP(rs.getString("ten_sp"));
                sp.setDonViTinh(rs.getString("dvt"));
                sp.setSoLuong(rs.getInt("sl"));
                sp.setGiaTien(rs.getFloat("gia"));

                kk.setMaKe(rs.getString("ma_ke"));
                sp.setKeKho(kk);

                list.add(sp);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

}
