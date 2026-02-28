package UI;

import BUS.NCC_BUS;
import Model.NhaCungCap;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
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

        String[] columns={"Mã nhà cung cấp","Tên nhà cung cấp","Số điện thoại","Địa chỉ","Xem chi tiết"};
        model = new DefaultTableModel(columns,0){
            @Override
            public boolean isCellEditable(int row,int column){
                return column==4;
            }
        };

        tableNCC= new JTable(model);
        tableNCC.setRowHeight(30);
        tableNCC.getTableHeader().setFont(new Font("Arial",Font.BOLD,13));
        tableNCC.getTableHeader().setBackground(new Color(66, 160, 203));
        tableNCC.getTableHeader().setForeground(Color.WHITE);

        tableNCC.getColumnModel().getColumn(4).setCellRenderer(new buttonRender());
        tableNCC.getColumnModel().getColumn(4).setCellEditor(new ButtonEditor(new JCheckBox()));

        //Căn giữa
        DefaultTableCellRenderer center= new DefaultTableCellRenderer();
        center.setHorizontalAlignment(JLabel.CENTER);
        for(int i=0;i<4; i++){
            tableNCC.getColumnModel().getColumn(i).setCellRenderer(center);
        }


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
                ncc.getDiaChi(),
                    "Xem"
            };
            model.addRow(row);
        }
    }
    }

