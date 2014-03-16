package bce.server.servicers;

import bce.java.utils.Hash;
import bce.jni.utils.BCEUtils;
import bce.server.dao.UserDAO;
import bce.server.entities.PersistentUser;
import bce.server.servlets.LoginServlet;

/**
 * 业务类，LoginServlet的业务服务者
 * 
 * @author robins
 *
 */
public class LoginPageServicer {

	private UserDAO userDAO;
	
	public LoginPageServicer() {
	}

	public void setUserDAO(UserDAO userDAO) {
		this.userDAO = userDAO;
	}

	/**
	 * 该方法用于检查登录
	 * 
	 * @param userName 登录用户名
	 * @param password 登录用户密码
	 * @param returnedUser 服务器端构建的登录用户对象
	 * @return 0：USER_NAME_NOT_EXISTS; 1：PASSWORD_NOT_MATCH; 2：LOGIN_CHECK_PASSED；
	 */
	public int checkLogin(String userName, String password, PersistentUser returnedUser) {
		PersistentUser genericUser = userDAO.get(userName);
		if (genericUser == null)
			return LoginServlet.USER_NAME_NOT_EXISTS;
		if (BCEUtils.hex(Hash.sha1(password)).equals(genericUser.getPassword())) {
			this.buildUser(returnedUser, genericUser);
			return LoginServlet.LOGIN_CHECK_PASSED;
		}
		return LoginServlet.PASSWORD_NOT_MATCH;
	}
	
	/**
	 * 为新登录用户构建一个持久化对象
	 * 
	 * @param returnedUser 返回用户对象
	 * @param genericUser 从数据库中检索出的用户对象
	 */
	private void buildUser(PersistentUser returnedUser, PersistentUser genericUser) {
		returnedUser.setEmail(genericUser.getEmail());
		returnedUser.setPassword(genericUser.getPassword());
		returnedUser.setUserId(genericUser.getUserId());
		returnedUser.setUserName(genericUser.getUserName());
		returnedUser.setRegDate(genericUser.getRegDate());
	}
}
