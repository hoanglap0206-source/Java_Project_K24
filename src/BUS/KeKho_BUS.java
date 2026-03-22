package BUS;

import DAO.KeKho_DAO;
import DAO.SanPham_DAO;
import DAO.ChiTietKe_DAO;
import Model.KeKho;
import Model.KeKhoDTO;
import Model.SanPham;
import Model.ChiTietKe;

import java.sql.Connection;
import java.util.ArrayList;

public class KeKho_BUS {
    private ArrayList<KeKho> listKK;
    private ArrayList<SanPham> listAllSP;
    private ArrayList<ChiTietKe> listAllCT;
    private KeKho_DAO kkDAO;
    private SanPham_DAO spDAO;
    private ChiTietKe_DAO ctDAO;
    // Cache các giá trị tính toán
    private ArrayList<Integer> cacheTongSoLuongTheoKe;
    private ArrayList<Integer> cachePhanTramTheoKe;
    private boolean isCacheValid = false;

    public KeKho_BUS(){
        kkDAO = new KeKho_DAO();
        spDAO = new SanPham_DAO();
        ctDAO = new ChiTietKe_DAO();
        loadAllData();
    }
    public ArrayList<KeKhoDTO> getDSKeKho_Nhap(Connection conn){
        return kkDAO.getDSKeKho_Nhap(conn);
    }
    public int getSucChua(Connection conn,String MaKe){
        return kkDAO.getSucChua(conn,MaKe);
    }
    private void loadAllData() {
        listKK = kkDAO.getAllKeKho();
        listAllSP = spDAO.getAllSanPham();
        listAllCT = ctDAO.getAll();

        // Khởi tạo cache
        int size = listKK.size();
        cacheTongSoLuongTheoKe = new ArrayList<>(size);
        cachePhanTramTheoKe = new ArrayList<>(size);

        // Tính toán cache ngay khi load
        for (int i = 0; i < size; i++) {
            KeKho ke = listKK.get(i);
            int tong = tinhTongSoLuongTheoKeTuData(ke.getMaKe());
            cacheTongSoLuongTheoKe.add(tong);

            int phanTram = ke.getSucChua() > 0 ? (int) ((double) tong / ke.getSucChua() * 100) : 0;
            cachePhanTramTheoKe.add(phanTram);
        }
        isCacheValid = true;
    }

    public void refreshData() {
        loadAllData();
    }

    public ArrayList<ChiTietKe> getAllCT() {
        return listAllCT;
    }

    public ArrayList<KeKho> getListKK(){
        return listKK;
    }

    public ArrayList<SanPham> getAllSanPham() {
        return listAllSP;
    }

