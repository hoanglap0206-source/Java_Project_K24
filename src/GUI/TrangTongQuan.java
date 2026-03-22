package GUI;

import BUS.NCC_BUS;
import BUS.NV_BUS;
import BUS.SanPham_BUS;
import BUS.ChiTietKe_BUS;
import Model.SanPham;
import Model.ChiTietKe;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.ArrayList;

public class TrangTongQuan extends JPanel {

    private static final Color BG_MAIN = new Color(245, 247, 252);
    private static final Color BG_CARD = Color.WHITE;
    private static final Color BG_HEADER = new Color(231, 242, 255);
    private static final Color BORDER_COLOR = new Color(218, 230, 248);

    private static final Color ACCENT_BLUE = new Color(37, 120, 220);
    private static final Color ACCENT_GREEN = new Color(34, 197, 120);
    private static final Color ACCENT_ORANGE = new Color(251, 146, 60);
    private static final Color ACCENT_PURPLE = new Color(139, 92, 246);

    private static final Color TEXT_TITLE = new Color(30, 40, 70);
    private static final Color TEXT_SUB = new Color(100, 115, 145);

    private final SanPham_BUS spBUS = new SanPham_BUS();
    private final NCC_BUS nccBUS = new NCC_BUS();
    private final NV_BUS nvBUS = new NV_BUS();
    private final ChiTietKe_BUS ctBUS = new ChiTietKe_BUS();

    public TrangTongQuan() {
        setLayout(new BorderLayout(0, 20));
        setBackground(BG_MAIN);
        setBorder(new EmptyBorder(24, 24, 24, 24));

        add(taoTieuDeTrang(), BorderLayout.NORTH);
        add(taoNoiDung(), BorderLayout.CENTER);
    }

