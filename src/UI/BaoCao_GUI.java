package UI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class BaoCao_GUI extends JPanel {
    public BaoCao_GUI(){
        setLayout(new BorderLayout(10,10));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(10,20,10,20));

        JPanel pnlNorth=new JPanel(new BorderLayout());
        pnlNorth.setBackground(Color.WHITE);
        JPanel pnlInput=new JPanel(new FlowLayout(FlowLayout.LEFT,20,0));
        pnlInput.setBackground(Color.WHITE);

        JPanel pnlLoai=createSelectionBox("Loại báo cáo",new String[]{"Nhập hàng","Xuất hàng"},100);
        JPanel pnlThoiGian=createDateBox("Thời gian");

        pnlInput.add(pnlLoai);
        pnlInput.add(pnlThoiGian);

        JPanel pnlSearch=new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlSearch.setBackground(Color.WHITE);

        JTextField txtSearch=new JTextField();
        txtSearch.setPreferredSize(new Dimension(250,30));
        txtSearch.setBorder(new LineBorder(Color.GRAY));

        JButton btnRefresh=new JButton("Làm mới");
        btnRefresh.setBackground(new Color(146,208,80));
        btnRefresh.setFocusPainted(false);


        pnlSearch.add(new JLabel("🔍 "));
        pnlSearch.add(txtSearch);
        pnlSearch.add(btnRefresh);

        pnlNorth.add(pnlInput, BorderLayout.WEST);
        pnlNorth.add(pnlSearch, BorderLayout.EAST);
        add(pnlNorth, BorderLayout.NORTH);


        String[] columns = {"Mã SP", "Tên SP", "Đơn vị tính", "Số lượng (đã nhập/xuất)", "Đơn giá"};
        Object[][] data = {
                {"SP001", "Pepsi", "Lon", "100", "10.000"},
                {"SP002", "Coca", "Chai", "50", "12.000"},
                {"SP003", "Sting", "Chai", "200", "11.000"},
                {"SP004", "Trà xanh", "Chai", "5", "9.000"}
        };
        DefaultTableModel model = new DefaultTableModel(data, columns){
            @Override
            public boolean isCellEditable(int row,int column){
             return false;
            }
        };
        JTable table = new JTable(model);
        table.setRowHeight(30);


        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
                new LineBorder(Color.BLACK), "Bảng thông tin báo cáo",
                TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION,
                new Font("Arial", Font.ITALIC, 12)));

        add(scrollPane, BorderLayout.CENTER);

        JPanel pnlSouth = new JPanel(new BorderLayout());
        pnlSouth.setBackground(Color.WHITE);
        pnlSouth.setPreferredSize(new Dimension(0, 100));


        JPanel pnlTotal = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 30));
        pnlTotal.setBackground(Color.WHITE);
        JLabel lblTotal = new JLabel("TỔNG TIỀN ĐÃ THANH TOÁN");
        lblTotal.setFont(new Font("Arial", Font.BOLD, 13));
        JTextField txtTotal = new JTextField();
        txtTotal.setPreferredSize(new Dimension(200, 35));
        txtTotal.setEditable(false);
        txtTotal.setBorder(new LineBorder(Color.BLACK));

        pnlTotal.add(lblTotal);
        pnlTotal.add(txtTotal);


        JPanel pnlAction = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 30));
        pnlAction.setBackground(Color.WHITE);

        JButton btnExcel = new JButton("Xuất Excel");
        btnExcel.setBackground(new Color(230, 230, 230));
        btnExcel.setPreferredSize(new Dimension(120, 40));

        JButton btnAdd = new JButton("Thêm báo cáo");
        btnAdd.setBackground(new Color(102, 255, 102));
        btnAdd.setPreferredSize(new Dimension(150, 40));
        btnAdd.setFont(new Font("Arial", Font.BOLD, 12));

        pnlAction.add(btnExcel);
        pnlAction.add(btnAdd);

        pnlSouth.add(pnlTotal, BorderLayout.WEST);
        pnlSouth.add(pnlAction, BorderLayout.EAST);
        add(pnlSouth, BorderLayout.SOUTH);
    }

    private JPanel createSelectionBox(String title, String[] options, int width) {
        JPanel pnl = new JPanel(new BorderLayout(5, 5));
        pnl.setBackground(Color.WHITE);
        pnl.add(new JLabel(title), BorderLayout.NORTH);

        JList<String> list = new JList<>(options);
        list.setBorder(new LineBorder(Color.BLACK));
        list.setFixedCellWidth(width);
        pnl.add(list, BorderLayout.CENTER);
        return pnl;
    }

    private JPanel createDateBox(String title) {
        JPanel pnl = new JPanel(new BorderLayout(5, 5));
        pnl.setBackground(Color.WHITE);
        pnl.add(new JLabel(title), BorderLayout.NORTH);

        JPanel pnlFields = new JPanel(new GridLayout(2, 2, 5, 5));
        pnlFields.setBackground(Color.WHITE);
        pnlFields.setBorder(new LineBorder(Color.BLACK));

        pnlFields.add(new JLabel(" Từ ngày:"));
        pnlFields.add(new JTextField(10));
        pnlFields.add(new JLabel(" Đến ngày:"));
        pnlFields.add(new JTextField(10));

        pnl.add(pnlFields, BorderLayout.CENTER);
        return pnl;
    }

}
