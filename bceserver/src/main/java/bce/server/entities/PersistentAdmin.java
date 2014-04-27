package bce.server.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 管理员持久化类
 *
 * @author robins
 *
 */
@Entity
@Table(name = "BCE_SERVER_ADMIN")
public class PersistentAdmin implements Serializable {

    private static final long serialVersionUID = -4832905461099907556L;

    /**
     * 数据库自动分配的管理员ID，用作标识
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ADMIN_ID")
    private Integer adminId;

    /**
     * 管理员用户名
     */
    @Column(nullable = false, name = "ADMIN_NAME")
    private String adminName;

    /**
     * 管理员输入的，他认为的密码的摘要值
     * 用SHA-1摘要，再用16进制表示为String形式，长度为40字节
     * 服务器端只需维护该摘要值，“密码”由管理员维护
     */
    @Column(nullable = false, name = "ADMIN_PWD", length = 40)
    private String password;

    public PersistentAdmin() {}

    public Integer getAdminId() {
        return adminId;
    }

    public void setAdminId(Integer adminId) {
        this.adminId = adminId;
    }

    public String getAdminName() {
        return adminName;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((adminId == null) ? 0 : adminId.hashCode());
        result = prime * result
                + ((adminName == null) ? 0 : adminName.hashCode());
        result = prime * result
                + ((password == null) ? 0 : password.hashCode());
        return result;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PersistentAdmin other = (PersistentAdmin) obj;
        if (adminId == null) {
            if (other.adminId != null)
                return false;
        } else if (!adminId.equals(other.adminId))
            return false;
        if (adminName == null) {
            if (other.adminName != null)
                return false;
        } else if (!adminName.equals(other.adminName))
            return false;
        if (password == null) {
            if (other.password != null)
                return false;
        } else if (!password.equals(other.password))
            return false;
        return true;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "PersistentAdmin [adminId=" + adminId + ", adminName="
                + adminName + ", password=" + password + "]";
    }

}
