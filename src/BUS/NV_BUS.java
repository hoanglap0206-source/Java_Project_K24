package BUS;

import DAO.NV_DAO;
import Model.NhanVien;
import java.util.*;
import java.util.stream.Collectors;

public class NV_BUS {
    private ArrayList<NhanVien> listNV;
    private NV_DAO nvDAO;

    public NV_BUS() {
        nvDAO   = new NV_DAO();
        listNV  = nvDAO.getAllNV();
    }

    public ArrayList<NhanVien> getAll() { return listNV; }

    public NhanVien checkLogin(String maNV, String matKhau) {
        for (NhanVien nv : listNV) {
            if (nv.getMaNV().equalsIgnoreCase(maNV) && nv.getMatKhau().equals(matKhau)) {
                if ("HoatDong".equalsIgnoreCase(nv.getTrangThai())
                        || "Active".equalsIgnoreCase(nv.getTrangThai())) {
                    return nv;
                }
                System.out.println("Tài khoản " + maNV + " đã bị khóa.");
                return null;
            }
        }
        return null;
    }

    public boolean login(String acc, String pass) {
        ArrayList<NhanVien> list = nvDAO.getAccoount();
        for (NhanVien account : list) {
            if (account.getMaNV().equalsIgnoreCase(acc)
                    && account.getMatKhau().equalsIgnoreCase(pass))
                return true;
        }
        return false;
    }

    public List<NhanVien> search(String keyword) {
        String lowerKey = keyword.toLowerCase();
        return listNV.stream()
                .filter(nv -> nv.getMaNV().toLowerCase().contains(lowerKey)
                        || nv.getHoTen().toLowerCase().contains(lowerKey))
                .collect(Collectors.toList());
    }

    public String addNV(NhanVien nv) {
        if (!Check.isValidManv(nv.getMaNV()))
            return "Mã nhân viên không đúng định dạng (Phải là NVxx, ví dụ NV01)";
        for (NhanVien existing : listNV)
            if (existing.getMaNV().equalsIgnoreCase(nv.getMaNV()))
                return "Mã nhân viên đã tồn tại!!";
        if (nv.getHoTen().isEmpty() || nv.getMatKhau().isEmpty())
            return "Họ tên và mật khẩu không được để trống";

        // Kiểm tra trùng SĐT
        if (nv.getSDT() != null && !nv.getSDT().isEmpty()) {
            for (NhanVien existing : listNV)
                if (nv.getSDT().equals(existing.getSDT()))
                    return "Số điện thoại đã được dùng bởi nhân viên " + existing.getMaNV() + "!";
        }

        if (nvDAO.insert(nv)) {
            listNV.add(nv);

            // Sau khi tạo NV mới, sync toàn bộ quyền từ nhóm xuống PHAN_QUYEN
            // với is_custom = FALSE (kế thừa nhóm, chưa tùy chỉnh tay)
            if (nv.getMaNhom() != null && !nv.getMaNhom().trim().isEmpty()) {
                DAO.PhanQuyen_DAO pqDAO = new DAO.PhanQuyen_DAO();
                DAO.NhomQuyen_DAO nhomDAO = new DAO.NhomQuyen_DAO();
                java.util.ArrayList<Model.NhomQuyenCT> dsQuyen =
                        nhomDAO.getBangQuyenCuaNhom(nv.getMaNhom());
                for (Model.NhomQuyenCT ct : dsQuyen) {
                    // Chỉ insert những quyền có ít nhất 1 bit true (giống hành vi nhóm)
                    if (ct.isXem() || ct.isXoa() || ct.isSua() || ct.isThem()) {
                        pqDAO.syncNhomQuyenToNhanVien(
                                nv.getMaNhom(),
                                ct.getChucNang().getMaCN(),
                                ct.isXem(), ct.isXoa(), ct.isSua(), ct.isThem()
                        );
                    }
                }
            }

            return "Thêm nhân viên thành công!";
        }
        return "Lỗi không thể thêm nhân viên";
    }

