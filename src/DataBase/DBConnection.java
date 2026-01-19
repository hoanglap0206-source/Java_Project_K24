package DataBase;

import java.sql.*;

public class DBConnection {
    private static final String URL =
            "jdbc:mysql://localhost:3306/quanlykhongk?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "vanlap6@2";

    public static Connection getConnection() {
        // Hàm tạo kết nối tới MySQL
        // static: gọi trực tiếp không cần tạo object
        // Trả về đối tượng Connection
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn =
                    DriverManager.getConnection(URL, USER, PASSWORD);
            // DriverManager dùng URL + USER + PASSWORD
            // để tạo kết nối tới MySQL
            System.out.println("Kết nối MySQL thành công");
            // In ra thông báo nếu kết nối thành công
            return conn;
            // Trả về kết nối để DAO sử dụng
            // DAO sẽ dùng conn để SELECT / INSERT / UPDATE / DELETE
        } catch (Exception e) {
            System.out.println(" Kết nối MySQL thất bại");
            // Thông báo kết nối thất bại
            e.printStackTrace();
            // In chi tiết lỗi ra console
            // Rất quan trọng để debug
            return null;
            // Trả về null nếu kết nối không thành công
        }
    }
}
