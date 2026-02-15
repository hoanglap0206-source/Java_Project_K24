package UI;

import javax.swing.*;
import java.awt.*;
import java.util.Map;
import java.util.LinkedHashMap;

public class GiaoDienChinh_Content extends JPanel {
    private CardLayout cardLayout;
    public GiaoDienChinh_Content(){
        cardLayout =new CardLayout();
        setLayout(cardLayout);
        setBackground(Color.WHITE);

        this.change_Panel_after_Click_Button();

    }

    public void showPanel(String cardName) {
        cardLayout.show(this, cardName);
    }

    public void change_Panel_after_Click_Button(){
        //Dùng mảng "map" theo cấu trúc: mapPages.put("Tên nút", new Tên lớp);
        // !!!! "mapPages" là tên đã được khởi tạo trước khi dùng !!!!
        // !!!! Các lớp muốn dùng được như bên dưới thì phải EXTENDS với JPanel trước
        // map này sẽ liên kết các nút có tên như bên dưới, link với các class đã được gán
        Map<String, JPanel> mapPages = new LinkedHashMap<>();
        mapPages.put("Quản lý tài khoản", new QLyTaiKhoan_GUI());
        mapPages.put("Quản lý nhà cung cấp", new QLyNhaCungCap_GUI());
        mapPages.put("Thông tin khách hàng", new ThongTinKH_GUI());
        mapPages.put("Quản lý sản phẩm", new QLySanPham_GUI());
        mapPages.put("Nhập kho", new NhapKho_GUI());
        mapPages.put("Phiếu nhập", new PhieuNhap_GUI());
        mapPages.put("Xuất kho", new XuatKho_GUI());
        mapPages.put("Phiếu xuất", new PhieuNhap_GUI());
        mapPages.put("Báo cáo", new BaoCao_GUI());
        mapPages.put("Kệ kho", new KeKho_GUI());
        mapPages.put("Tồn kho",new TonKho_GUI());

        // Dùng vòng lặp để add vào CardLayout
        mapPages.forEach((name, panel) ->{
            add(panel, name);
        });
    }
}

