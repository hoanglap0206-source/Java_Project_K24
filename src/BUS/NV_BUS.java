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
    public NhanVien checkLogin(String maNV,String matKhau){
        for(NhanVien nv: listNV){
            if(nv.getMaNV().equalsIgnoreCase(maNV) && nv.getMatKhau().equalsIgnoreCase(matKhau))
            {
                if("Đang làm việc".equals(nv.getTrangThai())) return nv;
            }
        }
        return null;
    }
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
       for(NhanVien existing: listNV) {
           if (existing.getMaNV().equalsIgnoreCase(nv.getMaNV())) {
               return "Mã nhân viên đã tồn tại!!";
           }
       }

       //kiểm tra các trường ràng buộc
        if(nv.getHoTen().isEmpty() || nv.getMatKhau().isEmpty())
            return "Họ tên vaf mật khẩu không đuợc đê trống";
        if(nvDAO.insert(nv)){
            listNV.add(nv);//Cập nhật RAM
            return "Thêm nhân viên thanhf công!";
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
        return "Xoá nhân viên thất bại";
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
}
