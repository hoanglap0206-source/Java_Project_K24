package UI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.text.ParseException;

public class PhieuXuat_GUI extends JPanel {
    private JPanel pnlCode_PhieuXuat;
    private JPanel pnlTop;

    private LineBorder lineBorder = new LineBorder(new Color(198, 226, 255), 1, true);

    public PhieuXuat_GUI(){
        this.Initcomponents();
    }

    public void Initcomponents(){
        setLayout(new BorderLayout());

        pnlCode_PhieuXuat = new JPanel(new BorderLayout());
        pnlCode_PhieuXuat.setBorder(new EmptyBorder(20, 20, 20, 20));

        ThanhTimKiem();

        pnlCode_PhieuXuat.add(pnlTop, BorderLayout.NORTH);
        add(pnlCode_PhieuXuat, BorderLayout.CENTER);
    }

    public void ThanhTimKiem(){
        //Panel của thanh Title và tìm kiếm
        pnlTop = new JPanel(new GridLayout(2,1,10,10));

        JLabel lblTitle = new JLabel("DANH SÁCH PHIẾU XUẤT");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20));

        //Panel thanh tìm kiếm
        JPanel pnlTimKiem = new JPanel(new FlowLayout());

        JPanel pnlSearchInput = new JPanel(new BorderLayout());
        pnlSearchInput.setBackground(Color.WHITE);
        pnlSearchInput.setBorder(this.lineBorder);
        pnlSearchInput.setPreferredSize(new Dimension(220, 35));

        JTextField txtSearch = new JTextField("Tìm mã phiếu");
        txtSearch.setBorder(new EmptyBorder(0, 10, 0, 0));
        txtSearch.setForeground(Color.GRAY);

        txtSearch.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                // Khi người dùng click vào ô
                if (txtSearch.getText().equals("Tìm mã phiếu")) {
                    txtSearch.setText("");           // Xóa chữ "Tìm mã phiếu"
                    txtSearch.setForeground(Color.BLACK); // Đổi màu chữ sang đen để người dùng nhập
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                // Khi người dùng click ra chỗ khác mà không nhập gì
                if (txtSearch.getText().isEmpty()) {
                    txtSearch.setForeground(Color.GRAY);
                    txtSearch.setText("Tìm mã phiếu");    // Hiện lại chữ gợi ý
                }
            }
        });

        JButton btnSearchIcon = new JButton("🔍");
        btnSearchIcon.setBackground(new Color(214,238,253));
        btnSearchIcon.setBorderPainted(false);
        btnSearchIcon.setFocusPainted(false);

        pnlSearchInput.add(txtSearch, BorderLayout.CENTER);
        pnlSearchInput.add(btnSearchIcon, BorderLayout.EAST);

        //Add tìm kiếm vào pnlTimKiem
        pnlTimKiem.add(pnlSearchInput);

        //Button làm mới
        JButton btnLamMoi = new JButton("\uD83D\uDD04");
        btnLamMoi.setBackground(Color.WHITE); // Màu xanh
        btnLamMoi.setForeground(Color.BLACK);
        btnLamMoi.setPreferredSize(new Dimension(btnLamMoi.getPreferredSize().width, 35));
        btnLamMoi.setBorder(this.lineBorder);
        btnLamMoi.setFocusPainted(false);

        //Add button làm mới vào pnlTimKiem
        pnlTimKiem.add(btnLamMoi);

        //Panel tìm kiếm từ ngày ... đến ngày ...
        JPanel pnlTimNgay = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));

        //Pnl kiểm tra có đúng định dạng
        MaskFormatter dateMask = null;
        try {
            dateMask = new MaskFormatter("##/##/####");
            dateMask.setPlaceholderCharacter('_'); // Hiển thị __/__/____ khi trống
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //Panel "Từ ngày"
        JPanel pnlTuNgay = new JPanel(new BorderLayout(5, 0));
        pnlTuNgay.setPreferredSize(new Dimension(180,35));

        JLabel lblTuNgay = new JLabel("Từ ngày: ");
        JFormattedTextField tfTuNgay = new JFormattedTextField(dateMask);

        tfTuNgay.setBorder(lineBorder);
        tfTuNgay.setBackground(Color.WHITE);

        tfTuNgay.setColumns(8); // Độ rộng vừa đủ cho 10 ký tự
        tfTuNgay.setPreferredSize(new Dimension(tfTuNgay.getPreferredSize().width + 10, 32));

        pnlTuNgay.add(lblTuNgay, BorderLayout.WEST);
        pnlTuNgay.add(tfTuNgay, BorderLayout.CENTER);

        //Panel "Đến ngày"
        JPanel pnlDenNgay = new JPanel(new BorderLayout(5, 0));
        pnlDenNgay.setPreferredSize(new Dimension(180,35));

        JLabel lblDenNgay = new JLabel("Đến ngày: ");
        JFormattedTextField tfDenNgay = new JFormattedTextField(dateMask);

        tfDenNgay.setBorder(lineBorder);
        tfDenNgay.setBackground(Color.WHITE);

        tfDenNgay.setColumns(8);
        tfDenNgay.setPreferredSize(new Dimension(tfDenNgay.getPreferredSize().width + 10, 32));
        pnlDenNgay.add(lblDenNgay, BorderLayout.WEST);
        pnlDenNgay.add(tfDenNgay, BorderLayout.CENTER);

        //Add 2 panel tìm ngày vào panel tìm ngày chính
        pnlTimNgay.add(pnlTuNgay);
        pnlTimNgay.add(pnlDenNgay);

        //Add panel tìm ngày chính vào pnlTimKiem
        pnlTimKiem.add(pnlTimNgay);

        //Tạo combobox Tên khách hàng
        String[] listKH = {"Tên nhà khách hàng", "kh1" , "kh2", "kh3", "kh4", "ncc5"};
        JComboBox<String> cbTenKH = new JComboBox<>(listKH);

        cbTenKH.setBackground(new Color(204, 227, 253));
        cbTenKH.setPreferredSize(new Dimension(cbTenKH.getPreferredSize().width + 10,35));
        cbTenKH.setBorder(this.lineBorder);
        ((JLabel) cbTenKH.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);

        //Add comboBox tên ncc vào pnlTimKiem
        pnlTimKiem.add(cbTenKH);

        //Tạo combobox Trạng thái
        String[] listTrangThai = {"Trạng thái", "Đã xuất kho" , "Đã hủy", "Chờ duyệt"};
        JComboBox<String> cbTrangThai = new JComboBox<>(listTrangThai);

        cbTrangThai.setBackground(new Color(204, 227, 253));
        cbTrangThai.setPreferredSize(new Dimension(cbTrangThai.getPreferredSize().width + 10,35));
        cbTrangThai.setBorder(this.lineBorder);
        ((JLabel) cbTrangThai.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);

        //Add comboBox tên Trạng thái vào pnlTimKiem
        pnlTimKiem.add(cbTrangThai);

        pnlTop.add(lblTitle);
        pnlTop.add(pnlTimKiem);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Quản lý phiếu xuất");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1200, 600); // Đặt kích thước cửa sổ

            // Tạo đối tượng từ class của bạn
            PhieuXuat_GUI phieuXuatPanel = new PhieuXuat_GUI();

            // Thêm panel vào frame
            frame.add(phieuXuatPanel);

            // Hiển thị ra giữa màn hình
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
