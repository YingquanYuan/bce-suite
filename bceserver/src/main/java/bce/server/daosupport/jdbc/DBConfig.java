package bce.server.daosupport.jdbc;

public class DBConfig {

    /**
     * 数据库地址
     */
    private String dbIp;

    /**
     * 数据库名
     */
    private String dbName;

    /**
     * 数据库用户名
     */
    private String dbUsername;

    /**
     * 数据库用户密码
     */
    private String dbUserPwd;

    /**
     * 数据库端口
     */
    private int dbPort;

    /**
     * 构造函数
     * @param dbIp 数据库地址
     * @param dbName 数据库名
     * @param dbUsername 数据库用户名
     * @param dbUserPwd 数据库用户密码
     * @param dbPort 数据库端口
     */
    public DBConfig(String dbIp, String dbName, String dbUsername, String dbUserPwd, int dbPort) {
        this.dbIp = dbIp;
        this.dbName = dbName;
        this.dbUsername = dbUsername;
        this.dbUserPwd = dbUserPwd;
        this.dbPort = dbPort;
    }

    /**
     * @return 数据库地址
     */
    public String getDbIp() {
        return dbIp;
    }

    /**
     * @return 数据库名
     */
    public String getDbName() {
        return dbName;
    }

    /**
     * @return 数据库用户名
     */
    public String getDbUsername() {
        return dbUsername;
    }

    /**
     * @return 数据库用户密码
     */
    public String getDbUserPwd() {
        return dbUserPwd;
    }

    /**
     * @return 数据库端口
     */
    public int getDbPort() {
        return dbPort;
    }

    @Override
    public String toString() {
        return "DBConfig [dbIp=" + dbIp + ", dbName=" + dbName
                + ", dbUsername=" + dbUsername + ", dbUserPwd=" + dbUserPwd
                + ", dbPort=" + dbPort + "]";
    }
}
