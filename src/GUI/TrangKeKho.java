package GUI;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class TrangKeKho extends JPanel {

    private DefaultTableModel tableModel;
    private JLabel lblTenKe; // để thay đổi tên kệ khi click

    public TrangKeKho() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250));

        add(taoTieuDe(), BorderLayout.NORTH);
        add(taoNoiDung(), BorderLayout.CENTER);
    }

    private JPanel taoTieuDe() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(15, 20, 10, 20));
        panel.setBackground(new Color(245, 247, 250));

        JLabel lblTitle = new JLabel("SƠ ĐỒ KHO TỔNG");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));

        panel.add(lblTitle, BorderLayout.WEST); // WEST = trái = hướng Tây
        return panel;
    }

    private JPanel taoNoiDung() {
        JPanel main = new JPanel(new BorderLayout(15, 15));
        main.setBorder(new EmptyBorder(10, 20, 20, 20));
        main.setBackground(new Color(245, 247, 250));

        JPanel panelSoDo = new JPanel(new BorderLayout());
        panelSoDo.setBackground(Color.WHITE);
        panelSoDo.setBorder(new CompoundBorder(
                new LineBorder(new Color(220, 220, 220)), // Viền nhìn thấy
                new EmptyBorder(15, 15, 15, 15) // Tạo khoảng cách bên trong
        ));

        panelSoDo.add(taoThanhCongCu(), BorderLayout.NORTH);
        panelSoDo.add(taoDanhSachKe(), BorderLayout.CENTER);

        main.add(panelSoDo, BorderLayout.NORTH);
        main.add(taoBangChiTiet(), BorderLayout.CENTER);

        return main;
    }

    private JPanel taoThanhCongCu() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10)); // FlowLayout(căn lề, khoảng cách ngang, khoảng cách dọc)
        panel.setBackground(Color.WHITE);

        JTextField txtSearch = new JTextField("Tìm mã kệ");
        txtSearch.setBackground(new Color(0x63AAFF));
        txtSearch.setForeground(Color.WHITE);
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtSearch.setPreferredSize(new Dimension(200, 30)); // cố định kích thước

        JButton btnLamMoi = new JButton("Làm mới");
        Style.styleButton(btnLamMoi);
        JButton btnExport = new JButton("Export");
        Style.styleButton(btnExport);
        JButton btnAdd = new JButton("+ Thêm");
        Style.styleButton(btnAdd);

        JComboBox<String> cbTrangThai = new JComboBox<>(new String[]{
                "Tất cả",
                "< 50%",
                "50 - 80%",
                "> 80%"
        }); // JComboBox tạo dropdown

        cbTrangThai.setPreferredSize(new Dimension(120, 30)); // ?

        panel.add(txtSearch);
        panel.add(btnLamMoi);
        panel.add(cbTrangThai);
        panel.add(btnExport);
        panel.add(btnAdd);

        return panel;
    }

    private JPanel taoDanhSachKe() {
        JPanel panel = new JPanel(new GridLayout(3, 5, 15, 15));
        panel.setBackground(Color.WHITE);

        for (int i = 1; i <= 15; i++) {
            String maKe = "A" + i;
            int phanTram = i * 6;
            panel.add(taoTheKe(maKe, phanTram));
        } // Lặp 15 lần để tạo 15 kệ: Tạo mã kệ, tạo phần trăm, gọi taoTheKe()

        return panel;
    }

    private JPanel taoTheKe(String maKe, int phanTram) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(new LineBorder(new Color(220, 220, 220)));
        card.setBackground(new Color(250, 250, 250));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR)); // Đổi con trỏ chuôt thành hình bàn tay, dùng để nhận biết cái này click được

        JLabel lblTen = new JLabel(maKe);
        lblTen.setBorder(new EmptyBorder(5, 10, 5, 10));

        JProgressBar progressBar = new JProgressBar();
        progressBar.setValue(phanTram); // min = 0, max = 100, setValue(60) là thanh đầy 60%
        progressBar.setStringPainted(true); // Hiển thị số % trên thanh
        progressBar.setForeground(mauTheoPhanTram(phanTram)); // Gọi method đổi màu progressBar

        card.add(lblTen, BorderLayout.NORTH);
        card.add(progressBar, BorderLayout.CENTER);

        card.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                capNhatBang(maKe);
            }
        }); // Click chuột chạy capNhatBang()

        return card;
    }

    private Color mauTheoPhanTram(int value) {
        if (value < 50) return new Color(0, 210, 77);
        if (value < 80) return new Color(255, 140, 0);
        return new Color(255, 97,97 );
    }

    private JPanel taoBangChiTiet() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new CompoundBorder(
                new LineBorder(new Color(220, 220, 220)),
                new EmptyBorder(15, 15, 15, 15)
        ));

        lblTenKe = new JLabel("Kệ A1");
        lblTenKe.setFont(new Font("Segoe UI", Font.BOLD, 16));

        panel.add(lblTenKe, BorderLayout.NORTH);

        String[] columns = {
                "STT",
                "SKU",
                "Tên sản phẩm",
                "Số lượng",
                "Ngày hết hạn",
                "Trạng thái"
        };

        tableModel = new DefaultTableModel(columns, 0);
        JTable table = new JTable(tableModel);

        table.setRowHeight(28); // Chiều cao của mỗi dòng = 28px
        table.setDefaultRenderer(Object.class, taoRenderer());
        // setDefaultRenderer(): vẽ bảng mặc định
        // Object.class: áp dụng cho các dữ liệu kiểu Object (String, integer,..)
        // taoRenderer(): gọi method tự custom

        JScrollPane scroll = new JScrollPane(table); // Add thanh cuộn
        panel.add(scroll, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footer.setBackground(Color.WHITE);

        JLabel lblTongSoLuong = new JLabel("Tổng số lượng: 14 thùng");
        lblTongSoLuong.setFont(new Font("Segoe UI", Font.BOLD, 14));

        footer.add(lblTongSoLuong);
        panel.add(footer, BorderLayout.SOUTH);
        
        capNhatBang("A1");

        return panel;
    }

    // Custom màu chữ cho cột Trạng thái
    private DefaultTableCellRenderer taoRenderer() { // Trả về bộ vẽ ô (một JLabel)
        return new DefaultTableCellRenderer() { // class ẩn danh, ghi đè cách vẽ ô

            public Component getTableCellRendererComponent(
                    JTable table, Object value, // bảng đang được vẽ, giá trị: "Hết hạn", "Bình thường", "Sắp hết hạn"
                    boolean isSelected, boolean hasFocus,
                    int row, int column) {

                Component c = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);
                // Phải gọi super để đặt style mặc định (text, font, nền) rồi mới được chỉnh màu

                if (column == 5 && !isSelected) { // column 5 = cột trạng thái
                    String trangThai = value.toString();

                    if (trangThai.equals("Hết hạn"))
                        c.setForeground(Color.RED);
                    else if (trangThai.equals("Sắp hết hạn"))
                        c.setForeground(Color.ORANGE);
                    else
                        c.setForeground(new Color(0, 150, 0));
                } else {
                    c.setForeground(Color.BLACK);
                }

                setHorizontalAlignment(SwingConstants.CENTER); // Căn giữa các text
                return c;
            }
        };
    }

    private void capNhatBang(String maKe) {
        lblTenKe.setText("Kệ " + maKe);
        tableModel.setRowCount(0); // Xóa toàn bộ dữ liệu trong bảng để click kệ không bị cộng dồn dữ liệu

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        for (int i = 1; i <= 5; i++) {
            LocalDate ngayHetHan = LocalDate.now().plusDays(i * 10); // ?
            String trangThai = tinhTrangThai(ngayHetHan);

            tableModel.addRow(new Object[]{
                    i,
                    "SKU00" + i,
                    "Sản phẩm " + maKe,
                    "5 thùng",
                    ngayHetHan.format(formatter),
                    trangThai
            });
        }
    }

    private String tinhTrangThai(LocalDate ngayHetHan) {
        long soNgay = ChronoUnit.DAYS.between(LocalDate.now(), ngayHetHan);
        // now(): ngày hôm nay, plusDays(10): cộng thêm 10 ngày (demo dữ liệu giả)
        // ChronoUnit.DAYS.between(ngay1, ngay2): Tính số ngày chênh lệch
        if (soNgay <= 0) return "Hết hạn";
        if (soNgay <= 30) return "Sắp hết hạn";
        return "Bình thường";
    }
}


/* Trang này dùng các loại:
- Layout
BorderLayout
FlowLayout
GridLayout

- Border
LineBorder
EmptyBorder
CompoundBorder

- Event
MouseListener

- JTable
DefaultTableModel
Custom Renderer
JScrollPane

- Date API (Java 8)
LocalDate
ChronoUnit
DateTimeFormatter
 */