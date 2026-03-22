//package BUS;
//
//import DAO.SanPham_DAO;
//import DAO.ChiTietKe_DAO;
//import Model.ChiTietKe;
//import Model.KeKho;
//import Model.SanPham;
//import Model.SanPhamDTO;
//
//import java.sql.Connection;
//import javax.swing.*;
//import javax.swing.table.DefaultTableModel;
//import java.util.ArrayList;
//
//public class SanPham_BUS {
//    private ArrayList<SanPham> listSP;
//    private SanPham_DAO spDAO;
//    private KeKho_BUS kkBUS;
//    private ChiTietKe_DAO ctDAO;
//
//    public ArrayList<SanPham> getListSP() {
//        return listSP;
//    }
//
//    public ArrayList<SanPhamDTO> getListspDTO() {
//        return ListspDTO;
//    }
//
//    public SanPham_BUS() {
//        spDAO = new SanPham_DAO();
//        kkBUS = new KeKho_BUS();
//        ctDAO = new ChiTietKe_DAO();
//        listSP = spDAO.getAllSanPham();
//        ListspDTO = spDAO.getListDTO();
//    }
//
//    public ArrayList<SanPham> getAll() {
//        return spDAO.getAllSanPham();
//    }
//
//    public ArrayList<SanPhamDTO> gettSPByKeyWord(String input){
//        return spDAO.getSpByKey(input);
//    }
//
//    public int getSoLuongTon(String maSP) {
//        return ctDAO.getTongSoLuongByMaSP(maSP);
//    }
//
//    public boolean updateSP(Connection conn, DefaultTableModel model) {
//        for (int i = 0; i < model.getRowCount(); i++) {
//            String maSP = model.getValueAt(i, 1).toString();
//            int sL = Integer.parseInt(model.getValueAt(i, 3).toString());
////            TH1: kệ chứa sản phẩm còn chỗ
//            ArrayList<String> keKho = spDAO.getAllMaKe(conn,maSP);
//            for (String maKe : keKho){
//                int soLuong = spDAO.SumSLbyMaKe(conn,maKe);
//                int sucChua = kkBUS.getSucChua(conn,maKe);
//                int khoangTrong = sucChua - soLuong;
//                if (khoangTrong>=sL){
//                    if (!spDAO.updateSL(conn,maSP,maKe,sL))
//                        return false;
//                    else{
//                        daUpdate = true;
//                        break;
//                    }
//                }
//            }
////            TH2: dời qua kệ mới
//            if (!daUpdate){
//
//                int soLuongMoi = tinhSLuong(maSP, sL);
//
//                if (!spDAO.updateSP(conn, soLuongMoi, null, maSP))
//                    return false;
//            }
//
//            listSP = spDAO.getAllSanPham();
//            return true;
//        }
//
//        public boolean updateSPPX(Connection conn, DefaultTableModel model) {
//            System.out.println("Bắt đầu updateSPPX - Số sản phẩm cần cập nhật: " + model.getRowCount());
//            for (int i=0;i<model.getRowCount();i++){
//
//                String maSP = model.getValueAt(i, 1).toString();
//                int sL = Integer.parseInt(model.getValueAt(i, 3).toString());
//
//                System.out.println("  Xử lý sản phẩm: " + maSP + " | SL xuất: " + sL);
//
//                int sLMoi = tinhSLuongConLai(maSP,sL);
//                System.out.println("    → SL còn lại sau xuất: " + sLMoi);
//
//                if (!spDAO.updateSP(conn,sLMoi,null,maSP)){
//                    System.out.println("  → Thất bại update SAN_PHAM cho maSP: " + maSP);
//                    return  false;
//                }
//                System.out.println("    → Update SAN_PHAM thành công");
//            }
//            System.out.println("updateSPPX hoàn tất → load lại listSP");
//            listSP = spDAO.getAllSanPham();
//            return true;
//        }
//
//        public int tinhSLuong(String maSP, int soLuong){
//            int slHienTai = getSoLuongTon(maSP);
//            return slHienTai + soLuong;
//        }
//
//        public int tinhSLuongConLai(String maSP, int soLuong){
//            int slHienTai = getSoLuongTon(maSP);
//            return slHienTai - soLuong;
//        }
//
//        private KeKho timKeKhoTheoMa(String maKe,Connection conn) {
//            for (KeKho kk : kkBUS.getListKK()) {
//                if (kk.getMaKe().equalsIgnoreCase(maKe)) {
//                    return kk;
//                }
//            }
//            return null;
//        }
//
//        public boolean updateSoLuong(String maSP, int soLuongThayDoi) {
//            ArrayList<ChiTietKe> listCT = ctDAO.getByMaSP(maSP);
//            if (listCT.isEmpty()) {
//                return false;
//            }
//
//            ChiTietKe ct = listCT.get(0);
//            int slMoi = ct.getSoLuong() + soLuongThayDoi;
//
//            if (slMoi < 0) return false;
//
//            ct.setSoLuong(slMoi);
//            return ctDAO.insertOrUpdate(ct);
//        }
//
//        public String addSanPham(SanPham sp, int soLuong) {
//            if(!Check.isValidSP(sp.getMaSP()))
//                return "Mã sản phẩm không đúng định dạng";
//
//            for(SanPham item : listSP)
//                if(item.getMaSP().equalsIgnoreCase(sp.getMaSP()))
//                    return "Mã sản phẩm đã tồn tại";
//
//            // Kiểm tra sức chứa
//            KeKho ke = kkBUS.getKeTheoMa(sp.getMaKe());
//            if (ke != null) {
//                int tongHienTai = kkBUS.tinhTongSoLuongTheoKe(ke.getMaKe());
//                if (tongHienTai + soLuong > ke.getSucChua()) {
//                    return "Kệ " + ke.getMaKe() + " không đủ sức chứa! (Đã có " + tongHienTai + "/" + ke.getSucChua() + ")";
//                }
//            }
//
//            if(spDAO.insert(sp)) {
//                ctDAO.insertOrUpdate(
//                        new ChiTietKe(
//                                sp.getMaKe(),
//                                sp.getMaSP(),
//                                soLuong
//                        )
//                );
//                listSP.add(sp);
//                return "Thêm sản phẩm thành công";
//            }
//            return "Thêm sản phẩm thất bại";
//        }
//
//        public String updateSanPham(SanPham sp, int soLuongMoi) {
//            // Kiểm tra sức chứa
//            KeKho ke = kkBUS.getKeTheoMa(sp.getMaKe());
//            if (ke != null) {
//                int tongHienTai = kkBUS.tinhTongSoLuongTheoKe(ke.getMaKe());
//
//                // Lấy số lượng cũ của sản phẩm từ ChiTietKe
//                int soLuongCu = 0;
//                ArrayList<ChiTietKe> listCT = ctDAO.getByMaSP(sp.getMaSP());
//                for (ChiTietKe ct : listCT) {
//                    if (ct.getMaKe().equals(sp.getMaKe())) {
//                        soLuongCu = ct.getSoLuong();
//                        break;
//                    }
//                }
//
//                tongHienTai -= soLuongCu;
//
//                if (tongHienTai + soLuongMoi > ke.getSucChua()) {
//                    return "Kệ " + ke.getMaKe() + " không đủ sức chứa!";
//                }
//            }
//
//            if (spDAO.update(sp)) {
//                ctDAO.insertOrUpdate(
//                        new ChiTietKe(
//                                sp.getMaKe(),
//                                sp.getMaSP(),
//                                soLuongMoi
//                        )
//                );
//
//                for (int i = 0; i < listSP.size(); i++) {
//                    if (listSP.get(i).getMaSP().equals(sp.getMaSP())) {
//                        listSP.set(i, sp);
//                        break;
//                    }
//                }
//                return "Cập nhật thành công!";
//            }
//            return "Cập nhật thất bại!";
//        }
//
//        public String deleteSanPham(String maSP) {
//            if (spDAO.delete(maSP)) {
//                listSP.removeIf(sp -> sp.getMaSP().equals(maSP));
//                return "Xóa sản phẩm thành công!";
//            }
//            return "Xóa thất bại !";
//        }
//
//        public void refeshdata(){
//            listSP = spDAO.getAllSanPham();
//        }
//
//        public ArrayList<SanPham> laySanPhamTheoKe(String maKe){
//            return spDAO.laySanPhamTheoKe(maKe);
//        }
//
//        public SanPham getSanPhamByMa(String maSP) {
//            for (SanPham sp : listSP) {
//                if (sp.getMaSP().equals(maSP)) {
//                    return sp;
//                }
//            }
//            return null;
//        }
//    }