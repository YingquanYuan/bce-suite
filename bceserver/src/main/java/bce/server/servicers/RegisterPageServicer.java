package bce.server.servicers;

import java.util.Date;
import java.util.Map;

import bce.server.dao.UserDAO;
import bce.server.entities.PersistentUser;

/**
 * 业务类，RegisterPageServlet的业务服务者
 *
 * @author robins
 *
 */
public class RegisterPageServicer {

    private UserDAO userDAO;

    public void setUserDAO(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public RegisterPageServicer() {
    }

    /**
     * Ajax异步验证用户名是否已存在
     *
     * @param userName 前台异步发来的用户名
     * @return true：该用户名可用；false：该用户名已被占用
     */
    public boolean asyncValidateUserName(String userName) {
        if (userDAO.get(userName) == null)
            return true;
        else
            return false;
    }

    /**
     * Ajax异步验证邮箱名是否已存在
     *
     * @param email 前台异步发来的邮箱名
     * @return true：该邮箱名可用；false：该邮箱名已被占用
     */
    public boolean asyncValidateEmail(String email) {
        if (userDAO.getByEmail(email) == null)
            return true;
        else
            return false;
    }

    /**
     * 前台表单提交时，用于验证验证码是否正确
     *
     * @param codeInRequest 存储在request域中的验证码
     * @param codeInSession 存储在session域中的验证码
     * @return true：验证码正确；false：验证码错误
     */
    public boolean checkValidateCode(String codeInRequest, String codeInSession) {
        if (codeInRequest.trim().equals(codeInSession.trim()))
            return true;
        else
            return false;
    }

    /**
     * 将新注册用户存入数据库
     *
     * @param userInfoMap 用户数据的哈希表
     * @return 经Hibernate存储并同步后的持久化用户对象
     */
    public PersistentUser save(Map<String, String> userInfoMap) {
        PersistentUser user = new PersistentUser();
        user.setUserName(userInfoMap.get("userName"));
        user.setPassword(userInfoMap.get("password"));
        user.setEmail(userInfoMap.get("email"));
        user.setRegDate(new Date(System.currentTimeMillis()));
        System.out.println(user.toString());
        userDAO.add(user);

        return user;
    }

}