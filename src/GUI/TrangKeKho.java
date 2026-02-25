package GUI;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.*;
import java.awt.*;

public class TrangKeKho extends JPanel {

    private DefaultTableModel model;
    private JLabel lblTenKe;

    public TrangKeKho() {
        setLayout(new BorderLayout());
        setBackground(new Color(255,255,255));

        add(taoThanhCongCu(), BorderLayout.NORTH);
        add(taoBody(), BorderLayout.CENTER);
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

    private JPanel taoBody() {
        JPanel main = new JPanel(new BorderLayout(0,15));
        main.setBorder(new EmptyBorder(10,20,10,20));
        main.setBackground(Color.WHITE);

        JLabel lblTitle = new JLabel("SƠ ĐỒ KHO TỔNG");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD,18));
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(Color.WHITE);
        titlePanel.add(lblTitle, BorderLayout.WEST);

        // Panel chứa grid + bảng
        JPanel content = new JPanel(new BorderLayout(0,20));
        content.setBackground(Color.WHITE);

        content.add(taoSoDoKe(), BorderLayout.NORTH);
        content.add(taoBang(), BorderLayout.CENTER);

        main.add(titlePanel, BorderLayout.NORTH);
        main.add(content, BorderLayout.CENTER);

        return main;
    }

    private JPanel taoSoDoKe() {
        JPanel grid = new JPanel(new GridLayout(3,5,15,15));
        grid.setBorder(new EmptyBorder(10,10,10,10));

        for(int i=1;i<=15;i++){
            grid.add(taoTheKe("A"+i, i*4));
        }

        return grid;
    }

    private JPanel taoTheKe(String ten, int percent){
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(new LineBorder(new Color(200,200,200)));
        card.setPreferredSize(new Dimension(140,50));

        // panel chứa label + progress nằm ngang
        JPanel content = new JPanel(new BorderLayout(10,0));
        content.setBackground(Color.WHITE);
        content.setBorder(new EmptyBorder(15,10,15,10));

        JLabel lbl = new JLabel(ten);
        lbl.setFont(new Font("Segoe UI", Font.BOLD,12));
        lbl.setPreferredSize(new Dimension(25,25)); // cố định độ rộng cho đều

        JProgressBar bar = new JProgressBar();
        bar.setValue(percent);
        bar.setString(percent + "%");
        bar.setStringPainted(true);
        bar.setForeground(mauTheoPhanTram(percent));
        bar.setBorderPainted(false);

        content.add(lbl, BorderLayout.WEST);
        content.add(bar, BorderLayout.CENTER);

        card.add(content, BorderLayout.CENTER);

        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                capNhatBang(ten);
            }
        });

        return card;
    }

    private void capNhatBang(String maKe) {

        lblTenKe.setText("Kệ " + maKe);
        model.setRowCount(0);

        model.addRow(new Object[]{1,"SKU001","Xá xị","Thùng",5,"01/02/2026"});
        model.addRow(new Object[]{2,"SKU002","Coca Cola","Thùng",5,"01/02/2026"});
    }

    private Color mauTheoPhanTram(int p){
        if(p<50) return new Color(40,200,100);
        if(p<80) return new Color(255,170,0);
        return new Color(230,50,50);
    }

    private JPanel taoBang(){
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new CompoundBorder(
                new LineBorder(new Color(220,220,220)),
                new EmptyBorder(15,15,15,15)
        ));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE,350));

        lblTenKe = new JLabel("Kệ A1");
        lblTenKe.setFont(new Font("Segoe UI", Font.BOLD,15));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(new EmptyBorder(0, 0, 10, 0)); // khoảng cách dưới 10px
        headerPanel.add(lblTenKe, BorderLayout.WEST);

        panel.add(headerPanel, BorderLayout.NORTH);

        String[] cols = {"STT","Mã sản phẩm","Tên sản phẩm","Đơn vị tính","Số lượng","Ngày nhập"};
        model = new DefaultTableModel(cols,0);
        JTable table = new JTable(model);
        table.setRowHeight(28);

        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(210,230,255));
        table.getTableHeader().setForeground(Color.BLACK);
        table.getTableHeader().setReorderingAllowed(false);

        JScrollPane scroll = new JScrollPane(table);
        panel.add(scroll, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footer.setBackground(Color.WHITE);

        JLabel lblTong = new JLabel("Tổng số lượng: 10 thùng");
        lblTong.setFont(new Font("Segoe UI", Font.BOLD,13));
        footer.add(lblTong);

        panel.add(footer, BorderLayout.SOUTH);

        // dữ liệu demo
        model.addRow(new Object[]{1,"SKU001","Xá xị","Thùng",5,"01/02/2026"});
        model.addRow(new Object[]{2,"SKU002","Coca Cola","Thùng",5,"01/02/2026"});
        model.addRow(new Object[]{2,"SKU002","Coca Cola","Thùng",5,"01/02/2026"});
        model.addRow(new Object[]{2,"SKU002","Coca Cola","Thùng",5,"01/02/2026"});
        model.addRow(new Object[]{2,"SKU002","Coca Cola","Thùng",5,"01/02/2026"});
        model.addRow(new Object[]{2,"SKU002","Coca Cola","Thùng",5,"01/02/2026"});

        return panel;
    }
}