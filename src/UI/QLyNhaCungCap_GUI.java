package UI;

import BUS.NCC_BUS;
import Model.NhaCungCap;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

public class QLyNhaCungCap_GUI extends JPanel {
    private JTable tableNCC;
    private DefaultTableModel model;
    private NCC_BUS nccBUS= new NCC_BUS();
    private JPanel panel;
    public QLyNhaCungCap_GUI(){
        setLayout(new BorderLayout());
        add(new GiaoDienChinh_TopContent(), BorderLayout.NORTH);

        //Code ở pnlCode_QlyNCC
//        JPanel pnlCode_QlyNCC = new JPanel();
//        pnlCode_QlyNCC.setBackground(new Color(255,204,153));//Xóa trước khi code
//        pnlCode_QlyNCC.add(new JLabel("GIAO DIỆN QUẢN LÝ NHÀ CUNG CẤP", SwingConstants.CENTER));//Xóa trước khi code
//        add(pnlCode_QlyNCC, BorderLayout.CENTER);

        String[] columns={"Mã nhà cung cấp","Tên nhà cung cấp","Số điện thoại","Địa chỉ"};
        model = new DefaultTableModel(columns,0);

        tableNCC= new JTable(model);
        tableNCC.setRowHeight(30);
        tableNCC.getTableHeader().setFont(new Font("Arial",Font.BOLD,13));

        JScrollPane scrollPane= new JScrollPane(tableNCC);
        add(scrollPane,BorderLayout.CENTER);

        FilltoTable();
    }

    public void FilltoTable(){
        model.setRowCount(0);
        ArrayList<NhaCungCap> list=nccBUS.getListNCC();
        for(NhaCungCap ncc:list){
            Object[] row={
                ncc.getMaNCC(),
                ncc.getTenNCC(),
                ncc.getSdt(),
                ncc.getDiaChi()
            };
            model.addRow(row);
        }
    }
    }

