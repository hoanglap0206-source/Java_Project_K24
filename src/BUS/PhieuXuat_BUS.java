package BUS;

import DAO.PhieuXuat_DAO;
import Model.ChiTiet_PhieuXuat;
import Model.PhieuXuat;
import Model.SanPham;

import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;

public class PhieuXuat_BUS {
    private ArrayList<PhieuXuat> listPX;
    private PhieuXuat_DAO pxDAO;

    public PhieuXuat_BUS() {
        pxDAO = new PhieuXuat_DAO();
        // Tải danh sách phiếu xuất từ DB lên RAM
        this.listPX = pxDAO.getAllPhieuXuat();
    }

    public ArrayList<PhieuXuat> getListPX() {
        return listPX;
    }

    public String addPhieuXuat(PhieuXuat px) {
        //Kiểm tra thêm phiếu xuất có đúng định dạng không
        if(!Check.isValidPX(px.getMaPX()))
            return "Mã phiểu xuất phải đúng định dạng (Phải là PXxx ví dụ PX01)";

        // Kiểm tra trùng mã
        for (PhieuXuat item : listPX)
            if (item.getMaPX().equalsIgnoreCase(px.getMaPX()))
                return "Mã phiếu xuất đã tồn tại!";

        if (pxDAO.insert(px)) {
            listPX.add(px); // Cập nhật RAM
            return "Thêm phiếu xuất thành công!";
        }
        return "Thêm thất bại!";
    }

//    public boolean insertPX(PhieuXuat px, DefaultTableModel model,){
//        if(!pxDAO.insert(px)){
//            return false;
//        }
//        for (int i=0;i<model.getRowCount();i++){
//            PhieuXuat pX = new PhieuXuat();
//            pX.setMaPX(px.getMaPX());
//
//            String maSP = model.getValueAt(i,1).toString();
//            SanPham sp = new SanPham();
//            sp.setMaSP(maSP);
//
//            int soLuong = Integer.parseInt(model.getValueAt(i,3).toString());
//
//            double donGia = Double.parseDouble(model.getValueAt(i,5).toString());
//
//            long thanhTien =0;
//            thanhTien += soLuong*donGia;
//
//            ChiTiet_PhieuXuat ctPX = new ChiTiet_PhieuXuat(pX,sp,soLuong,donGia,thanhTien);
//        }
//    }
    public String updatePhieuXuat(PhieuXuat px) {
        if (pxDAO.update(px)) {
            for (int i = 0; i < listPX.size(); i++)
                if (listPX.get(i).getMaPX().equals(px.getMaPX())) {
                    listPX.set(i, px); // Cập nhật RAM
                    break;
                }
            return "Cập nhật thành công!";
        }
        return "Cập nhật thất bại!";
    }

    public String deletePhieuXuat(String maPX) {
        if (pxDAO.delete(maPX)) {
            listPX.removeIf(px -> px.getMaPX().equals(maPX)); // Cập nhật RAM
            return "Xóa phiếu xuất thành công!";
        }
        return "Xóa thất bại !";
    }

    public ArrayList<PhieuXuat> search(String keyword) {
        ArrayList<PhieuXuat> result = new ArrayList<>();
        String key = keyword.toLowerCase();
        for (PhieuXuat px : listPX)
            if (px.getMaPX().toLowerCase().contains(key) ||
                    px.getKhachHang().getMaKH().toLowerCase().contains(key)) {
                result.add(px);
            }
        return result;
    }

    public void refeshData(){listPX=pxDAO.getAllPhieuXuat();}
}