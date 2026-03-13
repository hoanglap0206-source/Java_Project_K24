package BUS;

import DAO.TonKho_DAO;
import Model.BaoCaoTonKho;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BaoCaoTonKho_BUS {

    private ArrayList<BaoCaoTonKho> listBaoCao;
    private TonKho_DAO tonKhoDAO;

    public BaoCaoTonKho_BUS() {
        tonKhoDAO = new TonKho_DAO();

        this.listBaoCao = tonKhoDAO.getDanhSachTonKho();
    }


    public ArrayList<BaoCaoTonKho> getAll() {
        return listBaoCao;
    }



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




    public String addBaoCao(BaoCaoTonKho bc) {
        if (bc.getMaTonKho().isEmpty()) return "Mã báo cáo không được để trống!";

        if (tonKhoDAO.insert(bc)) {
            listBaoCao.add(bc);
            return "Thêm báo cáo thành công!";
        }
        return "Thêm thất bại!";
    }



    public String updateBaoCao(BaoCaoTonKho bc) {
        if (tonKhoDAO.update(bc)) {
            for (int i = 0; i < listBaoCao.size(); i++) {
                if (listBaoCao.get(i).getMaTonKho().equals(bc.getMaTonKho())) {
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
            listBaoCao.removeIf(bc -> bc.getMaTonKho().equals(maBC));
            return "Xóa thành công!";
        }
        return "Xóa thất bại!";
    }


    // Hàm làm mới dữ liệu từ SQL (Refresh)
    public void refreshData() {
        this.listBaoCao = tonKhoDAO.getDanhSachTonKho();
    }
}