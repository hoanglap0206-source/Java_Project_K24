package DAO;

import DataBase.DBConnection;
import Model.ChucNang;
import Model.NhanVien;
import Model.PhanQuyen;

import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class PhanQuyen_DAO{
    public ArrayList<PhanQuyen> getAllPhanQuyen(){
        ArrayList<PhanQuyen> list =new ArrayList<>();
        String sql=
                "SELECT * FROM PHAN_QUYEN";
        try(
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery();
        ){
            while ((rs.next())){
                NhanVien nv =new NhanVien();
                ChucNang cn =new ChucNang();
                PhanQuyen pq =new PhanQuyen();
                nv.setMaNV(rs.getString("ma_nhan_vien"));
                cn.setMaCN(rs.getString("ma_chuc_nang"));
                pq.setNhanVien(nv);
                pq.setChucNang(cn);
                pq.setXem(rs.getBoolean("duoc_xem"));
                pq.setXoa(rs.getBoolean("duoc_xoa"));
                pq.setSua(rs.getBoolean("duoc_sua"));
                pq.setThem(rs.getBoolean("duoc_them"));
                list.add(pq);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    public boolean insert(PhanQuyen pq){
        String sql=
                "INSERT INTO PHAN_QUYEN(ma_nhan_vien,ma_chuc_nang,duoc_xem,duoc_xoa,duoc_sua,duoc_them) VALUES (?, ?,?,?,?,?)";
        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1,pq.getNhanVien().getMaNV());
            ps.setString(2,pq.getChucNang().getMaCN());
            ps.setBoolean(3,pq.isXem());
            ps.setBoolean(4,pq.isXoa());
            ps.setBoolean(5,pq.isSua());
            ps.setBoolean(6,pq.isThem());
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean update(PhanQuyen pq){
        String sql=
                "UPDATE PHAN_QUYEN SET duoc_xem=?,duoc_xoa=?,duoc_sua=?,duoc_them=? WHERE ma_chuc_nang=? AND ma_nhan_vien=?";
        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setBoolean(1,pq.isXem());
            ps.setBoolean(2,pq.isXoa());
            ps.setBoolean(3,pq.isSua());
            ps.setBoolean(4,pq.isThem());
            ps.setString(5,pq.getChucNang().getMaCN());
            ps.setString(6,pq.getNhanVien().getMaNV());
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean delete(String maNV,String maCN){
        String sql=
                "DELETE FROM PHAN_QUYEN WHERE ma_nhan_vien=? AND ma_chuc_nang=?";
        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1,maNV);
            ps.setString(2,maCN);
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public ArrayList<PhanQuyen> getListQuyenCaNhan(String maNhanVien){
        ArrayList<PhanQuyen> ListPhanQuyen = new ArrayList<>();

        //Lệnh này sẽ kết bảng PHAN_QUYEN và bảng DM_CHUC_NANG lại và add vào danh sach PhanQuyen
        String sql = "SELECT pq.ma_nhan_vien, pq.ma_chuc_nang, cn.ten_chuc_nang, pq.duoc_xem, pq.duoc_xoa, pq.duoc_sua, pq.duoc_them " +
                "FROM PHAN_QUYEN pq " +
                "JOIN DM_CHUC_NANG cn ON pq.ma_chuc_nang = cn.ma_chuc_nang " +
                "WHERE pq.ma_nhan_vien = ? "+
                "ORDER BY pq.ma_chuc_nang ASC";
        try(Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){

            ps.setString(1, maNhanVien);

            try(ResultSet rs = ps.executeQuery()){
                while(rs.next()){
                    NhanVien nv = new NhanVien();
                    nv.setMaNV(rs.getString("ma_nhan_vien")); //Lấy mã nhân viên

                    ChucNang cn = new ChucNang();
                    cn.setMaCN(rs.getString("ma_chuc_nang")); //Lấy mã chức năng
                    cn.setTenCN(rs.getString("ten_chuc_nang"));//Lấy tên chức năng

                    boolean xem = rs.getBoolean("duoc_xem");
                    boolean xoa = rs.getBoolean("duoc_xoa");
                    boolean sua = rs.getBoolean("duoc_sua");
                    boolean them = rs.getBoolean("duoc_them");

                    PhanQuyen pq = new PhanQuyen(nv, cn, xem, xoa, sua, them);

                    ListPhanQuyen.add(pq);
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return ListPhanQuyen;
    }

    // Ktra xem các button có trong sql hay chưa
    public boolean checkExists (String maNv, String maCN){
        String sql = "SELECT 1 FROM phan_quyen WHERE ma_nhan_vien = ? AND ma_chuc_nang = ?";
        try(Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){

            ps.setString(1, maNv);
            ps.setString(2, maCN);

            try(ResultSet rs = ps.executeQuery()){
                return rs.next();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }

    // Lấy tất cả chức năng có trong sql và quyền của 1 người
    public ArrayList<PhanQuyen> getAllChucNang_QuyenCuaNV(String maNV){
        ArrayList<PhanQuyen> list = new ArrayList<>();

        String sql = "SELECT cn.ma_chuc_nang, cn.ten_chuc_nang, " +
                "IFNULL(pq.duoc_xem, 0) as duoc_xem, " +
                "IFNULL(pq.duoc_xoa, 0) as duoc_xoa, " +
                "IFNULL(pq.duoc_sua, 0) as duoc_sua, " +
                "IFNULL(pq.duoc_them, 0) as duoc_them " +
                "FROM DM_CHUC_NANG cn " +
                "LEFT JOIN PHAN_QUYEN pq ON cn.ma_chuc_nang = pq.ma_chuc_nang AND pq.ma_nhan_vien = ? " +
                "ORDER BY cn.ma_chuc_nang ASC";

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){

            ps.setString(1, maNV);

            try (ResultSet rs = ps.executeQuery()){
                while (rs.next()){
                    NhanVien nv = new NhanVien();
                    nv.setMaNV(maNV);

                    ChucNang cn = new ChucNang();
                    cn.setMaCN(rs.getString("ma_chuc_nang"));
                    cn.setTenCN(rs.getString("ten_chuc_nang"));

                    boolean xem = rs.getBoolean("duoc_xem");
                    boolean xoa = rs.getBoolean("duoc_xoa");
                    boolean sua = rs.getBoolean("duoc_sua");
                    boolean them = rs.getBoolean("duoc_them");

                    list.add(new PhanQuyen(nv, cn, xem, xoa, sua, them));
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return list;
    }
}