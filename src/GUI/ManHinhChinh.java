package GUI;

import BUS.NV_BUS;
import javax.swing.*;
import java.awt.*;

public class ManHinhChinh extends JFrame {

    private CardLayout cardLayout;
    private JPanel contentPanel;
    public static String currentMaNV;
    private ThanhTieuDe thanhTieuDe;
    private ThanhBen thanhBen;
    public static TrangKeKho trangKeKho;

    public ManHinhChinh(String maNV) {
        setUndecorated(true);
        setSize(1200, 720);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(0, 0));

        ManHinhChinh.currentMaNV = maNV;
        NV_BUS nv = new NV_BUS();
        String nameUser = nv.getTenNV_BUS(maNV);

        // Tạo và add ThanhTieuDe
        thanhTieuDe = new ThanhTieuDe(this, nameUser);
        add(thanhTieuDe, BorderLayout.NORTH);

        // Tạo Content Panel với CardLayout
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        TrangKeKho keKho = new TrangKeKho();
        trangKeKho = keKho;

        // Thêm các trang vào contentPanel
        contentPanel.add(new TrangTongQuan(), "Tổng quan");
        contentPanel.add(new TrangHoSo(), "Hồ sơ");
        contentPanel.add(new TrangSanPham(), "Quản lý sản phẩm");
        contentPanel.add(new TrangNhaCungCap(), "Quản lý nhà cung cấp");
        contentPanel.add(new TrangKhachHang(), "Thông tin khách hàng");
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

        thanhBen = new ThanhBen(this, maNV);
        add(thanhBen, BorderLayout.WEST);

        // Icon ứng dụng
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/Img/Logo.jpg"));
            setIconImage(icon.getImage());
        } catch (Exception e) {
            System.out.println("Không tìm thấy icon");
        }

        setVisible(true);

        // Popup chào mừng
        SwingUtilities.invokeLater(() -> new PopupChaoMung(this, nameUser));
    }

    public ThanhTieuDe getThanhTieuDe() {
        return thanhTieuDe;
    }

    public ThanhBen getThanhBen() {
        return thanhBen;
    }

    public void hienThiTrang(String tenTrang) {
        cardLayout.show(contentPanel, tenTrang);
        if (thanhTieuDe != null) {
            thanhTieuDe.setTitleCN(tenTrang);
        }
    }

    public JPanel getTrangHienTai() {
        for (Component c : contentPanel.getComponents()) {
            if (c.isVisible()) return (JPanel) c;
        }
        return null;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ManHinhChinh("NV99"));
    }
}