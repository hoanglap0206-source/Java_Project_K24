package GUI;

import BUS.BaoCao_BUS;
import Model.BaoCao;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterJob;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

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
        JButton btnIn=new JButton("In báo cáo");
        btnIn.setBackground(new Color(14, 129, 239));
        btnIn.setFont(new Font("Ariial", Font.BOLD, 12));
        btnIn.setPreferredSize(new Dimension(100,35));
        btnIn.setForeground(Color.WHITE);
        btnIn.setFocusPainted(false);

        JButton btnClose = new JButton("Đóng");
        btnClose.setFont(new Font("Arial", Font.BOLD, 12));
        btnClose.setPreferredSize(new Dimension(100, 35));
        btnClose.setBackground(new Color(220, 53, 69)); // Nút đỏ giống thiết kế
        btnClose.setForeground(Color.WHITE);
        btnClose.setFocusPainted(false);

        btnIn.addActionListener(e -> {
            inBaoCaoChiTiet(maSP, tenSP);
        });
        btnClose.addActionListener(e -> dispose()); // Lệnh tắt cửa sổ này đi
        pnlSouth.add(btnIn);
        pnlSouth.add(btnClose);
        add(pnlSouth, BorderLayout.SOUTH);


        loadDummyData(maSP);
    }

    private void inBaoCaoChiTiet(String maSP, String tenSP) {
        if (modelHistory.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Không có dữ liệu để in!", "Thông báo",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        PrinterJob job = PrinterJob.getPrinterJob();
        job.setJobName("Lich su giao dich " + maSP);

        // Vì có tận 7 cột dài, ép sang khổ ngang (LANDSCAPE) in cho đẹp
        PageFormat pf = job.defaultPage();
        pf.setOrientation(PageFormat.PORTRAIT); // Đổi thành Khổ Dọc
        final PageFormat finalPf = pf;

        // Cấu hình linh hoạt tên cột và độ rộng (Tổng ngang ~700px vừa khít A4 ngang)
        String doiTac = loaiBaoCao.equalsIgnoreCase("Nhập hàng") ? "NCC" : "Khách hàng";
        String[] cols = {"Mã phiếu", "Ngày giờ", doiTac, "Nhân viên", "Số lượng", "Đơn giá", "Thành tiền"};

        float[] colW = {110, 95, 50, 55, 50, 50, 60};

        Printable printable = (g, pageFormat, pageIndex) -> {
            java.awt.Graphics2D g2 = (java.awt.Graphics2D) g;
            float ox = (float) finalPf.getImageableX();
            float oy = (float) finalPf.getImageableY();
            float h  = (float) finalPf.getImageableHeight();
            float w  = (float) finalPf.getImageableWidth();
            g2.translate(ox, oy);

            float rowH = 20f; // Cao hơn bản gốc 1 chút để không bị dính chữ

            // --- THUẬT TOÁN PHÂN TRANG ---
            float titleH = 60f; // Khoảng trống cho phần Tiêu đề phía trên
            float footerH = 40f; // Khoảng trống cho dòng tính Tổng tiền phía dưới
            float availableHeight = h - titleH - footerH; // Chiều cao vẽ bảng

            // Số dòng thực tế vẽ được trên 1 trang (trừ đi 1 dòng cho Header bảng)
            int rowsPerPage = (int) (availableHeight / rowH) - 1;
            if (rowsPerPage <= 0) rowsPerPage = 1;

            int totalRows = modelHistory.getRowCount();
            int totalPages = (int) Math.ceil((double) totalRows / rowsPerPage);

            // Tự động ngắt in khi hết số trang
            if (pageIndex >= totalPages) {
                return Printable.NO_SUCH_PAGE;
            }
            // -----------------------------

            float curY = 0f;

            // 1. IN TIÊU ĐỀ
            g2.setFont(new Font("Segoe UI", Font.BOLD, 16));
            g2.setColor(new java.awt.Color(37, 100, 180));
            g2.drawString("LỊCH SỬ GIAO DỊCH SẢN PHẨM", 0, curY + 16);
            curY += 24;

            g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            g2.setColor(Color.BLACK);
            g2.drawString("Sản phẩm: " + tenSP + " (" + maSP + ")  |  Loại: " + loaiBaoCao, 0, curY + 12);
            g2.drawString("Trang " + (pageIndex + 1) + "/" + totalPages, w - 70, curY + 12);
            curY += 26;

            // 2. IN HEADER CỦA BẢNG
            g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
            float cx = 0;
            for (int i = 0; i < cols.length; i++) {
                g2.setColor(new java.awt.Color(200, 218, 240));
                g2.fillRect((int)cx, (int)curY, (int)colW[i], (int)rowH);
                g2.setColor(java.awt.Color.BLACK);
                g2.drawRect((int)cx, (int)curY, (int)colW[i], (int)rowH);
                g2.drawString(cols[i], cx + 3, curY + rowH - 6);
                cx += colW[i];
            }
            curY += rowH;

            // 3. IN NỘI DUNG (Chỉ in các dòng thuộc trang hiện tại)
            int startRow = pageIndex * rowsPerPage;
            int endRow = Math.min(startRow + rowsPerPage, totalRows);

            g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            for (int r = startRow; r < endRow; r++) {
                cx = 0;
                // Vòng lặp duyệt 7 cột (từ 0 đến 6)
                for (int c = 0; c < 7; c++) {
                    g2.setColor(r % 2 == 0 ? java.awt.Color.WHITE : new java.awt.Color(242, 247, 255));
                    g2.fillRect((int)cx, (int)curY, (int)colW[c], (int)rowH);
                    g2.setColor(new java.awt.Color(200, 215, 235));
                    g2.drawRect((int)cx, (int)curY, (int)colW[c], (int)rowH);
                    g2.setColor(java.awt.Color.BLACK);

                    Object val = modelHistory.getValueAt(r, c);
                    String text = val != null ? val.toString() : "";

                    // Căn phải cho các cột chứa số (Cột index 4, 5, 6)
                    if (c >= 4) {
                        int textWidth = g2.getFontMetrics().stringWidth(text);
                        g2.drawString(text, cx + colW[c] - textWidth - 5, curY + rowH - 6);
                    } else {
                        g2.drawString(text, cx + 3, curY + rowH - 6);
                    }

                    cx += colW[c];
                }
                curY += rowH;
            }




            return Printable.PAGE_EXISTS;
        };

        Window owner = SwingUtilities.getWindowAncestor(this);
        new PrintPreviewDialog(owner, printable, finalPf, "Lịch sử giao dịch " + maSP).setVisible(true);
    }
    private void loadDummyData(String maSP) {
        BaoCao_BUS bus=new BaoCao_BUS();
        ArrayList<Object[]> danhsach=bus.getLichSuGiaoDidh(maSP,this.loaiBaoCao);
        SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy HH:mm");
        for (Object[] row:danhsach){
            String ngayGio="";
            if(row[1]!=null){
                ngayGio=sdf.format((java.util.Date)row[1]);
            }
            modelHistory.addRow(new Object[]{
                    row[0],
                    ngayGio,
                    row[2],
                    row[3],
                    String.format("%,d", (Integer) row[4]),
                    String.format("%,.0f", (Float) row[5]),
                    String.format("%,.0f", (Float) row[6])
            });
        }
    }
}