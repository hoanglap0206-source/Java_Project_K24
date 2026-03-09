package GUI;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.TableModelEvent;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import BUS.PhanQuyen_BUS;
import Model.ChucNang;
import Model.NhanVien;
import Model.PhanQuyen;

public class TrangPhanQuyen extends JPanel {
    private JTextField txtMaNV;
    private DefaultTableModel model;
    private JTable table;
    private PhanQuyen_BUS pqBUS;
    private boolean isUpdatingTable = false; // Khóa event khi đang tải data

    public TrangPhanQuyen() {
        pqBUS = new PhanQuyen_BUS();

        setLayout(new BorderLayout(0, 10));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(20,20,20,20));

        add(taoThanhCongCu(), BorderLayout.NORTH);
        add(taoNoiDung(), BorderLayout.CENTER);
        setupTableListener();
        loadDataToTable(""); //Load toàn bộ chức năng ngay khi khởi tạo (truyền chuỗi rỗng)
    }

    private JPanel taoThanhCongCu() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(Color.WHITE);
        wrapper.setBorder(new EmptyBorder(15,20,5,20));

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT,12,8));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(4,10,4,10));

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

    public JPanel taoNoiDung() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setBackground(Color.WHITE);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        top.setBackground(Color.WHITE);

        JLabel phanQuyenCho = new JLabel("Mã nhân viên: ");
        phanQuyenCho.setFont(new Font("Segoe UI", Font.BOLD, 14));

        this.txtMaNV = new JTextField();
        this.txtMaNV.setPreferredSize(new Dimension(200, 28));
        this.txtMaNV.setBackground(Color.WHITE);
        this.txtMaNV.setBorder(new CompoundBorder(
                new LineBorder(new Color(214,238,253), 2),
                new EmptyBorder(0,5,0,5)
        ));

        this.txtMaNV.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER)
                    loadDataToTable(txtMaNV.getText().trim());
            }
        });

        top.add(phanQuyenCho);
        top.add(txtMaNV);

        // Tạo bảng
        String[] columnNames = {"Mã chức năng", "Tên chức năng", "Thêm", "Sửa", "Xóa", "Xem"};

        this.model = new DefaultTableModel(null, columnNames){
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return (columnIndex < 2) ? String.class : Boolean.class;
            }
            @Override
            public boolean isCellEditable(int row, int column) {
                return column >= 2;
            }
        };

        JTable table = new JTable(model);
        table.setRowHeight(40);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(231,242,245));

        // Ẩn cột Mã CN (Cột số 0)
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setPreferredWidth(0);

        //Tô màu xanh cho ô vuông nhỏ
        table.setDefaultRenderer(Boolean.class, new TableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                boolean isChecked = (value != null && (boolean) value);

                // 1. Tạo Panel bao ngoài (giữ màu nền của bảng)
                JPanel container = new JPanel(new GridBagLayout());
                container.setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);

                // 2. Tạo Ô vuông nhỏ
                JLabel box = new JLabel();
                box.setOpaque(true);
                box.setPreferredSize(new Dimension(18, 18)); // Kích thước ô nhỏ
                box.setHorizontalAlignment(SwingConstants.CENTER);
                box.setFont(new Font("Segoe UI", Font.BOLD, 14));

                if (isChecked) {
                    box.setBackground(new Color(40, 167, 69)); // Xanh lá
                    box.setForeground(Color.WHITE);            // Dấu tích màu trắng
                    box.setText("✓");
                    box.setBorder(new LineBorder(new Color(30, 130, 60), 1));
                } else {
                    box.setBackground(Color.WHITE);
                    box.setText("");
                    box.setBorder(new LineBorder(Color.GRAY, 1));
                }

                container.add(box);
                return container;
            }
        });

        // Bọc JScrollPane
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(new LineBorder(new Color(200,200,200)));

        panel.add(top, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void loadDataToTable(String maNV){
        isUpdatingTable = true;
        this.model.setRowCount(0); // Xóa dữ liệu cũ

        ArrayList<PhanQuyen> list = pqBUS.getBangPhanQuyen(maNV);

        for(PhanQuyen pq : list){
            this.model.addRow(new Object[]{
                    pq.getChucNang().getMaCN(),
                    pq.getChucNang().getTenCN(),
                    pq.isXem(),
                    pq.isThem(),
                    pq.isSua(),
                    pq.isXoa()
            });
        }
        this.isUpdatingTable = false;
    }

    // Hàm bắt sự kiện check/uncheck
    private void setupTableListener(){
        this.model.addTableModelListener(e ->{
            if(this.isUpdatingTable) return;

            // lắng nghe hành động click checkbox
            if(e.getType() == TableModelEvent.UPDATE){
                int row = e.getFirstRow();
                String maNV = this.txtMaNV.getText().trim();

                // Nếu chưa nhập mã NV thì không cho lưu vào SQL để tránh lỗi dữ liệu trống
                if (maNV.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Vui lòng nhập Mã nhân viên trước khi phân quyền!");
                    loadDataToTable(""); // Reset lại bảng
                    return;
                }

                // Lấy thông dữ liệu từ các phần tử
                String maCN = this.model.getValueAt(row, 0).toString();
                boolean xem = (boolean) model.getValueAt(row, 2);
                boolean them = (boolean) model.getValueAt(row, 3);
                boolean sua = (boolean) model.getValueAt(row, 4);
                boolean xoa = (boolean) model.getValueAt(row, 5);

                NhanVien nv = new NhanVien();
                nv.setMaNV(maNV);

                ChucNang cn = new ChucNang();
                cn.setMaCN(maCN);

                PhanQuyen pq = new PhanQuyen(nv, cn, xem, xoa, sua, them);

                pqBUS.LuuThayDoiPQ(pq);
            }
        });
    }
}
