package BUS;

import DAO.KeKho_DAO;
import DAO.SanPham_DAO;
import DAO.ChiTietKe_DAO;
import Model.KeKho;
import Model.SanPham;
import Model.ChiTietKe;

import java.sql.Connection;
import java.util.ArrayList;

public class KeKho_BUS {
    private ArrayList<KeKho> listKK;
    private ArrayList<SanPham> listAllSP; // Cache tất cả sản phẩm
    private KeKho_DAO kkDAO;
    private SanPham_DAO spDAO;
    private ChiTietKe_DAO ctDAO = new ChiTietKe_DAO();

    public KeKho_BUS(){
        kkDAO = new KeKho_DAO();
        spDAO = new SanPham_DAO();
        loadAllData(); // Load 1 lần khi khởi tạo
    }

    private void loadAllData() {
        listKK = kkDAO.getAllKeKho();
        listAllSP = spDAO.getAllSanPham();
    }

    public ArrayList<KeKho> getlistkK(Connection conn){
        return kkDAO.getAllKeKhoB(conn);
    }

    public void refreshData() {
        loadAllData();
    }

    public ArrayList<KeKho> getListKK(){
        return listKK; // Trả về cache, không query lại
    }

    public ArrayList<SanPham> getAllSanPham() {
        return listAllSP;
    }

    public ArrayList<SanPham> laySanPhamTheoKe(String maKe){
        ArrayList<SanPham> result = new ArrayList<>();
        ArrayList<ChiTietKe> listCT = ctDAO.getByMaKe(maKe);

        for (ChiTietKe ct : listCT) {
            for (SanPham sp : listAllSP) {
                if (sp.getMaSP().equals(ct.getMaSP())) {
                    sp.setSoLuong(ct.getSoLuong());
                    result.add(sp);
                    break;
                }
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
        ArrayList<ChiTietKe> list = ctDAO.getByMaKe(maKe);

        for (ChiTietKe ct : list) {
            tong += ct.getSoLuong();
        }

        return tong;
    }

    public int tinhPhanTramTheoKe(KeKho ke) {
        int tong = tinhTongSoLuongTheoKe(ke.getMaKe());
        int sucChua = ke.getSucChua();
        if (sucChua == 0)
            return 0;
        double phanTram = (double) tong / sucChua * 100;
        return (int) phanTram;
    }

    public String addKK(KeKho kk){
        String maMoi = sinhMaKeTuDong();
        kk.setMaKe(maMoi);

        if(kk.getSucChua() <= 0) return "Sức chứa phải lớn hơn 0!";

        if(kkDAO.insert(kk)) {
            listKK.add(kk);
            return "Thêm kệ kho thành công! Mã: " + maMoi;
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

    public String deleteKK(String maKe) {
        if(!ctDAO.getByMaKe(maKe).isEmpty()){
            return "Không thể xoá! Kệ vẫn còn dữ liệu chi tiết.";
        }

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

    public String sinhMaKeTuDong() {
        int max = 0;

        for (KeKho ke : listKK) {
            String ma = ke.getMaKe();

            if (ma != null && ma.matches("K\\d{4}")) {
                int num = Integer.parseInt(ma.substring(1));
                if (num > max) {
                    max = num;
                }
            }
        }

        return String.format("K%04d", max + 1);
    }
}