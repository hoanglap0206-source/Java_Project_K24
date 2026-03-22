package BUS;

import DAO.SanPham_DAO;
import DAO.ChiTietKe_DAO;
import Model.*;

import java.sql.Connection;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;

public class SanPham_BUS {
    private ArrayList<SanPham> listSP;
    private SanPham_DAO spDAO;
    private KeKho_BUS kkBUS;
    private ChiTietKe_DAO ctDAO;

    private ArrayList<SanPhamDTO> ListspDTO;
    // THÊM CACHE CHO DỮ LIỆU BẢNG
    private ArrayList<Object[]> cachedTableData;
    private boolean needRefresh = true;

    public ArrayList<SanPham> getListSP() {
        if (needRefresh) {
            refreshData();
        }
        return listSP;
    }

    public ArrayList<SanPhamDTO> getListspDTO() {
        return ListspDTO;
    }

    public SanPham_BUS() {
        spDAO = new SanPham_DAO();
        kkBUS = new KeKho_BUS();
        ctDAO = new ChiTietKe_DAO();
        cachedTableData = new ArrayList<>();
        ListspDTO = spDAO.getListDTO();
        loadAllData();
    }

    private void loadAllData() {
        listSP = spDAO.getAllSanPham();
        needRefresh = false;
        // Không cần cache số lượng tồn riêng nữa
    }

    // PHƯƠNG THỨC MỚI - Lấy dữ liệu đã xử lý sẵn cho bảng
    public ArrayList<Object[]> getTableData() {
        if (needRefresh || cachedTableData.isEmpty()) {
            refreshTableCache();
        }
        return cachedTableData;
    }

    // Refresh cache dữ liệu bảng
    private void refreshTableCache() {
        cachedTableData.clear();
        ArrayList<Object[]> rawData = spDAO.getAllSanPhamWithDetails();
        cachedTableData.addAll(rawData);
        needRefresh = false;
    }

    public ArrayList<SanPham> getAll() {
        if (needRefresh) {
            refreshData();
        }
        return listSP;
    }

    public ArrayList<SanPhamDTO> gettSPByKeyWordDTO(String input){
        return spDAO.getSpByKeyDTO(input);
    }
    public ArrayList<SanPham> gettSPByKeyWord(String input){
        return spDAO.getSpByKey(input);
    }

    public int getSoLuongTon(String maSP) {
        // Lấy từ cache thay vì query
        for (Object[] row : cachedTableData) {
            if (row[0].toString().equals(maSP)) {
                return (int) row[4];
            }
        }
        return 0;
    }

    public boolean updateSPNhap(Connection conn, DefaultTableModel model) {

        for (int i = 0; i < model.getRowCount(); i++) {
            boolean daUpdate = false;
            String maSP = model.getValueAt(i, 1).toString();
            int sL = Integer.parseInt(model.getValueAt(i, 3).toString());
//            TH1: kệ chứa sản phẩm còn chỗ
            ArrayList<String> keKho = spDAO.getAllMaKe(conn,maSP);
            for (String maKe : keKho){
                int soLuong = spDAO.SumSLbyMaKe(conn,maKe);
                int sucChua = kkBUS.getSucChua(conn,maKe);
                int khoangTrong = sucChua - soLuong;
                if (khoangTrong>=sL){
                    if (!spDAO.updateSL(conn,maSP,maKe,sL))
                        return false;
                    else{
                        daUpdate = true;
                        break;
                    }
                }
            }
//            TH2: dời qua kệ mới
            if (!daUpdate){
                boolean daChuyen = false;
                ArrayList<KeKhoDTO> listKK = kkBUS.getDSKeKho_Nhap(conn);
                for (KeKhoDTO kk : listKK){
                    int succhua = kkBUS.getSucChua(conn,kk.getMa_ke());
                    int khoangtrong = succhua - kk.getTongSL();
                    if (khoangtrong >= sL){
                        if (!spDAO.updateSL(conn,maSP,kk.getMa_ke(),sL)){
                            return false;
                        }else {
                            daChuyen = true;
                            break;
                        }
                    }
                }if (!daChuyen) return false;
            }
        }
        refeshdataDTO();
        return true;
    }

    public boolean updateSPPX(Connection conn, DefaultTableModel model) {
        System.out.println("Bắt đầu updateSPPX - Số sản phẩm cần cập nhật: " + model.getRowCount());
        for (int i=0;i<model.getRowCount();i++){

            String maSP = model.getValueAt(i, 1).toString();
            int sL = Integer.parseInt(model.getValueAt(i, 3).toString());

            System.out.println("  Xử lý sản phẩm: " + maSP + " | SL xuất: " + sL);

            ArrayList<KeKhoDTO> list = spDAO.getDanhSachKeXuat(maSP,conn);
            int SlConLai = sL;
            for (KeKhoDTO kk : list){
                if (SlConLai <=0)
                    break;
                int soLuongTru = Math.min(kk.getTongSL(), SlConLai);
                if (!spDAO.updateSLXuat(conn,maSP,kk.getMa_ke(),soLuongTru)){
                    System.out.println("  → Thất bại update SAN_PHAM cho maSP: " + maSP);
                    return  false;
                }
                SlConLai -= soLuongTru;
                boolean ok = spDAO.deleteIfZero(conn,maSP,kk.getMa_ke());
            }
            System.out.println("    → Update SAN_PHAM thành công");
        }
        System.out.println("updateSPPX hoàn tất → load lại listSP");
        refeshdataDTO();
        return true;
    }

