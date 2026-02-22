package GUI;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class TrangKeKho extends JPanel {

    private DefaultTableModel model;
    private JLabel lblTenKe;

    public TrangKeKho() {
        setLayout(new BorderLayout());
        setBackground(new Color(255,255,255));

        add(taoThanhCongCu(), BorderLayout.NORTH);
        add(taoBody(), BorderLayout.CENTER);
    }

    private JPanel taoThanhCongCu() {

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(new Color(240,242,245));
        wrapper.setBorder(new EmptyBorder(20,20,10,20));
        // khoảng cách ngoài: trên 20, trái phải 20, dưới 10

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT,12,8));
        panel.setBackground(new Color(220,230,245));
        panel.setBorder(new EmptyBorder(8,12,8,12));
        // khoảng cách trong nền xanh nhạt

        JTextField txtSearch = new JTextField("Tìm kiếm");
        txtSearch.setPreferredSize(new Dimension(220,30));

        JButton btnRefresh = new JButton("Làm mới");
        JButton btnFilter = new JButton("Lọc");
        JButton btnEdit = new JButton("Chỉnh sửa");
        JButton btnDelete = new JButton("Xóa");
        JButton btnAdd = new JButton("+ Thêm");
        JButton btnExcel = new JButton("Xuất excel");

        panel.add(txtSearch);
        panel.add(btnRefresh);
        panel.add(btnFilter);
        panel.add(btnEdit);
        panel.add(btnDelete);
        panel.add(btnAdd);
        panel.add(btnExcel);

        wrapper.add(panel, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel taoBody() {

        JPanel main = new JPanel(new BorderLayout(0,15));
        main.setBorder(new EmptyBorder(10,20,10,20));
        main.setBackground(new Color(240,242,245));

        // Title
        JLabel lblTitle = new JLabel("SƠ ĐỒ KHO TỔNG");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD,18));

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(new Color(240,242,245));
        titlePanel.add(lblTitle, BorderLayout.WEST);

        // Panel chứa grid + bảng
        JPanel content = new JPanel(new BorderLayout(0,20));
        content.setBackground(new Color(240,242,245));

        content.add(taoSoDoKe(), BorderLayout.NORTH);
        content.add(taoBang(), BorderLayout.CENTER);

        main.add(titlePanel, BorderLayout.NORTH);
        main.add(content, BorderLayout.CENTER);

        return main;
    }

    private JPanel taoSoDoKe() {

        JPanel grid = new JPanel(new GridLayout(3,5,15,15));
        grid.setBorder(new EmptyBorder(10,0,10,0));

        for(int i=1;i<=15;i++){
            grid.add(taoTheKe("A"+i, i*4));
        }

        return grid;
    }

    private JPanel taoTheKe(String ten, int percent){

        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(new LineBorder(new Color(200,200,200)));
        card.setPreferredSize(new Dimension(140,50));

        // panel chứa label + progress nằm ngang
        JPanel content = new JPanel(new BorderLayout(10,0));
        content.setBackground(Color.WHITE);
        content.setBorder(new EmptyBorder(15,10,15,10));

        JLabel lbl = new JLabel(ten);
        lbl.setFont(new Font("Segoe UI", Font.BOLD,12));
        lbl.setPreferredSize(new Dimension(25,25)); // cố định độ rộng cho đều

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
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                capNhatBang(ten);
            }
        });

        return card;
    }

    private void capNhatBang(String maKe) {

        lblTenKe.setText("Kệ " + maKe);
        model.setRowCount(0);

        model.addRow(new Object[]{1,"SKU001","Xá xị","Thùng",5,"01/02/2026"});
        model.addRow(new Object[]{2,"SKU002","Coca Cola","Thùng",5,"01/02/2026"});
    }

    private Color mauTheoPhanTram(int p){
        if(p<50) return new Color(40,200,100);
        if(p<80) return new Color(255,170,0);
        return new Color(230,50,50);
    }

    private JPanel taoBang(){
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new CompoundBorder(
                new LineBorder(new Color(220,220,220)),
                new EmptyBorder(15,15,15,15)
        ));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE,350));

        lblTenKe = new JLabel("Kệ A1");
        lblTenKe.setFont(new Font("Segoe UI", Font.BOLD,15));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(new EmptyBorder(0, 0, 10, 0)); // 👈 khoảng cách dưới 10px
        headerPanel.add(lblTenKe, BorderLayout.WEST);

        panel.add(headerPanel, BorderLayout.NORTH);

        String[] cols = {"STT","Mã sản phẩm","Tên sản phẩm","Đơn vị tính","Số lượng","Ngày nhập"};
        model = new DefaultTableModel(cols,0);
        JTable table = new JTable(model);
        table.setRowHeight(28);

        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(210,230,255));
        table.getTableHeader().setForeground(Color.BLACK);
        table.getTableHeader().setReorderingAllowed(false);

        JScrollPane scroll = new JScrollPane(table);
        panel.add(scroll, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footer.setBackground(Color.WHITE);

        JLabel lblTong = new JLabel("Tổng số lượng: 10 thùng");
        lblTong.setFont(new Font("Segoe UI", Font.BOLD,13));
        footer.add(lblTong);

        panel.add(footer, BorderLayout.SOUTH);

        // dữ liệu demo
        model.addRow(new Object[]{1,"SKU001","Xá xị","Thùng",5,"01/02/2026"});
        model.addRow(new Object[]{2,"SKU002","Coca Cola","Thùng",5,"01/02/2026"});
        model.addRow(new Object[]{2,"SKU002","Coca Cola","Thùng",5,"01/02/2026"});
        model.addRow(new Object[]{2,"SKU002","Coca Cola","Thùng",5,"01/02/2026"});
        model.addRow(new Object[]{2,"SKU002","Coca Cola","Thùng",5,"01/02/2026"});
        model.addRow(new Object[]{2,"SKU002","Coca Cola","Thùng",5,"01/02/2026"});

        return panel;
    }
}