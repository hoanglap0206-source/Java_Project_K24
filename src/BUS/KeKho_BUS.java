package BUS;

import DAO.KeKho_DAO;
import DAO.SanPham_DAO;
import Model.KeKho;
import Model.SanPham;
import java.util.*;

public class KeKho_BUS {
    private ArrayList<KeKho> listKK;
    private KeKho_DAO kkDAO;
    private SanPham_DAO spDAO;
    private SanPham_BUS sanPhamBUS;

    public KeKho_BUS(){
        kkDAO= new KeKho_DAO();
        spDAO = new SanPham_DAO();
        sanPhamBUS = new SanPham_BUS();
        this.listKK=kkDAO.getAllKeKho();
    }

    public ArrayList<KeKho> getListKK(){
//        return this.listKK; // load dữ liệu 1 lần, không dùng được cho sửa thêm xóa
        return kkDAO.getAllKeKho(); //
    }

    public ArrayList<SanPham> laySanPhamTheoKe(String maKe){
        return sanPhamBUS.laySanPhamTheoKe(maKe);
    }

    public KeKho getKeTheoMa(String maKe){
        return kkDAO.getKeTheoMa(maKe);
    }

    public int tinhTongSoLuongTheoKe(String maKe) {
        ArrayList<SanPham> list = laySanPhamTheoKe(maKe);
        int tong = 0;

        for (SanPham sp : list) {
            tong += sp.getSoLuong();
        }

        return tong;
    }

    public int tinhPhanTramTheoKe(String maKe) {
        KeKho ke = getKeTheoMa(maKe);
        if (ke == null) return 0;

        int tong = tinhTongSoLuongTheoKe(maKe);
        int sucChua = ke.getSucChua();

        if (sucChua == 0) return 0;

        return (int)((double) tong / sucChua * 100);
    }

    public String addKK(KeKho kk){
        if(kk.getMaKe().trim().isEmpty()) return "Mã kệ không được để trống!"; // ?
        if(kk.getSucChua()<=0) return "Sức chứa phải lớn hơn 0!";

        if(kkDAO.insert(kk)){ // insert ?
          listKK.add(kk);
          return "Thêm kệ kho thành công!";
        }
        return "Thêm thất bại!";
    }

    public String updateKK(KeKho kk){
        if(kkDAO.update(kk)){
            for(int i=0;i<listKK.size();i++){
                if(listKK.get(i).getMaKe().equals(kk.getMaKe()))
                {
                    listKK.set(i,kk);
                    break;
                }
            }
            return "Cập nhật thành công!";
        }
        return "Cập nhật thất bại!";
    }

    public String deleteKK(String maKe){
        // Kiểm tra còn sản phẩm không
        if(spDAO.countByMaKe(maKe) > 0){
            return "Không thể xoá! Kệ vẫn còn sản phẩm.";
        }

        if(kkDAO.delete(maKe)){
            listKK.removeIf(kk -> kk.getMaKe().equals(maKe));
            return "Xoá kệ kho thành công!";
        }

        return "Xoá thất bại!";
    }
}
