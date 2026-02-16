package UI;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class NhapKho_GUI extends JPanel {
    public NhapKho_GUI() {
        JPanel main = new JPanel(new GridLayout(1, 2));
        JPanel uiLeft = new JPanel(new BorderLayout());
        JPanel uiRight = new JPanel(new BorderLayout());

        main.add(uiLeft);
        main.add(uiRight);
//================= Left =========================================
        uiLeft.add(createSearchBar(),BorderLayout.NORTH);
        uiLeft.add(createTableLeft(), BorderLayout.CENTER);
        uiLeft.add(createTailLeft(), BorderLayout.SOUTH);
//=================== Right ========================================
        uiRight.add(createFormRight(),BorderLayout.NORTH);
        uiRight.add(createTableRight(),BorderLayout.CENTER);
        uiRight.add(createTailRight(),BorderLayout.SOUTH);

        setLayout(new BorderLayout());
        add(main, BorderLayout.CENTER);

    }
    public JPanel createSearchBar() {

        JPanel outerPanel = new JPanel();
        outerPanel.setBorder(new EmptyBorder(5, 5, 5, 15));

        JPanel outPanel = new JPanel();
        outPanel.setBorder(new CompoundBorder(
                new LineBorder(Color.BLACK, 2, true),
                new EmptyBorder(15, 20, 15, 20)   // ↓ GIẢM padding
        ));

        JPanel innerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 4)); // ↓ giảm vertical gap
        innerPanel.setBackground(new Color(230, 230, 230));
        innerPanel.setBorder(
                new CompoundBorder(
                        new LineBorder(Color.GRAY, 2, true),
                        new EmptyBorder(6, 6, 6, 6)   // ↓ gọn hơn
                )
        );

        // ===== TEXT SEARCH =====
        JTextField txtSearch = new JTextField(22); // ↑ dài hơn
        txtSearch.setText("Tìm kiếm");
        txtSearch.setForeground(Color.GRAY);
        txtSearch.setPreferredSize(new Dimension(255, 28)); // chiều cao chuẩn
        txtSearch.setBorder(
                new CompoundBorder(
                        new LineBorder(Color.GRAY, 1, true),
                        new EmptyBorder(5, 10, 5, 10)
                )
        );

        // ===== NÚT LÀM MỚI =====
        JButton btnRefresh = new JButton("Làm mới");
        btnRefresh.setFocusPainted(false);
        btnRefresh.setBackground(Color.WHITE);
        btnRefresh.setPreferredSize(new Dimension(115, 28)); // bằng chiều cao txtSearch

        innerPanel.add(txtSearch);
        innerPanel.add(btnRefresh);

        outPanel.add(innerPanel);
        outerPanel.add(outPanel);

        return outerPanel;
    }
    public JScrollPane createTableLeft(){
        String[] cols = {"Mã SP", "Tên sản phẩm", "Số lượng", "Đơn giá"};

        DefaultTableModel tableModel = new DefaultTableModel(cols, 100);
        JTable table = new JTable(tableModel);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.getColumnModel().getColumn(0).setPreferredWidth(60); // Mã SP
        table.getColumnModel().getColumn(1).setPreferredWidth(200); // Tên SP
        table.getColumnModel().getColumn(2).setPreferredWidth(70); // Số lượng
        table.getColumnModel().getColumn(3).setPreferredWidth(100); // Đơn giá
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(new EmptyBorder(15, 20, 20, 20));
        return scroll;
    }
    public JPanel createTailLeft(){
        JPanel tailWrapper = new JPanel(new GridBagLayout());
        tailWrapper.setBorder(new EmptyBorder(10, 0, 60, 0));

        JPanel tailLeft = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));

        JLabel lblSoLuong = new JLabel("SỐ LƯỢNG");
        JTextField txtSoLuong = new JTextField("3", 5);

        JButton btnThem = new JButton("Thêm");
        btnThem.setBackground(new Color(102, 255, 102));
        btnThem.setFocusPainted(false);

        tailLeft.add(lblSoLuong);
        tailLeft.add(txtSoLuong);
        tailLeft.add(btnThem);

        tailWrapper.add(tailLeft);
        return tailWrapper;
    }
    public JPanel createFormRight(){
        JPanel headRightWrap = new JPanel(new GridLayout(3,2,0,20));
        headRightWrap.setBorder(new EmptyBorder(40,5,15,20));
        JLabel lblID = new JLabel("Mã phiếu nhập");
        JLabel lblNCC = new JLabel("Nhà cung cấp");
        JLabel lblNTP = new JLabel("Người tạo phiếu");
        JTextField txtID = new JTextField("Tự động tạo");
        txtID.setEnabled(false);
        JTextField txtNCC = new JTextField("Tên nhà cung cấp");
        JTextField txtNTP = new JTextField("Tên người dùng");
        txtNTP.setEnabled(false);
        headRightWrap.add(lblID);
        headRightWrap.add(txtID);
        headRightWrap.add(lblNCC);
        headRightWrap.add(txtNCC);
        headRightWrap.add(lblNTP);
        headRightWrap.add(txtNTP);
        return headRightWrap;
    }
    public JPanel createTableRight(){
            // ===== WRAP TỔNG (FIX: KHÔNG DÙNG GRIDLAYOUT) =====
            JPanel TableRightWrap = new JPanel(new BorderLayout(0, 20));

            // ===== TABLE =====
            String[] colsRight = {"STT", "Mã SP", "Tên sản phẩm", "Số lượng", "NN", "Đơn giá"};
            DefaultTableModel tableModelRight = new DefaultTableModel(colsRight, 100);

            JTable tableRight = new JTable(tableModelRight);

            // cho phép bảng cao hơn
            tableRight.setPreferredScrollableViewportSize(
                    new Dimension(520, 600)
            );

            tableRight.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            tableRight.getColumnModel().getColumn(0).setPreferredWidth(40);
            tableRight.getColumnModel().getColumn(1).setPreferredWidth(60);
            tableRight.getColumnModel().getColumn(2).setPreferredWidth(150);
            tableRight.getColumnModel().getColumn(3).setPreferredWidth(60);
            tableRight.getColumnModel().getColumn(4).setPreferredWidth(70);
            tableRight.getColumnModel().getColumn(5).setPreferredWidth(70);

            JScrollPane scrollRight = new JScrollPane(tableRight);
            scrollRight.setBorder(new EmptyBorder(25, 10, 0, 10));
            // add bảng vào CENTER
            TableRightWrap.add(scrollRight, BorderLayout.CENTER);

            // ===== BUTTON =====
            JPanel btnWrap = new JPanel(new GridLayout(1, 3, 15, 0));

            JButton btnXuat = new JButton("Xuất Excel");
            JButton btnSua = new JButton("Sửa số lượng");
            JButton btnXoa = new JButton("Xóa sản phẩm");

            btnXuat.setFocusPainted(false);
            btnSua.setFocusPainted(false);
            btnXoa.setFocusPainted(false);

            btnXuat.setBackground(Color.WHITE);
            btnSua.setBackground(Color.WHITE);
            btnXoa.setBackground(Color.WHITE);

            btnWrap.add(btnXuat);
            btnWrap.add(btnSua);
            btnWrap.add(btnXoa);

            JPanel btnContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
            btnContainer.add(btnWrap);

            // add nút xuống SOUTH
            TableRightWrap.add(btnContainer, BorderLayout.SOUTH);

            return TableRightWrap;
        }

    public JPanel createTailRight(){
        JPanel tailRightWrapper = new JPanel(new GridBagLayout());
        tailRightWrapper.setBorder(new EmptyBorder(10, 0, 60, 0));

        JPanel tailRight = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));

        JLabel lblTongTien = new JLabel("TỔNG TIỀN NHẬP");
        JTextField txtTongTien = new JTextField("", 10);
        txtTongTien.setEditable(false);
        JButton btnNhap = new JButton("Nhập hàng");
        btnNhap.setBackground(new Color(102, 255, 102));
        btnNhap.setFocusPainted(false);

        tailRight.add(lblTongTien);
        tailRight.add(txtTongTien);
        tailRight.add(btnNhap);

        tailRightWrapper.add(tailRight);
        return tailRightWrapper;
    }
    public static void main(String[] arg){
        new NhapKho_GUI();
    }
}
