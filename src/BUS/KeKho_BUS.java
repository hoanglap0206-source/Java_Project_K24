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

    public int tinhTongSP() {
        int tong = 0;
        for (SanPham sp : listAllSP) {
            tong += sp.getSoLuong();
        }

        return tong;
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

        for (KeKho ke : listKK) {
            if(ke.getMaKe().equalsIgnoreCase(kk.getMaKe().trim()))
                return "Mã kệ đã tồn tại!";
        }

        if(kk.getSucChua() <= 0) return "Sức chứa phải lớn hơn 0!";

        if(kkDAO.insert(kk)) {
            listKK.add(kk);
            return "Thêm kệ kho thành công!";
        }
        return "Thêm thất bại!";
    }

    public String updateKK(KeKho kk){
        if(kk.getSucChua() <= 0)
            return "Sức chứa phải lớn hơn 0!";

        int tong = tinhTongSoLuongTheoKe(kk.getMaKe());

        if(kk.getSucChua() < tong)
            return "Sức chứa nhỏ hơn số lượng hiện tại!";

        if(kkDAO.update(kk)){
            refreshData();
            return "Cập nhật thành công!";
        }

        return "Cập nhật thất bại!";
    }

    public boolean updatekK(Connection conn, KeKho kk){
        if(kkDAO.updateKK(conn, kk)) {
            refreshData();
            return true;
        }
        return false;
    }

    public String deleteKK(String maKe) {

        if(maKe == null || maKe.trim().isEmpty()){
            return "Mã kệ không hợp lệ!";
        }

        if(spDAO.countByMaKe(maKe) > 0){
            return "Không thể xoá! Kệ vẫn còn sản phẩm.";
        }

        boolean deleted = kkDAO.delete(maKe);

        if(deleted){
            listKK.removeIf(k -> k.getMaKe().equals(maKe));
            return "Xóa kệ kho thành công!";
        }

        return "Không tìm thấy kệ để xoá!";
    }
}