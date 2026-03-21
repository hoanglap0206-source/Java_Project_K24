package GUI;

import Model.KeKho;
import Model.SanPham;
import BUS.KeKho_BUS;
import BUS.ChiTietKe_BUS;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.time.LocalDate;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.awt.print.PrinterJob;
import java.awt.print.Printable;

import java.io.File;

public class TrangKeKho extends JPanel implements QuyenTrang {
    private JTextField txtSearch;
    private JPanel soDoKe;
    private JLabel lblTenKe;
    private JTable table;
    private JLabel lblMaKe;
    private JLabel lblViTri;
    private JLabel lblSucChua;
    private JLabel lblHienTai;
    private JLabel lblTongSpTrongKho;

    private DefaultTableModel model;
    private JLabel lblKhoangTrong;
    private KeKho_BUS bus = new KeKho_BUS();
    private ChiTietKe_BUS chiTietKeBUS = new ChiTietKe_BUS();

    private JButton btnAdd;
    private JButton btnEdit;
    private JButton btnDelete;
    private JButton btnLamMoi;
    private JButton btnPrint;
    private JButton btnExport;
    private JButton btnImport;
    private JButton btnSearchIcon;
    private JPanel selectedCard = null;
    private String selectedMaKe = null;
    private String selectedTenHienThi = null;

    public TrangKeKho() {
        setLayout(new BorderLayout());
        setBackground(new Color(255,255,255));

        add(taoThanhCongCu(), BorderLayout.NORTH);
        add(taoBody(), BorderLayout.CENTER);
    }

