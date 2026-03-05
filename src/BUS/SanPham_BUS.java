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
        listSP = spDAO.getAllSanPham(); // thêm để load data listSP
    }

    public ArrayList<SanPham> getAll() {
        spDAO = new SanPham_DAO();
        return spDAO.getAllSanPham();
    }

    public ArrayList<SanPham> gettSPByKeyWord(String input){
        spDAO = new SanPham_DAO();
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
        kkBUS = new KeKho_BUS();
        for (int i = 0; i < model.getRowCount(); i++) {

            String maSP = model.getValueAt(i, 1).toString();
            int sL = Integer.parseInt(model.getValueAt(i, 3).toString());

            String maKeCu = findMaKe(maSP);

            int soLuongMoi = tinhSLuong(maSP, sL);
            int soLuongCu = soLuongMoi - sL;

            ArrayList<KeKho> listKe = kkBUS.getlistKK(conn);
            boolean daUpdate = false;

            for (KeKho kkho : listKe) {
                if (soLuongMoi > 0 && soLuongMoi <= kkho.getKhoangTrong()) {

                    if (!spDAO.updateSP(conn, soLuongMoi, kkho.getMaKe(), maSP)) {
                        return false;
                    }

                    int tongSlKeMoi = spDAO.SumSLbyMaKe(conn, kkho.getMaKe());
                    int khoangTrongMoi = kkho.getSucChua() - tongSlKeMoi;
                    kkho.setKhoangTrong(khoangTrongMoi);

                    if (!kkBUS.updatekK(conn, kkho)) {
                        return false;
                    }
                    daUpdate = true;
                    break;
                }
            }
            if (!daUpdate) {
                return false;
            }
            // trả lại chỗ cho kệ cũ
            int tongSlKeCu = spDAO.SumSLbyMaKe(conn, maKeCu);
            int khoangTrongKeCu = tongSlKeCu - soLuongCu;

            if (!kkBUS.updateKKtheoKT(conn, maKeCu, khoangTrongKeCu)) {
                return false;
            }
        }

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

    public String findMaKe(String maSP){
        for (SanPham sp : listSP){
            if (sp.getMaSP().equalsIgnoreCase(maSP)){
                return sp.getKeKho().getMaKe();
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

    public ArrayList<SanPham> laySanPhamTheoKe(String maKe){
        return spDAO.laySanPhamTheoKe(maKe);
    }
}