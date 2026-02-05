package DataBase;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection_BC {

    public static Connection getConnection() {
        try {
            String url =
                    "jdbc:mysql://localhost:3306/quanlykho"
                            + "?useSSL=false&serverTimezone=UTC&useUnicode=true&characterEncoding=UTF-8";

            return DriverManager.getConnection(url, "root", "@Chill051106");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
