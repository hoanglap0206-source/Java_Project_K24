package GUI;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.print.*;

public class PrintPreviewDialog extends JDialog {

    private final Printable  printable;
    private final PageFormat pageFormat;
    private final String     jobName;

    // Scale để vừa panel (tính sau khi biết kích thước panel)
    private double scale = 1.0;

    public PrintPreviewDialog(Window owner, Printable printable,
                              PageFormat pageFormat, String jobName) {
        super(owner, "Xem trước khi in — " + jobName, ModalityType.APPLICATION_MODAL);
        this.printable  = printable;
        this.pageFormat = pageFormat;
        this.jobName    = jobName;

        setLayout(new BorderLayout(0, 0));
        getContentPane().setBackground(new Color(60, 60, 65));

        add(taoHeader(),  BorderLayout.NORTH);
        add(taoPreview(), BorderLayout.CENTER);
        add(taoFooter(),  BorderLayout.SOUTH);

        setSize(860, 660);
        setMinimumSize(new Dimension(680, 500));
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    // ── HEADER ────────────────────────────────────────────
    private JPanel taoHeader() {
        JPanel pnl = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 10));
        pnl.setBackground(new Color(40, 40, 45));
        pnl.setBorder(new MatteBorder(0, 0, 1, 0, new Color(80, 80, 85)));

        JLabel lbl = new JLabel("Xem trước: " + jobName);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl.setForeground(new Color(220, 225, 235));
        pnl.add(lbl);
        return pnl;
    }

    // ── KHU VỰC PREVIEW ───────────────────────────────────
    private JScrollPane taoPreview() {
        // Kích thước trang (pt → pixel, 96 dpi)
        double dpi    = 96.0;
        double ptToPx = dpi / 72.0;
        int pageW = (int) Math.round(pageFormat.getWidth()  * ptToPx);
        int pageH = (int) Math.round(pageFormat.getHeight() * ptToPx);

        // Render Printable → BufferedImage
        BufferedImage img = new BufferedImage(pageW, pageH, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,   RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, pageW, pageH);

        // Tạo PageFormat theo pixel scale
        PageFormat pxPf = new PageFormat();
        Paper paper = new Paper();
        paper.setSize(pageW, pageH);
        double ix = pageFormat.getImageableX()  * ptToPx;
        double iy = pageFormat.getImageableY()  * ptToPx;
        double iw = pageFormat.getImageableWidth()  * ptToPx;
        double ih = pageFormat.getImageableHeight() * ptToPx;
        paper.setImageableArea(ix, iy, iw, ih);
        pxPf.setPaper(paper);
        pxPf.setOrientation(pageFormat.getOrientation());

        try {
            printable.print(g2, pxPf, 0);
        } catch (PrinterException e) {
            e.printStackTrace();
        }
        g2.dispose();

        // Hiển thị ảnh trong panel có bóng đổ giả
        JLabel imgLabel = new JLabel(new ImageIcon(img));
        imgLabel.setBorder(new CompoundBorder(
                new EmptyBorder(18, 18, 18, 18),
                new CompoundBorder(
                        BorderFactory.createLineBorder(new Color(30, 30, 30), 1),
                        BorderFactory.createLineBorder(new Color(180, 180, 180), 1)
                )
        ));
        imgLabel.setOpaque(false);

        JPanel pnlCenter = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        pnlCenter.setBackground(new Color(60, 60, 65));
        pnlCenter.add(imgLabel);

        JScrollPane scroll = new JScrollPane(pnlCenter);
        scroll.getViewport().setBackground(new Color(60, 60, 65));
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        return scroll;
    }

    // ── FOOTER (In ngay + Đóng) ───────────────────────────
    private JPanel taoFooter() {
        JPanel pnl = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 10));
        pnl.setBackground(new Color(40, 40, 45));
        pnl.setBorder(new MatteBorder(1, 0, 0, 0, new Color(80, 80, 85)));

        JButton btnIn = makeBtn("🖨  In ngay", new Color(37, 120, 220), Color.WHITE);
        btnIn.addActionListener(e -> {
            dispose(); // đóng preview trước
            guiLenhIn();
        });

        JButton btnDong = makeBtn("Đóng", new Color(75, 75, 80), new Color(220, 220, 220));
        btnDong.addActionListener(e -> dispose());

        pnl.add(btnIn);
        pnl.add(btnDong);
        return pnl;
    }

    // ── GỬI LỆNH IN THẬT ─────────────────────────────────
    private void guiLenhIn() {
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setJobName(jobName);
        job.setPrintable(printable, pageFormat);

        if (job.printDialog()) {
            try {
                job.print();
                JOptionPane.showMessageDialog(getOwner(),
                        "In thành công!", "Thông báo",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (PrinterException ex) {
                JOptionPane.showMessageDialog(getOwner(),
                        "Lỗi khi in: " + ex.getMessage(),
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ── HELPERS ───────────────────────────────────────────
    private JButton makeBtn(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(130, 34));
        btn.setOpaque(true);
        // Hover
        Color hover = bg.darker();
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) { btn.setBackground(hover); }
            @Override public void mouseExited (java.awt.event.MouseEvent e) { btn.setBackground(bg);    }
        });
        return btn;
    }
}