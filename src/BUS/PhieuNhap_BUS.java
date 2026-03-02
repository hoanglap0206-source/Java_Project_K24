package BUS;

import DAO.PhieuNhap_DAO;
import Model.ChiTiet_PhieuNhap;
import Model.PhieuNhap;
import Model.SanPham;

import javax.swing.table.DefaultTableModel;
import java.util.*;
public class PhieuNhap_BUS {
    private ArrayList<PhieuNhap> listPN;
    private PhieuNhap_DAO pnDAO;
    private ChiTietPN_BUS ctPN_Bus;
    public PhieuNhap_BUS(){
        pnDAO= new PhieuNhap_DAO();
        ctPN_Bus = new ChiTietPN_BUS();
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

    public boolean insertPN(PhieuNhap pn, DefaultTableModel model){
        if(!pnDAO.insert(pn)){
            return false;
        }
        for (int i=0;i<model.getRowCount();i++){
            PhieuNhap pN = new PhieuNhap();
            pN.setMaPN(pn.getMaPN());

            String maSP = model.getValueAt(i,1).toString();
            SanPham sp = new SanPham();
            sp.setMaSP(maSP);

            int soLuong = Integer.parseInt(model.getValueAt(i,3).toString());

            double donGia = Double.parseDouble(model.getValueAt(i,5).toString());

            long thanhTien = 0;
            thanhTien += soLuong*donGia;

            ChiTiet_PhieuNhap ctPN = new ChiTiet_PhieuNhap(pN,sp,soLuong,donGia,thanhTien);
            String result = ctPN_Bus.addCTPN(ctPN);
            if (!result.equals("Thêm chi tiết phiếu nhập thành công!")) {
                return false;
            }
        }return true;
    }
    public String updatePN(PhieuNhap pn){
        if(pnDAO.update(pn))
            return "Cập nhật thành công!";
        return "Cập nhật thất bại!";
    }

    public String deletePN(String maPN){
        if(maPN==null||maPN.trim().isEmpty())
            return "Mã phiếu nhập không hợp lệ!";
        if(pnDAO.delete(maPN))
        {
            listPN.removeIf(bc->bc.getMaPN().equalsIgnoreCase(maPN));
            return "Xoá phiếu nhập thành công!";
        }
        return "Xoá phiếu nhập thất bại!";
    }
    public void refeshData(){this.listPN=pnDAO.getAllPhieuNhap();}
}
