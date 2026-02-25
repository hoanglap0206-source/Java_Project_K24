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
        JPanel outerPanel = new JPanel();
        outerPanel.setBorder(new EmptyBorder(5,5,5,5));
        JPanel outPanel = new JPanel();
        outPanel.setBorder(new CompoundBorder(
                new LineBorder(Color.BLACK, 2, true),
                new EmptyBorder(20, 30, 20, 30)
        ));
        JPanel innerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 6));
        innerPanel.setBackground(new Color(230, 230, 230));
        innerPanel.setBorder(
                new CompoundBorder(
                        new LineBorder(Color.GRAY, 2, true),
                        new EmptyBorder(8, 8, 8, 8)
                )
        );
        JTextField txtSearch = new JTextField("Tìm kiếm", 18);
        txtSearch.setForeground(Color.GRAY);
        txtSearch.setBorder(
                new CompoundBorder(
                        new LineBorder(Color.GRAY, 1, true),
                        new EmptyBorder(5, 10, 5, 30)
                )
        );
        // ===== Nút làm mới =====
        JButton btnRefresh = new JButton("Làm mới");
        btnRefresh.setFocusPainted(false);
        btnRefresh.setBackground(Color.WHITE);
        innerPanel.add(txtSearch);
        innerPanel.add(btnRefresh);
        outPanel.add(innerPanel);
        outerPanel.add(outPanel);
        uiLeft.add(outerPanel,BorderLayout.NORTH);
//===================================================================================
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

        uiLeft.add(scroll, BorderLayout.CENTER);

        // ================= TAIL (BOTTOM) =================

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

        uiLeft.add(tailWrapper, BorderLayout.SOUTH);

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
        uiRight.add(headRightWrap,BorderLayout.NORTH);
//==========================================================================================
        JPanel TableRightWrap = new JPanel(new GridLayout(2,1,10,20));
        String[] colsRight = {"STT","Mã SP", "Tên sản phẩm", "Số lượng", "NN","Đơn giá"};

        DefaultTableModel tableModelRight = new DefaultTableModel(colsRight, 100);
        JTable tableRight = new JTable(tableModelRight);
        tableRight.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tableRight.getColumnModel().getColumn(0).setPreferredWidth(40); // STT
        tableRight.getColumnModel().getColumn(1).setPreferredWidth(60); // Mã SP
        tableRight.getColumnModel().getColumn(2).setPreferredWidth(150); // Tên SP
        tableRight.getColumnModel().getColumn(3).setPreferredWidth(60); // Số lượng
        tableRight.getColumnModel().getColumn(4).setPreferredWidth(70); // NN
        tableRight.getColumnModel().getColumn(5).setPreferredWidth(70); // Đơn giá
        JScrollPane scrollRight = new JScrollPane(tableRight);
        scrollRight.setBorder(new EmptyBorder(25, 10, 10, 10));
        TableRightWrap.add(scrollRight);

        JPanel btnWrap = new JPanel(new GridLayout(1,3,15,0));
        JButton btnXuat,btnSua,btnXoa;
        btnXuat = new JButton("Xuất Excel");
        btnSua = new JButton("Sửa số lượng");
        btnXoa = new JButton("Xóa sản phẩm");

        btnSua.setFocusPainted(false);
        btnXoa.setFocusPainted(false);
        btnXuat.setFocusPainted(false);

        btnSua.setBackground(Color.WHITE);
        btnXuat.setBackground(Color.WHITE);
        btnXoa.setBackground(Color.WHITE);

        btnWrap.add(btnXuat);
        btnWrap.add(btnSua);
        btnWrap.add(btnXoa);
        JPanel btnContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        btnContainer.add(btnWrap);
        TableRightWrap.add(btnContainer);
        uiRight.add(TableRightWrap,BorderLayout.CENTER);
//        =============================================================================

        JPanel tailRightWrapper = new JPanel(new GridBagLayout());
        tailRightWrapper.setBorder(new EmptyBorder(10, 0, 60, 0));

        JPanel tailRight = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));

        JLabel lblTongTien = new JLabel("TỔNG TIỀN NHẬP");
        JTextField txtTongTien = new JTextField("", 8);

        JButton btnNhap = new JButton("Nhập hàng");
        btnNhap.setBackground(new Color(102, 255, 102));
        btnNhap.setFocusPainted(false);

        tailRight.add(lblTongTien);
        tailRight.add(txtTongTien);
        tailRight.add(btnNhap);

        tailRightWrapper.add(tailRight);
        uiRight.add(tailRightWrapper,BorderLayout.SOUTH);

        setLayout(new BorderLayout());
        add(main, BorderLayout.CENTER);

    }
    public static void main(String[] arg){
        new NhapKho_GUI();
    }
}