    // TẠO GIAO DIỆN
    private JPanel taoThanhCongCu() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(Color.WHITE);
        wrapper.setBorder(new EmptyBorder(15,20,5,20));

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT,10,2));
        panel.setBackground(new Color(231, 242, 255));
        panel.setBorder(new EmptyBorder(4,10,4,10));

        // Thanh tìm kiếm
        txtSearch = new JTextField("Tìm kiếm");
        txtSearch.setColumns(10);
        txtSearch.setBorder(BorderFactory.createEmptyBorder(5,10,5,10));
        txtSearch.setForeground(Color.GRAY);

        JPanel pnlSearchInput = new JPanel(new BorderLayout());
        pnlSearchInput.setBackground(Color.WHITE);
        pnlSearchInput.setPreferredSize(new Dimension(260,30));
        pnlSearchInput.setBorder(new CompoundBorder(
                new LineBorder(new Color(198,226,255), 2, true),
                new EmptyBorder(0,2,0,0)
        ));

        btnSearchIcon = new JButton("🔍");
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
        btnLamMoi = new JButton("Làm mới");
        Style.styleButton(btnLamMoi);

        // Combobox Lọc
        String[] itemLoc = {"Lọc", "< 50%", "50 - 80%", "> 80%"};
        JComboBox<String> comboBoxLoc = new JComboBox<>(itemLoc);

        Style.styleLoc(comboBoxLoc);
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

        comboBoxLoc.addActionListener(e -> {
            String value = comboBoxLoc.getSelectedItem().toString();
            locKeTheoPhanTram(value);
        });

        // Nút xuất excel
        Image scaledImage = new ImageIcon(
                getClass().getResource("/Img/Excel.png")
        ).getImage().getScaledInstance(20,20,Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);

        // Các nút khác
        btnEdit = new JButton("Chỉnh sửa");
        Style.styleButton(btnEdit);
        btnDelete = new JButton("Xóa");
        Style.styleButton(btnDelete);
        btnAdd = new JButton("+ Thêm");
        Style.styleButton(btnAdd);
        btnPrint = new JButton("In");
        Style.styleButton(btnPrint);
        btnExport = new JButton("Xuất Excel", scaledIcon);
        Style.styleButton(btnExport);
        btnImport = new JButton("Nhập Excel");
        Style.styleButton(btnImport);

        // Xử lý sự kiện
        btnSearchIcon.addActionListener(e -> timKiem());
        btnAdd.addActionListener(e -> themKe());
        btnEdit.addActionListener(e -> suaKe());
        btnDelete.addActionListener(e -> xoaKe());
        btnLamMoi.addActionListener(e -> loadSoDo());
        btnPrint.addActionListener(e -> inDanhSach());
        btnExport.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setSelectedFile(new File("DanhSachKeKho.xlsx"));

            if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
                String filePath = file.getAbsolutePath();
                if (!filePath.endsWith(".xlsx")) {
                    filePath += ".xlsx";
                }

                boolean result = chiTietKeBUS.exportExcel(filePath);
                if (result) {
                    JOptionPane.showMessageDialog(this, "Xuất Excel thành công!\nFile: " + filePath);
                } else {
                    JOptionPane.showMessageDialog(this, "Xuất Excel thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnImport.addActionListener(e -> importExcel());

//        btnImport.addActionListener(e -> {
//            JFileChooser chooser = new JFileChooser();
//            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
//                chiTietKeBUS.importExcel(chooser.getSelectedFile());
//            }
//        });


        // Thêm vào panel
        panel.add(pnlSearchInput);
        panel.add(btnLamMoi);
        panel.add(comboBoxLoc);
        panel.add(btnEdit);
        panel.add(btnDelete);
        panel.add(btnAdd);
        panel.add(btnPrint);
        panel.add(btnExport);
        panel.add(btnImport);

        wrapper.add(panel, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel taoBody() {
        JPanel main = new JPanel(new BorderLayout(0,15));
        main.setBorder(new EmptyBorder(10,20,10,20));
        main.setBackground(Color.WHITE);

        JLabel lblTitle = new JLabel("SƠ ĐỒ KHO TỔNG");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD,18));

        lblTongSpTrongKho = new JLabel("Tổng số sản phẩm trong kho:");
        lblTongSpTrongKho.setFont(new Font("Segoe UI", Font.BOLD,13));
        lblTongSpTrongKho.setForeground(new Color(60,90,150));

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(Color.WHITE);
        titlePanel.add(lblTitle, BorderLayout.WEST);
        titlePanel.add(lblTongSpTrongKho, BorderLayout.EAST);

        // Panel chứa grid + bảng
        JPanel content = new JPanel(new BorderLayout(0,20));
        content.setBackground(Color.WHITE);

        content.add(taoSoDoKe(), BorderLayout.NORTH);
        content.add(taoBang(), BorderLayout.SOUTH);

        main.add(titlePanel, BorderLayout.NORTH);
        main.add(content, BorderLayout.CENTER);

        capNhatTongSanPham();

        return main;
    }

    private JScrollPane taoSoDoKe() {
        soDoKe = new JPanel();
        soDoKe.setLayout(new BoxLayout(soDoKe, BoxLayout.Y_AXIS));
        soDoKe.setBorder(new EmptyBorder(10,10,10,10));
        soDoKe.setBackground(new Color(231,242,245));

        loadSoDo();

        JScrollPane scroll = new JScrollPane(soDoKe);
        scroll.getVerticalScrollBar().setUnitIncrement(16); // tốc độ cuộn chuột, mỗi lần cuộn di chuyển 16px
        scroll.setPreferredSize(new Dimension(0, 250));

        return scroll;
    }

    private String taoTenHienThi(String day, int stt){
        return day + String.format("%02d", stt);
    }

    private JPanel taoTheKe(String ten, String maKe, int percent) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(new LineBorder(new Color(100, 180, 255), 2, true));

        card.setPreferredSize(new Dimension(170, 70));
        card.setMaximumSize(new Dimension(170, 70));
        card.setMinimumSize(new Dimension(170, 70));

        JPanel content = new JPanel(new BorderLayout(10, 0));
        content.setBackground(Color.WHITE);
        content.setBorder(new EmptyBorder(15, 10, 15, 10));

        JLabel lbl = new JLabel(ten);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setPreferredSize(new Dimension(30, 25));

        JProgressBar bar = new JProgressBar();
        int displayPercent = Math.min(percent, 100);
        bar.setValue(displayPercent);
        bar.setString(displayPercent + "%");
        bar.setStringPainted(true);
        bar.setForeground(mauTheoPhanTram(percent));
        bar.setBorderPainted(false);

        // Đảm bảo thanh hiển thị đúng
        bar.setMinimum(0);
        bar.setMaximum(100);

        content.add(lbl, BorderLayout.WEST);
        content.add(bar, BorderLayout.CENTER);
        card.add(content, BorderLayout.CENTER);
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        MouseAdapter hover = new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                card.setBackground(new Color(245, 250, 255));
                content.setBackground(new Color(245, 250, 255));
            }

            public void mouseExited(MouseEvent e) {
                card.setBackground(Color.WHITE);
                content.setBackground(Color.WHITE);
            }

            public void mouseClicked(MouseEvent e) {
                selectedMaKe = maKe;
                selectedTenHienThi = ten;
                capNhatBang(maKe);
                if (selectedCard != null) {
                    selectedCard.setBorder(new LineBorder(new Color(100, 180, 255), 2, true));
                }
                card.setBorder(new LineBorder(Color.BLUE, 3, true));
                selectedCard = card;
            }
        };

        card.addMouseListener(hover);
        content.addMouseListener(hover);
        bar.addMouseListener(hover);

        return card;
    }

    private JPanel taoBang(){
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new CompoundBorder(
                new LineBorder(new Color(220,220,220)),
                new EmptyBorder(15,15,15,15)
        ));
        panel.setPreferredSize(new Dimension(0,300));

        lblTenKe = new JLabel("Kệ A1");
        lblTenKe.setFont(new Font("Segoe UI", Font.BOLD,15));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(new EmptyBorder(0, 0, 10, 0));
        headerPanel.add(lblTenKe, BorderLayout.WEST);

        panel.add(headerPanel, BorderLayout.NORTH);

        String[] cols = {"STT","Mã sản phẩm","Tên sản phẩm","Đơn vị tính","Số lượng"};
        model = new DefaultTableModel(cols,0);
        table = new JTable(model);
        table.setRowHeight(28);

        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(210,230,255));
        table.getTableHeader().setForeground(Color.BLACK);

        table.getColumnModel().getColumn(0).setMaxWidth(50);
        table.getColumnModel().getColumn(1).setPreferredWidth(120);
        table.getColumnModel().getColumn(3).setPreferredWidth(120);
        table.getColumnModel().getColumn(4).setPreferredWidth(80);

        // Căn giữa toàn bộ
        DefaultTableCellRenderer center = new DefaultTableCellRenderer(); // DefaultTableCellRenderer ?
        center.setHorizontalAlignment(SwingConstants.CENTER);

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(center); // Giải thích cú pháp này
        }

        JScrollPane scroll = new JScrollPane(table);
        panel.add(scroll, BorderLayout.CENTER);

        panel.add(taoThongTinKe(), BorderLayout.EAST);

        capNhatBang("A1");
        return panel;
    }

    private JPanel taoThongTinKe() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20,20,20,20));
        panel.setPreferredSize(new Dimension(220,150));

        lblMaKe = new JLabel("Mã kệ: ");
        lblViTri = new JLabel("Vị trí: ");
        lblSucChua = new JLabel("Sức chứa tối đa: ");
        lblHienTai = new JLabel("Hiện đang chứa: ");
        lblKhoangTrong = new JLabel("Khoảng trống còn lại: ");

        lblMaKe.setFont(new Font("Segoe UI", Font.BOLD,13));
        lblViTri.setFont(new Font("Segoe UI", Font.BOLD,13));
        lblSucChua.setFont(new Font("Segoe UI", Font.BOLD,13));
        lblHienTai.setFont(new Font("Segoe UI", Font.BOLD,13));
        lblKhoangTrong.setFont(new Font("Segoe UI", Font.BOLD,13));

        panel.add(lblMaKe);
        panel.add(Box.createVerticalStrut(10));
        panel.add(lblViTri);
        panel.add(Box.createVerticalStrut(10));
        panel.add(lblSucChua);
        panel.add(Box.createVerticalStrut(10));
        panel.add(lblHienTai);
        panel.add(Box.createVerticalStrut(10));
        panel.add(lblKhoangTrong);

        return panel;
    }

    private JPanel taoPanelIn(String maKe) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));

        // Tiêu đề
        JLabel title = new JLabel("DANH SÁCH SẢN PHẨM TRONG KỆ");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(title);

        panel.add(Box.createVerticalStrut(5));

        JLabel subTitle = new JLabel("Kệ: " + maKe);
        subTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        subTitle.setForeground(new Color(60, 90, 150));
        subTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(subTitle);

        panel.add(Box.createVerticalStrut(20));

        // Lấy thông tin kệ hiện tại
        KeKho ke = bus.getKeTheoMa(maKe);
        ArrayList<SanPham> listSP = bus.laySanPhamTheoKe(maKe);
        int tongSoLuong = bus.tinhTongSoLuongTheoKe(maKe);
        int khoangTrong = ke.getSucChua() - tongSoLuong;

        // Tạo bảng dữ liệu
        String[] cols = {"STT", "Mã sản phẩm", "Tên sản phẩm", "Đơn vị tính", "Số lượng"};
        DefaultTableModel printModel = new DefaultTableModel(cols, 0);

        int stt = 1;
        for (SanPham sp : listSP) {
            printModel.addRow(new Object[]{
                    stt++,
                    sp.getMaSP(),
                    sp.getTenSP(),
                    sp.getDonViTinh(),
                    sp.getSoLuong()
            });
        }

        JTable tablePrint = new JTable(printModel);
        tablePrint.setRowHeight(28);
        tablePrint.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tablePrint.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tablePrint.getTableHeader().setBackground(new Color(210, 230, 255));
        tablePrint.setShowGrid(true);
        tablePrint.setGridColor(new Color(220, 220, 220));

        // Căn giữa các cột
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < tablePrint.getColumnCount(); i++) {
            tablePrint.getColumnModel().getColumn(i).setCellRenderer(center);
        }

        // Đặt độ rộng cột
        tablePrint.getColumnModel().getColumn(0).setMaxWidth(60);
        tablePrint.getColumnModel().getColumn(0).setPreferredWidth(60);
        tablePrint.getColumnModel().getColumn(1).setPreferredWidth(120);
        tablePrint.getColumnModel().getColumn(2).setPreferredWidth(250);
        tablePrint.getColumnModel().getColumn(3).setPreferredWidth(100);
        tablePrint.getColumnModel().getColumn(4).setPreferredWidth(80);

        JScrollPane scroll = new JScrollPane(tablePrint);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        panel.add(scroll);

        panel.add(Box.createVerticalStrut(20));

        // Thông tin kệ
        JPanel infoPanel = new JPanel(new GridLayout(5, 1, 5, 8));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(new CompoundBorder(
                BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)),
                        "Thông tin kệ", TitledBorder.LEFT, TitledBorder.TOP),
                new EmptyBorder(10, 15, 10, 15)
        ));

        JLabel lblNgay = new JLabel("Ngày in: " + LocalDate.now());
        lblNgay.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        JLabel lblViTri = new JLabel("Vị trí: " + ke.getViTri());
        lblViTri.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        JLabel lblSucChua = new JLabel("Sức chứa tối đa: " + ke.getSucChua() + " sản phẩm");
        lblSucChua.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        JLabel lblHienTai = new JLabel("Hiện đang chứa: " + tongSoLuong + " sản phẩm");
        lblHienTai.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        JLabel lblKhoangTrong = new JLabel("Khoảng trống còn lại: " + khoangTrong + " sản phẩm");
        lblKhoangTrong.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        int percent = (int) ((double) tongSoLuong / ke.getSucChua() * 100);
        JLabel lblPhanTram = new JLabel("Tỉ lệ sử dụng: " + percent + "%");
        lblPhanTram.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblPhanTram.setForeground(mauTheoPhanTram(percent));

        infoPanel.add(lblNgay);
        infoPanel.add(lblViTri);
        infoPanel.add(lblSucChua);
        infoPanel.add(lblHienTai);
        infoPanel.add(lblKhoangTrong);
        infoPanel.add(lblPhanTram);

        panel.add(infoPanel);

        // Footer
        panel.add(Box.createVerticalStrut(20));
        JLabel footer = new JLabel("--- Hệ thống quản lý kho ---");
        footer.setFont(new Font("Segoe UI", Font.ITALIC, 10));
        footer.setForeground(Color.GRAY);
        footer.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(footer);

        return panel;
    }


    // XỬ LÝ SỰ KIỆN
    private void timKiem() {
        String keyword = txtSearch.getText().trim().toLowerCase();
        if (keyword.isEmpty() || keyword.equals("tìm kiếm") || keyword.equals("Tìm kiếm")) {
            loadSoDo();
            return;
        }

        ArrayList<KeKho> listKe = bus.getListKK();
        ArrayList<KeKho> listKeTim = new ArrayList<>();

        for (KeKho ke : listKe) {
            boolean found = false;

            // 1. Tìm theo mã kệ
            if (ke.getMaKe().toLowerCase().contains(keyword)) {
                found = true;
            }

            // 2. Tìm theo tên hiển thị
            if (!found) {
                int stt = getSttCuaKe(ke);
                String tenHienThi = ke.getViTri() + stt;
                if (tenHienThi.toLowerCase().contains(keyword)) {
                    found = true;
                }
            }

            // 3. Tìm theo sản phẩm
            if (!found) {
                ArrayList<SanPham> spTrongKe = bus.laySanPhamTheoKe(ke.getMaKe());
                for (SanPham sp : spTrongKe) {
                    if (sp.getMaSP().toLowerCase().contains(keyword) ||
                            sp.getTenSP().toLowerCase().contains(keyword)) {
                        found = true;
                        break;
                    }
                }
            }

            if (found) {
                listKeTim.add(ke);
            }
        }

        if (listKeTim.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy kết quả phù hợp!",
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            // Quan trọng: xóa sạch sơ đồ khi không tìm thấy
            soDoKe.removeAll();
            JLabel lblThongBao = new JLabel("Không tìm thấy kệ phù hợp với từ khóa: " + keyword);
            lblThongBao.setFont(new Font("Segoe UI", Font.ITALIC, 14));
            lblThongBao.setForeground(Color.GRAY);
            lblThongBao.setAlignmentX(Component.LEFT_ALIGNMENT);
            soDoKe.add(lblThongBao);
            soDoKe.revalidate();
            soDoKe.repaint();
            return;
        }

        sapXepKe(listKeTim);
        veSoDoKe(listKeTim);
    }

    // Hàm hỗ trợ: lấy số thứ tự của kệ trong dãy
    private int getSttCuaKe(KeKho ke) {
        ArrayList<KeKho> allKe = bus.getListKK();
        String day = ke.getViTri();
        int stt = 0;

        for (KeKho k : allKe) {
            if (k.getViTri().equals(day)) {
                stt++;
                if (k.getMaKe().equals(ke.getMaKe())) {
                    return stt;
                }
            }
        }
        return stt;
    }

    private void themKe() {
        // Lấy danh sách dãy hiện có
        ArrayList<KeKho> listKK = bus.getListKK();
        ArrayList<String> dsDay = getAllDay(listKK);

        // Tạo dialog nhập liệu
        JTextField txtSucChua = new JTextField();
        JComboBox<String> cbDay = new JComboBox<>(dsDay.toArray(new String[0]));

        // Nút thêm dãy
        JButton btnAddDay = new JButton("+");
        JPanel dayPanel = new JPanel(new BorderLayout(5, 0));
        dayPanel.add(cbDay, BorderLayout.CENTER);
        dayPanel.add(btnAddDay, BorderLayout.EAST);

        btnAddDay.addActionListener(e -> {
            String nextDay = dayTiepTheo(dsDay);
            if (nextDay == null) {
                JOptionPane.showMessageDialog(null, "Đã hết dãy (A-Z)!");
                return;
            }
            cbDay.addItem(nextDay);
            cbDay.setSelectedItem(nextDay);
            dsDay.add(nextDay);
        });

        // Hiển thị thông báo mã sẽ tự động sinh
        JLabel lblMaAuto = new JLabel("(Mã sẽ tự động sinh khi lưu)");
        lblMaAuto.setForeground(Color.GRAY);
        lblMaAuto.setFont(new Font("Segoe UI", Font.ITALIC, 11));

        Object[] msg = {
                "Mã kệ:", lblMaAuto,
                "Sức chứa:", txtSucChua,
                "Vị trí (khu):", dayPanel
        };

        int option = JOptionPane.showConfirmDialog(
                null, msg, "Thêm kệ",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (option != JOptionPane.OK_OPTION) return;

        try {
            int sc = Integer.parseInt(txtSucChua.getText().trim());
            if (sc <= 0) {
                JOptionPane.showMessageDialog(null, "Sức chứa phải lớn hơn 0!");
                return;
            }

            String day = cbDay.getSelectedItem().toString();

            // SINH MÃ NGAY TRƯỚC KHI LƯU - tránh trùng
            String maMoi = bus.sinhMaKeTuDong();
            KeKho ke = new KeKho(maMoi, sc, day);

            String result = bus.addKK(ke);
            JOptionPane.showMessageDialog(null, result);

            if (result.contains("thành công")) {
                loadSoDo();
                // Tự động chọn kệ vừa thêm
                selectedMaKe = maMoi;
                capNhatBang(maMoi);
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Sức chứa phải là số nguyên!");
        }
    }

    private void suaKe() {
        if (selectedMaKe == null) {
            JOptionPane.showMessageDialog(null, "Chưa chọn kệ!");
            return;
        }

        KeKho ke = bus.getKeTheoMa(selectedMaKe);
        if (ke == null) {
            JOptionPane.showMessageDialog(null, "Không tìm thấy kệ!");
            return;
        }

        JTextField txtMa = new JTextField(ke.getMaKe());
        JTextField txtViTri = new JTextField(ke.getViTri());
        JTextField txtSucChua = new JTextField(String.valueOf(ke.getSucChua()));

        txtMa.setEditable(false);

        Object[] msg = {
                "Mã kệ:", txtMa,
                "Vị trí:", txtViTri,
                "Sức chứa:", txtSucChua
        };

        int option = JOptionPane.showConfirmDialog(
                null, msg, "Sửa kệ", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
        );

        if (option != JOptionPane.OK_OPTION) return;

        try {
            String ma = txtMa.getText().trim();
            int sc = Integer.parseInt(txtSucChua.getText().trim());
            String vt = txtViTri.getText().trim();

            if (sc <= 0) {
                JOptionPane.showMessageDialog(null, "Sức chứa phải lớn hơn 0!");
                return;
            }

            // Kiểm tra nếu sức chứa mới nhỏ hơn số lượng hiện tại
            int tongHienTai = bus.tinhTongSoLuongTheoKe(ma);
            if (sc < tongHienTai) {
                JOptionPane.showMessageDialog(null,
                        "Sức chứa mới (" + sc + ") nhỏ hơn số lượng hiện tại (" + tongHienTai + ")!");
                return;
            }

            KeKho newKe = new KeKho(ma, sc, vt);
            String result = bus.updateKK(newKe);
            JOptionPane.showMessageDialog(null, result);

            if (result.contains("thành công")) {
                loadSoDo();
                // Giữ lại kệ đang chọn
                capNhatBang(ma);
                // Cập nhật lại selectedCard
                selectedMaKe = ma;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Sức chứa phải là số nguyên!");
        }
    }

    private void xoaKe() {
        if (selectedMaKe == null) {
            JOptionPane.showMessageDialog(null, "Chưa chọn kệ!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                null,
                "Xóa kệ " + selectedMaKe + "?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            String result = bus.deleteKK(selectedMaKe);
            JOptionPane.showMessageDialog(null, result);

            if (result.contains("thành công")) {
                loadSoDo();
                // Clear selection
                selectedMaKe = null;
                selectedCard = null;
                // Xóa bảng
                model.setRowCount(0);
                lblTenKe.setText("Kệ ");
                lblMaKe.setText("Mã kệ: " );
                lblViTri.setText("Vị trí: ");
                lblSucChua.setText("Sức chứa tối đa: ");
                lblHienTai.setText("Hiện đang chứa: ");
                lblKhoangTrong.setText("Khoảng trống còn lại: ");
            }
        }
    }

    private void inDanhSach() {
        if (selectedMaKe == null) {
            JOptionPane.showMessageDialog(null, "Chưa chọn kệ để in!");
            return;
        }

        // Kiểm tra dữ liệu từ bảng hiện tại
        if (table.getRowCount() == 0) {
            JOptionPane.showMessageDialog(null, "Kệ không có dữ liệu để in!");
            return;
        }

        // Tạo panel in với dữ liệu hiện tại
        JPanel printPanel = taoPanelIn(selectedMaKe);

        // Tạo JDialog để xem trước
        JDialog previewDialog = new JDialog((Frame) null, "Xem trước khi in", true);
        previewDialog.setLayout(new BorderLayout());
        previewDialog.setSize(800, 600);
        previewDialog.setLocationRelativeTo(null);

        // Thêm panel vào JScrollPane
        JScrollPane scrollPreview = new JScrollPane(printPanel);
        scrollPreview.getVerticalScrollBar().setUnitIncrement(16);
        previewDialog.add(scrollPreview, BorderLayout.CENTER);

        // Panel chứa nút
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton btnPrint = new JButton("In");
        JButton btnCancel = new JButton("Hủy");

        Style.styleButton(btnPrint);
        Style.styleButton(btnCancel);

        btnPrint.addActionListener(e -> {
            previewDialog.dispose();
            // Thực hiện in
            PrinterJob job = PrinterJob.getPrinterJob();

            // Tạo Printable từ panel
            job.setPrintable((graphics, pageFormat, pageIndex) -> {
                if (pageIndex > 0) return Printable.NO_SUCH_PAGE;

                Graphics2D g2d = (Graphics2D) graphics;

                // Tính toán scale phù hợp
                double pageWidth = pageFormat.getImageableWidth();
                double pageHeight = pageFormat.getImageableHeight();
                double panelWidth = printPanel.getWidth();
                double panelHeight = printPanel.getHeight();

                double scaleX = pageWidth / panelWidth;
                double scaleY = pageHeight / panelHeight;
                double scale = Math.min(scaleX, scaleY);

                // Giới hạn scale tối đa là 1
                if (scale > 1.0) scale = 1.0;

                g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
                g2d.scale(scale, scale);

                printPanel.printAll(g2d);
                return Printable.PAGE_EXISTS;
            });

            try {
                if (job.printDialog()) {
                    job.print();
                    JOptionPane.showMessageDialog(null, "In thành công!");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Lỗi khi in: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        btnCancel.addActionListener(e -> previewDialog.dispose());

        buttonPanel.add(btnPrint);
        buttonPanel.add(btnCancel);
        previewDialog.add(buttonPanel, BorderLayout.SOUTH);

        previewDialog.setVisible(true);
    }


    // XỬ LÝ DỮ LIỆU
    private void loadSoDo() {
        bus.refreshData();
        ArrayList<KeKho> listKe = bus.getListKK();
        sapXepKe(listKe);
        veSoDoKe(listKe);
        selectedCard = null; // đảm bảo không còn tham chiếu đến card cũ
        selectedMaKe = null; // tương tự, không tham chiếu đến mã kệ cũ
        selectedTenHienThi = null;
        capNhatTongSanPham();
    }

    private void locKeTheoPhanTram(String value) {
        ArrayList<KeKho> listKe = bus.getListKK();
        ArrayList<KeKho> listKeLoc = new ArrayList<>();

        // Lọc các kệ theo điều kiện
        for (KeKho ke : listKe) {
            int percent = bus.tinhPhanTramTheoKe(ke);

            switch (value) {
                case "< 50%":
                    if (percent < 50) listKeLoc.add(ke);
                    break;
                case "50 - 80%":
                    if (percent >= 50 && percent <= 80) listKeLoc.add(ke);
                    break;
                case "> 80%":
                    if (percent > 80) listKeLoc.add(ke);
                    break;
                default: // "Lọc"
                    listKeLoc.add(ke);
            }
        }

        // Vẽ lại sơ đồ với danh sách đã lọc
        sapXepKe(listKeLoc);
        veSoDoKe(listKeLoc);
    }

    private void sapXepKe(ArrayList<KeKho> list){
        list.sort((a, b) -> {
            // Sắp xếp theo dãy (A, B, C...)
            String vtA = a.getViTri() == null ? "" : a.getViTri();
            String vtB = b.getViTri() == null ? "" : b.getViTri();
            int cmp = vtA.compareTo(vtB);
            if (cmp != 0) return cmp;

            // Sắp xếp theo số thứ tự trong mã kệ
            int numA = Integer.parseInt(a.getMaKe().substring(1));
            int numB = Integer.parseInt(b.getMaKe().substring(1));
            return Integer.compare(numA, numB);
        });
    }

    private void veSoDoKe(ArrayList<KeKho> danhSachKe) {
        soDoKe.removeAll();

        String currentDay = "";
        JPanel currentRow = null;

        int count = 0;
        int stt = 0;

        for (KeKho ke : danhSachKe) {
            String day = ke.getViTri();

            // reset STT khi sang dãy mới
            if (!day.equals(currentDay)) {
                currentDay = day;
                stt = 0;
                count = 0;

                JLabel lblDay = new JLabel("Dãy " + day + ":");
                lblDay.setFont(new Font("Segoe UI", Font.BOLD, 14));
                lblDay.setBorder(new EmptyBorder(10, 5, 5, 5));
                lblDay.setAlignmentX(Component.LEFT_ALIGNMENT);

                soDoKe.add(lblDay);

                currentRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
                currentRow.setBackground(new Color(231,242,245));
                currentRow.setAlignmentX(Component.LEFT_ALIGNMENT);

                soDoKe.add(currentRow);
            }

            // tạo dòng mới nếu đủ 5 kệ
            if (count == 5) {
                currentRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
                currentRow.setBackground(new Color(231,242,245));
                currentRow.setAlignmentX(Component.LEFT_ALIGNMENT);

                soDoKe.add(currentRow);
                count = 0;
            }

            stt++;
            String tenHienThi = taoTenHienThi(day, stt);

            int percent = bus.tinhPhanTramTheoKe(ke);
            JPanel card = taoTheKe(tenHienThi, ke.getMaKe(), percent);

            currentRow.add(card);
            count++;
        }

        soDoKe.revalidate();
        soDoKe.repaint();
    }

    private void capNhatBang(String maKe) {
        lblTenKe.setText("Kệ " + selectedTenHienThi);
        model.setRowCount(0); // Xóa dữ liệu cũ

        ArrayList<SanPham> listSP = bus.laySanPhamTheoKe(maKe);

        int stt = 1;
        for (SanPham sp : listSP) {
            model.addRow(new Object[]{
                    stt++,
                    sp.getMaSP(),
                    sp.getTenSP(),
                    sp.getDonViTinh(),
                    sp.getSoLuong()
            });
        }

        KeKho ke = bus.getKeTheoMa(maKe);
        if (ke != null) {
            int tong = bus.tinhTongSoLuongTheoKe(maKe);
            int khoangTrong = ke.getSucChua() - tong;
            lblTenKe.setText("Mã kệ: " + maKe);
            lblViTri.setText("Vị trí: " + ke.getViTri());
            lblSucChua.setText("Sức chứa tối đa: " + ke.getSucChua());
            lblHienTai.setText("Hiện đang chứa: " + tong);
            lblKhoangTrong.setText("Khoảng trống còn lại: "+ khoangTrong);
        }
    }

    private Color mauTheoPhanTram(int p){
        if(p<50) return new Color(40,200,100);
        if(p<80) return new Color(255,170,0);
        return new Color(230,50,50);
    }

    private void capNhatTongSanPham() {
        int tong = bus.tinhTongSPTrongKho();
        lblTongSpTrongKho.setText("Tổng số sản phẩm trong kho: " + tong);
    }

    // Lấy danh sách dãy không trùng
    private ArrayList<String> getAllDay(ArrayList<KeKho> list){
        ArrayList<String> dsDay = new ArrayList<>();

        for(KeKho ke : list){
            String d = ke.getViTri();

            if(d != null && !d.trim().isEmpty() && !dsDay.contains(d)){
                dsDay.add(d.trim());
            }
        }

        Collections.sort(dsDay);

        return dsDay;
    }

    // Sinh dãy tiếp theo
    private String dayTiepTheo(ArrayList<String> dsDay){
        if(dsDay.isEmpty()) return "A";

        char max = 'A' - 1;

        for(String d : dsDay){
            if(d != null && d.matches("[A-Z]")){
                char c = d.charAt(0);
                if(c > max){
                    max = c;
                }
            }
        }

        if(max >= 'Z') return null;

        return String.valueOf((char)(max + 1));
    }

    private void inPanel(JPanel panel){
        PrinterJob job = PrinterJob.getPrinterJob();

        job.setPrintable((graphics, pageFormat, pageIndex) -> {
            if(pageIndex > 0) return Printable.NO_SUCH_PAGE;

            Graphics2D g2d = (Graphics2D) graphics;
            g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

            panel.printAll(g2d);
            return Printable.PAGE_EXISTS;
        });

        try{
            if(job.printDialog()){
                job.print();
            }
        }catch(Exception e){
            JOptionPane.showMessageDialog(null,"Lỗi khi in!");
        }
    }

    private void importExcel() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Chọn file Excel để nhập");
        chooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.isDirectory() || f.getName().toLowerCase().endsWith(".xlsx")
                        || f.getName().toLowerCase().endsWith(".xls");
            }

            @Override
            public String getDescription() {
                return "Excel Files (*.xlsx, *.xls)";
            }
        });

        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();

            // Hiển thị dialog tiến trình
            JDialog progressDialog = new JDialog((Frame) null, "Đang nhập dữ liệu...", true);
            progressDialog.setLayout(new BorderLayout());
            progressDialog.setSize(300, 100);
            progressDialog.setLocationRelativeTo(this);

            JProgressBar progressBar = new JProgressBar();
            progressBar.setIndeterminate(true);
            JLabel lblStatus = new JLabel("Đang xử lý file: " + file.getName());
            lblStatus.setHorizontalAlignment(SwingConstants.CENTER);

            progressDialog.add(lblStatus, BorderLayout.NORTH);
            progressDialog.add(progressBar, BorderLayout.CENTER);

            // Chạy import trong thread riêng
            SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
                @Override
                protected String doInBackground() throws Exception {
                    return chiTietKeBUS.importExcel(file);
                }

                @Override
                protected void done() {
                    progressDialog.dispose();
                    try {
                        String result = get();
                        if (result.contains("thành công")) {
                            JOptionPane.showMessageDialog(TrangKeKho.this, result,
                                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
                            loadSoDo(); // Làm mới dữ liệu
                            if (selectedMaKe != null) {
                                capNhatBang(selectedMaKe);
                            }
                            capNhatTongSanPham();
                        } else {
                            JOptionPane.showMessageDialog(TrangKeKho.this,
                                    "Nhập Excel thất bại!\n" + result,
                                    "Lỗi", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(TrangKeKho.this,
                                "Lỗi: " + e.getMessage(),
                                "Lỗi", JOptionPane.ERROR_MESSAGE);
                        e.printStackTrace();
                    }
                }
            };

            worker.execute();
            progressDialog.setVisible(true);
        }
    }

    @Override
    public void apDungQuyen(boolean coQuyen_Xem, boolean coQuyen_Them,
                            boolean coQuyen_Sua, boolean coQuyen_Xoa) {
        btnAdd.setVisible(coQuyen_Them);
        btnEdit.setVisible(coQuyen_Sua);
        btnDelete.setVisible(coQuyen_Xoa);
    }
}
