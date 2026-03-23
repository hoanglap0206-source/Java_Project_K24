package BUS;

import DAO.PhieuNhap_DAO;
import DataBase.DBConnection;
import Model.ChiTiet_PhieuNhap;
import Model.PhieuNhap;
import Model.SanPham;
import Model.KeKho;

import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
public class PhieuNhap_BUS {
    private ArrayList<PhieuNhap> listPN;
    private PhieuNhap_DAO pnDAO;
    private ChiTietPN_BUS ctPN_Bus;
    private SanPham_BUS spBus;
    private KeKho_BUS kkBUS;

    public PhieuNhap_BUS(){
        pnDAO= new PhieuNhap_DAO();
        ctPN_Bus = new ChiTietPN_BUS();
        spBus = new SanPham_BUS();
        this.listPN= pnDAO.getAllPhieuNhap();
    }

    public ArrayList<PhieuNhap> getListPN(){
        return this.listPN;
    }

    public String addPN(PhieuNhap pn){
        //Kiểm tra thêm phiếu nhập có đúng định dạng không
        if(!Check.isValidPN(pn.getMaPN()))
            return "Mã phiểu nhập phải đúng định dạng (Phải là PNxx ví dụ PN01)";

        if(pnDAO.insert(pn))
        {
            listPN.add(pn);
            return"Thêm phiếu nhập thành công!";
        }

        return "Thêm phiếu nhập thất bại!";
    }

    public boolean insertPN(Connection conn, PhieuNhap pn, DefaultTableModel model) {

        if (!pnDAO.inSert(conn, pn)) {
            return false;
        }

        for (int i = 0; i < model.getRowCount(); i++) {

            PhieuNhap pN = new PhieuNhap();
            pN.setMaPN(pn.getMaPN());

            String maSP = model.getValueAt(i, 1).toString();
            SanPham sp = new SanPham();
            sp.setMaSP(maSP);

            int soLuong = Integer.parseInt(model.getValueAt(i, 3).toString());
            double donGia = Double.parseDouble(model.getValueAt(i, 5).toString());

            long thanhTien = (long) (soLuong * donGia);

            ChiTiet_PhieuNhap ctPN =
                    new ChiTiet_PhieuNhap(pN, sp, soLuong, donGia, thanhTien);

            if (!ctPN_Bus.addCTPN(conn, ctPN)) {
                return false;
            }
        }
        return true;
    }

    public boolean taoPhieuNhap(PhieuNhap pn, DefaultTableModel model) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

//            // KIỂM TRA SỨC CHỨA KỆ TRƯỚC KHI NHẬP
//            for (int i = 0; i < model.getRowCount(); i++) {
//                String maSP = model.getValueAt(i, 1).toString();
//                int soLuongNhap = Integer.parseInt(model.getValueAt(i, 3).toString());
//                String maKe = model.getValueAt(i, 6).toString(); // Giả sử cột 6 là mã kệ
//
//                KeKho ke = kkBUS.getKeTheoMa(maKe);
//                if (ke != null) {
//                    int tongHienTai = kkBUS.tinhTongSoLuongTheoKe(maKe);
//                    if (tongHienTai + soLuongNhap > ke.getSucChua()) {
//                        return false; // Trả về false, GUI sẽ hiển thị lỗi
//                    }
//                }
//            }
            if (!insertPN(conn, pn, model)) {
                throw new SQLException("Thêm PN thất bại");
            }

            if (!spBus.updateSPNhap(conn, model)) {
                throw new SQLException("Lỗi cập nhật sản phẩm");
            }

            conn.commit();
            return true;

        } catch (Exception e) {
            try {
                if (conn != null) conn.rollback();
            } catch (Exception ignored) {}
            return false;

        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (Exception ignored) {}
        }
    }

    public String updatePN(PhieuNhap pn){
        if(pnDAO.update(pn))
            return "Cập nhật thành công!";
        return "Cập nhật thất bại!";
    }

    public boolean deletePN(String maPN){
        if(maPN == null || maPN.trim().isEmpty())
            return false;
        if(pnDAO.delete(maPN)) {
            listPN.removeIf(bc -> bc.getMaPN().equalsIgnoreCase(maPN));
            // Làm mới cache chi tiết phiếu nhập
            ctPN_Bus.refeshData();
            return true;
        }
        return false;
    }
    public void refeshData(){this.listPN=pnDAO.getAllPhieuNhap();}

    //Lấy danh sách dùng cho TrangPhieuNhap
    public ArrayList<PhieuNhap> getListTongKet() {
        return pnDAO.getDanhSachPhieuNhapTongKet();
    }
}