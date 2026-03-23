package BUS;

import DAO.PhieuXuat_DAO;
import DataBase.DBConnection;
import Model.ChiTiet_PhieuXuat;
import Model.PhieuNhap;
import Model.PhieuXuat;
import Model.SanPham;

import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

public class PhieuXuat_BUS {
    private ArrayList<PhieuXuat> listPX;
    private PhieuXuat_DAO pxDAO;
    private ChiTietPX_BUS ctpxBUS;
    private SanPham_BUS spBUS;
    public PhieuXuat_BUS() {
        pxDAO = new PhieuXuat_DAO();
        ctpxBUS = new ChiTietPX_BUS();
        spBUS = new SanPham_BUS();
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

    public boolean insertPX(Connection conn, PhieuXuat px, DefaultTableModel model,double thue){
        if(!pxDAO.inSert(conn,px)){
            System.out.println("→ Thất bại khi insert PhieuXuat chính (pxDAO.inSert)");
            return false;
        }
        System.out.println("→ Insert PhieuXuat chính thành công");
        for (int i=0;i<model.getRowCount();i++){
            PhieuXuat pX = new PhieuXuat();
            pX.setMaPX(px.getMaPX());

            String maSP = model.getValueAt(i,1).toString();
            SanPham sp = new SanPham();
            sp.setMaSP(maSP);

            int soLuong = Integer.parseInt(model.getValueAt(i,3).toString());

            double donGia = Double.parseDouble(model.getValueAt(i,4).toString());

            System.out.println("  Đang xử lý dòng " + (i + 1) + " | maSP: " + maSP + " | SL: " + soLuong + " | Đơn giá: " + donGia);
            double thueVat = thue/100.0;
            long thanhTien =0;
            thanhTien += (long) (soLuong * donGia + thueVat * soLuong * donGia);

            ChiTiet_PhieuXuat ctPX = new ChiTiet_PhieuXuat(pX,sp,soLuong,donGia,thanhTien,thue);
            if (!ctpxBUS.addCTPX(conn,ctPX)){
                System.out.println("  → Thất bại khi add CTPX cho maSP: " + maSP);
                return false;
            }
        }System.out.println("insertPX hoàn tất thành công");
        return true;
    }

    public boolean taoPX(PhieuXuat px, DefaultTableModel model,double thue){
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            if (!insertPX(conn,px,model,thue)) {
                System.out.println("Lỗi insert PhieuXuat");
                throw new SQLException("Thêm PX thất bại");
            }

            if (!spBUS.updateSPPX(conn,model)) {
                System.out.println("Lỗi updateSPPX - kiểm tra số lượng tồn / khoảng trống kệ");
                throw new SQLException("Lỗi cập nhật sản phẩm");
            }

            conn.commit();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            try {
                if (conn != null) conn.rollback();
            } catch (Exception ignored) {}
            return false;

        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (Exception ignored) {}
        }
    }

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

    public boolean deletePX(String maPX) {
        if(maPX== null || maPX.trim().isEmpty())
            return false;
        if(pxDAO.delete(maPX)) {
            listPX.removeIf(bc -> bc.getMaPX().equalsIgnoreCase(maPX));
            // Làm mới cache chi tiết phiếu nhập
            ctpxBUS.refeshData();
            return true;
        }
        return false;
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