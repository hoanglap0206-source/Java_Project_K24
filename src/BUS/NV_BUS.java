package BUS;

import DAO.NV_DAO;
import Model.NhanVien;
import java.util.*;
import java.util.stream.Collectors;

public class NV_BUS {
    private ArrayList<NhanVien> listNV;
    private NV_DAO nvDAO;

    public NV_BUS(){
        nvDAO= new NV_DAO();
        this.listNV= nvDAO.getAllNV();
    }

    public  ArrayList<NhanVien> getAll(){
        return listNV;
    }


    //LOGIC NGHIỆP VỤ
    /**
     * @param maNV Mã Nhân Viên(Tên đăng nhập)
     * @param matKhau Mật khẩu
     * @return Đối Tượng NhanVien nếu đúng, null nếu sai
     */
    // 1. Hàm checkLogin trả về đối tượng (Dùng khi cần lấy thông tin nhân viên sau đăng nhập)
    // 1. Hàm checkLogin: Trả về đối tượng NhanVien (Dùng khi cần lấy thông tin Họ tên, Chức vụ sau khi login)
    public NhanVien checkLogin(String maNV, String matKhau) {
        for (NhanVien nv : listNV) {
            if (nv.getMaNV().equalsIgnoreCase(maNV) && nv.getMatKhau().equals(matKhau)) {
                // Kiểm tra trạng thái: chỉ cho phép "HoatDong" hoặc "Active"
                if ("HoatDong".equalsIgnoreCase(nv.getTrangThai()) || "Active".equalsIgnoreCase(nv.getTrangThai())) {
                    return nv;
                } else {
                    // Log để kiểm tra nếu cần
                    System.out.println("Tài khoản " + maNV + " đã bị khóa.");
                    return null;
                }
            }
        }
        return null;
    }

//    // 2. Hàm login: Trả về true/false (Gọi lại checkLogin để đồng bộ logic chặn tài khoản bị khóa)
//    public boolean login(String acc, String pass) {
//        return checkLogin(acc, pass) != null;
//    }

    /**
     *Tìm kiếm nhân viên theo mã nhân viên
     */
    public List<NhanVien> search(String keyword){
        String lowerKey=keyword.toLowerCase();
        return listNV.stream()
                .filter(nv->nv.getMaNV().toLowerCase().contains(lowerKey)||
                        nv.getHoTen().toLowerCase().contains(lowerKey))
                .collect(Collectors.toList());
    }

    //Thao tác dữ liệu
    public String addNV(NhanVien nv){
        //Hàm kiểm tra thêm nhân viên có đúng định dạng không
        if(!Check.isValidManv(nv.getMaNV()))
            return "Mã nhân viên không đúng định dạng (Phải là NVxx, ví dụ NV01)";

       for(NhanVien existing: listNV) {
           if (existing.getMaNV().equalsIgnoreCase(nv.getMaNV())) {
               return "Mã nhân viên đã tồn tại!!";
           }
       }
       //kiểm tra các trường ràng buộc
        if(nv.getHoTen().isEmpty() || nv.getMatKhau().isEmpty())
            return "Họ tên và mật khẩu không đuợc đê trống";

        if(nvDAO.insert(nv)){
            listNV.add(nv);//Cập nhật RAM
            return "Thêm nhân viên thành công!";
        }
        return "Lỗi không thể thêm nhân viên";
    }

    public String updateNV(NhanVien nv){
        if(nvDAO.update(nv)){
            for(int i=0;i<=listNV.size();i++){
                if(listNV.get(i).getMaNV().equals(nv.getMaNV())){
                    listNV.set(i,nv);
                    break;
                }
            }
            return "Cập nhật thành công!";
        }
        return "Cập nhật thất bại!";
    }

    public String deleteNV(String maNV){
        if(nvDAO.delete(maNV)){
            listNV.removeIf(nv->nv.getMaNV().equals(maNV));
            return "Xoá nhân viên thành công!";
        }
        return "Không thể xoá vì nhân viên đã có phiếu nhập/xuất";
    }
    public void refesh(){
        this.listNV=nvDAO.getAllNV();
    }

    public boolean login(String acc,String pass){
        ArrayList<NhanVien> list = new ArrayList<>();
        NV_DAO nv = new NV_DAO();
        list = nv.getAccoount();
        for(NhanVien account : list){
            if(account.getMaNV().equalsIgnoreCase(acc) && account.getMatKhau().equalsIgnoreCase(pass)){
                return true;
            }
        }return false;
    }

    public String getTenNV_BUS(String maNV){
        NV_DAO nv = new NV_DAO();
        String ten = nv.getTenNV_DAO(maNV);
        return ten;
    }

    public ArrayList<NhanVien> getInfo_NV_BUS(String maNV){
        NV_DAO nv = new NV_DAO();
        if(maNV == null || maNV.isEmpty())
            return new ArrayList<>();
        ArrayList<NhanVien> list = nv.getInfo_NV_DAO(maNV);
        return list == null ? new ArrayList<>() : list;
    }
}
