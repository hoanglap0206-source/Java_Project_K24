package BUS;

import DAO.BaoCaoTonKho_DAO;
import Model.BaoCaoTonKho;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BaoCaoTonKho_BUS {

    private ArrayList<BaoCaoTonKho> listBaoCao;
    private BaoCaoTonKho_DAO baoCaoDAO;

    public BaoCaoTonKho_BUS() {
        baoCaoDAO = new BaoCaoTonKho_DAO();
        // 2. Vừa mở app là gọi DAO lấy hết dữ liệu từ SQL nạp vào listBaoCao
        this.listBaoCao = baoCaoDAO.getAllBaoCao();
    }

    // Lấy toàn bộ danh sách hiện có trong RAM (không cần gọi SQL)
    public ArrayList<BaoCaoTonKho> getAll() {
        return listBaoCao;
    }

    // --- CÁC HÀM BÁO CÁO NHANH (Xử lý trực tiếp trên RAM) ---

    // Tìm kiếm báo cáo theo mã sản phẩm (SKU)
    public List<BaoCaoTonKho> findBySku(String maSKU) {
        return listBaoCao.stream()
                .filter(bc -> bc.getSanPham().getMaSP().equalsIgnoreCase(maSKU))
                .collect(Collectors.toList());
    }

    // Xuất báo cáo các sản phẩm đang dưới mức cảnh báo (Sắp hết hàng)
    public List<BaoCaoTonKho> getCanhBaoHangSapHet() {
        return listBaoCao.stream()
                .filter(bc -> bc.getsLTon() <= bc.getCanhBaoHH())
                .collect(Collectors.toList());
    }


    public int tinhTongHangTon() {
        return listBaoCao.stream().mapToInt(BaoCaoTonKho::getsLTon).sum();
    }

    // --- CÁC HÀM CẬP NHẬT (Giao tiếp ngược lại với DAO và SQL) ---

    public String addBaoCao(BaoCaoTonKho bc) {
        // Kiểm tra nghiệp vụ (Check logic)
        if (bc.getMaBC().isEmpty()) return "Mã báo cáo không được để trống!";

        // Gọi DAO ghi xuống SQL
        if (baoCaoDAO.insert(bc)) {
            listBaoCao.add(bc); // Ghi thành công thì cập nhật RAM luôn
            return "Thêm báo cáo thành công!";
        }
        return "Thêm thất bại!";
    }

    public String updateBaoCao(BaoCaoTonKho bc) {
        if (baoCaoDAO.update(bc)) {
            // Cập nhật lại đối tượng tương ứng trong RAM
            for (int i = 0; i < listBaoCao.size(); i++) {
                if (listBaoCao.get(i).getMaBC().equals(bc.getMaBC())) {
                    listBaoCao.set(i, bc);
                    break;
                }
            }
            return "Sửa thành công!";
        }
        return "Sửa thất bại!";
    }

    public String deleteBaoCao(String maBC) {
        if (baoCaoDAO.delete(maBC)) {
            // Xóa khỏi RAM
            listBaoCao.removeIf(bc -> bc.getMaBC().equals(maBC));
            return "Xóa thành công!";
        }
        return "Xóa thất bại!";
    }

    // Hàm làm mới dữ liệu từ SQL (Refresh)
    public void refreshData() {
        this.listBaoCao = baoCaoDAO.getAllBaoCao();
    }
}