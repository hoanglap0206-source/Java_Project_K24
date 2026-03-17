package GUI;

import UI.TonKho_GUI;
import BUS.BaoCaoTonKho_BUS;
import Model.BaoCaoTonKho;
import Model.SanPham;
import BUS.SanPham_BUS;

import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.SwingConstants;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class TrangTonKho extends JPanel implements QuyenTrang {
    private JTable table;
    private DefaultTableModel model;
    private BaoCaoTonKho_BUS bus;
    private TableRowSorter<DefaultTableModel> rowSorter;


    private JButton btnAdd, btnEdit, btnDelete, btnRefresh, btnExcel;


    private JSplitPane splitPane;
    private JPanel panelForm;
    private JLabel lblFormTitle;
    private JTextField txtMaSP, txtTenSP, txtSL, txtDG, txtCanhBao, txtMaKe;
    private JComboBox<String> cbDVT;
    private boolean isEditMode = false;

    public TrangTonKho() {
        bus = new BaoCaoTonKho_BUS();
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);


        add(taoThanhCongCu(), BorderLayout.NORTH);


        JPanel pnlCenter = new JPanel(new BorderLayout());
        pnlCenter.setBackground(Color.WHITE);
        pnlCenter.setBorder(new EmptyBorder(10, 20, 20, 20));


        JPanel tableWrapper = new JPanel(new BorderLayout());


        JPanel pnlCompactInputs = new JPanel(new GridLayout(1, 2, 10, 0));
        pnlCompactInputs.setOpaque(false);
        pnlCompactInputs.setBorder(new EmptyBorder(0, 0, 10, 0));

        tableWrapper.add(pnlCompactInputs, BorderLayout.NORTH);
        tableWrapper.add(taoBang(), BorderLayout.CENTER);


        panelForm = taoFormBenPhai();
        panelForm.setVisible(false);

        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tableWrapper, panelForm);
        splitPane.setResizeWeight(1.0);
        splitPane.setDividerSize(6);
        splitPane.setBorder(null);
        pnlCenter.add(splitPane, BorderLayout.CENTER);

        add(pnlCenter, BorderLayout.CENTER);


        loadDataToTable();
        addEvents();
    }



    private JScrollPane taoBang() {
        String[] columnNames = {"Mã SP", "Tên SP", "Đơn vị tính", "Số lượng", "Đơn giá", "Số ngày hết hạn", "Mã kệ"};
        model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        table = new JTable(model);
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        table.getTableHeader().setPreferredSize(new Dimension(0, 35));
        table.getTableHeader().setBackground(new Color(230, 230, 230));
        table.getTableHeader().setReorderingAllowed(false);
        table.setShowGrid(true);
        table.setGridColor(Color.LIGHT_GRAY);

        JTableHeader header = table.getTableHeader();
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                lbl.setBackground(new Color(210, 230, 255));
                lbl.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, Color.GRAY));
                return lbl;
            }
        });

        rowSorter = new TableRowSorter<>(model);
        table.setRowSorter(rowSorter);

        JScrollPane scrollPane = new JScrollPane(table);
        return scrollPane;
    }

    private JPanel taoFormBenPhai() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setPreferredSize(new Dimension(320, 0));
        outer.setBackground(new Color(245, 247, 250));
        outer.setBorder(new MatteBorder(0, 1, 0, 0, new Color(210, 220, 235)));

        JPanel pnl = new JPanel();
        pnl.setLayout(new BoxLayout(pnl, BoxLayout.Y_AXIS));
        pnl.setBackground(new Color(245, 247, 250));
        pnl.setBorder(new EmptyBorder(20, 20, 20, 20));


        lblFormTitle = new JLabel("THÊM SẢN PHẨM");
        lblFormTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblFormTitle.setForeground(new Color(30, 80, 160));
        pnl.add(lblFormTitle);
        pnl.add(Box.createVerticalStrut(20));


        txtMaSP = new JTextField();
        txtTenSP = new JTextField();
        cbDVT = new JComboBox<>(new String[]{"Lon", "Chai", "Thùng"});
        txtSL = new JTextField();
        txtDG = new JTextField();
        txtCanhBao = new JTextField();
        txtMaKe = new JTextField();

        pnl.add(nhomField("Mã sản phẩm", txtMaSP)); pnl.add(Box.createVerticalStrut(10));
        pnl.add(nhomField("Tên sản phẩm", txtTenSP)); pnl.add(Box.createVerticalStrut(10));
        pnl.add(nhomCombo("Đơn vị tính", cbDVT)); pnl.add(Box.createVerticalStrut(10));
        pnl.add(nhomField("Số lượng", txtSL)); pnl.add(Box.createVerticalStrut(10));
        pnl.add(nhomField("Đơn giá", txtDG)); pnl.add(Box.createVerticalStrut(10));
        pnl.add(nhomField("Cảnh báo (ngày)", txtCanhBao)); pnl.add(Box.createVerticalStrut(10));
        pnl.add(nhomField("Mã kệ", txtMaKe)); pnl.add(Box.createVerticalStrut(20));


        JButton btnSave = new JButton("💾  Lưu");
        btnSave.setBackground(new Color(37, 120, 220));
        btnSave.setForeground(Color.WHITE);
        btnSave.setFocusPainted(false);
        btnSave.addActionListener(e -> luuDuLieu());

        JButton btnCancel = new JButton("✕  Hủy");
        btnCancel.setBackground(Color.LIGHT_GRAY);
        btnCancel.setFocusPainted(false);
        btnCancel.addActionListener(e -> hideFormPanel());

        JPanel pnlBtn = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        pnlBtn.setOpaque(false);
        pnlBtn.add(btnSave); pnlBtn.add(btnCancel);
        pnl.add(pnlBtn);

        outer.add(pnl, BorderLayout.CENTER);
        return outer;
    }


    private JPanel nhomField(String label, JTextField field) {
        JPanel g = new JPanel(new BorderLayout(0, 5));
        g.setOpaque(false);
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        field.setPreferredSize(new Dimension(280, 30));
        g.add(lbl, BorderLayout.NORTH);
        g.add(field, BorderLayout.CENTER);
        return g;
    }

    private JPanel nhomCombo(String label, JComboBox<String> combo) {
        JPanel g = new JPanel(new BorderLayout(0, 5));
        g.setOpaque(false);
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        combo.setPreferredSize(new Dimension(280, 30));
        g.add(lbl, BorderLayout.NORTH);
        g.add(combo, BorderLayout.CENTER);
        return g;
    }

    private JPanel createCompactInput(String labelText, Color bgColor, Color textColor) {
        JPanel pnl = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        pnl.setOpaque(false);
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("Arial", Font.BOLD, 11));
        JTextField txt = new JTextField(5);
        txt.setBackground(bgColor);
        txt.setForeground(textColor);
        txt.setBorder(new CompoundBorder(new LineBorder(Color.LIGHT_GRAY, 1), new EmptyBorder(2, 5, 2, 5)));
        pnl.add(lbl); pnl.add(txt);
        return pnl;
    }

    private JPanel taoThanhCongCu() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(Color.WHITE);
        wrapper.setBorder(new EmptyBorder(15, 20, 5, 20));

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        panel.setBackground(Color.WHITE);

        JTextField txtSearch = new JTextField("Tìm kiếm", 15);
        txtSearch.setForeground(Color.GRAY);
        txtSearch.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        JPanel pnlSearchInput = new JPanel(new BorderLayout());
        pnlSearchInput.setBackground(Color.WHITE);
        pnlSearchInput.setBorder(new LineBorder(new Color(198, 226, 255), 2, true));
        JButton btnSearchIcon = new JButton("🔍");
        btnSearchIcon.setBackground(new Color(214, 238, 253));
        btnSearchIcon.setBorderPainted(false);
        pnlSearchInput.add(txtSearch, BorderLayout.CENTER);
        pnlSearchInput.add(btnSearchIcon, BorderLayout.EAST);

        txtSearch.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) { if (txtSearch.getText().equals("Tìm kiếm")) { txtSearch.setText(""); txtSearch.setForeground(Color.BLACK); } }
            public void focusLost(java.awt.event.FocusEvent e) { if (txtSearch.getText().isEmpty()) { txtSearch.setForeground(Color.GRAY); txtSearch.setText("Tìm kiếm"); } }
        });
        txtSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener(){
            public void insertUpdate(javax.swing.event.DocumentEvent e) { s(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { s(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { s(); }
            private void s() { String t = txtSearch.getText(); rowSorter.setRowFilter(t.trim().isEmpty() || t.equals("Tìm kiếm") ? null : RowFilter.regexFilter("(?i)" + t)); }
        });

        btnRefresh = new JButton("⟳ Làm mới"); Style.styleButton(btnRefresh);
        btnEdit = new JButton("Chỉnh sửa"); Style.styleButton(btnEdit);
        btnDelete = new JButton("Xóa"); Style.styleButton(btnDelete);
        btnAdd = new JButton("+ Thêm"); Style.styleButton(btnAdd);
        btnExcel = new JButton("Xuất excel"); Style.styleButton(btnExcel);

        panel.add(pnlSearchInput); panel.add(btnRefresh);
        panel.add(btnEdit); panel.add(btnDelete); panel.add(btnAdd); panel.add(btnExcel);
        wrapper.add(panel, BorderLayout.CENTER);
        return wrapper;
    }


    private void addEvents() {
        btnAdd.addActionListener(e -> {
            lblFormTitle.setText("THÊM SẢN PHẨM");
            txtMaSP.setEditable(true);
            clearForm();
            isEditMode = false;
            showFormPanel();
        });

        btnEdit.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm cần sửa!");
                return;
            }

            lblFormTitle.setText("SỬA SẢN PHẨM");
            txtMaSP.setText(model.getValueAt(row, 0).toString());
            txtMaSP.setEditable(false);
            txtTenSP.setText(model.getValueAt(row, 1).toString());
            cbDVT.setSelectedItem(model.getValueAt(row, 2).toString());
            txtSL.setText(model.getValueAt(row, 3).toString());
            txtDG.setText(model.getValueAt(row, 4).toString().replace(",", "").replace(".", ""));
            txtCanhBao.setText(model.getValueAt(row, 5).toString());
            txtMaKe.setText(model.getValueAt(row, 6) != null ? model.getValueAt(row, 6).toString() : "");

            isEditMode = true;
            showFormPanel();
        });

        btnDelete.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm cần xoá!");
                return;
            }
            String masp = table.getValueAt(row, 0).toString();
            String tensp = table.getValueAt(row, 1).toString();

            if (JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa: " + tensp + "?", "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                String tb = bus.deleteBaoCao(masp);
                JOptionPane.showMessageDialog(this, tb);
                if (tb.contains("thành công")) {
                    loadDataToTable();
                    hideFormPanel();
                }
            }
        });

        btnRefresh.addActionListener(e -> {
            bus.refreshData();
            loadDataToTable();
            hideFormPanel();
        });
        btnExcel.addActionListener(e -> {
            if (table.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "Không có dữ liệu trong bảng để xuất Excel!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Chọn vị trí lưu file Báo Cáo Tồn Kho");

            // Tự động tạo tên file có ngày giờ hiện tại cho chuyên nghiệp
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("ddMMyyyy_HHmmss");
            String defaultFileName = "TonKho_" + sdf.format(new java.util.Date()) + ".xlsx";
            fileChooser.setSelectedFile(new java.io.File(defaultFileName));

            if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                java.io.File fileToSave = fileChooser.getSelectedFile();
                String filePath = fileToSave.getAbsolutePath();

                // Đảm bảo đuôi file luôn là .xlsx
                if (!filePath.endsWith(".xlsx")) {
                    filePath += ".xlsx";
                }

                try (org.apache.poi.xssf.usermodel.XSSFWorkbook workbook = new org.apache.poi.xssf.usermodel.XSSFWorkbook();
                     java.io.FileOutputStream out = new java.io.FileOutputStream(filePath)) {

                    org.apache.poi.xssf.usermodel.XSSFSheet sheet = workbook.createSheet("Tồn Kho Hiện Tại");

                    // --- 1. Tạo Style cho dòng Tiêu đề (In đậm, Căn giữa) ---
                    org.apache.poi.xssf.usermodel.XSSFCellStyle headerStyle = workbook.createCellStyle();
                    org.apache.poi.xssf.usermodel.XSSFFont font = workbook.createFont();
                    font.setBold(true);
                    headerStyle.setFont(font);
                    headerStyle.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER);

                    // --- 2. Ghi dòng Tiêu đề ---
                    org.apache.poi.xssf.usermodel.XSSFRow headerRow = sheet.createRow(0);
                    for (int i = 0; i < table.getColumnCount(); i++) {
                        org.apache.poi.xssf.usermodel.XSSFCell cell = headerRow.createCell(i);
                        cell.setCellValue(table.getColumnName(i));
                        cell.setCellStyle(headerStyle);
                    }

                    // --- 3. Ghi Dữ liệu từ bảng JTable ra Excel ---
                    for (int i = 0; i < table.getRowCount(); i++) {
                        org.apache.poi.xssf.usermodel.XSSFRow row = sheet.createRow(i + 1);
                        for (int j = 0; j < table.getColumnCount(); j++) {
                            Object value = table.getValueAt(i, j); // Lấy đúng dữ liệu đang hiển thị trên màn hình

                            if (value != null) {
                                // Xử lý thông minh: Ép các cột Số lượng (3), Đơn giá (4), Cảnh báo (5) về dạng SỐ
                                // để khi mở Excel lên nó có thể tính toán hàm SUM(), AVG()... được ngay.
                                if (j == 3 || j == 4 || j == 5) {
                                    String strNum = value.toString().replaceAll("[,\\s]", ""); // Xóa dấu phẩy ngàn
                                    try {
                                        row.createCell(j).setCellValue(Double.parseDouble(strNum));
                                    } catch (NumberFormatException ex) {
                                        row.createCell(j).setCellValue(value.toString()); // Nếu lỗi thì cứ in ra dạng chữ
                                    }
                                } else {
                                    row.createCell(j).setCellValue(value.toString()); // Các cột Mã, Tên thì in dạng chữ
                                }
                            } else {
                                row.createCell(j).setCellValue("");
                            }
                        }
                    }

                    // Tự động căn chỉnh độ rộng của các cột cho vừa khít với chữ
                    for (int i = 0; i < table.getColumnCount(); i++) {
                        sheet.autoSizeColumn(i);
                    }

                    // Xuất file và đóng luồng
                    workbook.write(out);
                    JOptionPane.showMessageDialog(this, "Xuất file Excel Tồn Kho thành công!\nĐã lưu tại: " + filePath, "Thành công", JOptionPane.INFORMATION_MESSAGE);

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Có lỗi xảy ra khi xuất file Excel:\n" + ex.getMessage(), "Lỗi Xuất File", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
        });
    }


    private void luuDuLieu() {
        try {
            String ma = txtMaSP.getText().trim();
            String ten = txtTenSP.getText().trim();
            if (ma.isEmpty() || ten.isEmpty()) { JOptionPane.showMessageDialog(this, "Mã và tên không được để trống!"); return; }
            String strSL = txtSL.getText().replaceAll("[,\\s]", "");

            int sl = Integer.parseInt(strSL);

            String strGia = txtDG.getText().replaceAll("[,\\s]", "");
            float gia = Float.parseFloat(strGia);
            String strCanhBao = txtCanhBao.getText().replaceAll("[,\\s]", "");
            int canhbao = Integer.parseInt(strCanhBao);

            SanPham sp = new SanPham(); sp.setMaSP(ma); sp.setTenSP(ten); sp.setDonViTinh(cbDVT.getSelectedItem().toString()); sp.setGiaTien(gia); sp.setMaKe(txtMaKe.getText().trim());
            BaoCaoTonKho bc = new BaoCaoTonKho(); bc.setMaBC(ma); bc.setsLTon(sl); bc.setCanhBaoHH(canhbao); bc.setSanPham(sp);



            String kq;
            if (isEditMode){
                kq=bus.updateBaoCao(bc);
            }else {
                kq = bus.addBaoCao(bc);
            }

            JOptionPane.showMessageDialog(this, kq);

            if (kq.contains("thành công")) { loadDataToTable(); hideFormPanel(); }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Số lượng, Đơn giá và Cảnh báo phải là số hợp lệ!");
        }
    }

    public void loadDataToTable(){
        model.setRowCount(0);
        for(BaoCaoTonKho tk: bus.getAll()){
            model.addRow(new Object[]{ tk.getSanPham().getMaSP(), tk.getSanPham().getTenSP(), tk.getSanPham().getDonViTinh(), tk.getsLTon(), String.format("%,.0f", tk.getSanPham().getGiaTien()), tk.getCanhBaoHH(), tk.getSanPham().getMaKe() });
        }
    }

    private void clearForm() {
        txtMaSP.setText(""); txtTenSP.setText(""); txtSL.setText("0"); txtDG.setText("0"); txtCanhBao.setText("10"); txtMaKe.setText("");
        cbDVT.setSelectedIndex(0);
    }

    private void showFormPanel() {
        panelForm.setVisible(true);
        splitPane.setDividerLocation(getWidth() - 320);
    }

    private void hideFormPanel() {
        panelForm.setVisible(false);
    }
    public static void main(String[]  args){
        SwingUtilities.invokeLater(()->
        {
            JFrame frame =new JFrame(" Ton kho");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            frame.add(new TrangTonKho());
            frame.setSize(1000, 600);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });

    }

    @Override
    public void apDungQuyen(boolean coQuyen_Xem, boolean coQuyen_Them,
                            boolean coQuyen_Sua, boolean coQuyen_Xoa) {
        btnAdd.setVisible(coQuyen_Them);
        btnEdit.setVisible(coQuyen_Sua);
        btnDelete.setVisible(coQuyen_Xoa);
    }
}