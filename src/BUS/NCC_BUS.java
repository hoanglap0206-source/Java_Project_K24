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
        if(NCC.getTenNCC().trim().isEmpty())
            return "Tên nhà cung cấp không được để trống!";

        if(NCC.getSdt().trim().isEmpty())
            return"Số điên thoại không được để trống!";
        if(!NCC.getSdt().matches("\\d{10}"))
            return "Số điện thoại phải có đúng 10 số";
        if(NCC.getDiaChi().trim()  .isEmpty())
            return"Địa chỉ không được để trống!";


        if(nccDAO.insert(NCC)){
            listNCC.add(NCC);
            return "Thêm Nhà Cung Cấp thành công!";
        }
        return "Thêm Nhà Cung Cấp thất bại!";
    }

    public String updateNCC(NhaCungCap NCC) {
        // 1. Validate dữ liệu trước khi gọi DAO
        if (NCC.getMaNCC() == null || !Check.isValidNCC(NCC.getMaNCC()))
            return "Mã nhà cung cấp không hợp lệ!";

        if (NCC.getTenNCC().trim().isEmpty())
            return "Tên nhà cung cấp không được để trống!";

        if (NCC.getSdt().trim().isEmpty())
            return "Số điện thoại không được để trống!";

        if (!NCC.getSdt().matches("\\d{10}")) // Kiểm tra SĐT phải là 10 số
            return "Số điện thoại phải có 10 chữ số!";

        if (NCC.getDiaChi().trim().isEmpty())
            return "Địa chỉ không được để trống!";

        // 2. Chỉ khi dữ liệu sạch mới gọi xuống Database
        if (nccDAO.update(NCC)) {
            this.refeshData(); // Cập nhật lại list trong bộ nhớ
            return "Cập nhật thành công!";
        }

        return "Cập nhật thất bại tại hệ thống!";
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
