package GUI;

import BUS.BaoCao_BUS;
import Model.BaoCao;
import Model.ChiTietBaoCao;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.SwingConstants;

public class TrangBaoCao extends JPanel {

    private JTable table;
    private DefaultTableModel model;
    private BaoCao_BUS bus;
    private TableRowSorter<DefaultTableModel> rowSorter; // Đã khai báo sorter

    private JComboBox<String> cboloai;
    private JTextField txtTuNgay;
    private JTextField txtDenNgay;
    private JLabel lblTong;
    private JTextField txtSearch;
    private JTextField txtMaBC = new JTextField();

    public TrangBaoCao() {
        bus = new BaoCao_BUS();

        setLayout(new BorderLayout(0, 15));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(15, 20, 15, 20));

        JPanel pnlNorth = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        pnlNorth.setBackground(new Color(230, 245, 255));
        pnlNorth.setBorder(new LineBorder(new Color(200, 220, 255, 1)));

        JPanel pnlSearch = new JPanel(new BorderLayout());
        txtSearch = new JTextField(" Tìm kiếm ", 15);
        txtSearch.setPreferredSize(new Dimension(150, 30));

        txtSearch.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (txtSearch.getText().equals(" Tìm kiếm ")) {
                    txtSearch.setText("");
                    txtSearch.setForeground(Color.BLACK);
                }
            }
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (txtSearch.getText().trim().isEmpty()) {
                    txtSearch.setForeground(Color.GRAY);
                    txtSearch.setText(" Tìm kiếm ");
                }
            }
        });

        JButton btnSearchIcon = new JButton("🔍");
        btnSearchIcon.setBackground(Color.WHITE);
        btnSearchIcon.setFocusPainted(false);
        pnlSearch.add(txtSearch, BorderLayout.CENTER);
        pnlSearch.add(btnSearchIcon, BorderLayout.EAST);

        JButton btnRefresh = new JButton("🔄 Làm mới");
        btnRefresh.setBackground(Color.WHITE);
        btnRefresh.setFocusPainted(false);

        btnRefresh.addActionListener(e -> {
            txtTuNgay.setText("");
            txtDenNgay.setText("");
            txtSearch.setText(" Tìm kiếm ");
            txtSearch.setForeground(Color.GRAY);
            if (rowSorter != null) rowSorter.setRowFilter(null);
            loadBaoCao();
        });

        JButton btnExcel = new JButton("📊 Xuất excel");
        btnExcel.setBackground(Color.WHITE);
        btnExcel.setFocusPainted(false);

        JPanel pnlLoai = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        pnlLoai.setOpaque(false);
        pnlLoai.add(new JLabel("Loại báo cáo"));
        cboloai = new JComboBox<>(new String[]{"Nhập hàng", "Xuất hàng"});
        cboloai.setPreferredSize(new Dimension(100, 28));
        cboloai.setBackground(Color.WHITE);
        cboloai.addActionListener(e -> loadBaoCao());
        pnlLoai.add(cboloai);

        JPanel pnlThoiGian = new JPanel(new GridLayout(2, 2, 5, 5));
        pnlThoiGian.setOpaque(false);
        pnlThoiGian.add(new JLabel("Từ ngày:"));
        txtTuNgay = new JTextField(8);
        txtTuNgay.setToolTipText("Nhập yyyy-MM-dd rồi nhấn Enter");
        txtTuNgay.addActionListener(e -> loadBaoCao());
        pnlThoiGian.add(txtTuNgay);

        pnlThoiGian.add(new JLabel("Đến ngày:"));
        txtDenNgay = new JTextField(8);
        txtDenNgay.setToolTipText("Nhập yyyy-MM-dd rồi nhấn Enter");
        txtDenNgay.addActionListener(e -> loadBaoCao());
        pnlThoiGian.add(txtDenNgay);

        pnlNorth.add(pnlSearch);
        pnlNorth.add(btnRefresh);
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

        txtMaBC.setEditable(false);
        txtMaBC.setBackground(Color.WHITE);
        pnlMaBaoCao.add(txtMaBC);

        pnlTableHeader.add(lblTitle, BorderLayout.WEST);
        pnlTableHeader.add(pnlMaBaoCao, BorderLayout.EAST);


        String[] columns = {"Mã SP", "Tên SP", "Đơn vị tính", "Số lượng (nhập)", "Số lượng (xuất)", "Đơn giá", "Xem chi tiết"};

        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6;
            }
        };

        table = new JTable(model);
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }


        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));

        table.getTableHeader().setPreferredSize(new Dimension(0,35));

        table.getTableHeader().setBackground(new Color(230, 230, 230));
        table.getTableHeader().setReorderingAllowed(false);
        table.setShowGrid(true);
        table.setGridColor(Color.BLACK);
        table.setShowVerticalLines(true);
        table.setShowHorizontalLines(true);


        JTableHeader header = table.getTableHeader();

        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {

                JLabel lbl = (JLabel) super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);

                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                lbl.setBackground(new Color(210,230,255)); // màu xanh
                lbl.setForeground(Color.BLACK);
                lbl.setOpaque(true);
                lbl.setBorder(BorderFactory.createMatteBorder(0,0,2,1,Color.BLACK));

                return lbl;
            }
        });


        rowSorter = new TableRowSorter<>(model);
        table.setRowSorter(rowSorter);
        setupSearchLogic();

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(new LineBorder(Color.BLACK, 1));

        table.getColumnModel().getColumn(6).setCellRenderer(new ButtonRenderer());
        table.getColumnModel().getColumn(6).setCellEditor(new ButtonEditor(new JCheckBox()));

        pnlCenter.add(pnlTableHeader, BorderLayout.NORTH);
        pnlCenter.add(scrollPane, BorderLayout.CENTER);

        add(pnlCenter, BorderLayout.CENTER);

        JPanel pnlSouth = new JPanel(new BorderLayout());
        pnlSouth.setBackground(Color.WHITE);
        pnlSouth.setBorder(new EmptyBorder(5, 0, 0, 0));

        lblTong = new JLabel("<html>TỔNG TIỀN ĐÃ THANH TOÁN: <font color='#1A932B'><b>0 VNĐ</b></font></html>");
        lblTong.setFont(new Font("Arial", Font.PLAIN, 14));

        JButton btnAdd = new JButton("Xoá báo cáo");
        btnAdd.setBackground(new Color(244, 66, 66));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setFont(new Font("Arial", Font.BOLD, 12));
        btnAdd.setFocusPainted(false);
        btnAdd.setPreferredSize(new Dimension(140, 35));

        pnlSouth.add(lblTong, BorderLayout.WEST);
        pnlSouth.add(btnAdd, BorderLayout.EAST);

        add(pnlSouth, BorderLayout.SOUTH);

        // Tải dữ liệu ngay khi vừa bật Form
        loadBaoCao();
    }

    // Hàm thiết lập event cho thanh tìm kiếm (Đã được gọi ở trên)
    private void setupSearchLogic() {
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { performSearch(); }
            @Override
            public void removeUpdate(DocumentEvent e) { performSearch(); }
            @Override
            public void changedUpdate(DocumentEvent e) { performSearch(); }

            private void performSearch() {
                String text = txtSearch.getText();
                // Bỏ qua nếu là chữ gợi ý hoặc rỗng
                if (text.trim().isEmpty() || text.equals("Tìm kiếm") || text.equals(" Tìm kiếm ")) {
                    rowSorter.setRowFilter(null);
                } else {
                    rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text.trim()));
                }
            }
        });
    }

    // Hàm gọi BUS lấy dữ liệu
    private void loadBaoCao() {
        try {
            String loai = cboloai.getSelectedItem().toString();
            String strTuNgay = txtTuNgay.getText().trim();
            String strDenNgay = txtDenNgay.getText().trim();

            if (strTuNgay.isEmpty()) strTuNgay = "1970-01-01";
            if (strDenNgay.isEmpty()) strDenNgay = "2100-12-31";

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date tuNgay = sdf.parse(strTuNgay);
            Date denNgay = sdf.parse(strDenNgay);

            BaoCao bc = bus.taoBaoCao(loai, tuNgay, denNgay);
            phatSinhMaBaoCao();

            model.setRowCount(0);
            for (ChiTietBaoCao ct : bc.getDanhSachChiTiet()) {
                Object[] row = {
                        ct.getSanPham().getMaSP(),
                        ct.getSanPham().getTenSP(),
                        ct.getSanPham().getDonViTinh(),
                        ct.getSoLuongNhap(),
                        ct.getSoLuongXuat(),
                        String.format("%,.0f", ct.getSanPham().getGiaTien()),
                        "Xem"
                };
                model.addRow(row);
            }

            lblTong.setText("<html>TỔNG TIỀN ĐÃ THANH TOÁN: <font color='#1A932B'><b>"
                    + String.format("%,.0f", bc.getTongTien()) + " VNĐ</b></font></html>");

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi định dạng ngày! Vui lòng nhập chuẩn theo yyyy-MM-dd", "Lỗi", JOptionPane.ERROR_MESSAGE);
            // Reset ô nhập để không bị lỗi liên hoàn
            txtTuNgay.setText("");
            txtDenNgay.setText("");
            loadBaoCao();
        }
    }

    // --- CÁC CLASS RENDERER VÀ EDITOR ---

    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setText("Xem");
            setFocusPainted(false);
            setBackground(new Color(220, 220, 220));
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
            return this;
        }
    }
    public void phatSinhMaBaoCao() {
        String loai = cboloai.getSelectedItem().toString();


        String prefix = loai.equals("Nhập hàng") ? "BCN" : "BCX";


        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyy_HHmmss");
        String timeStamp = sdf.format(new Date());


        String maBaoCaoMoi = prefix + "-" + timeStamp;
        txtMaBC.setText(maBaoCaoMoi);
    }

    class ButtonEditor extends DefaultCellEditor {
        protected JButton button;
        private JTable table;
        private int currentRow;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton("Xem");
            button.setFocusPainted(false);
            button.setBackground(new Color(147, 211, 255));
            button.addActionListener(e -> {
                fireEditingStopped();
                hienThiChiTietSanPham();
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            this.table = table;
            this.currentRow = row;
            return button;
        }

        private void hienThiChiTietSanPham() {
            // Lấy Mã SP và Tên SP ở dòng mà người dùng vừa click
            String maSP = table.getValueAt(currentRow, 0).toString();
            String tenSP = table.getValueAt(currentRow, 1).toString();

            // Lấy loại báo cáo đang hiển thị (Nhập hay Xuất) từ Combobox
            String loai = cboloai.getSelectedItem().toString();

            // Lấy cửa sổ cha để làm nền
            Window parentWindow = SwingUtilities.getWindowAncestor(button);

            // Mở Dialog
            ChiTietSanPham_Dialog dialog = new ChiTietSanPham_Dialog(parentWindow, maSP, tenSP, loai);
            dialog.setVisible(true);
        }


    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Báo cáo");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(new TrangBaoCao());
            frame.setSize(1000, 600);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}