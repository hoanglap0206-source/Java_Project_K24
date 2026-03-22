package DAO;

import DataBase.DBConnection;
import Model.KeKho;
import Model.SanPham;
import Model.ChiTietKe;
import DAO.ChiTietKe_DAO;
import Model.SanPhamDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class SanPham_DAO {
    private ChiTietKe_DAO chiTietKeDAO = new ChiTietKe_DAO();

//    public ArrayList<SanPham> getAllSanPham(){
//        ArrayList<SanPham> list = new ArrayList<>();
//        String sql = "SELECT * FROM SAN_PHAM";
//        try (
//                Connection conn = DBConnection.getConnection();
//                PreparedStatement ps = conn.prepareStatement(sql);
//                ResultSet rs = ps.executeQuery();
//        ) {
//            while(rs.next()){
//                SanPham sp = new SanPham();
//                KeKho kk = new KeKho();
//                sp.setMaSP(rs.getString("ma_sku"));
//                sp.setTenSP(rs.getString("ten_sp"));
//                sp.setDonViTinh(rs.getString("dvt"));
//                sp.setSoLuong(rs.getInt("sl"));
//                sp.setGiaTien(rs.getFloat("gia"));
//                kk.setMaKe(rs.getString("ma_ke"));
//                sp.setKeKho(kk);
//                list.add(sp);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return list;
//    }

//    public ArrayList<SanPham> getAllSanPham(){
//        ArrayList<SanPham> list = new ArrayList<>();
//        String sql = "SELECT * FROM SAN_PHAM";
//
//        try (
//                Connection conn = DBConnection.getConnection();
//                PreparedStatement ps = conn.prepareStatement(sql);
//                ResultSet rs = ps.executeQuery();
//        ) {
//            while(rs.next()){
//                SanPham sp = new SanPham();
//
//                sp.setMaSP(rs.getString("ma_sku"));
//                sp.setTenSP(rs.getString("ten_sp"));
//                sp.setDonViTinh(rs.getString("dvt"));
//                sp.setSoLuong(rs.getInt("sl"));
//                sp.setGiaTien(rs.getFloat("gia"));
//
//                list.add(sp);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return list;
//    }

    public ArrayList<SanPham> getAllSanPham(){
        ArrayList<SanPham> list = new ArrayList<>();

        String sql = """
        SELECT sp.ma_sku, sp.ten_sp, sp.dvt, sp.sl, sp.gia,
               ck.ma_ke
        FROM SAN_PHAM sp
        LEFT JOIN CHITIET_KE ck ON sp.ma_sku = ck.ma_sku
    """;

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery();
        ) {
            while(rs.next()){
                SanPham sp = new SanPham();

                sp.setMaSP(rs.getString("ma_sku"));
                sp.setTenSP(rs.getString("ten_sp"));
                sp.setDonViTinh(rs.getString("dvt"));
                sp.setSoLuong(rs.getInt("sl"));
                sp.setGiaTien(rs.getFloat("gia"));

                KeKho kk = new KeKho();
                kk.setMaKe(rs.getString("ma_ke"));
                sp.setKeKho(kk);

                list.add(sp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public ArrayList<SanPhamDTO> getListDTO(){
        ArrayList<SanPhamDTO> listsp = new ArrayList<>();
        String sql = """
                SELECT sp.ma_sku, sp.ten_sp, sp.trang_thai, sp.gia, sp.dvt
                , COALESCE(SUM(kksp.soluong), 0) AS tongSL
                FROM san_pham sp 
                JOIN kekho_sanpham kksp ON sp.ma_sp = kksp.ma_sp
                GROUP BY sp.ma_sp
                """;
        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery();
        ){
            while(rs.next()){
                SanPhamDTO sp = new SanPhamDTO();
                sp.setMa_sku(rs.getString("ma_sku"));
                sp.setTenSP(rs.getString("ten_sp"));
                sp.setSl(rs.getInt("tongSL"));
                sp.setTrangthai(rs.getInt("trang_thai"));
                sp.setGiaTien(rs.getFloat("gia"));
                sp.setDonViTinh(rs.getString("dvt"));
                listsp.add(sp);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return listsp;
    }

    public ArrayList<SanPhamDTO> getSpByKey(String input){
        ArrayList<SanPhamDTO> list = new ArrayList<>();
        String sql = """
        SELECT sp.*, COALESCE(SUM(kksp.soluong), 0) AS tongSL
        FROM san_pham sp
        JOIN kekho_sanpham kksp ON sp.ma_sp = kksp.ma_sp
        WHERE LOWER(sp.ma_sku) = LOWER(?)
        OR LOWER(sp.ma_sku) LIKE LOWER(CONCAT('%', ?, '%'))
        OR LOWER(sp.ten_sp) LIKE LOWER(CONCAT('%', ?, '%'))
        GROUP BY sp.ma_sp
        ORDER BY (LOWER(sp.ma_sku) = LOWER(?)) DESC;
    """;
        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
        ) {
            String keyword = "%" + input + "%";
            ps.setString(1, input);
            ps.setString(2, keyword);
            ps.setString(3, keyword);
            ps.setString(4,input);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                SanPhamDTO sp = new SanPhamDTO();
                sp.setMa_sku(rs.getString("ma_sku"));
                sp.setTenSP(rs.getString("ten_sp"));
                sp.setDonViTinh(rs.getString("dvt"));
                sp.setSl(rs.getInt("tongSL"));
                sp.setGiaTien(rs.getFloat("gia"));
                sp.setTrangthai(rs.getInt("trang_thai"));
                list.add(sp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

//    public boolean insert(SanPham sp){
////        Trước khi thêm sản phẩm cần kiểm tra sức chứa ở kệ chứa sản phẩm nếu vượt mức sức chứa thì báo lỗi
//        String sql ="INSERT INTO SAN_PHAM(ma_sku,ten_sp,dvt,sl,gia,ma_ke) VALUES (?, ?, ?,?,?,?)";
//        try (
//                Connection conn = DBConnection.getConnection();
//                PreparedStatement ps = conn.prepareStatement(sql)
//        ) {
//            ps.setString(1,sp.getMaSP());
//            ps.setString(2,sp.getTenSP());
//            ps.setString(3,sp.getDonViTinh());
//            ps.setInt(4,sp.getSoLuong());
//            ps.setFloat(5,sp.getGiaTien());
//            ps.setString(6,sp.getKeKho().getMaKe());
//            int rows = ps.executeUpdate();
//
//            if(rows > 0){
//                chiTietKeDAO.insertOrUpdate(
//                        new ChiTietKe(
//                                sp.getKeKho().getMaKe(),
//                                sp.getMaSP(),
//                                sp.getSoLuong()
//                        )
//                );
//            }
//
//            return rows > 0;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false;
//        }
//    }

    public boolean insert(SanPham sp){
        String sql ="INSERT INTO SAN_PHAM(ma_sku,ten_sp,dvt,sl,gia) VALUES (?, ?, ?, ?, ?)";

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, sp.getMaSP());
            ps.setString(2, sp.getTenSP());
            ps.setString(3, sp.getDonViTinh());
            ps.setInt(4, sp.getSoLuong());
            ps.setFloat(5, sp.getGiaTien());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

//    public boolean update(SanPham sp){
////        Nếu muốn thay đổi mã kệ,cần kiểm tra số sức chứa còn lại của kệ xem có chứa đc số lượng sản phẩm mới này ko?
//        String sql =
//                "UPDATE SAN_PHAM SET ten_sp=?,dvt=?,sl=?,gia=?,ma_ke=? WHERE ma_sku=?";
//        try (
//                Connection conn = DBConnection.getConnection();
//                PreparedStatement ps = conn.prepareStatement(sql)
//        ) {
//            ps.setString(1,sp.getTenSP());
//            ps.setString(2,sp.getDonViTinh());
//            ps.setInt(3,sp.getSoLuong());
//            ps.setFloat(4,sp.getGiaTien());
//            ps.setString(5,sp.getKeKho().getMaKe());
//            ps.setString(6,sp.getMaSP());
//            int rows = ps.executeUpdate();
//
//            if(rows > 0){
//                chiTietKeDAO.insertOrUpdate(
//                        new ChiTietKe(
//                                sp.getKeKho().getMaKe(),
//                                sp.getMaSP(),
//                                sp.getSoLuong()
//                        )
//                );
//            }
//
//            return rows > 0;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false;
//        }
//    }

    public boolean update(SanPham sp){
        String sql =
                "UPDATE SAN_PHAM SET ten_sp=?, dvt=?, sl=?, gia=? WHERE ma_sku=?";

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, sp.getTenSP());
            ps.setString(2, sp.getDonViTinh());
            ps.setInt(3, sp.getSoLuong());
            ps.setFloat(4, sp.getGiaTien());
            ps.setString(5, sp.getMaSP());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean updateSP(Connection conn,int soLuong,String maKe,String maSP){
//        Nếu muốn thay đổi mã kệ,cần kiểm tra số sức chứa còn lại của kệ xem có chứa đc số lượng sản phẩm mới này ko?
        String sql =
                "UPDATE SAN_PHAM SET sl=?,ma_ke=? WHERE ma_sku=?";
        try (
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setInt(1,soLuong);
            ps.setString(2,maKe);
            ps.setString(3,maSP);
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public ArrayList<String> getAllMaKe(Connection conn,String maSP){
        String sql = """
                SELECT kksp.ma_ke
                FROM chitiet_ke kksp
                WHERE kksp.ma_sku = ?;
                """;
        ArrayList<String> list = new ArrayList<>();
        try(
                PreparedStatement ps = conn.prepareStatement(sql)
        ){
            ps.setString(1, maSP);
            ResultSet rs = ps.executeQuery();

            while(rs.next()){
                String maKe = rs.getString("ma_ke");
                list.add(maKe);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

//    public int SumSLbyMaKe(Connection conn,String maKe){
//        int tong = 0;
//        String sql = "SELECT SUM(sl) FROM San_Pham WHERE ma_ke = ?";
//        try (
//                PreparedStatement ps = conn.prepareStatement(sql)
//        ) {
//            ps.setString(1, maKe);
//            ResultSet rs = ps.executeQuery();
//            if (rs.next()) {
//                tong = rs.getInt(1); // nếu NULL thì tự về 0
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }return tong;
//    }

//    public boolean delete(String maSP) {
//        Connection conn = null;
//        try {
//            conn = DBConnection.getConnection();
//            conn.setAutoCommit(false);
//
//            PreparedStatement ps0 = conn.prepareStatement(
//                    "DELETE FROM CHITIET_KE WHERE ma_sp = ?");
//            ps0.setString(1, maSP);
//            ps0.executeUpdate();
//            ps0.close();
//
//            //Xóa chi tiết phiếu nhập có chứa sản phẩm này
//            PreparedStatement ps1 = conn.prepareStatement(
//                    "DELETE FROM CHITIET_PHIEU_NHAP WHERE ma_sku = ?");
//            ps1.setString(1, maSP);
//            ps1.executeUpdate();
//            ps1.close();
//
//            //Xóa chi tiết phiếu xuất có chứa sản phẩm này
//            PreparedStatement ps2 = conn.prepareStatement(
//                    "DELETE FROM CHITIET_PHIEU_XUAT WHERE ma_sku = ?");
//            ps2.setString(1, maSP);
//            ps2.executeUpdate();
//            ps2.close();
//
//            //Xóa sản phẩm
//            PreparedStatement ps3 = conn.prepareStatement(
//                    "DELETE FROM SAN_PHAM WHERE ma_sku = ?");
//            ps3.setString(1, maSP);
//            int rows = ps3.executeUpdate();
//            ps3.close();
//
//            if (rows == 0) {
//                conn.rollback();
//                return false;
//            }
//
//            conn.commit();
//            return true;
//
//        } catch (Exception e) {
//            try { if (conn != null) conn.rollback(); } catch (Exception ignored) {}
//            e.printStackTrace();
//            return false;
//        } finally {
//            try {
//                if (conn != null) {
//                    conn.setAutoCommit(true);
//                    conn.close();
//                }
//            } catch (Exception ignored) {}
//        }
//    }

    public int SumSLbyMaKe(Connection conn, String maKe){
        int tong = 0;

        String sql = "SELECT SUM(so_luong) FROM CHITIET_KE WHERE ma_ke = ? GROUP BY ma_ke";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maKe);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                tong = rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return tong;
    }

    public boolean updateSL(Connection conn,String maSP,String maKe,int soLuong){
        String sql = """
                UPDATE CHITIET_KE SET sl= sl + ? WHERE ma_sku=? AND ma_ke =?
                """;
        try(
               PreparedStatement ps = conn.prepareStatement(sql);)
        {
            ps.setInt(1,soLuong);
            ps.setString(2,maSP);
            ps.setString(3,maKe);
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    //    public boolean delete(String maSP) {
//        Connection conn = null;
//
//        try {
//            conn = DBConnection.getConnection();
//            conn.setAutoCommit(false);
//
//            // xóa liên kết kệ
//            PreparedStatement ps0 = conn.prepareStatement(
//                    "DELETE FROM CHITIET_KE WHERE ma_sku = ?");
//            ps0.setString(1, maSP);
//            ps0.executeUpdate();
//
//            // xóa nhập
//            PreparedStatement ps1 = conn.prepareStatement(
//                    "DELETE FROM CHITIET_PHIEU_NHAP WHERE ma_sku = ?");
//            ps1.setString(1, maSP);
//            ps1.executeUpdate();
//
//            // xóa xuất
//            PreparedStatement ps2 = conn.prepareStatement(
//                    "DELETE FROM CHITIET_PHIEU_XUAT WHERE ma_sku = ?");
//            ps2.setString(1, maSP);
//            ps2.executeUpdate();
//
//            // xóa SP
//            PreparedStatement ps3 = conn.prepareStatement(
//                    "DELETE FROM SAN_PHAM WHERE ma_sku = ?");
//            ps3.setString(1, maSP);
//
//            int rows = ps3.executeUpdate();
//
//            if (rows == 0) {
//                conn.rollback();
//                return false;
//            }
//
//            conn.commit();
//            return true;
//
//        } catch (Exception e) {
//            try { if (conn != null) conn.rollback(); } catch (Exception ignored) {}
//            e.printStackTrace();
//            return false;
//
//        } finally {
//            try {
//                if (conn != null) {
//                    conn.setAutoCommit(true);
//                    conn.close();
//                }
//            } catch (Exception ignored) {}
//        }
//    }

//    public int countByMaKe(String maKe){ // trả về số lượng sản phẩm trong kệ
//        String sql = "SELECT COUNT(*) FROM SAN_PHAM WHERE ma_ke = ?";
//        try (
//                Connection conn = DBConnection.getConnection();
//                PreparedStatement ps = conn.prepareStatement(sql)
//        ) {
//            ps.setString(1, maKe);
//            ResultSet rs = ps.executeQuery();
//
//            if(rs.next()){
//                return rs.getInt(1);
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return 0;
//    }

    public boolean delete(String maSP) {
        Connection conn = null;

        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            // 1. xóa chi tiết kệ
            PreparedStatement ps0 = conn.prepareStatement(
                    "DELETE FROM CHITIET_KE WHERE ma_sku = ?");
            ps0.setString(1, maSP);
            ps0.executeUpdate();

            // 2. xóa sản phẩm
            PreparedStatement ps1 = conn.prepareStatement(
                    "DELETE FROM SAN_PHAM WHERE ma_sku = ?");
            ps1.setString(1, maSP);

            int rows = ps1.executeUpdate();

            if (rows == 0) {
                conn.rollback();
                return false;
            }

            conn.commit();
            return true;

        } catch (Exception e) {
            try { if (conn != null) conn.rollback(); } catch (Exception ignored) {}
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (Exception ignored) {}
        }
    }

    public int countByMaKe(String maKe){
        String sql = "SELECT COUNT(*) FROM CHITIET_KE WHERE ma_ke = ?";

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, maKe);
            ResultSet rs = ps.executeQuery();

            if(rs.next()){
                return rs.getInt(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

//    public ArrayList<SanPham> laySanPhamTheoKe(String maKe){
//        ArrayList<SanPham> list = new ArrayList<>();
//        String sql = "SELECT * FROM SAN_PHAM WHERE ma_ke = ?";
//
//        try (
//                Connection conn = DBConnection.getConnection();
//                PreparedStatement ps = conn.prepareStatement(sql)
//        ) {
//            ps.setString(1, maKe);
//            ResultSet rs = ps.executeQuery();
//
//            while(rs.next()){
//                SanPham sp = new SanPham();
//                KeKho kk = new KeKho();
//
//                sp.setMaSP(rs.getString("ma_sku"));
//                sp.setTenSP(rs.getString("ten_sp"));
//                sp.setDonViTinh(rs.getString("dvt"));
//                sp.setSoLuong(rs.getInt("sl"));
//                sp.setGiaTien(rs.getFloat("gia"));
//
//                kk.setMaKe(rs.getString("ma_ke"));
//                sp.setKeKho(kk);
//
//                list.add(sp);
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return list;
//    }

    public ArrayList<SanPham> laySanPhamTheoKe(String maKe){
        ArrayList<SanPham> list = new ArrayList<>();

        String sql =
                "SELECT sp.* " +
                        "FROM san_pham sp " +
                        "JOIN chitiet_ke ck ON sp.ma_sku = ck.ma_sku " +
                        "WHERE ck.ma_ke = ?";

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, maKe);
            ResultSet rs = ps.executeQuery();

            while(rs.next()){
                SanPham sp = new SanPham();

                sp.setMaSP(rs.getString("ma_sku"));
                sp.setTenSP(rs.getString("ten_sp"));
                sp.setDonViTinh(rs.getString("dvt"));
                sp.setSoLuong(rs.getInt("sl"));
                sp.setGiaTien(rs.getFloat("gia"));

                list.add(sp);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    // Lấy sản phẩm theo mã (mới)
    public SanPham getSanPhamByMa(String maSP) {
        String sql = "SELECT ma_sku, ten_sp, dvt, sl, gia FROM SAN_PHAM WHERE ma_sku = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maSP);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                SanPham sp = new SanPham();
                sp.setMaSP(rs.getString("ma_sku"));
                sp.setTenSP(rs.getString("ten_sp"));
                sp.setDonViTinh(rs.getString("dvt"));
                sp.setSoLuong(rs.getInt("sl"));
                sp.setGiaTien(rs.getFloat("gia"));
                return sp;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    // Lấy sản phẩm theo mã (mới, có truyền Connection)
    public SanPham getSanPhamByMa(String maSP, Connection conn) {
        String sql = "SELECT ma_sku, ten_sp, dvt, sl, gia FROM SAN_PHAM WHERE ma_sku = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maSP);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                SanPham sp = new SanPham();
                sp.setMaSP(rs.getString("ma_sku"));
                sp.setTenSP(rs.getString("ten_sp"));
                sp.setDonViTinh(rs.getString("dvt"));
                sp.setSoLuong(rs.getInt("sl"));
                sp.setGiaTien(rs.getFloat("gia"));
                return sp;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
