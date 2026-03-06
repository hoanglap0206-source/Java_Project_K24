package GUI;

import BUS.KeKho_BUS;
import Model.KeKho;
import Model.SanPham;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.ArrayList;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.awt.print.PrinterException;
import java.text.MessageFormat;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.FileOutputStream;
import java.io.File;

public class TrangKeKho extends JPanel {
    private DefaultTableModel model;
    private JPanel soDoKe;
    private JLabel lblTenKe;
    private JTable table;
    private JLabel lblViTri;
    private JLabel lblSucChua;
    private JLabel lblHienTai;
    private KeKho_BUS bus = new KeKho_BUS();
    private JButton btnAdd;
    private JButton btnEdit;
    private JButton btnDelete;
    private JButton btnLamMoi;
    private JButton btnPrint;
    private JButton btnExcel;
    private JTextField txtSearch;
    private JButton btnSearchIcon;
    private JPanel selectedCard = null;

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

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT,10,2));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(4,10,4,10));

        // Thanh tìm kiếm
        txtSearch = new JTextField("Tìm kiếm");
        txtSearch.setColumns(10);
        txtSearch.setBorder(BorderFactory.createEmptyBorder(5,10,5,10));
        txtSearch.setForeground(Color.GRAY);

        JPanel pnlSearchInput = new JPanel(new BorderLayout());
        pnlSearchInput.setBackground(Color.WHITE);
        pnlSearchInput.setPreferredSize(new Dimension(260,30)); // ?
        pnlSearchInput.setBorder(new CompoundBorder(
                new LineBorder(new Color(198,226,255), 2, true),
                new EmptyBorder(0,2,0,0)
        ));

        btnSearchIcon = new JButton("🔍");
        btnSearchIcon.setBackground(new Color(214,238,253));
        btnSearchIcon.setBorderPainted(false); // ?
        btnSearchIcon.setFocusPainted(false); // ?
        btnSearchIcon.setCursor(new Cursor(Cursor.HAND_CURSOR)); // ?

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

        btnSearchIcon.addActionListener(e -> timKe());

        // Nút làm mới
        btnLamMoi = new JButton("⟳");
        Style.styleButton(btnLamMoi);

        // Combobox Lọc
        String[] itemLoc = {"Lọc", "< 50%", "50 - 80%", "> 80%"};
        JComboBox<String> comboBoxLoc = new JComboBox<>(itemLoc);

        Style.styleLoc(comboBoxLoc);
        comboBoxLoc.setSelectedIndex(0); // ?

        // Placeholder "Lọc"
        comboBoxLoc.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {

                JLabel lbl = (JLabel) super.getListCellRendererComponent(
                        list, value, index, isSelected, cellHasFocus); // ?

                lbl.setHorizontalAlignment(SwingConstants.CENTER);

                if (index == -1 && comboBoxLoc.getSelectedIndex() == -1) {
                    lbl.setText("Lọc");
                    lbl.setForeground(Color.GRAY); // setForeground ?
                }
                return lbl;
            }
        });

        comboBoxLoc.addActionListener(e -> {
            String value = comboBoxLoc.getSelectedItem().toString();
            locKeTheoPhanTram(value);
        });


        // Các nút khác
        btnEdit = new JButton("Chỉnh sửa");
        Style.styleButton(btnEdit);
        btnDelete = new JButton("Xóa");
        Style.styleButton(btnDelete);
        btnAdd = new JButton("+ Thêm");
        Style.styleButton(btnAdd);
        btnPrint = new JButton("In");
        Style.styleButton(btnPrint);

        // Nút xuất excel
        ImageIcon excelIcon = new ImageIcon(getClass().getResource("/Img/Excel.png"));
        Image scaledImage = excelIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);
        btnExcel = new JButton("Xuất Excel", scaledIcon);
        Style.styleButton(btnExcel);

        // Xử lý sự kiện
        btnAdd.addActionListener(e -> themKe());
        btnEdit.addActionListener(e -> suaKe());
        btnDelete.addActionListener(e -> xoaKe());
        btnLamMoi.addActionListener(e -> loadSoDo());
        btnPrint.addActionListener(e -> inDanhSach());
        btnExcel.addActionListener(e -> xuatExcelTuBang(table));

        // Thêm vào panel
        panel.add(pnlSearchInput);
        panel.add(btnLamMoi);
        panel.add(comboBoxLoc);
        panel.add(btnEdit);
        panel.add(btnDelete);
        panel.add(btnAdd);
        panel.add(btnPrint);
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
        JLabel ltlTongSpTrongKho = new JLabel("Tổng số sản phẩm trong kho:");
        ltlTongSpTrongKho.setFont(new Font("Segoe UI", Font.PLAIN, 12));


        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(Color.WHITE);
        titlePanel.add(lblTitle, BorderLayout.WEST);


        // Panel chứa grid + bảng
        JPanel content = new JPanel(new BorderLayout(0,20));
        content.setBackground(Color.WHITE);

        content.add(taoSoDoKe(), BorderLayout.CENTER);
        content.add(taoBang(), BorderLayout.SOUTH);

        main.add(titlePanel, BorderLayout.NORTH);
        main.add(content, BorderLayout.CENTER);

        return main;
    }

    // Bọc Scroll
    private JScrollPane taoSoDoKe() {
        soDoKe = new JPanel(new GridLayout(0, 5, 15, 15));
        soDoKe.setBorder(new EmptyBorder(10,10,10,10));
        soDoKe.setBackground(new Color(231,242,245));

        loadSoDo();

        JScrollPane scroll = new JScrollPane(soDoKe);
        scroll.getVerticalScrollBar().setUnitIncrement(16); // Tăng tốc cuộn chuột

        scroll.setPreferredSize(new Dimension(0, 250));

        return scroll;
    }

    private JPanel taoTheKe(String ten, int percent){
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(new LineBorder(new Color(200,200,200), 1, true));
        card.setPreferredSize(new Dimension(150,70));

        // panel chứa label + progress nằm ngang
        JPanel content = new JPanel(new BorderLayout(10,0));
        content.setBackground(Color.WHITE);
        content.setBorder(new EmptyBorder(15,10,15,10));

        JLabel lbl = new JLabel(ten);
        lbl.setFont(new Font("Segoe UI", Font.BOLD,12));
        lbl.setPreferredSize(new Dimension(25,25));

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

        MouseAdapter hover = new MouseAdapter() {
            public void mouseEntered(MouseEvent e){
                card.setBackground(new Color(245,250,255));
                content.setBackground(new Color(245,250,255));
            }

            public void mouseExited(MouseEvent e){
                card.setBackground(Color.WHITE);
                content.setBackground(Color.WHITE);
            }

            public void mouseClicked(MouseEvent e) {
                capNhatBang(ten);
                if (selectedCard != null) {
                    selectedCard.setBorder(new LineBorder(new Color(200,200,200),1,true));
                }

                card.setBorder(new LineBorder(Color.BLUE,2,true));
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
//        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE,350)); // ?
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
        table.getTableHeader().setReorderingAllowed(false); // ?

        // Căn giữa toàn bộ
        DefaultTableCellRenderer center = new DefaultTableCellRenderer(); // DefaultTableCellRenderer ?
        center.setHorizontalAlignment(SwingConstants.CENTER);

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(center); // Giải thích cú pháp này
        }

        JScrollPane scroll = new JScrollPane(table);
        panel.add(scroll, BorderLayout.CENTER);

        panel.add(taoThongTinKe(), BorderLayout.EAST);

        return panel;
    }

    private JPanel taoThongTinKe() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20,20,20,20));
        panel.setPreferredSize(new Dimension(220,150));

        lblViTri = new JLabel("Vị trí: ");
        lblSucChua = new JLabel("Sức chứa tối đa: ");
        lblHienTai = new JLabel("Hiện đang chứa: ");

        lblViTri.setFont(new Font("Segoe UI", Font.BOLD,13));
        lblSucChua.setFont(new Font("Segoe UI", Font.BOLD,13));
        lblHienTai.setFont(new Font("Segoe UI", Font.BOLD,13));

        panel.add(lblViTri);
        panel.add(Box.createVerticalStrut(10));
        panel.add(lblSucChua);
        panel.add(Box.createVerticalStrut(10));
        panel.add(lblHienTai);

        return panel;
    }

    private void timKe() {
        String keyword = txtSearch.getText().trim();

        if (keyword.isEmpty() || keyword.equals("Tìm kiếm")) {
            loadSoDo();
            return;
        }

        soDoKe.removeAll();

        ArrayList<KeKho> listKe = bus.getListKK();

        for (KeKho ke : listKe) {
            if (ke.getMaKe().toLowerCase().contains(keyword.toLowerCase())) {
                int percent = bus.tinhPhanTramTheoKe(ke.getMaKe());
                soDoKe.add(taoTheKe(ke.getMaKe(), percent));
            }
        }

        soDoKe.revalidate();
        soDoKe.repaint();
    }

    private void themKe(){
        int khoangTrong =0;
        String ma = JOptionPane.showInputDialog("Nhập mã kệ:"); // ?
        if(ma == null || ma.trim().isEmpty()) return; // ? return

        String viTri = JOptionPane.showInputDialog("Nhập vị trí:");
        if(viTri == null) return;

        String suc = JOptionPane.showInputDialog("Nhập sức chứa:");
        if(suc == null) return;

        try{
            int sucChua = Integer.parseInt(suc);
            KeKho ke = new KeKho(ma.trim(), sucChua, viTri.trim(),khoangTrong);
            JOptionPane.showMessageDialog(null, bus.addKK(ke)); // ?
            loadSoDo();
        }catch(Exception ex){
            JOptionPane.showMessageDialog(null,"Sức chứa phải là số!");
        }
    }

    private void suaKe(){
        String maKe = lblTenKe.getText().replace("Kệ ","").trim();
        KeKho ke = bus.getKeTheoMa(maKe);

        if(ke == null){
            JOptionPane.showMessageDialog(null,"Chưa chọn kệ!");
            return;
        }

        String viTri = JOptionPane.showInputDialog("Vị trí mới:", ke.getViTri());
        if(viTri == null) return;

        String suc = JOptionPane.showInputDialog("Sức chứa mới:", ke.getSucChua());
        if(suc == null) return;

        try{
            ke.setViTri(viTri.trim());
            ke.setSucChua(Integer.parseInt(suc));
            JOptionPane.showMessageDialog(null, bus.updateKK(ke));
            loadSoDo();
        }catch(Exception ex){
            JOptionPane.showMessageDialog(null,"Sức chứa phải là số!");
        }
    }

    private void xoaKe(){
        String maKe = lblTenKe.getText().replace("Kệ ","").trim();
        if(maKe.isEmpty()) return;

        int confirm = JOptionPane.showConfirmDialog(
                null,
                "Xóa kệ " + maKe + "?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION
        );

        if(confirm == JOptionPane.YES_OPTION){
            JOptionPane.showMessageDialog(null, bus.deleteKK(maKe));
            loadSoDo();
        }
    }

    private void loadSoDo(){
        soDoKe.removeAll();

        ArrayList<KeKho> listKe = bus.getListKK();

        for (KeKho ke : listKe) {
            int percent = bus.tinhPhanTramTheoKe(ke.getMaKe());
            soDoKe.add(taoTheKe(ke.getMaKe(), percent));
        }

        int soKe = soDoKe.getComponentCount();
        int soDong = (int) Math.ceil(soKe / 5.0);

        int cardHeight = 60;   // chiều cao card
        int gap = 15;          // khoảng cách grid

        int height = soDong * cardHeight + (soDong - 1) * gap + 20;

        soDoKe.setPreferredSize(new Dimension(0, height));

        soDoKe.revalidate();
        soDoKe.repaint();
    }

    private void inDanhSach(){
        if(table.getRowCount() == 0){
            JOptionPane.showMessageDialog(null,"Không có dữ liệu để in!");
            return;
        }

        try {
            String tenKe = lblTenKe.getText();
            MessageFormat header = new MessageFormat("Danh sách sản phẩm - " + tenKe);
            MessageFormat footer = new MessageFormat("Trang {0}");

            boolean complete = table.print(
                    JTable.PrintMode.FIT_WIDTH,
                    header,
                    footer
            );

            if(complete){
                JOptionPane.showMessageDialog(null,"In thành công!");
            }else{
                JOptionPane.showMessageDialog(null,"Đã hủy in.");
            }

        } catch (PrinterException ex){
            JOptionPane.showMessageDialog(null,"Lỗi khi in!");
            ex.printStackTrace();
        }
    }

