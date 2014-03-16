package bce.server.daosupport.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.mysql.jdbc.Driver;

/**
 * DB类的MySQL实现
 * 
 * @author Yingquan Yuan
 */
public class DBMySqlImpl extends DB {
	
	public DBMySqlImpl() {
	}

//	/**
//	 * 静态的DBMySqlImpl实例
//	 * 采用单实例模式，系统运行时只有一个DBMySqlImpl实例存在
//	 * 所有数据库操作都调用该实例实现
//	 */
//	private static DBMySqlImpl db;

//	static {
//		if(db == null)
//			db = new DBMySqlImpl();
//	}
//
//	private DBMySqlImpl() {
//		super.dbIp = "localhost";
//		super.dbName = "AESKeyDB";
//		super.dbPort = 3306;
//		super.dbUsername = "root";
//		super.dbUserPwd = "robins";
//	}

//	private void setConfig(DBConfig config) {
//		super.dbIp = config.getDbIp();
//		super.dbName = config.getDbName();
//		super.dbPort = config.getDbPort();
//		super.dbUsername = config.getDbUsername();
//		super.dbUserPwd = config.getDbUserPwd();
//	}

//	/**
//	 * 取得唯一的DBMySqlImpl实例
//	 * @return DBMySqlImpl实例
//	 * @deprecated 仅供调试使用
//	 */
//	public static DBMySqlImpl newInstance() {
//		return db;
//	}

//	/**
//	 * 取得已经实例化的DB
//	 * @return 实例化的DB
//	 */
//	public static DBMySqlImpl getExistInstance() {
//		try {
//			db.getConnection().close();
//			return db;
//		} catch (Exception e) {
//			return null;
//		}
//	}

//	/**
//	 * 取得唯一的DBMySqlImpl实例
//	 * @param config 实际运行时的数据库配置
//	 * @return DBMySqlImpl实例
//	 */
//	public static DBMySqlImpl newInstance(DBConfig config) {
//		db.setConfig(config);
//		return db;
//	}

	/*
	 * (non-Javadoc)
	 * @see bce.server.daosupport.jdbc.DB#getConnection()
	 */
	@Override
	public Connection getConnection() throws SQLException {
		DriverManager.registerDriver(new Driver());
		return DriverManager.getConnection("jdbc:mysql://" + super.dbIp + ":" + super.dbPort + "/" + super.dbName + "?useUnicode=true&characterEncoding=UTF-8", super.dbUsername, super.dbUserPwd);
	}
}
