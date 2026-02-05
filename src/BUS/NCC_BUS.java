package BUS;

import DAO.NCC_DAO;
import Model.NhaCungCap;
import java.util.*;

public class NCC_BUS {
    private ArrayList<NhaCungCap> listNCC;
    private NCC_DAO nccDAO;
    public NCC_BUS(){
        nccDAO=new NCC_DAO();
        this.listNCC=nccDAO.getAllNCC();
    }
    public ArrayList<NhaCungCap>getListNCC(){
        return this.listNCC;
    }
    public String addNCC(NhaCungCap NCC){
        if(NCC.getMaNCC().isEmpty()||NCC.getTenNCC().isEmpty()) return "Mã nhà cung cấp và tên nhà cung cấp không được để trống!";
        if(nccDAO.insert(NCC)){

            listNCC.add(NCC);
            return "Thêm Nhà Cung Cấp thành công!";
        }
        return "Thêm Nhà Cung Cấp thất bại!";
    }

    public String updateNCC(NhaCungCap NCC){
        if(nccDAO.update(NCC))
        {

            return "Cập nhật thành công!";
        }
        return "Cập nhật thất bại!";
    }

    public String deleteNCC(String maNCC){
        if(maNCC==null||maNCC.trim().isEmpty()) return"Mã NCC không hợp lệ!";
        if(nccDAO.delete(maNCC)){
            listNCC.removeIf(bc->bc.getTenNCC().equalsIgnoreCase(maNCC));
            return "Xoá Nhà Cung Cấp Thành Công!";
        }
        return "Xoá Thất bại!";
    }
    public void refeshData(){this.listNCC=nccDAO.getAllNCC();}

}
