package BUS;

import DAO.PhieuNhap_DAO;
import Model.PhieuNhap;
import java.util.*;
public class PhieuNhap_BUS {
    private ArrayList<PhieuNhap> listPN;
    private PhieuNhap_DAO pnDAO;
    public PhieuNhap_BUS(){
        pnDAO= new PhieuNhap_DAO();
        this.listPN= pnDAO.getAllPhieuNhap();
    }

    public ArrayList<PhieuNhap> getListPN(){
        return this.listPN;
    }

    public String addPN(PhieuNhap pn){
        if(pn.getMaPN().trim().isEmpty()) return "Mã phiếu nhập không được để trống!";
        if(pnDAO.insert(pn))
        {
            listPN.add(pn);
            return"Thêm phiếu nhập thành công!";
        }
        return "Thêm phiếu nhập thất bại!";
    }

    public String updatePN(PhieuNhap pn){
        if(pnDAO.update(pn)) return "Cập nhật thành công!";
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
