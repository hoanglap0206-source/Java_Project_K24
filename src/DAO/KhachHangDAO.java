package DAO;

import DataBase.DBConnection;
import Model.KhachHang;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class KhachHangDAO extends Component {
    public ArrayList<KhachHang> getAllKhachHang(){
        ArrayList<KhachHang> list = new ArrayList<>();
        String sql = "SELECT ma_kh, ten_kh, dia_chi, sdt, chi_tieu FROM KHACH_HANG ORDER BY ma_kh ASC";
        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery();
        ) {
            while(rs.next()){
                KhachHang kh = new KhachHang();
                kh.setMaKH(rs.getString("ma_kh"));
                kh.setHoTenKH(rs.getString("ten_kh"));
                kh.setDiaChi(rs.getString("dia_chi"));
                kh.setSdt(rs.getString("sdt"));
                double chiTieu=rs.getDouble("chi_tieu");
                kh.setCT(String.valueOf(chiTieu));
                list.add(kh);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    public void DongBoChiTieu() {
        // Câu lệnh SQL này sẽ tính tổng thực tế từ hóa đơn và cập nhật vào cột chi_tieu
        // chỉ khi con số đó khác với con số hiện tại trong bảng KHACH_HANG.
        String sqlUpdate = "UPDATE KHACH_HANG kh " +
                "JOIN ( " +
                "    SELECT kh.ma_kh, CAST(SUM(ct.thanh_tien * (1 + IFNULL(ct.thue_vat, 0)))AS SIGNED) as thuc_te " +
                "    FROM KHACH_HANG kh " +
                "    LEFT JOIN PHIEU_XUAT px ON kh.ma_kh = px.ma_kh " +
                "    LEFT JOIN CHITIET_PHIEU_XUAT ct ON px.ma_px = ct.ma_px " +
                "    GROUP BY kh.ma_kh " +
                ") t ON kh.ma_kh = t.ma_kh " +
                "SET kh.chi_tieu = IFNULL(t.thuc_te, 0) " +
                "WHERE ABS(IFNULL(kh.chi_tieu, 0) - IFNULL(t.thuc_te, 0)) > 0.001";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sqlUpdate)) {
            int rows = ps.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this," He thong da tu dong cap nhat chi tieu cho " + rows + " khach hang.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public boolean insert(KhachHang kh){
        String sql ="INSERT INTO KHACH_HANG(ma_kh,ten_kh,dia_chi,sdt,chi_tieu) VALUES (?, ?, ?,?,?)";
        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ){
            ps.setString(1,kh.getMaKH());
            ps.setString(2,kh.getHoTenKH());
            ps.setString(3,kh.getDiaChi());
            ps.setString(4,kh.getSdt());
            ps.setString(5,kh.getCT());
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean update(KhachHang kh){
        String sql =
                "UPDATE KHACH_HANG SET ten_kh=?,dia_chi=?,sdt=?,chi_tieu=? WHERE ma_kh=?";
        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ){
            ps.setString(1,kh.getHoTenKH());
            ps.setString(2,kh.getDiaChi());
            ps.setString(3,kh.getSdt());
            double ct = 0;
            try { ct = Double.parseDouble(kh.getCT()); } catch (Exception e) {}
            ps.setDouble(4, Double.parseDouble(kh.getCT())); // Vẫn gửi chi tiêu hiện tại
            ps.setString(5, kh.getMaKH());
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean delete(String maKH){
        String sql = "DELETE FROM KHACH_HANG WHERE ma_kh=?";
        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, maKH);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public String getNextMaKH(){
        String sql="SELECT ma_kh FROM KHACH_HANG ORDER BY ma_kh DESC LIMIT 1";
        try(Connection conn= DBConnection.getConnection();
            PreparedStatement ps=  conn.prepareStatement(sql);
            ResultSet rs= ps.executeQuery()){
            if(rs.next()){
                String lastMa= rs.getString("ma_kh");
                int number = Integer.parseInt(lastMa.substring(2));
                return String.format("KH%010d",number);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return "KH1";
    }


}