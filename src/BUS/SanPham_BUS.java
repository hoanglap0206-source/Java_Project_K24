package BUS;

import DAO.KeKho_DAO;
import DAO.SanPham_DAO;
import DataBase.DBConnection;
import Model.KeKho;
import Model.SanPham;
import java.sql.Connection;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;

public class SanPham_BUS {
    private ArrayList<SanPham> listSP;
    private SanPham_DAO spDAO;
    private KeKho_BUS kkBUS;

    public ArrayList<SanPham> getListSP() {
        return listSP;
    }

    public SanPham_BUS() {
        spDAO = new SanPham_DAO();
        kkBUS = new KeKho_BUS();
        listSP = spDAO.getAllSanPham();
    }

    public ArrayList<SanPham> getAll() {
        return spDAO.getAllSanPham();
    }

    public ArrayList<SanPham> gettSPByKeyWord(String input){
        return spDAO.getSpByKey(input);
    }

    public int getSoLuongTon(String maSP) {
        for (SanPham sp : listSP) {
            if (sp.getMaSP().equalsIgnoreCase(maSP)) {
                return sp.getSoLuong();
            }
        }
        return 0;
    }

    public boolean updateSP(Connection conn, DefaultTableModel model) {

        for (int i = 0; i < model.getRowCount(); i++) {

            String maSP = model.getValueAt(i, 1).toString();
            int sL = Integer.parseInt(model.getValueAt(i, 3).toString());

            String maKeCu = findMaKe(maSP);
            if (maKeCu == null) return false;

            int soLuongMoi = tinhSLuong(maSP, sL);

            KeKho keCu = timKeKhoTheoMa(maKeCu, conn);
            if (keCu == null) return false;

            int tongSL = spDAO.SumSLbyMaKe(conn, maKeCu);
            int khoangTrong = keCu.getSucChua() - tongSL;

            // giữ kệ cũ
            if (sL <= khoangTrong) {

                if (!spDAO.updateSP(conn, soLuongMoi, maKeCu, maSP))
                    return false;

                System.out.println("[Giữ kệ] " + maSP + " → " + maKeCu);
                continue;
            }

            // tìm kệ mới
            boolean daChuyen = false;

            ArrayList<KeKho> listKe = kkBUS.getlistkK(conn);

            for (KeKho kkho : listKe) {

                if (kkho.getMaKe().equals(maKeCu)) continue;

                int tongSLKe = spDAO.SumSLbyMaKe(conn, kkho.getMaKe());
                int khoangTrongMoi = kkho.getSucChua() - tongSLKe;

                if (soLuongMoi <= khoangTrongMoi) {

                    if (!spDAO.updateSP(conn, soLuongMoi, kkho.getMaKe(), maSP))
                        return false;

                    System.out.println("[Chuyển kệ] " + maSP + " → " + kkho.getMaKe());

                    daChuyen = true;
                    break;
                }
            }

            if (!daChuyen) {
                System.out.println("Không đủ chỗ cho " + maSP);
                return false;
            }
        }

        listSP = spDAO.getAllSanPham();
        return true;
    }
    public boolean updateSPPX(Connection conn, DefaultTableModel model) {
        System.out.println("Bắt đầu updateSPPX - Số sản phẩm cần cập nhật: " + model.getRowCount());
        for (int i=0;i<model.getRowCount();i++){

            String maSP = model.getValueAt(i, 1).toString();
            int sL = Integer.parseInt(model.getValueAt(i, 3).toString());

            System.out.println("  Xử lý sản phẩm: " + maSP + " | SL xuất: " + sL);

            String maKe = findMaKe(maSP);
            if (maKe == null) {
                System.out.println("  → Không tìm thấy maKe cho maSP: " + maSP + " (findMaKe trả về null)");
                return false;
            }

            int sLMoi = tinhSLuongConLai(maSP,sL);
            System.out.println("    → SL còn lại sau xuất: " + sLMoi);

            if (!spDAO.updateSP(conn,sLMoi,maKe,maSP)){
                System.out.println("  → Thất bại update SAN_PHAM cho maSP: " + maSP);
                return  false;
            }
            System.out.println("    → Update SAN_PHAM thành công");
        }
        System.out.println("updateSPPX hoàn tất → load lại listSP");
        listSP = spDAO.getAllSanPham();
        return true;
    }

    public int tinhSLuong(String maSP,int soLuong){
        for (SanPham sp : listSP){
            if (sp.getMaSP().equalsIgnoreCase(maSP)){
                int sLMoi = sp.getSoLuong()+soLuong;
                return sLMoi;
            }
        }return 0;
    }
    public int tinhSLuongConLai(String maSP,int soLuong){
        for (SanPham sp : listSP){
            if (sp.getMaSP().equalsIgnoreCase(maSP)){
                int sLMoi = sp.getSoLuong()-soLuong;
                return sLMoi;
            }
        }return 0;
    }

    public String findMaKe(String maSP){
        for (SanPham sp : listSP){
            if (sp.getMaSP().equalsIgnoreCase(maSP)){
                return sp.getKeKho().getMaKe();
            }
        }
        return null;
    }
    private KeKho timKeKhoTheoMa(String maKe,Connection conn) {
        for (KeKho kk : kkBUS.getlistkK(conn)) {  // hoặc dùng list cache nếu có
            if (kk.getMaKe().equalsIgnoreCase(maKe)) {
                return kk;
            }
        }
        return null;
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

        if(!Check.isValidSP(sp.getMaSP()))
            return "Mã sản phẩm không đúng định dạng";

        for(SanPham item : listSP)
            if(item.getMaSP().equalsIgnoreCase(sp.getMaSP()))
                return "Mã sản phẩm đã tồn tại";

        // nếu chưa có kệ thì gán mặc định A1
        if(sp.getKeKho() == null){
            KeKho kk = new KeKho();
            kk.setMaKe("A1");
            sp.setKeKho(kk);
        }

        if(spDAO.insert(sp)){
            listSP.add(sp);
            return "Thêm sản phẩm thành công";
        }

        return "Thêm sản phẩm thất bại";
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

    public ArrayList<SanPham> laySanPhamTheoKe(String maKe){
        return spDAO.laySanPhamTheoKe(maKe);
    }
}