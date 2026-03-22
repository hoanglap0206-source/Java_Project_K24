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
    public List<BaoCaoTonKho> findbySku2 (String maSKU){
        List<BaoCaoTonKho> ketqua=new ArrayList<>();
        for (BaoCaoTonKho bc:listBaoCao){
            if (bc.getSanPham().getMaSP().equalsIgnoreCase(maSKU)){
                ketqua.add(bc);
            }
        }
        return ketqua;
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

        String maSPMoi = bc.getSanPham().getMaSP();
        boolean daCoTrongKho = false;
        for (BaoCaoTonKho item : listBaoCao) {
            if (item.getSanPham().getMaSP().equalsIgnoreCase(maSPMoi)) {
                daCoTrongKho = true;
                break;
            }
        }
        if (daCoTrongKho) {
            return "Sản phẩm [" + bc.getSanPham().getTenSP() + "] ĐÃ CÓ TRONG KHO!\nVui lòng chọn sản phẩm trên bảng và bấm nút 'Chỉnh sửa'.";
        }
        if (bc.getsLTon() < 0) {
            return "Số lượng tồn kho không được là số âm!";
        }
        if (tonKhoDAO.insert(bc)) {
            listBaoCao.add(bc);
            return "Thêm thành công!";
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


    public String deleteBaoCao(String maBC) {

        if (tonKhoDAO.delete(maBC)) {


            listBaoCao.removeIf(bc -> bc.getSanPham().getMaSP().equalsIgnoreCase(maBC));


            return "Xóa thành công (Sản phẩm đã được ẩn khỏi danh sách)!";
        }
        return "Xóa thất bại!";
    }

    public String deleteBaoCao2 (String maBC){
        for (int i = 0; i < listBaoCao.size(); i++) {
            if (listBaoCao.get(i).getSanPham().getMaSP().equalsIgnoreCase(maBC)) {
                listBaoCao.remove(i);

                return "Xóa thành công (Sản phẩm đã được ẩn khỏi danh sách)!";
            }
        }
        return "Xóa thất bại!";
    }


    // Hàm làm mới dữ liệu từ SQL (Refresh)
    public void refreshData() {
        this.listBaoCao = tonKhoDAO.getDanhSachTonKho();
    }
}