    public String updateNV(NhanVien nv) {
        // Lưu maNhom cũ trước khi update để so sánh
        String maNhomCu = listNV.stream()
                .filter(n -> n.getMaNV().equals(nv.getMaNV()))
                .map(NhanVien::getMaNhom)
                .findFirst().orElse(null);

        // Kiểm tra trùng SĐT — bỏ qua chính mình
        if (nv.getSDT() != null && !nv.getSDT().isEmpty()) {
            for (NhanVien existing : listNV)
                if (nv.getSDT().equals(existing.getSDT())
                        && !existing.getMaNV().equalsIgnoreCase(nv.getMaNV()))
                    return "Số điện thoại đã được dùng bởi nhân viên " + existing.getMaNV() + "!";
        }

        if (nvDAO.update(nv)) {
            for (int i = 0; i < listNV.size(); i++) {
                if (listNV.get(i).getMaNV().equals(nv.getMaNV())) {
                    listNV.set(i, nv);
                    break;
                }
            }

            // Nếu nhóm bị đổi → xóa các quyền kế thừa cũ (is_custom=FALSE)
            // rồi sync quyền từ nhóm mới
            String maNhomMoi = nv.getMaNhom();
            if (maNhomMoi != null && !maNhomMoi.trim().isEmpty()
                    && !maNhomMoi.equals(maNhomCu)) {

                DAO.PhanQuyen_DAO pqDAO = new DAO.PhanQuyen_DAO();
                DAO.NhomQuyen_DAO nhomDAO = new DAO.NhomQuyen_DAO();

                // Xóa toàn bộ quyền kế thừa (is_custom=FALSE) của NV này
                pqDAO.deleteAllNonCustom(nv.getMaNV());

                // Sync quyền từ nhóm mới xuống
                java.util.ArrayList<Model.NhomQuyenCT> dsQuyen =
                        nhomDAO.getBangQuyenCuaNhom(maNhomMoi);
                for (Model.NhomQuyenCT ct : dsQuyen) {
                    if (ct.isXem() || ct.isXoa() || ct.isSua() || ct.isThem()) {
                        pqDAO.syncNhomQuyenToNhanVien(
                                maNhomMoi,
                                ct.getChucNang().getMaCN(),
                                ct.isXem(), ct.isXoa(), ct.isSua(), ct.isThem()
                        );
                    }
                }
            }

            return "Cập nhật thành công!";
        }
        return "Cập nhật thất bại!";
    }

    public String deleteNV(String maNV) {
        if (nvDAO.delete(maNV)) {
            listNV.removeIf(nv -> nv.getMaNV().equals(maNV));
            return "Xoá nhân viên thành công!";
        }
        return "Không thể xoá vì nhân viên đã có phiếu nhập/xuất";
    }

    public void refesh() { listNV = nvDAO.getAllNV(); }

    /**
     * Tìm số nhỏ nhất chưa dùng trong dãy liên tục từ 1.
     * VD: có NV01→NV07, NV99 → trả về NV08
     *     có NV01→NV99 đầy đủ → trả về NV100
     */
    public String getNextMaNV() {
        Set<Integer> daSoDung = new HashSet<>();
        for (NhanVien nv : listNV) {
            String ma = nv.getMaNV().toUpperCase();
            if (ma.startsWith("NV")) {
                try { daSoDung.add(Integer.parseInt(ma.substring(2))); }
                catch (NumberFormatException ignored) {}
            }
        }
        int so = 1;
        while (daSoDung.contains(so)) so++;
        return String.format("NV%02d", so);
    }

    public String getTenNV_BUS(String maNV) {
        return nvDAO.getTenNV_DAO(maNV);
    }

    public ArrayList<NhanVien> getInfo_NV_BUS(String maNV) {
        if (maNV == null || maNV.isEmpty()) return new ArrayList<>();
        ArrayList<NhanVien> list = nvDAO.getInfo_NV_DAO(maNV);
        return list == null ? new ArrayList<>() : list;
    }
}