    public ArrayList<SanPham> laySanPhamTheoKe(String maKe){
        ArrayList<SanPham> result = new ArrayList<>();

        // Tạo map tạm thời để tìm nhanh sản phẩm
        for (ChiTietKe ct : listAllCT) {
            if (ct.getMaKe().equals(maKe)) {
                for (SanPham sp : listAllSP) {
                    if (sp.getMaSP().equals(ct.getMaSP())) {
                        SanPham spCopy = new SanPham();
                        spCopy.setMaSP(sp.getMaSP());
                        spCopy.setTenSP(sp.getTenSP());
                        spCopy.setDonViTinh(sp.getDonViTinh());
                        spCopy.setGiaTien(sp.getGiaTien());
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

    // Lấy index của kệ
    private int getIndexKe(String maKe) {
        for (int i = 0; i < listKK.size(); i++) {
            if (listKK.get(i).getMaKe().equals(maKe)) {
                return i;
            }
        }
        return -1;
    }

    public int tinhTongSPTrongKho() {
        if (cacheTongSoLuongTheoKe == null) {
            int tong = 0;
            for (ChiTietKe ct : listAllCT) {
                tong += ct.getSoLuong();
            }
            return tong;
        }

        int tong = 0;
        for (int soLuong : cacheTongSoLuongTheoKe) {
            tong += soLuong;
        }
        return tong;
    }

    public int tinhTongSoLuongTheoKe(String maKe) {
        int index = getIndexKe(maKe);
        if (index >= 0 && cacheTongSoLuongTheoKe != null && index < cacheTongSoLuongTheoKe.size()) {
            return cacheTongSoLuongTheoKe.get(index);
        }

        // Fallback: tính trực tiếp
        int tong = 0;
        for (ChiTietKe ct : listAllCT) {
            if (ct.getMaKe().equals(maKe)) {
                tong += ct.getSoLuong();
            }
        }
        return tong;
    }

    private int tinhTongSoLuongTheoKeTuData(String maKe) {
        int tong = 0;
        for (ChiTietKe ct : listAllCT) {
            if (ct.getMaKe().equals(maKe)) {
                tong += ct.getSoLuong();
            }
        }
        return tong;
    }

    public int tinhPhanTramTheoKe(KeKho ke) {
        int index = getIndexKe(ke.getMaKe());
        if (index >= 0 && cachePhanTramTheoKe != null && index < cachePhanTramTheoKe.size()) {
            return cachePhanTramTheoKe.get(index);
        }

        // Fallback: tính trực tiếp
        int tong = tinhTongSoLuongTheoKe(ke.getMaKe());
        int sucChua = ke.getSucChua();
        if (sucChua == 0) return 0;
        double phanTram = (double) tong / sucChua * 100;
        return (int) Math.min(100, Math.round(phanTram));
    }

    public String addKK(KeKho kk){
        String maMoi = sinhMaKeTuDong();
        kk.setMaKe(maMoi);

        if(kk.getSucChua() <= 0) return "Sức chứa phải lớn hơn 0!";

        if(kkDAO.insert(kk)) {
            listKK.add(kk);
            // Cập nhật cache
            cacheTongSoLuongTheoKe.add(0);
            cachePhanTramTheoKe.add(0);
            isCacheValid = true;
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
            // Cập nhật cache
            int index = getIndexKe(kk.getMaKe());
            if (index >= 0) {
                int phanTram = kk.getSucChua() > 0 ? (int) ((double) cacheTongSoLuongTheoKe.get(index) / kk.getSucChua() * 100) : 0;
                cachePhanTramTheoKe.set(index, phanTram);
                // Cập nhật lại kệ trong list
                listKK.set(index, kk);
            }
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

        for (ChiTietKe ct : listAllCT) {
            if (ct.getMaKe().equals(maKe)) {
                return "Không thể xóa kệ. Kệ còn sản phẩm!";
            }
        }

        boolean deleted = kkDAO.delete(maKe);
        if(deleted){
            int index = getIndexKe(maKe);
            if (index >= 0) {
                listKK.remove(index);
                cacheTongSoLuongTheoKe.remove(index);
                cachePhanTramTheoKe.remove(index);
            }
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

    public boolean isKeFull(String maKe) {
        KeKho ke = getKeTheoMa(maKe);
        if (ke == null) return true;

        int tongHienTai = tinhTongSoLuongTheoKe(maKe);
        return tongHienTai >= ke.getSucChua();
    }

    public int getKhoangTrong(String maKe) {
        KeKho ke = getKeTheoMa(maKe);
        if (ke == null) return 0;

        int tongHienTai = tinhTongSoLuongTheoKe(maKe);
        return Math.max(0, ke.getSucChua() - tongHienTai);
    }

    public ArrayList<KeKho> getListKeConTrong() {
        ArrayList<KeKho> listConTrong = new ArrayList<>();
        for (KeKho ke : listKK) {
            int tongHienTai = tinhTongSoLuongTheoKe(ke.getMaKe());
            if (tongHienTai < ke.getSucChua()) {
                listConTrong.add(ke);
            }
        }
        return listConTrong;
    }
}