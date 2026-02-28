package GUI;

import javax.swing.*;
import java.awt.*;

public class ManHinhChinh extends JFrame {

    private CardLayout cardLayout;
    private JPanel contentPanel;
    public static String currentMaNV;

    public ManHinhChinh() {
        setTitle("QUẢN LÝ KHO NƯỚC GIẢI KHÁT");

        ImageIcon icon = new ImageIcon(getClass().getResource("/Img/ConRua.jpg"));
        setIconImage(icon.getImage());

        setSize(1200, 720);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(5, 3));

        add(new ThanhTieuDe(this), BorderLayout.NORTH);

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        contentPanel.add(new TrangTongQuan(), "Tổng quan");
        contentPanel.add(new TrangHoSo(), "Hồ sơ");
        contentPanel.add(new TrangSanPham(), "Sản phẩm");
        contentPanel.add(new TrangNhaCungCap(), "Nhà cung cấp");
        contentPanel.add(new TrangKhachHang(), "Khách hàng");
        contentPanel.add(new TrangKeKho(), "Kệ kho");
        contentPanel.add(new TrangNhapKho(), "Nhập kho");
        contentPanel.add(new TrangXuatKho(), "Xuất kho");
        contentPanel.add(new TrangPhieuNhap(), "Phiếu nhập");
        contentPanel.add(new TrangPhieuXuat(), "Phiếu xuất");
        contentPanel.add(new TrangTonKho(), "Tồn kho");
        contentPanel.add(new TrangBaoCao(), "Báo cáo");
        contentPanel.add(new TrangApThue(), "Áp thuế");
        contentPanel.add(new TrangQuanLyTaiKhoan(), "Quản lý tài khoản");
        contentPanel.add(new TrangPhanQuyen(), "Phân quyền");

        add(contentPanel, BorderLayout.CENTER);

        add(new ThanhBen(this), BorderLayout.WEST);

        setVisible(true);
    }

    public void hienThiTrang(String tenTrang) {
        cardLayout.show(contentPanel, tenTrang);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ManHinhChinh::new);
    }
}
