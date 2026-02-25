package UI;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import BUS.KhachHang_BUS;
import Model.KhachHang;

import java.util.*;
public class ThongTinKH_GUI extends JPanel {
    private JTable TableKH;
    private DefaultTableModel model;
    private KhachHang_BUS khBUS= new KhachHang_BUS();
    public ThongTinKH_GUI(){
        setLayout(new BorderLayout());
        add(new GiaoDienChinh_TopContent(), BorderLayout.NORTH);

        String[] columns={"Mã khách hàng","Tên khách hàng","Số điện thoại","Địa chỉ","Chi tiêu"};
        model = new DefaultTableModel(columns,0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        TableKH= new JTable(model);
        TableKH.setRowHeight(30);
        TableKH.getTableHeader().setFont(new Font("Arial",Font.BOLD,13));
        TableKH.getTableHeader().setBackground(new Color(66, 160, 203));
        TableKH.getTableHeader().setForeground(Color.WHITE);
        TableKH.getTableHeader().setReorderingAllowed(false);

        DefaultTableCellRenderer center= new DefaultTableCellRenderer();
        center.setHorizontalAlignment(JLabel.CENTER);
        for(int i=0;i<4; i++){
            TableKH.getColumnModel().getColumn(i).setCellRenderer(center);
        }
        JScrollPane scrollPane=new JScrollPane(TableKH);
        add(scrollPane,BorderLayout.CENTER);

        filltoTable();
    }
    public void filltoTable(){
        model.setRowCount(0);
        ArrayList<KhachHang> list= khBUS.getListKH();
        for(KhachHang kh: list){
            Object[] row={
                kh.getMaKH(),
                kh.getHoTenKH(),
                kh.getSdt(),
                kh.getDiaChi(),
                kh.getCT()
            };
            model.addRow(row);
        }
    }
}
