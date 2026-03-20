package DAO;

import DataBase.DBConnection;
import Model.ChucNang;
import Model.NhanVien;
import Model.PhanQuyen;

import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class PhanQuyen_DAO{
    public ArrayList<PhanQuyen> getAllPhanQuyen(){
        ArrayList<PhanQuyen> list =new ArrayList<>();
        String sql= "SELECT * FROM PHAN_QUYEN";
        try(
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery();
        ){
            while ((rs.next())){
                NhanVien nv =new NhanVien();
                ChucNang cn =new ChucNang();
                PhanQuyen pq =new PhanQuyen();
                nv.setMaNV(rs.getString("ma_nhan_vien"));
                cn.setMaCN(rs.getString("ma_chuc_nang"));
                pq.setNhanVien(nv);
                pq.setChucNang(cn);
                pq.setXem(rs.getBoolean("duoc_xem"));
                pq.setXoa(rs.getBoolean("duoc_xoa"));
                pq.setSua(rs.getBoolean("duoc_sua"));
                pq.setThem(rs.getBoolean("duoc_them"));
                pq.setCustom(rs.getBoolean("is_custom"));
                list.add(pq);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    public boolean insert(PhanQuyen pq){
        String sql=
                "INSERT INTO PHAN_QUYEN(ma_nhan_vien,ma_chuc_nang,duoc_xem,duoc_xoa,duoc_sua,duoc_them,is_custom) VALUES (?,?,?,?,?,?,?)";
        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1,pq.getNhanVien().getMaNV());
            ps.setString(2,pq.getChucNang().getMaCN());
            ps.setBoolean(3,pq.isXem());
            ps.setBoolean(4,pq.isXoa());
            ps.setBoolean(5,pq.isSua());
            ps.setBoolean(6,pq.isThem());
            ps.setBoolean(7,pq.isCustom()); // true = tick thủ công, false = kế thừa nhóm
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean update(PhanQuyen pq){
        String sql=
                "UPDATE PHAN_QUYEN SET duoc_xem=?,duoc_xoa=?,duoc_sua=?,duoc_them=?,is_custom=? WHERE ma_chuc_nang=? AND ma_nhan_vien=?";
        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setBoolean(1,pq.isXem());
            ps.setBoolean(2,pq.isXoa());
            ps.setBoolean(3,pq.isSua());
            ps.setBoolean(4,pq.isThem());
            ps.setBoolean(5,pq.isCustom());
            ps.setString(6,pq.getChucNang().getMaCN());
            ps.setString(7,pq.getNhanVien().getMaNV());
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean delete(String maNV,String maCN){
        String sql=
                "DELETE FROM PHAN_QUYEN WHERE ma_nhan_vien=? AND ma_chuc_nang=?";
        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1,maNV);
            ps.setString(2,maCN);
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public ArrayList<PhanQuyen> getListQuyenCaNhan(String maNhanVien){
        ArrayList<PhanQuyen> ListPhanQuyen = new ArrayList<>();

        //Lệnh này sẽ kết bảng PHAN_QUYEN và bảng DM_CHUC_NANG lại và add vào danh sach PhanQuyen
        String sql = "SELECT pq.ma_nhan_vien, pq.ma_chuc_nang, cn.ten_chuc_nang, pq.duoc_xem, pq.duoc_xoa, pq.duoc_sua, pq.duoc_them " +
                "FROM PHAN_QUYEN pq " +
                "JOIN DM_CHUC_NANG cn ON pq.ma_chuc_nang = cn.ma_chuc_nang " +
                "WHERE pq.ma_nhan_vien = ? "+
                "ORDER BY pq.ma_chuc_nang ASC";
        try(Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){

            ps.setString(1, maNhanVien);

            try(ResultSet rs = ps.executeQuery()){
                while(rs.next()){
                    NhanVien nv = new NhanVien();
                    nv.setMaNV(rs.getString("ma_nhan_vien")); //Lấy mã nhân viên

                    ChucNang cn = new ChucNang();
                    cn.setMaCN(rs.getString("ma_chuc_nang")); //Lấy mã chức năng
                    cn.setTenCN(rs.getString("ten_chuc_nang"));//Lấy tên chức năng

                    boolean xem = rs.getBoolean("duoc_xem");
                    boolean xoa = rs.getBoolean("duoc_xoa");
                    boolean sua = rs.getBoolean("duoc_sua");
                    boolean them = rs.getBoolean("duoc_them");

                    PhanQuyen pq = new PhanQuyen(nv, cn, xem, xoa, sua, them);

                    ListPhanQuyen.add(pq);
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return ListPhanQuyen;
    }

    // Ktra xem các button có trong sql hay chưa
    public boolean checkExists (String maNv, String maCN){
        String sql = "SELECT 1 FROM phan_quyen WHERE ma_nhan_vien = ? AND ma_chuc_nang = ?";
        try(Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){

            ps.setString(1, maNv);
            ps.setString(2, maCN);

            try(ResultSet rs = ps.executeQuery()){
                return rs.next();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }

    // Lấy tất cả chức năng có trong sql và quyền của 1 người
    public ArrayList<PhanQuyen> getAllChucNang_QuyenCuaNV(String maNV){
        ArrayList<PhanQuyen> list = new ArrayList<>();

        String sql = "SELECT cn.ma_chuc_nang, cn.ten_chuc_nang, " +
                "IFNULL(pq.duoc_xem, 0) as duoc_xem, " +
                "IFNULL(pq.duoc_xoa, 0) as duoc_xoa, " +
                "IFNULL(pq.duoc_sua, 0) as duoc_sua, " +
                "IFNULL(pq.duoc_them, 0) as duoc_them, " +
                "IFNULL(pq.is_custom, 0) as is_custom " +
                "FROM DM_CHUC_NANG cn " +
                "LEFT JOIN PHAN_QUYEN pq ON cn.ma_chuc_nang = pq.ma_chuc_nang AND pq.ma_nhan_vien = ? " +
                "ORDER BY cn.ma_chuc_nang ASC";

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){

            ps.setString(1, maNV);

            try (ResultSet rs = ps.executeQuery()){
                while (rs.next()){
                    NhanVien nv = new NhanVien();
                    nv.setMaNV(maNV);

                    ChucNang cn = new ChucNang();
                    cn.setMaCN(rs.getString("ma_chuc_nang"));
                    cn.setTenCN(rs.getString("ten_chuc_nang"));

                    PhanQuyen pq = new PhanQuyen(nv, cn,
                            rs.getBoolean("duoc_xem"),
                            rs.getBoolean("duoc_xoa"),
                            rs.getBoolean("duoc_sua"),
                            rs.getBoolean("duoc_them"));
                    pq.setCustom(rs.getBoolean("is_custom"));
                    list.add(pq);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Đồng bộ quyền nhóm xuống PHAN_QUYEN của nhân viên.
     * Chỉ ghi đè những dòng is_custom = FALSE (chưa bị tùy chỉnh tay).
     * Dòng is_custom = TRUE giữ nguyên, không đụng vào.
     *
     * Gọi khi: thay đổi quyền nhóm (NhomQuyen_BUS.luuThayDoiQuyenNhom)
     */
    public void syncNhomQuyenToNhanVien(String maNhom, String maCN, boolean xem, boolean xoa, boolean sua, boolean them) {
        // Lấy tất cả nhân viên thuộc nhóm này
        String sqlGetNV = "SELECT ma_nhan_vien FROM NHAN_VIEN WHERE ma_nhom = ?";
        String sqlCheck = "SELECT is_custom FROM PHAN_QUYEN " +
                "WHERE ma_nhan_vien = ? AND ma_chuc_nang = ?";
        String sqlUpdate = "UPDATE PHAN_QUYEN " +
                "SET duoc_xem=?, duoc_xoa=?, duoc_sua=?, duoc_them=? " +
                "WHERE ma_nhan_vien=? AND ma_chuc_nang=? AND is_custom=FALSE";
        String sqlInsert = "INSERT INTO PHAN_QUYEN " +
                "(ma_nhan_vien,ma_chuc_nang,duoc_xem,duoc_xoa,duoc_sua,duoc_them,is_custom) " +
                "VALUES (?,?,?,?,?,?,FALSE)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement psGetNV = conn.prepareStatement(sqlGetNV)) {

            psGetNV.setString(1, maNhom);
            try (ResultSet rs = psGetNV.executeQuery()) {
                while (rs.next()) {
                    String maNV = rs.getString("ma_nhan_vien");

                    // Kiểm tra dòng này có tồn tại trong PHAN_QUYEN chưa
                    try (PreparedStatement psCheck = conn.prepareStatement(sqlCheck)) {
                        psCheck.setString(1, maNV);
                        psCheck.setString(2, maCN);
                        try (ResultSet rsCheck = psCheck.executeQuery()) {
                            if (rsCheck.next()) {
                                boolean isCustom = rsCheck.getBoolean("is_custom");
                                if (!isCustom) {
                                    // Chưa ghi đè cập nhật theo nhóm
                                    try (PreparedStatement psUpd = conn.prepareStatement(sqlUpdate)) {
                                        psUpd.setBoolean(1, xem);  psUpd.setBoolean(2, xoa);
                                        psUpd.setBoolean(3, sua);  psUpd.setBoolean(4, them);
                                        psUpd.setString(5, maNV);  psUpd.setString(6, maCN);
                                        psUpd.executeUpdate();
                                    }
                                }
                                // is_custom=TRUE giữ nguyên quyền riêng
                            } else {
                                // Chưa có dòng nào INSERT mới với is_custom=FALSE
                                try (PreparedStatement psIns = conn.prepareStatement(sqlInsert)) {
                                    psIns.setString(1, maNV);   psIns.setString(2, maCN);
                                    psIns.setBoolean(3, xem);   psIns.setBoolean(4, xoa);
                                    psIns.setBoolean(5, sua);   psIns.setBoolean(6, them);
                                    psIns.executeUpdate();
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Khi NV tick/bỏ tick thủ công → đánh dấu is_custom = TRUE.
     * Lần sau nhóm quyền thay đổi sẽ không ghi đè dòng này nữa.
     */
    public void markAsCustom(String maNV, String maCN) {
        String sql = "UPDATE PHAN_QUYEN SET is_custom=TRUE " +
                "WHERE ma_nhan_vien=? AND ma_chuc_nang=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maNV); ps.setString(2, maCN);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Reset về kế thừa nhóm: xóa is_custom, đồng bộ lại quyền từ nhóm.
     * Dùng khi người dùng muốn "Hoàn về mặc định nhóm".
     */
    public void resetToNhom(String maNV, String maCN,
                            boolean xem, boolean xoa, boolean sua, boolean them) {
        String sql = "UPDATE PHAN_QUYEN " +
                "SET duoc_xem=?, duoc_xoa=?, duoc_sua=?, duoc_them=?, is_custom=FALSE " +
                "WHERE ma_nhan_vien=? AND ma_chuc_nang=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, xem);  ps.setBoolean(2, xoa);
            ps.setBoolean(3, sua);  ps.setBoolean(4, them);
            ps.setString(5, maNV);  ps.setString(6, maCN);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Xóa toàn bộ quyền kế thừa (is_custom=FALSE) của 1 nhân viên.
     * Dùng khi đổi nhóm để sync lại quyền từ nhóm mới.
     * Những quyền tùy chỉnh tay (is_custom=TRUE) không bị xóa.
     */
    public void deleteAllNonCustom(String maNV) {
        String sql = "DELETE FROM PHAN_QUYEN WHERE ma_nhan_vien=? AND is_custom=FALSE";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maNV);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}