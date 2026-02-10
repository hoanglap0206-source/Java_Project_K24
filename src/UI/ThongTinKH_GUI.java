package UI;

import javax.swing.*;
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

        //Code ở pnlCode_QLKh
//        JPanel pnlCode_QLKh = new JPanel();
//        pnlCode_QLKh.setBackground(new Color(200,201,111));//Xóa trước khi code
//        pnlCode_QLKh.add(new JLabel("GIAO DIỆN THÔNG TIN KHÁCH HÀNG", SwingConstants.CENTER));//Xóa trước khi code
        String[] columns={"Mã khách hàng","Tên khách hàng","Số điện thoại","Địa chỉ"};
        model = new DefaultTableModel(columns,0);

        TableKH= new JTable();
        TableKH.setRowHeight(30);
        TableKH.getTableHeader().setFont(new Font("Arial",Font.BOLD,13));
        TableKH.getTableHeader().setReorderingAllowed(false);

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
                kh.getDiaChi()
            };
            model.addRow(row);
        }
    }
}