//    public void xuatExcelTuBang(JTable table) {
//        try {
//            Workbook workbook = new XSSFWorkbook();
//            Sheet sheet = workbook.createSheet("Data");
//
//            for (int i = 0; i < table.getRowCount(); i++) {
//                Row row = sheet.createRow(i);
//                for (int j = 0; j < table.getColumnCount(); j++) {
//                    row.createCell(j).setCellValue(
//                            table.getValueAt(i, j).toString()
//                    );
//                }
//            }
//
//            FileOutputStream fos = new FileOutputStream("Data.xlsx");
//            workbook.write(fos);
//            fos.close();
//            workbook.close();
//
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//    }

    public void xuatExcelTuBang(JTable table) {
        try {
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Data");

            // Header
            Row header = sheet.createRow(0);
            for (int i = 0; i < table.getColumnCount(); i++) {
                header.createCell(i).setCellValue(table.getColumnName(i));
            }

            // Data
            for (int i = 0; i < table.getRowCount(); i++) {
                Row row = sheet.createRow(i + 1);
                for (int j = 0; j < table.getColumnCount(); j++) {
                    Object value = table.getValueAt(i, j);
                    row.createCell(j).setCellValue(value == null ? "" : value.toString());
                }
            }

            // Auto size
            for (int i = 0; i < table.getColumnCount(); i++) {
                sheet.autoSizeColumn(i);
            }

            FileOutputStream fos = new FileOutputStream("DanhSachSanPham.xlsx");
            workbook.write(fos);

            fos.close();
            workbook.close();

            JOptionPane.showMessageDialog(null, "Xuất Excel thành công!");

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void locKeTheoPhanTram(String value){
        soDoKe.removeAll();

        ArrayList<KeKho> listKe = bus.getListKK();

        for (KeKho ke : listKe) {
            int percent = bus.tinhPhanTramTheoKe(ke.getMaKe());

            switch (value) {
                case "< 50%":
                    if (percent < 50) {
                        soDoKe.add(taoTheKe(ke.getMaKe(), percent));
                    }
                    break;

                case "50 - 80%":
                    if (percent >= 50 && percent <= 80) {
                        soDoKe.add(taoTheKe(ke.getMaKe(), percent));
                    }
                    break;

                case "> 80%":
                    if (percent > 80) {
                        soDoKe.add(taoTheKe(ke.getMaKe(), percent));
                    }
                    break;

                default: // "Lọc"
                    soDoKe.add(taoTheKe(ke.getMaKe(), percent));
            }
        }

        soDoKe.revalidate();
        soDoKe.repaint();
    }

    private Color mauTheoPhanTram(int p){
        if(p<50) return new Color(40,200,100);
        if(p<80) return new Color(255,170,0);
        return new Color(230,50,50);
    }

    private void capNhatBang(String maKe) {
        lblTenKe.setText("Kệ " + maKe);
        model.setRowCount(0);

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
        int tong = bus.tinhTongSoLuongTheoKe(maKe);
        if (ke != null) {
            lblViTri.setText("Vị trí: " + ke.getViTri());
            lblSucChua.setText("Sức chứa tối đa: " + ke.getSucChua());
            lblHienTai.setText("Hiện đang chứa: " + tong);
        }
    }
}