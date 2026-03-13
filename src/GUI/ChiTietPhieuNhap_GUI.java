package GUI;

import BUS.ChiTietPN_BUS;
import Model.ChiTiet_PhieuNhap;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class ChiTietPhieuNhap_GUI extends JDialog {

    private DefaultTableModel model;
    private JTable table;
    private ChiTietPN_BUS ctBus = new ChiTietPN_BUS();
    private DecimalFormat df = new DecimalFormat("#,### VNĐ");

    public ChiTietPhieuNhap_GUI(JFrame frame, String maPN, String ngayNhap, String ncc, String tongTien) {
        super(frame, "Chi tiết Phiếu Nhập", true);
        setSize(750, 500);
        setLocationRelativeTo(frame);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        // Tiêu đề
        JPanel pnlHeader = new JPanel(new GridLayout(2, 2, 10, 10));
        pnlHeader.setBackground(Color.WHITE);
        pnlHeader.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel lblTitle = new JLabel("CHI TIẾT PHIẾU NHẬP: " + maPN);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(new Color(0, 51, 102));

        // Thông tin Phiếu nhập
        pnlHeader.add(lblTitle);
        pnlHeader.add(new JLabel(""));
        pnlHeader.add(createInfoLabel("Ngày nhập: ", ngayNhap));
        pnlHeader.add(createInfoLabel("Nhà cung cấp: ", ncc));

        add(pnlHeader, BorderLayout.NORTH);

        // Bảng chi tiết các sản phẩm có trong phiếu nhập
        JPanel pnlTable = new JPanel(new BorderLayout());
        pnlTable.setBorder(new EmptyBorder(0, 20, 10, 20));
        pnlTable.setBackground(Color.WHITE);

        String[] cols = {"STT", "Mã SP", "Tên Sản Phẩm", "Số Lượng", "Đơn Giá", "Thành Tiền"};
        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(model);
        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setBackground(new Color(52, 73, 94));
        table.getTableHeader().setPreferredSize(new Dimension(0, 40));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            if (i != 2) // Căn giữa tất cả trừ Tên Sản Phẩm
                table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        pnlTable.add(new JScrollPane(table), BorderLayout.CENTER);
        add(pnlTable, BorderLayout.CENTER);

        // Phần Footer
        JPanel pnlFooter = new JPanel(new BorderLayout());
        pnlFooter.setBackground(Color.WHITE);
        pnlFooter.setBorder(new EmptyBorder(15, 30, 30, 30));

        JLabel lblTongCongText = new JLabel("TỔNG CỘNG: ");
        lblTongCongText.setFont(new Font("Segoe UI", Font.BOLD, 16));

        JLabel lblValue = new JLabel(tongTien);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblValue.setForeground(new Color(38, 195, 106));

        JPanel pnlTotal = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlTotal.setOpaque(false);
        pnlTotal.add(lblTongCongText);
        pnlTotal.add(lblValue);

        pnlFooter.add(pnlTotal, BorderLayout.EAST);
        add(pnlFooter, BorderLayout.SOUTH);

        // Tải dữ liệu vào bảng
        loadData(maPN);
    }

    private JLabel createInfoLabel(String title, String value) {
        JLabel lbl = new JLabel("<html><b>" + title + "</b> " + value + "</html>");
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        return lbl;
    }

    private void loadData(String maPN) {
        ArrayList<ChiTiet_PhieuNhap> list = ctBus.getChiTietByMaPN_DB(maPN);
        int stt = 1;
        for (ChiTiet_PhieuNhap ct : list) {
            model.addRow(new Object[]{
                    stt++,
                    ct.getSanPham().getMaSP(),
                    ct.getSanPham().getTenSP(), // Lấy tên sản phẩm đã JOIN từ DAO
                    ct.getSoLuong(),
                    df.format(ct.getDonGia()),
                    df.format(ct.getThanhTien())
            });
        }
    }
}