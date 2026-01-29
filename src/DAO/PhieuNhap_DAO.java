package DAO;

import DataBase.DBConnection;
import Model.NhaCungCap;
import Model.NhanVien;
import Model.PhieuNhap;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class PhieuNhap_DAO {
    public ArrayList<PhieuNhap> getAllPhieuNhap(){
        ArrayList<PhieuNhap> list = new ArrayList<>();
        String sql = "SELECT * FROM PHIEU_NHAP";
        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery();
        ) {
            while(rs.next()){
                PhieuNhap pn =new PhieuNhap();
                NhanVien nv =new NhanVien();
                NhaCungCap ncc =new NhaCungCap();
                pn.setMaPN(rs.getString("ma_pn"));
                pn.setNgay_ct(
                        rs.getTimestamp("ngay_ct").toLocalDateTime()
                );
                ncc.setMaNCC(rs.getString("ma_ncc"));
                pn.setNhaCC(ncc);
                nv.setMaNV(rs.getString("ma_nhan_vien"));
                pn.setNhanVien(nv);
                list.add(pn);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