    private JPanel taoTieuDeTrang() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(0, 0, 4, 0));

        JLabel lblTitle = new JLabel("Tổng quan hệ thống");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(TEXT_TITLE);

        JLabel lblSub = new JLabel("Thống kê dữ liệu kho hàng theo thời gian thực");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSub.setForeground(TEXT_SUB);

        JPanel txtBlock = new JPanel();
        txtBlock.setOpaque(false);
        txtBlock.setLayout(new BoxLayout(txtBlock, BoxLayout.Y_AXIS));
        txtBlock.add(lblTitle);
        txtBlock.add(Box.createVerticalStrut(2));
        txtBlock.add(lblSub);

        panel.add(txtBlock, BorderLayout.WEST);
        return panel;
    }

    private JPanel taoNoiDung() {
        JPanel panel = new JPanel(new BorderLayout(0, 20));
        panel.setOpaque(false);

        panel.add(taoKhuTheCard(), BorderLayout.NORTH);
        panel.add(taoKhuBang(), BorderLayout.CENTER);

        return panel;
    }

    private JPanel taoKhuTheCard() {
        spBUS.refreshData();

        // Thu thập dữ liệu thực tế
        ArrayList<SanPham> dsSP = spBUS.getListSP();
        int tongLoaiSP = dsSP.size();

        // Tính tổng tồn kho từ ChiTietKe
        int tongTonKho = 0;
        for (SanPham sp : dsSP) {
            tongTonKho += spBUS.getSoLuongTon(sp.getMaSP());
        }

        int tongNCC = nccBUS.getListNCC().size();
        int tongNV = nvBUS.getAll().size();

        JPanel grid = new JPanel(new GridLayout(1, 4, 16, 0));
        grid.setOpaque(false);

        grid.add(taoCard("Loại sản phẩm", tongLoaiSP + " loại",
                "📦", ACCENT_BLUE, new Color(235, 245, 255)));
        grid.add(taoCard("Tổng tồn kho", String.format("%,d", tongTonKho) + " sp",
                "🏭", ACCENT_GREEN, new Color(236, 253, 245)));
        grid.add(taoCard("Nhà cung cấp", tongNCC + " NCC",
                "🤝", ACCENT_ORANGE, new Color(255, 247, 237)));
        grid.add(taoCard("Nhân viên", tongNV + " NV",
                "👤", ACCENT_PURPLE, new Color(245, 243, 255)));

        return grid;
    }

    private JPanel taoCard(String title, String value, String icon, Color accent, Color bgLight) {
        JPanel card = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(accent);
                g2.fillRect(0, 0, 5, getHeight());
            }
        };
        card.setBackground(BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(18, 20, 18, 20)));

        JLabel lblIcon = new JLabel(icon, SwingConstants.CENTER);
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 26));
        lblIcon.setOpaque(true);
        lblIcon.setBackground(bgLight);
        lblIcon.setPreferredSize(new Dimension(56, 56));
        lblIcon.setBorder(new LineBorder(bgLight.darker(), 0));

        JPanel iconWrap = new JPanel(new GridBagLayout());
        iconWrap.setOpaque(false);
        iconWrap.add(lblIcon);

        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblValue.setForeground(TEXT_TITLE);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblTitle.setForeground(TEXT_SUB);

        JPanel bar = new JPanel();
        bar.setBackground(bgLight);
        bar.setPreferredSize(new Dimension(0, 4));
        bar.setBorder(new EmptyBorder(0, 0, 0, 0));

        JPanel barFill = new JPanel();
        barFill.setBackground(accent);
        barFill.setPreferredSize(new Dimension(48, 4));

        JPanel barRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        barRow.setOpaque(false);
        barRow.add(barFill);

        JPanel textBlock = new JPanel();
        textBlock.setOpaque(false);
        textBlock.setLayout(new BoxLayout(textBlock, BoxLayout.Y_AXIS));
        textBlock.add(lblTitle);
        textBlock.add(Box.createVerticalStrut(4));
        textBlock.add(lblValue);
        textBlock.add(Box.createVerticalStrut(10));
        textBlock.add(barRow);

        card.add(iconWrap, BorderLayout.WEST);
        card.add(textBlock, BorderLayout.CENTER);

        return card;
    }

    private JPanel taoKhuBang() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 16, 0));
        panel.setOpaque(false);

        panel.add(taoBangTonKhoTheoSanPham());
        panel.add(taoBangHoatDong());

        return panel;
    }

    // Sản phẩm tồn kho
    private JPanel taoBangTonKhoTheoSanPham() {
        JPanel panel = taoKhungBang("  Tồn kho theo sản phẩm");

        spBUS.refreshData();

        ArrayList<SanPham> dsSP = spBUS.getListSP();

        // Sắp xếp theo tồn kho giảm dần để hiển thị top
        for (int i = 0; i < dsSP.size() - 1; i++) {
            for (int j = i + 1; j < dsSP.size(); j++) {
                int sl1 = spBUS.getSoLuongTon(dsSP.get(i).getMaSP());
                int sl2 = spBUS.getSoLuongTon(dsSP.get(j).getMaSP());
                if (sl1 < sl2) {
                    SanPham temp = dsSP.get(i);
                    dsSP.set(i, dsSP.get(j));
                    dsSP.set(j, temp);
                }
            }
        }

        String[] cols = {"Mã SP", "Tên sản phẩm", "ĐVT", "Tồn kho", "Kệ"};
        Object[][] data = new Object[dsSP.size()][5];

        for (int i = 0; i < dsSP.size(); i++) {
            SanPham sp = dsSP.get(i);
            data[i][0] = sp.getMaSP();
            data[i][1] = sp.getTenSP();
            data[i][2] = sp.getDonViTinh();

            // Lấy số lượng tồn từ ChiTietKe
            int soLuongTon = spBUS.getSoLuongTon(sp.getMaSP());
            data[i][3] = String.format("%,d", soLuongTon);

            // Lấy mã kệ từ ChiTietKe
            ArrayList<ChiTietKe> listCT = ctBUS.getByMaSP(sp.getMaSP());
            String maKe = "";
            if (!listCT.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                for (int j = 0; j < listCT.size(); j++) {
                    if (j > 0) sb.append(", ");
                    sb.append(listCT.get(j).getMaKe());
                }
                maKe = sb.toString();
            } else {
                maKe = "Chưa có kệ";
            }
            data[i][4] = maKe;
        }

        JTable table = taoTable(data, cols);

        // Tô màu cột tồn kho theo mức độ
        table.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val,
                                                           boolean sel, boolean foc, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                setHorizontalAlignment(CENTER);
                if (!sel) {
                    try {
                        String strVal = val.toString().replace(",", "").trim();
                        int sl = Integer.parseInt(strVal);
                        if (sl == 0) {
                            c.setBackground(new Color(255, 235, 235));
                            c.setForeground(Color.BLACK);
                        } else if (sl < 50) {
                            c.setBackground(new Color(255, 247, 225));
                            c.setForeground(Color.BLACK);
                        } else {
                            c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(247, 251, 255));
                            c.setForeground(Color.BLACK);
                        }
                    } catch (NumberFormatException ignored) {}
                }
                return c;
            }
        });

        JScrollPane scroll = taoScroll(table);
        panel.add(scroll, BorderLayout.CENTER);

        // Chú thích màu
        JPanel legend = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 4));
        legend.setOpaque(false);
        legend.add(taoLegendItem(new Color(255, 235, 235), "Hết hàng"));
        legend.add(taoLegendItem(new Color(255, 247, 225), "Sắp hết (< 50)"));
        legend.add(taoLegendItem(new Color(230, 250, 240), "Còn hàng"));
        panel.add(legend, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel taoLegendItem(Color color, String text) {
        JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        item.setOpaque(false);
        JPanel box = new JPanel();
        box.setBackground(color);
        box.setPreferredSize(new Dimension(14, 14));
        box.setBorder(new LineBorder(BORDER_COLOR, 1));
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lbl.setForeground(TEXT_SUB);
        item.add(box);
        item.add(lbl);
        return item;
    }

    // Bảng Hoạt động gần đây
    private JPanel taoBangHoatDong() {
        JPanel panel = taoKhungBang("  Hoạt động 8 ngày gần đây");

        String[] cols = {"Thứ", "Nhập (sp)", "Xuất (sp)", "Chênh lệch"};
        Object[][] data = {
                {"Thứ 2", 120, 95, "+25"},
                {"Thứ 3", 80, 110, "-30"},
                {"Thứ 4", 200, 175, "+25"},
                {"Thứ 5", 60, 45, "+15"},
                {"Thứ 6", 140, 160, "-20"},
                {"Thứ 7", 90, 80, "+10"},
                {"Chủ nhật", 30, 20, "+10"},
                {"Thứ 2", 110, 100, "+10"},
        };

        JTable table = taoTable(data, cols);

        // Tô màu cột chênh lệch
        table.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val,
                                                           boolean sel, boolean foc, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                setHorizontalAlignment(CENTER);
                if (!sel) {
                    String s = val.toString();
                    if (s.startsWith("+")) {
                        setForeground(ACCENT_GREEN);
                        setFont(getFont().deriveFont(Font.BOLD));
                    } else if (s.startsWith("-")) {
                        setForeground(new Color(220, 60, 60));
                        setFont(getFont().deriveFont(Font.BOLD));
                    }
                    setBackground(row % 2 == 0 ? Color.WHITE : new Color(247, 251, 255));
                }
                return c;
            }
        });

        JScrollPane scroll = taoScroll(table);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private JPanel taoKhungBang(String tieuDe) {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(BG_CARD);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(16, 16, 12, 16)));

        JLabel title = new JLabel(tieuDe);
        title.setFont(new Font("Segoe UI", Font.BOLD, 14));
        title.setForeground(TEXT_TITLE);
        title.setBorder(new EmptyBorder(0, 0, 8, 0));

        JSeparator sep = new JSeparator();
        sep.setForeground(BORDER_COLOR);

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.add(title, BorderLayout.NORTH);
        header.add(sep, BorderLayout.SOUTH);

        panel.add(header, BorderLayout.NORTH);
        return panel;
    }

    private JTable taoTable(Object[][] data, String[] cols) {
        DefaultTableModel model = new DefaultTableModel(data, cols) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(34);
        table.setShowVerticalLines(false);
        table.setGridColor(new Color(235, 241, 250));
        table.setSelectionBackground(new Color(220, 238, 255));
        table.setIntercellSpacing(new Dimension(0, 1));

        // Header
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(BG_HEADER);
        header.setForeground(new Color(50, 80, 130));
        header.setPreferredSize(new Dimension(0, 38));
        header.setBorder(new MatteBorder(0, 0, 2, 0, BORDER_COLOR));

        // Căn giữa mặc định
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++)
            table.getColumnModel().getColumn(i).setCellRenderer(center);

        // Striped rows
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable t, Object val, boolean sel, boolean foc, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                setHorizontalAlignment(CENTER);
                if (!sel)
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(247, 251, 255));
                return c;
            }
        });

        return table;
    }

    private JScrollPane taoScroll(JTable table) {
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(new LineBorder(BORDER_COLOR, 1));
        scroll.getViewport().setBackground(Color.WHITE);
        return scroll;
    }
}