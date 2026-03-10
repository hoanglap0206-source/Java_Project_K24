package GUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ChiTietSanPham_Dialog extends JDialog {
    private JTable tableHistory;
    private DefaultTableModel modelHistory;
    private String loaiBaoCao;

    // Constructor nhận vào cửa sổ cha, Mã SP, Tên SP và Loại báo cáo (Nhập hay Xuất)
    public ChiTietSanPham_Dialog(Window parent, String maSP, String tenSP, String loaiBaoCao) {
        super(parent, "Chi Tiết Giao Dịch Sản Phẩm", Dialog.ModalityType.APPLICATION_MODAL);
        this.loaiBaoCao = loaiBaoCao;

        setSize(750, 450);
        setLocationRelativeTo(parent); // Căn giữa so với màn hình chính
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        // ================= 1. PHẦN HEADER (THÔNG TIN CHUNG) =================
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setBackground(new Color(230, 245, 255)); // Màu xanh nhạt đồng bộ
        pnlHeader.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel lblTenSP = new JLabel(tenSP);
        lblTenSP.setFont(new Font("Arial", Font.BOLD, 22));
        lblTenSP.setForeground(new Color(30, 144, 255)); // Chữ màu xanh biển nhấn mạnh

        JLabel lblMaSP = new JLabel("Mã Sản Phẩm: " + maSP + "   |   Phân loại: Báo cáo " + loaiBaoCao.toLowerCase());
        lblMaSP.setFont(new Font("Arial", Font.PLAIN, 14));
        lblMaSP.setForeground(Color.DARK_GRAY);

        pnlHeader.add(lblTenSP, BorderLayout.NORTH);
        pnlHeader.add(lblMaSP, BorderLayout.SOUTH);
        add(pnlHeader, BorderLayout.NORTH);

        // ================= 2. PHẦN CENTER (BẢNG LỊCH SỬ GIAO DỊCH) =================
        JPanel pnlCenter = new JPanel(new BorderLayout());
        pnlCenter.setBackground(Color.WHITE);
        pnlCenter.setBorder(new EmptyBorder(10, 15, 10, 15));

        JLabel lblTitleTable = new JLabel("Lịch sử các đợt " + loaiBaoCao.toLowerCase() + ":");
        lblTitleTable.setFont(new Font("Arial", Font.BOLD, 14));
        lblTitleTable.setBorder(new EmptyBorder(0, 0, 10, 0));
        pnlCenter.add(lblTitleTable, BorderLayout.NORTH);

        // Đổi cột linh hoạt: Nhập thì hiện Nhà cung cấp, Xuất thì hiện Khách hàng
        String[] columns;
        if (loaiBaoCao.equalsIgnoreCase("Nhập hàng")) {
            columns = new String[]{"Mã Phiếu", "Ngày Giờ", "Nhà Cung Cấp", "Nhân Viên", "Số Lượng", "Đơn Giá", "Thành Tiền"};
        } else {
            columns = new String[]{"Mã Phiếu", "Ngày Giờ", "Khách Hàng", "Nhân Viên", "Số Lượng", "Đơn Giá", "Thành Tiền"};
        }


        modelHistory = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tableHistory = new JTable(modelHistory);
        tableHistory.setRowHeight(28);
        tableHistory.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tableHistory.getTableHeader().setBackground(new Color(187, 219, 243));
        tableHistory.getTableHeader().setOpaque(true);
        tableHistory.getTableHeader().setReorderingAllowed(false); // không đổi vị trí cột
        tableHistory.getTableHeader().setResizingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(tableHistory);
        scrollPane.setBorder(new LineBorder(Color.LIGHT_GRAY, 1));

        pnlCenter.add(scrollPane, BorderLayout.CENTER);
        add(pnlCenter, BorderLayout.CENTER);

        // ================= 3. PHẦN SOUTH (NÚT ĐÓNG) =================
        JPanel pnlSouth = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlSouth.setBackground(Color.WHITE);
        pnlSouth.setBorder(new EmptyBorder(5, 15, 15, 15));

        JButton btnClose = new JButton("Đóng");
        btnClose.setFont(new Font("Arial", Font.BOLD, 12));
        btnClose.setPreferredSize(new Dimension(100, 35));
        btnClose.setBackground(new Color(220, 53, 69)); // Nút đỏ giống thiết kế
        btnClose.setForeground(Color.WHITE);
        btnClose.setFocusPainted(false);
        btnClose.addActionListener(e -> dispose()); // Lệnh tắt cửa sổ này đi

        pnlSouth.add(btnClose);
        add(pnlSouth, BorderLayout.SOUTH);

        // Tạm gọi dữ liệu giả để test vẽ UI
        loadDummyData();
    }

    // TODO: Sau này bạn thay hàm này bằng việc gọi DAO lấy dữ liệu thật từ SQL
    private void loadDummyData() {
        if (loaiBaoCao.equalsIgnoreCase("Nhập hàng")) {
            modelHistory.addRow(new Object[]{"PN01", "2026-03-01 16:20", "NCC05", "NV02", "10", "150,000", "1,500,000"});
            modelHistory.addRow(new Object[]{"PN05", "2026-03-15 09:10", "NCC01", "NV01", "10", "150,000", "1,500,000"});
        } else {
            modelHistory.addRow(new Object[]{"PX02", "2026-01-02 10:00", "KH10", "NV01", "5", "180,000", "900,000"});
            modelHistory.addRow(new Object[]{"PX07", "2026-02-07 14:30", "KH07", "NV02", "7", "180,000", "1,260,000"});
        }
    }
}