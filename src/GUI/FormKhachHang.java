package GUI;

import BUS.KhachHang_BUS;
import Model.KhachHang;
import javax.swing.*;
import java.awt.*;

public class FormKhachHang extends JDialog {
    private JTextField txtMa, txtTen, txtSdt, txtDiaChi;
    private JButton btnLuu;
    private KhachHang_BUS khBUS ;
    private TrangKhachHang parent;
    private String mode; // "THEM" hoặc "SUA"
    private KhachHang khSelected; // Lưu dữ liệu cũ nếu là chế độ Sửa

    // Constructor dùng chung
    public FormKhachHang(TrangKhachHang parent, String mode, KhachHang kh) {
        this.parent = parent;
        this.mode = mode;

        this.khSelected = kh;
        this.khBUS=parent.get_khBUS();
        initUI();

        // Nếu là chế độ SỬA, đổ dữ liệu vào các ô
        if (mode.equals("SUA") && kh != null) {
            setTitle("Chỉnh Sửa Thông Tin Khách Hàng");
            txtMa.setText(kh.getMaKH());
            txtMa.setEditable(false); // Sửa thì không cho đổi Mã
            txtTen.setText(kh.getHoTenKH());
            txtSdt.setText(kh.getSdt());
            txtDiaChi.setText(kh.getDiaChi());
            btnLuu.setText("Cập nhật");
        } else {
            setTitle("Thêm Khách Hàng Mới");
            btnLuu.setText("Lưu mới");
        }
    }

    private void initUI() {
        setSize(450, 350);
        setLocationRelativeTo(null);
        setModal(true);
        setLayout(new BorderLayout());

        // Panel chứa 2 cột (Labels và Fields)
        JPanel pnlInput = new JPanel(new GridBagLayout());
        pnlInput.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Cột 1 (Label) và Cột 2 (TextField)
        addControl(pnlInput, "Mã khách hàng:", txtMa = new JTextField(20), gbc, 0);
        addControl(pnlInput, "Tên khách hàng:", txtTen = new JTextField(20), gbc, 1);
        addControl(pnlInput, "Số điện thoại:", txtSdt = new JTextField(20), gbc, 2);
        addControl(pnlInput, "Địa chỉ:", txtDiaChi = new JTextField(20), gbc, 3);

        // Nút lưu bên dưới góc phải
        JPanel pnlBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnLuu = new JButton();
        btnLuu.setPreferredSize(new Dimension(120, 35));
        pnlBottom.add(btnLuu);

        add(pnlInput, BorderLayout.CENTER);
        add(pnlBottom, BorderLayout.SOUTH);

        // Sự kiện nút Lưu
        btnLuu.addActionListener(e -> xuLyLuu());
    }

    private void addControl(JPanel p, String label, JTextField tf, GridBagConstraints gbc, int row) {
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        p.add(new JLabel(label), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        p.add(tf, gbc);
    }

    private void xuLyLuu() {
        // Tạo đối tượng từ Input
        KhachHang kh = new KhachHang(
                txtMa.getText(),
                txtTen.getText(),
                txtDiaChi.getText(),
                txtSdt.getText(),
                (mode.equals("THEM") ? "0" : khSelected.getCT())
        );

        String mess;
        if (mode.equals("THEM")) {
            mess = khBUS.addKhachHang(kh); // Gọi BUS thêm
        } else {
            mess = khBUS.updateKH(kh); // Gọi BUS sửa
        }

        JOptionPane.showMessageDialog(this, mess);
        if (mess.contains("thành công")) {
            parent.fillToTable(); // Refresh SQL lên bảng
            dispose();
        }
    }
}