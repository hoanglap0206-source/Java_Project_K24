package DataBase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection_BC {

    private static final String URL =
            "jdbc:mysql://localhost:3306/quanlykho"
                    + "?useSSL=false&serverTimezone=UTC&useUnicode=true&characterEncoding=UTF-8";

    private static final String USER = "root";
    private static final String PASSWORD = "123456";

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            System.out.println("Lỗi kết nối database!");
            e.printStackTrace();
            return null;
        }
    }
}