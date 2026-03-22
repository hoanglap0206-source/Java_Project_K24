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
    private ArrayList<ChiTietKe> listAllCT;
    private KeKho_DAO kkDAO;
    private SanPham_DAO spDAO;
    private ChiTietKe_DAO ctDAO = new ChiTietKe_DAO();

    public KeKho_BUS(){
        kkDAO = new KeKho_DAO();
        spDAO = new SanPham_DAO();
        ctDAO = new ChiTietKe_DAO();
        loadAllData(); // Load 1 lần khi khởi tạo
    }
    public int getSucChua(Connection conn,String maKe){
        return kkDAO.getSucChua(conn,maKe);
    }
    private void loadAllData() {
        listKK = kkDAO.getAllKeKho();
        listAllSP = spDAO.getAllSanPham();
        listAllCT = ctDAO.getAll();
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

        // Dùng cache listAllCT, không query database
        for (ChiTietKe ct : listAllCT) {
            if (ct.getMaKe().equals(maKe)) {
                for (SanPham sp : listAllSP) {
                    if (sp.getMaSP().equals(ct.getMaSP())) {
                        // Clone để tránh ảnh hưởng cache
                        SanPham spCopy = new SanPham();
                        spCopy.setMaSP(sp.getMaSP());
                        spCopy.setTenSP(sp.getTenSP());
                        spCopy.setDonViTinh(sp.getDonViTinh());
                        spCopy.setSoLuong(ct.getSoLuong());
                        result.add(spCopy);
                        break;
                    }
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

    public int tinhTongSPTrongKho() {
            int tong = 0;
            for (ChiTietKe ct : listAllCT) {
                tong += ct.getSoLuong();
            }
            return tong;
    }

    public int tinhTongSoLuongTheoKe(String maKe) {
        int tong = 0;
        for (ChiTietKe ct : listAllCT) {
            if (ct.getMaKe().equals(maKe)) {
                tong += ct.getSoLuong();
            }
        }
        return tong;
    }

    public int tinhPhanTramTheoKe(KeKho ke) {
        int tong = tinhTongSoLuongTheoKe(ke.getMaKe());
        int sucChua = ke.getSucChua();
        if (sucChua == 0)
            return 0;
        double phanTram = (double) tong / sucChua * 100;
        return (int) Math.round(phanTram);
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
        KeKho existing = getKeTheoMa(kk.getMaKe());
        if(existing == null) return "Không tìm thấy kệ!";

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
        KeKho ke = getKeTheoMa(maKe);
        if(ke == null) {
            return "Không tìm thấy kệ!";
        }

        ArrayList<ChiTietKe> dsSanPham = ctDAO.getByMaKe(maKe);
        if(!dsSanPham.isEmpty()) {
            // Chỉ thông báo đơn giản, không liệt kê sản phẩm
            return "Không thể xóa kệ " + maKe + ". Kệ vẫn đang chứa sản phẩm!";
        }

        boolean deleted = kkDAO.delete(maKe);
        if(deleted){
            listKK.removeIf(k -> k.getMaKe().equals(maKe));
            return "Xóa kệ " + maKe + " thành công!";
        }

        return "Xóa thất bại!";
    }

    private String layTenSanPham(String maSP) {
        for(SanPham sp : listAllSP) {
            if(sp.getMaSP().equals(maSP)) {
                return sp.getTenSP();
            }
        }
        return "Không xác định";
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