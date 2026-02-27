package BUS;

import DAO.SanPham_DAO;
import Model.SanPham;
import java.util.ArrayList;

public class SanPham_BUS {
    private ArrayList<SanPham> listSP;
    private SanPham_DAO spDAO;

    public SanPham_BUS() {
        spDAO = new SanPham_DAO();
        listSP = spDAO.getAllSanPham(); // thêm để load data listSP
    }

    public ArrayList<SanPham> getAll() {
        spDAO = new SanPham_DAO();
        return spDAO.getAllSanPham();
    }


    public int getSoLuongTon(String maSP) {
        for (SanPham sp : listSP) {
            if (sp.getMaSP().equalsIgnoreCase(maSP)) {
                return sp.getSoLuong();
            }
        }
        return 0;
    }


    public boolean updateSoLuong(String maSP, int soLuongThayDoi) {
        for (int i = 0; i < listSP.size(); i++) {
            SanPham sp = listSP.get(i);
            if (sp.getMaSP().equalsIgnoreCase(maSP)) {
                int slMoi = sp.getSoLuong() + soLuongThayDoi;
                if (slMoi < 0) return false; // Không cho phép tồn kho âm

                sp.setSoLuong(slMoi);
                if (spDAO.update(sp)) { // Gọi DAO để update xuống database
                    listSP.set(i, sp); // Cập nhật lại RAM
                    return true;
                }
            }
        }
        return false;
    }

    public String addSanPham(SanPham sp) {
        //Kiểm tra thêm sản phẩm có đúng đinh dạng không
        if(!Check.isValidSP(sp.getMaSP()))
            return "Thêm mã sản phẩm không đúng định dạng (Phải là SPxx ví dụ SP01)";

        for (SanPham item : listSP)
            if (item.getMaSP().equalsIgnoreCase(sp.getMaSP()))
                return "Mã sản phẩm đã tồn tại!";

        if (spDAO.insert(sp)) {
            listSP.add(sp);
            return "Thêm sản phẩm thành công!";
        }
        return "Thêm thất bại!";
    }


    public String updateSanPham(SanPham sp) {
        if (spDAO.update(sp)) {
            for (int i = 0; i < listSP.size(); i++) {
                if (listSP.get(i).getMaSP().equals(sp.getMaSP())) {
                    listSP.set(i, sp); // Sử dụng .set để thay thế, không dùng .add
                    break;
                }
            }
            return "Cập nhật thành công!";
        }
        return "Cập nhật thất bại!";
    }


    public String deleteSanPham(String maSP) {
        if (spDAO.delete(maSP)) {
            listSP.removeIf(sp -> sp.getMaSP().equals(maSP));
            return "Xóa sản phẩm thành công!";
        }
        return "Xóa thất bại !";
    }
    public  void refeshdata(){listSP=spDAO.getAllSanPham();}
}