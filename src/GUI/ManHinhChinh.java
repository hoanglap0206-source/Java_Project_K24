package GUI;

import BUS.NV_BUS;
import javax.swing.*;
import java.awt.*;

public class ManHinhChinh extends JFrame {

    private CardLayout cardLayout;
    private JPanel contentPanel;
    public static String currentMaNV;
    private ThanhTieuDe thanhTieuDe;

    public ManHinhChinh(String maNV) {
        // 1. Cấu hình Frame TRƯỚC khi add linh kiện
        setUndecorated(true);
        setSize(1200, 720);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Thiết lập Layout ngay từ đầu
        setLayout(new BorderLayout(0, 0));

        ManHinhChinh.currentMaNV = maNV;
        NV_BUS nv = new NV_BUS();
        String nameUser = nv.getTenNV_BUS(maNV);

        // 2. Khởi tạo và add ThanhTieuDe
        thanhTieuDe = new ThanhTieuDe(this, nameUser);
        add(thanhTieuDe, BorderLayout.NORTH);

        // 3. Khởi tạo Content Panel với CardLayout
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

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

        // 4. Thanh bên (Menu) - Đã tích hợp phân quyền SQL
        add(new ThanhBen(this, maNV), BorderLayout.WEST);

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

    public void hienThiTrang(String tenTrang) {
        cardLayout.show(contentPanel, tenTrang);
        if (thanhTieuDe != null) {
            thanhTieuDe.setTitleCN(tenTrang);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ManHinhChinh("NV99"));
    }
}