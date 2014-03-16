package bce.server.daosupport.jdbc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * JDBC数据库操作抽象类，继承这个类并实现getConnection方法可以用于不同的数据库
 * 比如MySQL，SQL Server或Oracle，调用数据库操作时使用本类的引用
 * 而不是其子类的引用，可以最大程度上实现不同数据库间的通用性
 * 
 * @author Yingquan Yuan
 */
public abstract class DB {

	/**
	 * 数据库IP地址
	 */
	protected String dbIp;

	/**
	 * 数据库名
	 */
	protected String dbName;

	/**
	 * 数据库用户名
	 */
	protected String dbUsername;

	/**
	 * 数据库用户密码
	 */
	protected String dbUserPwd;

	/**
	 * 数据库端口
	 */
	protected int dbPort;

	/**
	 * 取得数据库连接<br>
	 * 根据不同的数据库，有不同的方法取得连接<br>
	 * 子类必须实现这个方法以取得数据库连接
	 * @return 数据库连接的Connection对象
	 * @throws SQLException 连接失败时（比如密码认证失败）会抛出该异常
	 */
	public abstract Connection getConnection() throws SQLException;

	/**
	 * 关闭连接
	 * @param conn 要关闭的连接
	 * @throws SQLException 抛出SQL异常，比如连接意外终止
	 */
	public void close(Connection conn) throws SQLException {
		if(conn != null) {
			conn.close();
			conn = null;
		}
	}

	/**
	 * 关闭结果集
	 * @param rs 要关闭的结果集
	 * @throws SQLException 抛出SQL异常，比如连接意外终止
	 */
	public void close(ResultSet rs) throws SQLException {
		if(rs != null) {
			rs.close();
			rs = null;
		}
	}

	/**
	 * 关闭Statement
	 * @param stmt 要关闭的Statement
	 * @throws SQLException 抛出SQL异常，比如连接意外终止
	 */
	public void close(Statement stmt) throws SQLException {
		if(stmt != null) {
			stmt.close();
			stmt = null;
		}
	}
	
	public void setDbIp(String dbIp) {
		this.dbIp = dbIp;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public void setDbUsername(String dbUsername) {
		this.dbUsername = dbUsername;
	}

	public void setDbUserPwd(String dbUserPwd) {
		this.dbUserPwd = dbUserPwd;
	}

	public void setDbPort(int dbPort) {
		this.dbPort = dbPort;
	}

}