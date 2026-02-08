package DataBase;

import java.sql.Connection;
import DataBase.DBConnection;
public class TestConnection {
    public static void main(String[] args) {
        Connection conn = DBConnection.getConnection();
    }


}
