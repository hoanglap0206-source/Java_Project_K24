package BUS;

import DAO.UserDAO;

public class UserBUS {

    public boolean login(String username, String password) {
        if (username.isEmpty() || password.isEmpty()) return false;
        return new UserDAO().checkLogin(username, password);
    }
}
