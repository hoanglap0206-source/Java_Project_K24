package BUS;

import DAO.ChiTietPX_DAO;
import Model.ChiTiet_PhieuXuat;
import java.util.ArrayList;

public class ChiTietPX_BUS {
    private ArrayList<ChiTiet_PhieuXuat> listCTPX;
    private ChiTietPX_DAO ctDAO;

    public ChiTietPX_BUS() {
        ctDAO = new ChiTietPX_DAO();
        this.listCTPX = ctDAO.getAllCtPX();
    }

    public ArrayList<ChiTiet_PhieuXuat> getAll() {
        return listCTPX;
    }


    public ArrayList<ChiTiet_PhieuXuat> getListByMaPX(String maPX) {
        ArrayList<ChiTiet_PhieuXuat> result = new ArrayList<>();
        for (ChiTiet_PhieuXuat ct : listCTPX) {
            if (ct.getPhieuXuat().getMaPX().equalsIgnoreCase(maPX)) {
                result.add(ct);
            }
        }
        return result;
    }


    public String addCTPX(ChiTiet_PhieuXuat ct) {
        if (ct.getSoLuong() <= 0) return "Số lượng xuất phải lớn hơn 0!";

        // Kiểm tra trùng sản phẩm trong cùng 1 phiếu
        for (ChiTiet_PhieuXuat item : listCTPX) {
            if (item.getPhieuXuat().getMaPX().equals(ct.getPhieuXuat().getMaPX())
                    && item.getSanPham().getMaSP().equals(ct.getSanPham().getMaSP())) {
                return "Sản phẩm này đã tồn tại trong phiếu xuất!";
            }
        }

        // Tính toán trước khi lưu (ThanhTien = SL * DonGia + VAT)
        // Lưu ý: Tùy vào cách bạn tính VAT (số tiền hay %) mà điều chỉnh công thức này

        if (ctDAO.insert(ct)) {
            listCTPX.add(ct);
            return "Thêm chi tiết phiếu xuất thành công!";
        }
        return "Thêm thất bại!";
    }


    public String updateCTPX(ChiTiet_PhieuXuat ct) {
        if (ctDAO.update(ct)) {
            for (int i = 0; i < listCTPX.size(); i++) {
                ChiTiet_PhieuXuat item = listCTPX.get(i);
                if (item.getPhieuXuat().getMaPX().equals(ct.getPhieuXuat().getMaPX())
                        && item.getSanPham().getMaSP().equals(ct.getSanPham().getMaSP())) {
                    listCTPX.set(i, ct);
                    break;
                }
            }
            return "Cập nhật thành công!";
        }
        return "Cập nhật thất bại!";
    }


    public String deleteCTPX(String maPX, String maSP) {
        if (ctDAO.delete(maPX, maSP)) {
            listCTPX.removeIf(ct -> ct.getPhieuXuat().getMaPX().equals(maPX)
                    && ct.getSanPham().getMaSP().equals(maSP));
            return "Đã xóa sản phẩm khỏi phiếu xuất!";
        }
        return "Xóa thất bại!";
    }

    public void refeshData(){listCTPX=ctDAO.getAllCtPX();}
}