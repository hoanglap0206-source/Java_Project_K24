package GUI;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class TrangTonKho extends JPanel {

    public TrangTonKho() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JPanel pnlCompactInputs = new JPanel(new GridLayout(2, 1, 0, 5));
        pnlCompactInputs.setOpaque(false);


        JPanel pnlWarn = createCompactInput("Cảnh báo (ngày):", new Color(255, 255, 204), Color.BLACK);

        JPanel pnlExp = createCompactInput("Sắp hết hạn (SP):", new Color(255, 102, 0), Color.WHITE);

        pnlCompactInputs.add(pnlWarn);
        pnlCompactInputs.add(pnlExp);


        add(taoThanhCongCu(), BorderLayout.NORTH);



        String[] columnNames = {"Mã SP", "Tên SP", "Đơn vị tính", "Số lượng", "Đơn giá", "Ngày nhập", "Ngày hết hạn", "Mã kệ"};
        Object[][] data = {
                {"SP001", "Pepsi", "Lon", "100", "10.000", "01/01/2024", "01/01/2025", "K01"},
                {"SP002", "Coca", "Chai", "50", "12.000", "15/02/2024", "15/02/2025", "K02"},
                {"SP003", "Sting", "Chai", "200", "11.000", "10/03/2024", "10/03/2025", "K01"},
                {"SP004", "Trà xanh", "Chai", "5", "9.000", "05/01/2024", "05/04/2024", "K03"}
        };

        //hàm không cho thay đổi data//
        DefaultTableModel model=new DefaultTableModel(data,columnNames){
            @Override
            public boolean isCellEditable(int row,int column){
                return false;
            }
        };

        JTable table = new JTable(model);
        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(230, 230, 230));

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
    }


    private JPanel createCompactInput(String labelText, Color bgColor, Color textColor) {
        JPanel pnl = new JPanel(new BorderLayout(5, 0));
        pnl.setBackground(Color.WHITE);

        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("Arial", Font.BOLD, 11));

        JTextField txt = new JTextField();
        txt.setBackground(bgColor);
        txt.setForeground(textColor);
        txt.setCaretColor(textColor);
        txt.setFont(new Font("Arial", Font.BOLD, 12));
        // Tạo viền mỏng và padding cho ô nhập
        txt.setBorder(new CompoundBorder(
                new LineBorder(Color.LIGHT_GRAY, 1),
                new EmptyBorder(2, 5, 2, 5)
        ));
        txt.setPreferredSize(new Dimension(80, 25));

        pnl.add(lbl, BorderLayout.WEST);
        pnl.add(txt, BorderLayout.CENTER);
        return pnl;
    }

    private JPanel taoThanhCongCu() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(Color.WHITE);
        wrapper.setBorder(new EmptyBorder(15,20,5,20));

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT,12,8));
        panel.setBackground(new Color(231,242,245));
        panel.setBorder(new CompoundBorder(
                new LineBorder(new Color(198,226,255), 2),
                new EmptyBorder(2,12,2,12)
        ));


        // Thanh tìm kiếm
        JTextField txtSearch = new JTextField("Tìm kiếm");
        txtSearch.setColumns(15);
        txtSearch.setBorder(BorderFactory.createEmptyBorder(5,10,5,10));
        txtSearch.setForeground(Color.GRAY);

        JPanel pnlSearchInput = new JPanel(new BorderLayout());
        pnlSearchInput.setBackground(Color.WHITE);
        pnlSearchInput.setPreferredSize(new Dimension(260,30));
        pnlSearchInput.setBorder(new CompoundBorder(
                new LineBorder(new Color(198,226,255), 2, true),
                new EmptyBorder(0,2,0,0)
        ));

        JButton btnSearchIcon = new JButton("🔍");
        btnSearchIcon.setBackground(new Color(214,238,253));
        btnSearchIcon.setBorderPainted(false);
        btnSearchIcon.setFocusPainted(false);
        btnSearchIcon.setCursor(new Cursor(Cursor.HAND_CURSOR));

        pnlSearchInput.add(txtSearch, BorderLayout.CENTER);
        pnlSearchInput.add(btnSearchIcon, BorderLayout.EAST);

        txtSearch.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                // Khi người dùng click vào ô
                if (txtSearch.getText().equals("Tìm kiếm")) {
                    txtSearch.setText("");           // Xóa chữ "Tìm kiếm"
                    txtSearch.setForeground(Color.BLACK); // Đổi màu chữ sang đen để người dùng nhập
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                // Khi người dùng click ra chỗ khác mà không nhập gì
                if (txtSearch.getText().isEmpty()) {
                    txtSearch.setForeground(Color.GRAY);
                    txtSearch.setText("Tìm kiếm");    // Hiện lại chữ gợi ý
                }
            }
        });


        // Nút làm mới
        JButton btnLamMoi = new JButton("⟳ Làm mới");
        Style.styleButton(btnLamMoi);


        // Combobox Lọc
        String[] itemLoc = {"Lọc", "1", "2", "3", "4", "5"};
        JComboBox<String> comboBoxLoc = new JComboBox<>(itemLoc);

        // Style cơ bản
        comboBoxLoc.setBackground(new Color(214, 238, 253));
        comboBoxLoc.setPreferredSize(new Dimension(90, 30));
        comboBoxLoc.setFont(new Font("Segoe UI", Font.BOLD, 13));
        comboBoxLoc.setCursor(new Cursor(Cursor.HAND_CURSOR));
        comboBoxLoc.setSelectedIndex(0);

        // Placeholder "Lọc"
        comboBoxLoc.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {

                JLabel lbl = (JLabel) super.getListCellRendererComponent(
                        list, value, index, isSelected, cellHasFocus);

                lbl.setHorizontalAlignment(SwingConstants.CENTER);

                if (index == -1 && comboBoxLoc.getSelectedIndex() == -1) {
                    lbl.setText("Lọc");
                    lbl.setForeground(Color.GRAY);
                }
                return lbl;
            }
        });


        // Các nút khác
        JButton btnEdit = new JButton("Chỉnh sửa");
        Style.styleButton(btnEdit);
        JButton btnDelete = new JButton("Xóa");
        Style.styleButton(btnDelete);
        JButton btnAdd = new JButton("+ Thêm");
        Style.styleButton(btnAdd);
        JButton btnExcel = new JButton("Xuất excel");
        Style.styleButton(btnExcel);

        // Thêm vào panel
        panel.add(pnlSearchInput);
        panel.add(btnLamMoi);
        panel.add(comboBoxLoc);
        panel.add(btnEdit);
        panel.add(btnDelete);
        panel.add(btnAdd);
        panel.add(btnExcel);

        wrapper.add(panel, BorderLayout.CENTER);
        return wrapper;
    }
}