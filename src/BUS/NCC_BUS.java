package BUS;

import DAO.NCC_DAO;
import Model.NhaCungCap;
import Model.ThongKeNCCDTO;

import java.util.*;

public class NCC_BUS {
    private ArrayList<NhaCungCap> listNCC;
    private NCC_DAO nccDAO;

    public NCC_BUS(){
        nccDAO=new NCC_DAO();
        this.listNCC=nccDAO.getAllNCC();
    }


    public ArrayList<NhaCungCap> getListNCC(){
        return this.listNCC;
    }

    public boolean isduplicateMaNCC(String NCC){
        for(NhaCungCap ncc:listNCC){
            if(ncc.getMaNCC().equalsIgnoreCase(NCC))
                return  true;
        }
        return false;
    }

    public String addNCC(NhaCungCap NCC){
        //Kiểm tra thêm nhà cung cấp có đúng định dạng không
        if(!Check.isValidNCC(NCC.getMaNCC()))
            return "Mã nhà cung cấp không đúng định dạng (Phải là NCCxx ví dụ NCC01)";
        if(isduplicateMaNCC(NCC.getMaNCC()))
            return "Mã nhà cung cấp đã tồn tại trên hệ thống";
        if(NCC.getTenNCC().isEmpty())
            return "Tên nhà cung cấp không được để trống!";

        if(NCC.getSdt().isEmpty())
            return"Số điên thoại không được để trống!";
        if(NCC.getDiaChi().isEmpty())
            return"Địa chỉ không được để trống!";


        if(nccDAO.insert(NCC)){
            listNCC.add(NCC);
            return "Thêm Nhà Cung Cấp thành công!";
        }
        return "Thêm Nhà Cung Cấp thất bại!";
    }

    public String updateNCC(NhaCungCap NCC){
        if(nccDAO.update(NCC)){
            for(int i=0;i< listNCC.size();i++){
                if(listNCC.get(i).getMaNCC().equalsIgnoreCase(NCC.getMaNCC()))
                    listNCC.set(i,NCC);
            }
            return "Cập nhật thành công!";
        }

        return "Cập nhật thất bại!";
    }

    public String deleteNCC(String maNCC){
        if(maNCC==null||maNCC.trim().isEmpty())
            return"Mã NCC không hợp lệ!";
        if(nccDAO.delete(maNCC)){
            listNCC.removeIf(bc->bc.getMaNCC().equalsIgnoreCase(maNCC));

            return "Xoá Nhà Cung Cấp Thành Công!";

        }
        return "Xoá Thất bại!";
    }
    public ThongKeNCCDTO getThongKe(String maNCC){
        return nccDAO.getThongKeTuSQL(maNCC);
    }

    public ArrayList<Object[]>getLichSuPhieu(String maNCC){
        return nccDAO.getLichSuPhieu(maNCC);
    }
    public void refeshData(){this.listNCC=nccDAO.getAllNCC();}
}
