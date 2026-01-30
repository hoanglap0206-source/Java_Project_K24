package DAO;

import DataBase.DBConnection;
import Model.BaoCaoTonKho;
import Model.SanPham;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class BaoCaoTonKho_DAO {
    public ArrayList<BaoCaoTonKho> getAllBaoCao(){
        ArrayList<BaoCaoTonKho> list =new ArrayList<>();
        String sql =
                "SELECT * FROM BAO_CAO_TON_KHO";
        try(
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery();
        ){
            while ((rs.next())){
                BaoCaoTonKho bc =new BaoCaoTonKho();
                SanPham sp =new SanPham();
                bc.setMaBC(rs.getString("ma_bc"));
                bc.setsLTon(rs.getInt("ton"));
                bc.setCanhBaoHH(rs.getInt("canh_bao_hh"));
                sp.setMaSP(rs.getString("ma_sku"));
                bc.setSanPham(sp);
                list.add(bc);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    public boolean insert(BaoCaoTonKho bc){
        String sql=
                "INSERT INTO BAO_CAO_TON_KHO(ma_bc,ton,canh_bao_hh,ma_sku) VALUES (?, ?, ?,?)";
        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1,bc.getMaBC());
            ps.setInt(2,bc.getsLTon());
            ps.setInt(3,bc.getCanhBaoHH());
            ps.setString(4,bc.getSanPham().getMaSP());
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean update(BaoCaoTonKho bc){
        String sql=
                "UPDATE BAO_CAO_TON_KHO SET ton=?,canh_bao_hh=?,ma_sku=? WHERE ma_bc=?";
        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setInt(1,bc.getsLTon());
            ps.setInt(2,bc.getCanhBaoHH());
            ps.setString(3,bc.getSanPham().getMaSP());
            ps.setString(4,bc.getMaBC());
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean delete(String maBC){
        String sql=
                "DELETE FROM BAO_CAO_TON_KHO WHERE ma_bc=?";
        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1,maBC);
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
