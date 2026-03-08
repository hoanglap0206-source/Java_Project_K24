package BUS;

import DAO.PhanQuyen_DAO;
import Model.PhanQuyen;
import java.util.ArrayList;

public class PhanQuyen_BUS {
    private ArrayList<PhanQuyen> listPQ;
    private PhanQuyen_DAO pqDAO;

    public PhanQuyen_BUS() {
        pqDAO = new PhanQuyen_DAO();
        this.listPQ = pqDAO.getAllPhanQuyen();
    }

    public ArrayList<PhanQuyen> getAll() {
        return listPQ;
    }

    public ArrayList<PhanQuyen> getListByNV(String maNV) {
        ArrayList<PhanQuyen> result = new ArrayList<>();
        for (PhanQuyen pq : listPQ) {
            if (pq.getNhanVien().getMaNV().equals(maNV)) {
                result.add(pq);
            }
        }
        return result;
    }

    public String addPhanQuyen(PhanQuyen pq) {
        // Kiểm tra xem đã tồn tại quyền này cho nhân viên này chưa
        for (PhanQuyen item : listPQ) {
            if (item.getNhanVien().getMaNV().equals(pq.getNhanVien().getMaNV())
                    && item.getChucNang().getMaCN().equals(pq.getChucNang().getMaCN())) {
                return "Quyền này đã được thiết lập trước đó!";
            }
        }

        if (pqDAO.insert(pq)) {
            listPQ.add(pq);
            return "Phân quyền thành công!";
        }
        return "Phân quyền thất bại!";
    }

    public String updatePhanQuyen(PhanQuyen pq) {
        if (pqDAO.update(pq)) {
            for (int i = 0; i < listPQ.size(); i++) {
                PhanQuyen item = listPQ.get(i);
                if (item.getNhanVien().getMaNV().equals(pq.getNhanVien().getMaNV())
                        && item.getChucNang().getMaCN().equals(pq.getChucNang().getMaCN())) {
                    listPQ.set(i, pq);
                    break;
                }
            }
            return "Cập nhật quyền thành công!";
        }
        return "Cập nhật thất bại!";
    }

    public String deletePhanQuyen(String maNV, String maCN) {
        if (pqDAO.delete(maNV, maCN)) {
            listPQ.removeIf(pq -> pq.getNhanVien().getMaNV().equals(maNV)
                    && pq.getChucNang().getMaCN().equals(maCN));
            return "Đã xóa quyền!";
        }
        return "Xóa thất bại!";
    }

    public boolean checkQuyen(String maNV, String maCN, String action) {
        for (PhanQuyen pq : listPQ) {
            if (pq.getNhanVien().getMaNV().equals(maNV) && pq.getChucNang().getMaCN().equals(maCN)) {
                switch (action.toLowerCase()) {
                    case "xem": return pq.isXem();
                    case "them": return pq.isThem();
                    case "sua": return pq.isSua();
                    case "xoa": return pq.isXoa();
                }
            }
        }
        return false;
    }

    public void refeshData(){this.listPQ=pqDAO.getAllPhanQuyen();}

    public ArrayList<PhanQuyen> getDSQuyenCaNhan(String maNhanVien){
        pqDAO = new PhanQuyen_DAO();
        if(maNhanVien == null || maNhanVien.isEmpty())
            return new ArrayList<>();
        ArrayList<PhanQuyen> ds = pqDAO.getListQuyenCaNhan(maNhanVien);
        return ds == null ? new ArrayList<>() : ds;
    }

    public ArrayList<PhanQuyen> getBangPhanQuyen(String maNV){
        return pqDAO.getAllChucNang_QuyenCuaNV(maNV);
    }

    // Lưu ngay khi người dùng click checkbox
    public void LuuThayDoiPQ(PhanQuyen pq){
        // Nếu tất cả quyền đều false thì Xóa khỏi database để không hiển thị
        if (!pq.isXem() && !pq.isThem() && !pq.isSua() && !pq.isXoa())
            pqDAO.delete(pq.getNhanVien().getMaNV(), pq.getChucNang().getMaCN());
        else // Nếu đã tồn tại quyền thì Cập nhật, nếu chưa có thì Thêm mới
            if(pqDAO.checkExists(pq.getNhanVien().getMaNV(), pq.getChucNang().getMaCN()))
                pqDAO.update(pq);
            else
                pqDAO.insert(pq);
        refeshData();
    }
}