package GUI;

import BUS.ChiTietPX_BUS;
import Model.ChiTiet_PhieuXuat;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.print.*;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class ChiTietPhieuXuat_GUI extends JDialog {

    private final String maPX;
    private final String ngayXuat;
    private final String khachHang;
    private final String tongTien;

    private final ChiTietPX_BUS ctBus = new ChiTietPX_BUS();
    private final DecimalFormat df = new DecimalFormat("#,### VNĐ");

    private DefaultTableModel tableModel;
    private JTable table;

    public ChiTietPhieuXuat_GUI(Frame owner,
                                String maPX, String ngayXuat,
                                String khachHang, String tongTien) {
        super(owner, "Chi tiết phiếu xuất — " + maPX, true);
        this.maPX      = maPX;
        this.ngayXuat  = ngayXuat;
        this.khachHang = khachHang;
        this.tongTien  = tongTien;

        setLayout(new BorderLayout(0, 0));
        getContentPane().setBackground(Color.WHITE);

        add(taoHeader(),  BorderLayout.NORTH);
        add(taoContent(), BorderLayout.CENTER);
        add(taoFooter(),  BorderLayout.SOUTH);

        setSize(760, 540);
        setMinimumSize(new Dimension(640, 440));
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    // ── HEADER ────────────────────────────────────────────
    private JPanel taoHeader() {
        JPanel pnl = new JPanel(new BorderLayout());
        pnl.setBackground(new Color(30, 130, 80));
        pnl.setBorder(new EmptyBorder(14, 20, 14, 20));

        JLabel lblTitle = new JLabel("PHIẾU XUẤT HÀNG");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(Color.WHITE);

        JPanel pnlRight = new JPanel(new GridLayout(3, 2, 4, 2));
        pnlRight.setOpaque(false);
        pnlRight.add(headerLbl("Mã phiếu:"));    pnlRight.add(headerVal(maPX));
        pnlRight.add(headerLbl("Ngày xuất:"));   pnlRight.add(headerVal(ngayXuat));
        pnlRight.add(headerLbl("Khách hàng:"));  pnlRight.add(headerVal(khachHang));

        pnl.add(lblTitle, BorderLayout.WEST);
        pnl.add(pnlRight, BorderLayout.EAST);
        return pnl;
    }

    // ── BẢNG CHI TIẾT ─────────────────────────────────────
    private JPanel taoContent() {
        JPanel pnl = new JPanel(new BorderLayout(0, 8));
        pnl.setBackground(Color.WHITE);
        pnl.setBorder(new EmptyBorder(12, 16, 8, 16));

        String[] cols = {"STT", "Mã SP", "Tên sản phẩm", "Số lượng",
                "Đơn giá", "VAT", "Thành tiền"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(190, 230, 210));
        table.getTableHeader().setReorderingAllowed(false);
        table.setGridColor(new Color(210, 235, 220));

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < cols.length; i++)
            table.getColumnModel().getColumn(i).setCellRenderer(center);

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable t, Object val, boolean sel, boolean foc, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                if (!sel) c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 252, 248));
                setHorizontalAlignment(SwingConstants.CENTER);
                return c;
            }
        });

        loadData();

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(new LineBorder(new Color(180, 220, 200)));

        JPanel pnlSum = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 4));
        pnlSum.setOpaque(false);
        JLabel lblSum = new JLabel("Tổng cộng (đã VAT): " + tongTien);
        lblSum.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblSum.setForeground(new Color(30, 130, 80));
        pnlSum.add(lblSum);

        pnl.add(scroll,  BorderLayout.CENTER);
        pnl.add(pnlSum,  BorderLayout.SOUTH);
        return pnl;
    }

    // ── FOOTER (nút In + Đóng) ────────────────────────────
    private JPanel taoFooter() {
        JPanel pnl = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 10));
        pnl.setBackground(new Color(245, 250, 248));
        pnl.setBorder(new MatteBorder(1, 0, 0, 0, new Color(200, 230, 215)));

        JButton btnIn = makeBtn("  In phiếu", new Color(30, 130, 80), Color.WHITE);
        btnIn.addActionListener(e -> inPhieu());

        JButton btnDong = makeBtn("Đóng", new Color(220, 225, 235), new Color(60, 60, 60));
        btnDong.addActionListener(e -> dispose());

        pnl.add(btnIn);
        pnl.add(btnDong);
        return pnl;
    }

    // ── LOAD DATA ─────────────────────────────────────────
    private void loadData() {
        tableModel.setRowCount(0);
        ArrayList<ChiTiet_PhieuXuat> list = ctBus.getChiTietByMaPx_DB(maPX);
        int stt = 1;
        for (ChiTiet_PhieuXuat ct : list) {
            String vatStr = String.format("%.0f%%", ct.getThueVAT() * 100);
            tableModel.addRow(new Object[]{
                    stt++,
                    ct.getSanPham().getMaSP(),
                    ct.getSanPham().getTenSP(),
                    ct.getSoLuong(),
                    df.format(ct.getDonGia()),
                    vatStr,
                    df.format(ct.getThanhTien())
            });
        }
    }

    // ── IN PHIẾU ──────────────────────────────────────────
    private void inPhieu() {
        PrinterJob job = PrinterJob.getPrinterJob();
        PageFormat pf = job.defaultPage();
        pf.setOrientation(PageFormat.PORTRAIT);

        final PageFormat finalPf = pf;
        Printable printable = (graphics, pageFormat, pageIndex) -> {
            if (pageIndex > 0) return Printable.NO_SUCH_PAGE;

            Graphics2D g2 = (Graphics2D) graphics;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            float x = (float) finalPf.getImageableX();
            float y = (float) finalPf.getImageableY();
            float w = (float) finalPf.getImageableWidth();

            g2.translate(x, y);

            g2.setFont(new Font("Segoe UI", Font.BOLD, 16));
            g2.setColor(new Color(30, 130, 80));
            drawCentered(g2, "PHIẾU XUẤT HÀNG", w, 0);

            g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            g2.setColor(Color.BLACK);
            float lineH = 18f;
            float curY = 28f;

            g2.drawString("Mã phiếu  : " + maPX,       0, curY);
            g2.drawString("Ngày xuất : " + ngayXuat,   w / 2f, curY); curY += lineH;
            g2.drawString("Khách hàng: " + khachHang,  0, curY); curY += lineH + 4;

            String[] headers = {"STT", "Mã SP", "Tên sản phẩm", "SL",
                    "Đơn giá", "VAT", "Thành tiền"};
            float nameW = w - 30 - 70 - 40 - 90 - 40 - 90;
            float[] colW = {30, 70, nameW, 40, 90, 40, 90};
            float rowH = 16f;

            g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
            g2.setColor(new Color(30, 130, 80));
            float cx = 0;
            for (int i = 0; i < headers.length; i++) {
                g2.drawRect((int)cx, (int)curY, (int)colW[i], (int)rowH);
                g2.drawString(headers[i], cx + 3, curY + rowH - 4);
                cx += colW[i];
            }
            curY += rowH;

            g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            g2.setColor(Color.BLACK);
            for (int r = 0; r < tableModel.getRowCount(); r++) {
                cx = 0;
                for (int c = 0; c < headers.length; c++) {
                    g2.setColor(r % 2 == 0 ? Color.WHITE : new Color(240, 250, 245));
                    g2.fillRect((int)cx, (int)curY, (int)colW[c], (int)rowH);
                    g2.setColor(new Color(190, 220, 205));
                    g2.drawRect((int)cx, (int)curY, (int)colW[c], (int)rowH);
                    g2.setColor(Color.BLACK);
                    String val = tableModel.getValueAt(r, c) != null
                            ? tableModel.getValueAt(r, c).toString() : "";
                    g2.drawString(val, cx + 3, curY + rowH - 4);
                    cx += colW[c];
                }
                curY += rowH;
            }

            curY += 15;
            g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
            g2.setColor(new Color(30, 130, 80));
            g2.drawString("Tổng cộng (đã VAT): " + tongTien, w - 240, curY);

            return Printable.PAGE_EXISTS;
        };

        new PrintPreviewDialog(this, printable, finalPf,
                "Phiếu xuất — " + maPX).setVisible(true);
    }

    // ── HELPERS ───────────────────────────────────────────
    private void drawCentered(Graphics2D g2, String text, float width, float y) {
        FontMetrics fm = g2.getFontMetrics();
        float tx = (width - fm.stringWidth(text)) / 2f;
        g2.drawString(text, tx, y + fm.getAscent());
    }

    private JLabel headerLbl(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        l.setForeground(new Color(180, 230, 200));
        return l;
    }

    private JLabel headerVal(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        l.setForeground(Color.WHITE);
        return l;
    }

    private JButton makeBtn(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(140, 34));
        btn.setOpaque(true);
        return btn;
    }
}