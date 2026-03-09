package BUS;

import DAO.TonKho_DAO; // Sửa thành TonKho_DAO cho khớp với file vừa tạo
import Model.BaoCaoTonKho;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BaoCaoTonKho_BUS {

    private ArrayList<BaoCaoTonKho> listBaoCao;
    private TonKho_DAO tonKhoDAO; // Sửa tên biến DAO

    public BaoCaoTonKho_BUS() {
        tonKhoDAO = new TonKho_DAO();
        // Sửa thành getDanhSachTonKho() để gọi đúng hàm trong DAO
        this.listBaoCao = tonKhoDAO.getDanhSachTonKho();
    }

    // Lấy toàn bộ danh sách hiện có trong RAM
    public ArrayList<BaoCaoTonKho> getAll() {
        return listBaoCao;
    }

    // --- CÁC HÀM BÁO CÁO NHANH (Xử lý trực tiếp trên RAM) ---

    public List<BaoCaoTonKho> findBySku(String maSKU) {
        return listBaoCao.stream()
                .filter(bc -> bc.getSanPham().getMaSP().equalsIgnoreCase(maSKU))
                .collect(Collectors.toList());
    }

    public List<BaoCaoTonKho> getCanhBaoHangSapHet() {
        return listBaoCao.stream()
                .filter(bc -> bc.getsLTon() <= bc.getCanhBaoHH())
                .collect(Collectors.toList());
    }

    public int tinhTongHangTon() {
        return listBaoCao.stream().mapToInt(BaoCaoTonKho::getsLTon).sum();
    }

    // --- CÁC HÀM CẬP NHẬT ---

    // Lưu ý: Các hàm insert, update, delete dưới đây yêu cầu trong file TonKho_DAO
    // của bạn phải viết thêm các hàm tương ứng (insert, update, delete).
    // Tạm thời mình sửa lại getMaTonKho() thành getMaBC() để không bị báo lỗi đỏ.

    // Tạm ẩn nếu DAO chưa có hàm insert
    public String addBaoCao(BaoCaoTonKho bc) {
        if (bc.getMaTonKho().isEmpty()) return "Mã báo cáo không được để trống!"; // Sửa thành getMaBC()

        if (tonKhoDAO.insert(bc)) {
            listBaoCao.add(bc);
            return "Thêm báo cáo thành công!";
        }
        return "Thêm thất bại!";
    }


    // Tạm ẩn nếu DAO chưa có hàm update
    public String updateBaoCao(BaoCaoTonKho bc) {
        if (tonKhoDAO.update(bc)) {
            for (int i = 0; i < listBaoCao.size(); i++) {
                if (listBaoCao.get(i).getMaTonKho().equals(bc.getMaTonKho())) { // Sửa thành getMaBC()
                    listBaoCao.set(i, bc);
                    break;
                }
            }
            return "Sửa thành công!";
        }
        return "Sửa thất bại!";
    }


    // Tạm ẩn nếu DAO chưa có hàm delete
    public String deleteBaoCao(String maBC) {
        if (tonKhoDAO.delete(maBC)) {
            listBaoCao.removeIf(bc -> bc.getMaTonKho().equals(maBC)); // Sửa thành getMaBC()
            return "Xóa thành công!";
        }
        return "Xóa thất bại!";
    }


    // Hàm làm mới dữ liệu từ SQL (Refresh)
    public void refreshData() {
        this.listBaoCao = tonKhoDAO.getDanhSachTonKho();
    }
}