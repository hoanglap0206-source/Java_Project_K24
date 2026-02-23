package DAO;

import DataBase.DBConnection;
import Model.NhanVien;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class NV_DAO {
    public ArrayList<NhanVien> getAllNV(){
        ArrayList<NhanVien> list = new ArrayList<>();
        String sql = "SELECT * FROM NHAN_VIEN";
        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery();
        ) {
            while(rs.next()){
                NhanVien nv = new NhanVien();
                nv.setMaNV(rs.getString("ma_nhan_vien"));
                nv.setHoTen(rs.getString("ho_ten"));
                nv.setMatKhau(rs.getString("mat_khau"));
                nv.setChucVu(rs.getString("chuc_vu"));
                nv.setTrangThai(rs.getString("trang_thai"));
                list.add(nv);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    public boolean insert(NhanVien nv){
        String sql ="INSERT INTO NHAN_VIEN(ma_nhan_vien,ho_ten,mat_khau,chuc_vu,trang_thai) VALUES (?, ?, ?,?,?)";
        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1,nv.getMaNV());
            ps.setString(2,nv.getHoTen());
            ps.setString(3,nv.getMatKhau());
            ps.setString(4,nv.getChucVu());
            ps.setString(5,nv.getTrangThai());
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean update(NhanVien nv){
//        Hàm này dựa vào id của nhân viên để sửa, bên xử lý sự kiện cần tạo ra nhân viên và lưu
//  các thuộc tính,bao gồm ma_nhan_vien khi click vào dòng chứa nhân viên đó (MouseListener)
//  lưu ý: không nên sửa đổi mã nhân viên. hàm xóa cũng tương tự
        String sql =
                "UPDATE NHAN_VIEN SET ho_ten=?,mat_khau=?,chuc_vu=?,trang_thai=? WHERE ma_nhan_vien=?";
        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1,nv.getHoTen());
            ps.setString(2,nv.getMatKhau());
            ps.setString(3,nv.getChucVu());
            ps.setString(4,nv.getTrangThai());
            ps.setString(5,nv.getMaNV());
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean delete(String maNv){
        String sql = "DELETE FROM NHAN_VIEN WHERE ma_nhan_vien=?";
        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, maNv);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        //Hello GITHUB
    }
    public ArrayList<NhanVien> getAccoount(){
        String sql =" SELECT ma_nhan_vien,mat_khau FROM NHAN_VIEN";
        ArrayList<NhanVien> list = new ArrayList<>();
        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery();
        ) {
            while(rs.next()){
                NhanVien nv = new NhanVien();
                nv.setMaNV(rs.getString("ma_nhan_vien"));
                nv.setMatKhau(rs.getString("mat_khau"));
                list.add(nv);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public String getTenNV_DAO(String maNV){
        String sql = "SELECT ho_ten FROM NHAN_VIEN Where ma_nhan_vien = ?";
        try(Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ){
            ps.setString(1, maNV);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                return rs.getString("ho_ten");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    //Lấy thông tin nhân viên từ mã
    public ArrayList<NhanVien> getInfo_NV_DAO(String maNV){
        String sql = "SELECT ma_nhan_vien, ho_ten, sdt, chuc_vu FROM NHAN_VIEN WHERE ma_nhan_vien = ?";
        ArrayList<NhanVien> list = new ArrayList<>();

        try( Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql))
        {
            ps.setString(1, maNV);

            try(ResultSet rs = ps.executeQuery()){
                while(rs.next()){
                    NhanVien nv = new NhanVien();
                    nv.setMaNV(rs.getString("ma_nhan_vien"));
                    nv.setHoTen(rs.getString("ho_ten"));
                    nv.setSDT(rs.getString("sdt"));
                    nv.setChucVu(rs.getString("chuc_vu"));

                    list.add(nv);
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
