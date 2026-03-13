package BUS;

import DAO.KeKho_DAO;
import DAO.SanPham_DAO;
import Model.KeKho;
import Model.SanPham;

import java.sql.Connection;
import java.util.ArrayList;

public class KeKho_BUS {
    private ArrayList<KeKho> listKK;
    private ArrayList<SanPham> listAllSP; // Cache tất cả sản phẩm
    private KeKho_DAO kkDAO;
    private SanPham_DAO spDAO;

    public KeKho_BUS(){
        kkDAO = new KeKho_DAO();
        spDAO = new SanPham_DAO();
        loadAllData(); // Load 1 lần khi khởi tạo
    }

    // Load tất cả dữ liệu 1 lần
    private void loadAllData() {
        listKK = kkDAO.getAllKeKho();
        listAllSP = spDAO.getAllSanPham();
    }

    public void refreshData() {
        loadAllData();
    }

    public ArrayList<KeKho> getListKK(){
        return listKK; // Trả về cache, không query lại
    }

    public ArrayList<KeKho> getlistkK(Connection conn){
        return kkDAO.getListKK(conn);
    }

    public ArrayList<SanPham> getAllSanPham() {
        return listAllSP;
    }

    public ArrayList<SanPham> laySanPhamTheoKe(String maKe){
        ArrayList<SanPham> result = new ArrayList<>();
        for (SanPham sp : listAllSP) {
            if (sp.getKeKho() != null && maKe.equals(sp.getKeKho().getMaKe())) {
                result.add(sp);
            }
        }
        return result;
    }

    public KeKho getKeTheoMa(String maKe){
        for (KeKho ke : listKK) {
            if (ke.getMaKe().equals(maKe)) {
                return ke;
            }
        }
        return null;
    }

    public int tinhTongSoLuongTheoKe(String maKe) {
        int tong = 0;
        for (SanPham sp : listAllSP) {
            if (sp.getKeKho() != null && maKe.equals(sp.getKeKho().getMaKe())) {
                tong += sp.getSoLuong();
            }
        }
        return tong;
    }

    public int tinhPhanTramTheoKe(KeKho ke) {
        int tong = tinhTongSoLuongTheoKe(ke.getMaKe());
        int sucChua = ke.getSucChua();
        return (sucChua == 0) ? 0 : (int)((double) tong / sucChua * 100);
    }

    public String addKK(KeKho kk){
        if(kk.getMaKe().trim().isEmpty()) return "Mã kệ không được để trống!";
        if(kk.getSucChua() <= 0) return "Sức chứa phải lớn hơn 0!";

        if(kkDAO.insert(kk)) {
            listKK.add(kk);
            return "Thêm kệ kho thành công!";
        }
        return "Thêm thất bại!";
    }

    public boolean updateKK(KeKho kk){
        if(kkDAO.update(kk)) {
            refreshData();
            return true;
        }
        return false;
    }

    public boolean updatekK(Connection conn, KeKho kk){
        if(kkDAO.updateKK(conn, kk)) {
            refreshData();
            return true;
        }
        return false;
    }

//    public boolean updateKKtheoKT(Connection conn, String maKe, int khoangTrong){
//        if(kkDAO.updateKhoangTrong(conn, maKe, khoangTrong)) {
//            refreshData();
//            return true;
//        }
//        return false;
//    }

    public String deleteKK(String maKe){
        if(spDAO.countByMaKe(maKe) > 0){
            return "Không thể xoá! Kệ vẫn còn sản phẩm.";
        }
        if(kkDAO.delete(maKe)) {
            listKK.removeIf(k -> k.getMaKe().equals(maKe));
            return "Xoá kệ kho thành công!";
        }
        return "Xoá thất bại!";
    }
}