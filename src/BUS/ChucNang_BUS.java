package BUS;

import DAO.ChucNang_DAO;
import Model.ChucNang;
import java.util.*;
public class ChucNang_BUS {
    private ArrayList<ChucNang> listCN;
    private ChucNang_DAO cnDAO;

    public ChucNang_BUS(){
        cnDAO=new ChucNang_DAO();
        this.listCN= cnDAO.getAllChucNang();
    }

    public ArrayList<ChucNang> getListCN(){
        return this.listCN;
    }

    public String addChucNang(ChucNang cn){
        if(cn.getMaCN().trim().isEmpty()||cn.getTenCN().trim().isEmpty())
            return "Mã và tên chức năng không được để trống!";

        for(ChucNang item:listCN){
            if(item.getMaCN().equalsIgnoreCase(cn.getMaCN()))
                return "Chức năng này đã tồn tại!";
        }
        if(cnDAO.insert(cn)){
            listCN.add(cn);
            return "Thêm chức năng thành công!";
        }
        return "Thêm thất bại!";
    }

    public String updateChucNang(ChucNang cn){
        if(cnDAO.update(cn)){
            for(int i=0;i<listCN.size();i++){
                if(listCN.get(i).getMaCN().equals(cn.getMaCN())){
                    listCN.set(i,cn);
                    break;
                }
            }
            return "Cập nhật thành công !";
        }
        return "Cập nhật thất bại!";
    }

    public String deleteChucNang(String maCC){
        if(cnDAO.delete(maCC)){
            listCN.removeIf(cn->cn.getMaCN().equalsIgnoreCase(maCC));
            return "Xoá chức năng thành công";
        }
        return "Xoá chức năng thất bại!";
    }

    public ChucNang getByMa(String maCN){
        for(ChucNang cn: listCN){
            if(cn.getMaCN().equalsIgnoreCase(maCN))
                return cn;
        }
        return null;
    }

    public void refeshData(){this.listCN=cnDAO.getAllChucNang();}

}
