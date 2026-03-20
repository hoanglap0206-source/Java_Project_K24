package BUS;

import DAO.NhomQuyen_DAO;
import Model.NhomQuyen;
import Model.NhomQuyenCT;

import java.util.ArrayList;

public class NhomQuyen_BUS {

    private ArrayList<NhomQuyen> listNhom;
    private NhomQuyen_DAO dao;

    public NhomQuyen_BUS() {
        dao      = new NhomQuyen_DAO();
        listNhom = dao.getAllNhomQuyen();
    }

    public ArrayList<NhomQuyen> getAll() { return listNhom; }

    public String addNhomQuyen(NhomQuyen nhom) {
        if (nhom.getMaNhom() == null || nhom.getMaNhom().trim().isEmpty())
            return "Mã nhóm không được để trống!";
        if (nhom.getTenNhom() == null || nhom.getTenNhom().trim().isEmpty())
            return "Tên nhóm không được để trống!";
        if (dao.isDuplicateMaNhom(nhom.getMaNhom()))
            return "Mã nhóm \"" + nhom.getMaNhom() + "\" đã tồn tại!";
        if (dao.insertNhomQuyen(nhom)) {
            listNhom.add(nhom);
            return "Tạo nhóm quyền thành công!";
        }
        return "Tạo nhóm quyền thất bại!";
    }

    public String deleteNhomQuyen(String maNhom) {
        if (maNhom == null || maNhom.trim().isEmpty())
            return "Mã nhóm không hợp lệ!";
        if (dao.deleteNhomQuyen(maNhom)) {
            listNhom.removeIf(n -> n.getMaNhom().equalsIgnoreCase(maNhom));
            return "Xóa nhóm quyền thành công!";
        }
        return "Xóa nhóm quyền thất bại!";
    }

    public ArrayList<NhomQuyenCT> getBangQuyenCuaNhom(String maNhom) {
        return dao.getBangQuyenCuaNhom(maNhom);
    }

    /**
     * Lưu thay đổi quyền của nhóm và đồng bộ xuống PHAN_QUYEN.
     *
     * - Tất cả 4 bit = false → xóa khỏi NHOM_QUYEN_CT + xóa dòng
     *   is_custom=FALSE trong PHAN_QUYEN của các NV thuộc nhóm.
     * - Có ít nhất 1 bit true → update/insert NHOM_QUYEN_CT + sync
     *   các bit mới (kể cả bit vừa bỏ tick) xuống PHAN_QUYEN.
     */
    public void luuThayDoiQuyenNhom(String maNhom, String maCN,
                                    boolean xem, boolean xoa,
                                    boolean sua, boolean them) {
        if (!xem && !xoa && !sua && !them) {
            // Tất cả false → xóa dòng trong nhóm, deleteCT tự sync xuống NV
            dao.deleteCT(maNhom, maCN);
        } else if (dao.checkExists(maNhom, maCN)) {
            // Đã có dòng → update, updateCT tự sync xuống NV
            dao.updateCT(maNhom, maCN, xem, xoa, sua, them);
        } else {
            // Chưa có dòng → insert, insertCT tự sync xuống NV
            dao.insertCT(maNhom, maCN, xem, xoa, sua, them);
        }
    }

    public String getNextMaNhom() {
        String last = dao.getMaxMaNhom();
        if (last == null || last.isEmpty()) return "NQ01";
        try {
            int num = Integer.parseInt(last.replaceAll("[^0-9]", ""));
            return String.format("NQ%02d", num + 1);
        } catch (Exception e) {
            return "NQ" + (new java.util.Random().nextInt(98) + 1);
        }
    }

    public void refreshData() { listNhom = dao.getAllNhomQuyen(); }
}