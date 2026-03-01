package GUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class TrangBaoCao extends JPanel {
    public TrangBaoCao() {
        setLayout(new BorderLayout(0, 15));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(15, 20, 15, 20));

        JPanel pnlNorth = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        pnlNorth.setBackground(new Color(230, 245, 255));
        pnlNorth.setBorder(new LineBorder(new Color(200, 220, 255, 1)));

        JPanel pnlSearch = new JPanel(new BorderLayout());
        JTextField txtSearch = new JTextField(" Tìm kiếm ", 15);

        txtSearch.setPreferredSize(new Dimension(150, 30));

        txtSearch.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                // Khi người dùng click chuột vào ô
                if (txtSearch.getText().equals(" Tìm kiếm ")) {
                    txtSearch.setText(""); // Xóa chữ
                    txtSearch.setForeground(Color.BLACK); // Đổi chữ sang màu đen để nhập
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                // Khi người dùng click chuột ra chỗ khác
                if (txtSearch.getText().trim().isEmpty()) {
                    txtSearch.setForeground(Color.GRAY); // Trả lại màu xám
                    txtSearch.setText(" Tìm kiếm "); // Hiện lại chữ gợi ý
                }
            }
        });
        JButton btnSearch = new JButton("🔍");

        btnSearch.setBackground(Color.WHITE);
        btnSearch.setFocusPainted(false);

        pnlSearch.add(txtSearch, BorderLayout.CENTER);
        pnlSearch.add(btnSearch, BorderLayout.EAST);



        JButton btnRefresh = new JButton("🔄 Làm mới");
        btnRefresh.setBackground(Color.WHITE);
        btnRefresh.setFocusPainted(false);


        JButton btnExcel = new JButton("📊 Xuất excel");
        btnExcel.setBackground(Color.WHITE);
        btnExcel.setFocusPainted(false);

        JPanel pnlLoai = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        pnlLoai.setOpaque(false); //set trong suốt
        pnlLoai.add(new JLabel("Loại báo cáo"));
        JComboBox<String> cboloai = new JComboBox<>(new String[]{"Nhập hàng", "Xuất hàng"});
        cboloai.setPreferredSize(new Dimension(100, 28));
        cboloai.setBackground(Color.WHITE);
        pnlLoai.add(cboloai);

        JPanel pnlThoiGian = new JPanel(new GridLayout(2, 2, 5, 5));
        pnlThoiGian.setOpaque(false);
        pnlThoiGian.add(new JLabel("Từ ngày:"));
        pnlThoiGian.add(new JTextField(8));
        pnlThoiGian.add(new JLabel("Đến ngày:"));
        pnlThoiGian.add(new JTextField(8));

        pnlNorth.add(pnlSearch);
        pnlNorth.add(btnSearch);
        pnlNorth.add(btnExcel);
        pnlNorth.add(pnlLoai);
        pnlNorth.add(pnlThoiGian);

        add(pnlNorth, BorderLayout.NORTH);


        JPanel pnlCenter = new JPanel(new BorderLayout(0, 10));
        pnlCenter.setBackground(Color.WHITE);

        JPanel pnlTableHeader = new JPanel(new BorderLayout());
        pnlTableHeader.setBackground(Color.WHITE);

        JLabel lblTitle = new JLabel("BẢNG THÔNG TIN BÁO CÁO");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 16));

        JPanel pnlMaBaoCao = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        pnlMaBaoCao.setBackground(Color.WHITE);
        pnlMaBaoCao.add(new JLabel("Mã báo cáo:"));
        JTextField txtMaBC = new JTextField("Mã tại đây", 18);
        txtMaBC.setEditable(false);
        txtMaBC.setBackground(Color.WHITE);
        pnlMaBaoCao.add(txtMaBC);

        pnlTableHeader.add(lblTitle, BorderLayout.WEST);
        pnlTableHeader.add(pnlMaBaoCao, BorderLayout.EAST);


        String[] columns = {"Mã SP", "Tên SP", "Đơn vị tính", "Số lượng (đã nhập/xuất)", "Đơn giá"};
        Object[][] data = {
                {"SP001", "Pepsi", "Lon", "100", "10.000"},
                {"SP002", "Coca", "Chai", "50", "12.000"},
                {"SP003", "Sting", "Chai", "200", "11.000"},
                {"SP004", "Trà xanh", "Chai", "5", "9.000"}
        };
        DefaultTableModel model = new DefaultTableModel(data, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(model);
        table.setRowHeight(30);


        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(147, 211, 255));
        table.getTableHeader().setOpaque(true);
        table.getTableHeader().setReorderingAllowed(false);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(new LineBorder(Color.BLACK, 1));


        pnlCenter.add(pnlTableHeader, BorderLayout.NORTH);
        pnlCenter.add(scrollPane, BorderLayout.CENTER);

        add(pnlCenter, BorderLayout.CENTER);

        JPanel pnlSouth = new JPanel(new BorderLayout());
        pnlSouth.setBackground(Color.WHITE);
        pnlSouth.setBorder(new EmptyBorder(5, 0, 0, 0));
        JLabel lblTong = new JLabel("<html>TỔNG TIỀN ĐÃ THANH TOÁN: <font color='#1A932B'><b>(Điền tổng tại đây)</b></font></html>");
        lblTong.setFont(new Font("Arial", Font.PLAIN, 14));


        JButton btnAdd=new JButton("+Thêm báo cáo");
        btnAdd.setBackground(new Color(66,133,244));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setFont(new Font("Arial",Font.BOLD,12));
        btnAdd.setFocusPainted(false);
        btnAdd.setPreferredSize(new Dimension(140,35));

        pnlSouth.add(lblTong,BorderLayout.WEST);
        pnlSouth.add(btnAdd,BorderLayout.EAST);

        add(pnlSouth,BorderLayout.SOUTH);

    }
    public static void main(String[]  args){
        SwingUtilities.invokeLater(()->
        {
            JFrame frame =new JFrame(" Bao cao");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            frame.add(new TrangBaoCao());
            frame.setSize(1000, 600);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });

    }

}
