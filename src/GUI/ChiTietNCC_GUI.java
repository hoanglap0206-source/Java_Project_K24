package GUI;

import BUS.NCC_BUS;
import Model.ThongKeNCCDTO;
import Model.NhaCungCap;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

public class ChiTietNCC_GUI extends JFrame {
    private NhaCungCap ncc;
    private NCC_BUS nccBus = new NCC_BUS();
    private JTable tblLichSu;
    private DefaultTableModel model;
    private JLabel lblSoDon, lblTongGiaTri;

    public ChiTietNCC_GUI(NhaCungCap ncc) {
        this.ncc = ncc;
        initGUI();
        loadThongKeVaBang();

    }

    private void initGUI() {

        this.setUndecorated(true);
        //setTitle("Hồ Sơ Chi Tiết Nhà Cung Cấp");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(15, 15));
        getContentPane().setBackground(Color.WHITE);

        // --- 1. Header: Tên nhà cung cấp ---
        JPanel pnlHeader = new JPanel();
        pnlHeader.setBackground(new Color(0, 120, 215));
        JLabel lblTitle = new JLabel("CHI TIẾT ĐƠN VỊ: " + ncc.getTenNCC().toUpperCase());
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        pnlHeader.add(lblTitle);
        add(pnlHeader, BorderLayout.NORTH);

        // --- 2. Center: Gồm Thông tin và Bảng dữ liệu ---
        JPanel pnlCenter = new JPanel(new BorderLayout(10, 10));
        pnlCenter.setOpaque(false);
        pnlCenter.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // 2a. Top Center: Thông tin hành chính (Từ RAM)
        JPanel pnlInfo = new JPanel(new GridLayout(2, 2, 20, 15));
        pnlInfo.setOpaque(false);
        pnlInfo.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY), "Thông tin hành chính"));

        pnlInfo.add(createLabelInfo("Mã NCC: ", ncc.getMaNCC()));
        pnlInfo.add(createLabelInfo("Số điện thoại: ", ncc.getSdt()));
        pnlInfo.add(createLabelInfo("Địa chỉ: ", ncc.getDiaChi()));
        pnlInfo.add(createLabelInfo("Trạng thái đối tác: ", "Đang hoạt động")); // Mặc định hoặc từ DB

        // 2b. Middle Center: Panel Thống kê (Dùng hàm COUNT, SUM từ SQL)
        JPanel pnlStats = new JPanel(new GridLayout(1, 2, 20, 0));
        pnlStats.setPreferredSize(new Dimension(0, 100));

        // Ô tổng đơn hàng
        JPanel pnlCount = createStatCard("TỔNG ĐƠN HÀNG ĐÃ NHẬP", Color.ORANGE);
        lblSoDon = (JLabel) pnlCount.getClientProperty("valueLabel");

        // Ô tổng giá trị
        JPanel pnlSum = createStatCard("TỔNG GIÁ TRỊ GIAO DỊCH", new Color(46, 204, 113));
        lblTongGiaTri = (JLabel) pnlSum.getClientProperty("valueLabel");

        pnlStats.add(pnlCount);
        pnlStats.add(pnlSum);

        // Gom nhóm Info và Stats
        JPanel pnlTopGroup = new JPanel(new BorderLayout(10, 15));
        pnlTopGroup.setOpaque(false);
        pnlTopGroup.add(pnlInfo, BorderLayout.NORTH);
        pnlTopGroup.add(pnlStats, BorderLayout.CENTER);

        // 2c. Bottom Center: Bảng lịch sử
        String[] cols = {"Mã Phiếu Nhập", "Ngày Chứng Từ", "Giá Trị Đơn (VNĐ)"};
        model = new DefaultTableModel(cols, 0);
        tblLichSu = new JTable(model);
        tblLichSu.setRowHeight(30);
        tblLichSu.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        JScrollPane sp = new JScrollPane(tblLichSu);
        sp.setBorder(BorderFactory.createTitledBorder("Lịch sử nhập hàng chi tiết"));

        pnlCenter.add(pnlTopGroup, BorderLayout.NORTH);
        pnlCenter.add(sp, BorderLayout.CENTER);
        //Căn giữa toàn bộ
        DefaultTableCellRenderer center= new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        for(int i=0; i<tblLichSu.getColumnCount();i++){
            tblLichSu.getColumnModel().getColumn(i).setCellRenderer(center);
        }
        add(pnlCenter, BorderLayout.CENTER);

        // --- 3. Footer: Nút đóng ---
        JPanel pnlFooter = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnClose = new JButton("Đóng cửa sổ");
        btnClose.addActionListener(e -> dispose());
        pnlFooter.add(btnClose);
        add(pnlFooter, BorderLayout.SOUTH);
    }

    // Hàm tạo Label thông tin đẹp hơn
    private JLabel createLabelInfo(String title, String value) {
        JLabel label = new JLabel("<html><b>" + title + "</b> " + value + "</html>");
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        return label;
    }

    // Hàm tạo thẻ thống kê (Card)
    private JPanel createStatCard(String title, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(color);
        card.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 12));

        JLabel lblValue = new JLabel("0");
        lblValue.setForeground(Color.WHITE);
        lblValue.setFont(new Font("Arial", Font.BOLD, 28));
        lblValue.setHorizontalAlignment(SwingConstants.RIGHT);

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(lblValue, BorderLayout.CENTER);

        card.putClientProperty("valueLabel", lblValue); // Lưu reference để cập nhật sau
        return card;
    }

    private void loadThongKeVaBang() {
        // Cập nhật 2 ô thống kê (Sử dụng hàm COUNT/SUM trong DAO)
        ThongKeNCCDTO tk = nccBus.getThongKe(ncc.getMaNCC());
        lblSoDon.setText(String.valueOf(tk.getSoDonHang()));
        lblTongGiaTri.setText(String.format("%,.0f", tk.getTongGiaTri()) + " đ");

        // Cập nhật bảng danh sách (Dùng JOIN trong DAO)
        ArrayList<Object[]> ds = nccBus.getLichSuPhieu(ncc.getMaNCC());
        model.setRowCount(0);
        for (Object[] row : ds) {
            row[2] = String.format("%,.0f", (Double)row[2]); // Định dạng tiền trong bảng
            model.addRow(row);
        }

    }
}