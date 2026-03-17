package BUS;

import DAO.KhachHangDAO;
import Model.KhachHang;
import java.util.*;
public class KhachHang_BUS {
    private ArrayList<KhachHang> listKH;
    private KhachHangDAO khDAO;

    public KhachHang_BUS(){
        khDAO=new KhachHangDAO();
        this.listKH=khDAO.getAllKhachHang();
    }
    public ArrayList<KhachHang>getListKH(){
        return listKH;
    }

    public String addKhachHang(KhachHang kh){
        //Kiểm tra thêm khách hàng có đúng định dạng không
        if (!Check.isValidKH(kh.getMaKH()))
            return "Thêm mã khách hàng không đúng định dạng (Phải là KHxx ví dụ KH01)";
        for (KhachHang existingKH : listKH) {
            if (existingKH.getMaKH().equalsIgnoreCase(kh.getMaKH())) {
                return "Lỗi: Mã khách hàng [" + kh.getMaKH() + "] đã tồn tại!";
            }
        }

        if(kh.getHoTenKH().trim().isEmpty())
            return"Họ tên không được để trống!";
        if(kh.getSdt().trim().isEmpty())
            return"Số điện thoại khôgn được để trống!";
        if(kh.getDiaChi().trim().isEmpty())
            return"Địa chỉ không được để trống!";
        if(khDAO.insert(kh)) {
            listKH.add(kh);
            return"Thêm khách hàng thành công!";
        }

        return"Thêm khách hàng thất bại!";
    }

    public String updateKH(KhachHang kh){
        if(khDAO.update(kh)) return "Cập nhật thành công!";
        return "Cập nhật thất bại!";
    }

    public String deleteKH(String maKH){
        if(maKH == null|| maKH.trim().isEmpty())
            return "Mã Khách hàng không hợp lệ!";

        if(khDAO.delete(maKH))
        {
            listKH.removeIf(bc->bc.getMaKH().equalsIgnoreCase(maKH));
            return"Xoá khách hàng thành công!";
        }
        return"Xoá Khách hàng thất bại!";
    }
    public void refreshList() {
        this.listKH = khDAO.getAllKhachHang(); // Quét lại toàn bộ DB
    }
    public void refeshData(){this.listKH=khDAO.getAllKhachHang();}

    public void DongBoCT(){
        khDAO.DongBoChiTieu();
    }
}
