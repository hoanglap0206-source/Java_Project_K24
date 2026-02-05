package BUS;

import DAO.ChiTietPN_DAO;
import Model.ChiTiet_PhieuNhap;
import java.util.*;
public class ChiTietPN_BUS {
    private ArrayList<ChiTiet_PhieuNhap> listPN;
    private ChiTietPN_DAO CTPNDAO;

    public ChiTietPN_BUS(){
        CTPNDAO= new ChiTietPN_DAO();
        this.listPN=CTPNDAO.getAllCtPN();
    }

    public ArrayList<ChiTiet_PhieuNhap> getListPN(String maPN){
        ArrayList<ChiTiet_PhieuNhap> result = new ArrayList<>();
        for(ChiTiet_PhieuNhap CT: listPN){
            if(CT.getPhieuNhap().getMaPN().equalsIgnoreCase(maPN))
                result.add(CT);
        }
        return result;
    }

    public String addCTPN(ChiTiet_PhieuNhap CTPN){
        if(CTPN.getSoLuong()<=0) return "Số Lượng phải lớn hơn 0";
        for(ChiTiet_PhieuNhap items:listPN){
            if(items.getPhieuNhap().getMaPN().equals(CTPN.getPhieuNhap().getMaPN())
            || items.getSanPham().getMaSP().equals(CTPN.getSanPham().getMaSP()))
                return"Sản phẩm này đã có trong phiếu nhập.Hãy dùng chức năng cập nhật số lương!";
        }
        if(CTPNDAO.insert(CTPN)){
            listPN.add(CTPN);
            return"thêm phiếu nhập thành công!";
        }
        return"Thêm phiếu nhập thất bại!";
    }

    public String updateCTPN(ChiTiet_PhieuNhap ct){
        if(CTPNDAO.insert(ct))
        {
            for(int i=0;i<listPN.size();i++){
                ChiTiet_PhieuNhap item= listPN.get(i);
                if(item.getPhieuNhap().getMaPN().equals(ct.getPhieuNhap().getMaPN())
                || item.getSanPham().getMaSP().equals(ct.getSanPham().getMaSP())) {
                    listPN.set(i, ct);
                    break;
                }
            }
            return "cập nhật chi tiết thành công!";
        }
        return "Cập nhật thất bai!";
    }

    public String deleteCTPN(String maPN,String maSP){
        if(CTPNDAO.delete(maPN,maSP))
        {
            listPN.removeIf(bc->bc.getPhieuNhap().getMaPN().equals(maPN)
            && bc.getSanPham().getMaSP().equals(maSP));
            return "Đã xoá sản phẩm khỏi phiếu!";
        }
        return"Xoá thất bại!";
    }

    public void refeshData(){this.listPN=CTPNDAO.getAllCtPN();}

}
