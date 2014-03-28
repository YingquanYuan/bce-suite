package bce.server.servicers;

import bce.server.dao.AdminDAO;
import bce.server.dao.BCESystemDAO;
import bce.server.dao.PrivateKeyDAO;
import bce.server.dao.UserDAO;

public class AdminPageServicer {

    BCESystemDAO systemDAO;

    PrivateKeyDAO privateKeyDAO;

    UserDAO userDAO;

    AdminDAO adminDAO;

    public void setSystemDAO(BCESystemDAO systemDAO) {
        this.systemDAO = systemDAO;
    }

    public void setPrivateKeyDAO(PrivateKeyDAO privateKeyDAO) {
        this.privateKeyDAO = privateKeyDAO;
    }

    public void setUserDAO(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public void setAdminDAO(AdminDAO adminDAO) {
        this.adminDAO = adminDAO;
    }

    public AdminPageServicer() {
    }

}
