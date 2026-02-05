package BUS;

import DAO.KeKho_DAO;
import Model.KeKho;
import java.util.*;
public class KeKho_BUS {
    private ArrayList<KeKho> listKK;
    private KeKho_DAO kkDAO;

    public KeKho_BUS(){
        kkDAO= new KeKho_DAO();
        this.listKK=kkDAO.getAllKeKho();
    }

    public ArrayList<KeKho> getListKK(){
        return this.listKK;
    }

    public String addKK(KeKho kk){
        if(kk.getMaKe().trim().isEmpty()) return "Mã kệ không được để trống!";
        if(kk.getSucChua()<=0) return "Sức chứa phải lớn hơn 0!";

        if(kkDAO.insert(kk)){
          listKK.add(kk);
          return "Thêm kệ kho thành công!";
        }
        return "Thêm thất bại!";
    }

    public String updateKK(KeKho kk){
        if(kkDAO.update(kk)){
            for(int i=0;i<listKK.size();i++){
                if(listKK.get(i).getMaKe().equals(kk.getMaKe()))
                {
                    listKK.set(i,kk);
                    break;
                }
            }
            return "Cập Nhật thành công!";
        }
        return"Cập nhật thất bại!";
    }

    public String deleteKK(String maKe){
        if(kkDAO.delete(maKe)){
            listKK.removeIf(kk->kk.getMaKe().equals(maKe));
            return "Xoá kệ kho thành công!";
        }
        return "Xoá thất bại!";
    }

}
