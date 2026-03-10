package GUI;

import BUS.ChiTietPX_BUS;
import Model.ChiTiet_PhieuXuat;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class ChiTietPhieuXuat_GUI extends JDialog {
    private DefaultTableModel model;
    private JTable table;
    private ChiTietPX_BUS ctBus = new ChiTietPX_BUS();
    private DecimalFormat df = new DecimalFormat("#,### VNĐ");

    public ChiTietPhieuXuat_GUI(JFrame parent, String maPX, String ngay, String khach, String tongTien) {
        super(parent, "Chi tiết phiếu xuất hàng", true);
        setSize(850, 600);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        // --- PANEL THÔNG TIN CHUNG (NORTH) ---
        JPanel pnlHeader = new JPanel(new GridLayout(2, 2, 30, 15));
        pnlHeader.setBackground(new Color(248, 249, 250)); // Màu nền xám nhạt hiện đại
        pnlHeader.setBorder(new EmptyBorder(25, 30, 25, 30));

        // Font chữ
        Font fontBold = new Font("Segoe UI", Font.BOLD, 14);
        Font fontPlain = new Font("Segoe UI", Font.PLAIN, 14);

        // Tạo các Label thông tin (Dùng Panel con để ghép Tiêu đề Bold + Nội dung Plain)
        pnlHeader.add(createLabelGroup("Mã phiếu xuất: ", maPX, fontBold, fontPlain, new Color(0, 102, 204)));
        pnlHeader.add(createLabelGroup("Ngày xuất: ", ngay, fontBold, fontPlain, Color.BLACK));
        pnlHeader.add(createLabelGroup("Khách hàng: ", khach, fontBold, fontPlain, Color.BLACK));
        pnlHeader.add(createLabelGroup("Trạng thái: ", "Đã xuất kho", fontBold, fontPlain, new Color(40, 167, 69)));

        add(pnlHeader, BorderLayout.NORTH);

        // --- PANEL BẢNG CHI TIẾT (CENTER) ---
        JPanel pnlTable = new JPanel(new BorderLayout());
        pnlTable.setBackground(Color.WHITE);
        pnlTable.setBorder(new EmptyBorder(10, 30, 10, 30));

        String[] columns = {"STT", "Mã SKU", "Tên Sản Phẩm", "Số Lượng", "Đơn Giá", "Thành Tiền", "Thuế VAT"};
        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(model);
        table.setRowHeight(35);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setSelectionBackground(new Color(232, 240, 254));

        // Thiết kế Header cho bảng
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(52, 73, 94));
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setPreferredSize(new Dimension(0, 40));

        // Căn lề cho các cột
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);

        table.getColumnModel().getColumn(0).setPreferredWidth(50); // STT
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(4).setCellRenderer(rightRenderer);
        table.getColumnModel().getColumn(5).setCellRenderer(rightRenderer);
        table.getColumnModel().getColumn(6).setCellRenderer(centerRenderer);

        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(Color.WHITE);
        pnlTable.add(scroll, BorderLayout.CENTER);
        add(pnlTable, BorderLayout.CENTER);

        // --- PANEL TỔNG TIỀN (SOUTH) ---
        JPanel pnlFooter = new JPanel(new BorderLayout());
        pnlFooter.setBackground(Color.WHITE);
        pnlFooter.setBorder(new EmptyBorder(15, 30, 30, 30));

        JLabel lblTongCongText = new JLabel("TỔNG TIỀN THANH TOÁN: ");
        lblTongCongText.setFont(new Font("Segoe UI", Font.BOLD, 16));

        JLabel lblValue = new JLabel(tongTien);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblValue.setForeground(new Color(38, 195, 106)); // Màu đỏ đậm

        JPanel pnlTotal = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlTotal.setOpaque(false);
        pnlTotal.add(lblTongCongText);
        pnlTotal.add(lblValue);

        pnlFooter.add(pnlTotal, BorderLayout.EAST);
        add(pnlFooter, BorderLayout.SOUTH);

        loadData(maPX);
    }

    /**
     * Hàm hỗ trợ tạo nhóm Label (Tiêu đề + Giá trị)
     */
    private JPanel createLabelGroup(String title, String value, Font fTitle, Font fValue, Color colorValue) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        p.setOpaque(false);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(fTitle);

        JLabel lblVal = new JLabel(value);
        lblVal.setFont(fValue);
        lblVal.setForeground(colorValue);

        p.add(lblTitle);
        p.add(lblVal);
        return p;
    }

    private void loadData(String maPX) {
        // Gọi hàm từ BUS (hàm đã viết ở bước trên)
        ArrayList<ChiTiet_PhieuXuat> list = ctBus.getChiTietByMaPx_DB(maPX);
        int stt = 1;
        for (ChiTiet_PhieuXuat ct : list) {
            model.addRow(new Object[]{
                    stt++,
                    ct.getSanPham().getMaSP(),
                    ct.getSanPham().getTenSP(),
                    ct.getSoLuong(),
                    df.format(ct.getDonGia()),
                    df.format(ct.getThanhTien()),
                    ct.getThueVAT()
            });
        }
    }
}