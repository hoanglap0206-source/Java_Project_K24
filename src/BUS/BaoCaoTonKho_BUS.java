package BUS;

import DAO.BaoCaoTonKho_DAO;
import Model.BaoCaoTonKho;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BaoCaoTonKho_BUS {
    // Danh sách lưu trữ trong RAM để xuất báo cáo nhanh theo ý giảng viên
    private ArrayList<BaoCaoTonKho> listBaoCao;
    private BaoCaoTonKho_DAO baoCaoDAO;

    public BaoCaoTonKho_BUS() {
        baoCaoDAO = new BaoCaoTonKho_DAO();
        // Bước 1: Lấy dữ liệu từ DAO để đưa lên BUS ngay khi khởi tạo
        this.listBaoCao = baoCaoDAO.getAllBaoCao();
    }

    // Lấy toàn bộ danh sách (Phục vụ hiển thị GUI)
    public ArrayList<BaoCaoTonKho> getAll() {
        return listBaoCao;
    }

    // Làm mới dữ liệu từ Database (nếu cần đồng bộ lại)
    public void refresh() {
        this.listBaoCao = baoCaoDAO.getAllBaoCao();
    }

    // --- CHỨC NĂNG BÁO CÁO NHANH (Lấy trực tiếp từ RAM) ---

    // Tìm kiếm báo cáo theo mã sản phẩm (ma_sku)
    public List<BaoCaoTonKho> filterBySanPham(String maSKU) {
        return listBaoCao.stream()
                .filter(bc -> bc.getSanPham().getMaSP().equalsIgnoreCase(maSKU))
                .collect(Collectors.toList());
    }

    // Báo cáo các sản phẩm dưới mức cảnh báo (Hàng sắp hết)
    public List<BaoCaoTonKho> getCanhBaoHangSapHet() {
        return listBaoCao.stream()
                .filter(bc -> bc.getsLTon() <= bc.getCanhBaoHH())
                .collect(Collectors.toList());
    }

    // --- CHỨC NĂNG CẬP NHẬT (Giao tiếp ngược lại với DAO) ---

    public String addBaoCao(BaoCaoTonKho bc) {
        // Kiểm tra logic: Số lượng tồn không được nhỏ hơn 0
        if (bc.getsLTon() < 0) return "Số lượng tồn không hợp lệ!";

        // Gọi DAO để thực hiện insert vào SQL
        if (baoCaoDAO.insert(bc)) {
            listBaoCao.add(bc); // Cập nhật RAM
            return "Thêm báo cáo thành công!";
        }
        return "Thêm báo cáo thất bại!";
    }

    public String updateBaoCao(BaoCaoTonKho bc) {
        // Gọi DAO để thực hiện update trong SQL
        if (baoCaoDAO.update(bc)) {
            // Cập nhật lại đối tượng trong danh sách RAM
            for (int i = 0; i < listBaoCao.size(); i++) {
                if (listBaoCao.get(i).getMaBC().equals(bc.getMaBC())) {
                    listBaoCao.set(i, bc);
                    break;
                }
            }
            return "Cập nhật thành công!";
        }
        return "Cập nhật thất bại!";
    }

    public String deleteBaoCao(String maBC) {
        // Gọi DAO để xóa trong SQL
        if (baoCaoDAO.delete(maBC)) {
            // Xóa khỏi danh sách RAM
            listBaoCao.removeIf(bc -> bc.getMaBC().equals(maBC));
            return "Xóa thành công!";
        }
        return "Xóa thất bại!";
    }
}