    public int tinhSLuong(String maSP, int soLuong){
        int slHienTai = getSoLuongTon(maSP);
        return slHienTai + soLuong;
    }

    public int tinhSLuongConLai(String maSP, int soLuong){
        int slHienTai = getSoLuongTon(maSP);
        return slHienTai - soLuong;
    }

    private KeKho timKeKhoTheoMa(String maKe,Connection conn) {
        for (KeKho kk : kkBUS.getListKK()) {
            if (kk.getMaKe().equalsIgnoreCase(maKe)) {
                return kk;
            }
        }
        return null;
    }

    public boolean updateSoLuong(String maSP, int soLuongThayDoi) {
        ArrayList<ChiTietKe> listCT = ctDAO.getByMaSP(maSP);
        if (listCT.isEmpty()) {
            return false;
        }

        ChiTietKe ct = listCT.get(0);
        int slMoi = ct.getSoLuong() + soLuongThayDoi;

        if (slMoi < 0) return false;

        ct.setSoLuong(slMoi);
        boolean result = ctDAO.insertOrUpdate(ct);

        if (result) {
            refreshData();
        }

        return result;
    }

    public String addSanPham(SanPham sp, int soLuong) {
        if(!Check.isValidSP(sp.getMaSP()))
            return "Mã sản phẩm không đúng định dạng";

        for(SanPham item : listSP)
            if(item.getMaSP().equalsIgnoreCase(sp.getMaSP()))
                return "Mã sản phẩm đã tồn tại";

        KeKho ke = kkBUS.getKeTheoMa(sp.getMaKe());
        if (ke != null) {
            int tongHienTai = kkBUS.tinhTongSoLuongTheoKe(ke.getMaKe());
            if (tongHienTai + soLuong > ke.getSucChua()) {
                return "Kệ " + ke.getMaKe() + " không đủ sức chứa! (Đã có " + tongHienTai + "/" + ke.getSucChua() + ")";
            }
        }

        if(spDAO.insert(sp)) {
            ctDAO.insertOrUpdate(
                    new ChiTietKe(
                            sp.getMaKe(),
                            sp.getMaSP(),
                            soLuong
                    )
            );
            refreshData();
            return "Thêm sản phẩm thành công";
        }
        return "Thêm sản phẩm thất bại";
    }

    public String updateSanPham(SanPham sp, int soLuongMoi) {
//        KeKho ke = kkBUS.getKeTheoMa(sp.getMaKe());
//        if (ke != null) {
//            int tongHienTai = kkBUS.tinhTongSoLuongTheoKe(ke.getMaKe());
//
//            int soLuongCu = 0;
//            ArrayList<ChiTietKe> listCT = ctDAO.getByMaSP(sp.getMaSP());
//            for (ChiTietKe ct : listCT) {
//                if (ct.getMaKe().equals(sp.getMaKe())) {
//                    soLuongCu = ct.getSoLuong();
//                    break;
//                }
//            }
//
//            tongHienTai -= soLuongCu;
//
//            if (tongHienTai + soLuongMoi > ke.getSucChua()) {
//                return "Kệ " + ke.getMaKe() + " không đủ sức chứa!";
//            }
//        }
//
//        if (spDAO.update(sp)) {
//            ctDAO.insertOrUpdate(
//                    new ChiTietKe(
//                            sp.getMaKe(),
//                            sp.getMaSP(),
//                            soLuongMoi
//                    )
//            );
//            refreshData();
//            return "Cập nhật thành công!";
//        }
        if (spDAO.update(sp)) {
            refreshData();
            return "Cập nhật thành công!";
        }
        return "Cập nhật thất bại!";
    }

    public String deleteSanPham(String maSP) {
        if (spDAO.delete(maSP)) {
            refreshData();
            return "Xóa sản phẩm thành công!";
        }
        return "Xóa thất bại !";
    }
    public  void refeshdata(){listSP=spDAO.getAllSanPham();}
    public  void refeshdataDTO(){ListspDTO = spDAO.getListDTO();}
    public ArrayList<SanPham> laySanPhamTheoKe(String maKe){
        return spDAO.laySanPhamTheoKe(maKe);
    }

    public SanPham getSanPhamByMa(String maSP) {
        if (needRefresh) {
            refreshData();
        }

        for (SanPham sp : listSP) {
            if (sp.getMaSP().equals(maSP)) {
                return sp;
            }
        }
        return null;
    }
    public void refreshData() {
        loadAllData();
        needRefresh = true;  // Đánh dấu cần refresh cache bảng
        refreshTableCache(); // Refresh luôn cache bảng
        if (kkBUS != null) {
            kkBUS.refreshData();
        }
    }
}