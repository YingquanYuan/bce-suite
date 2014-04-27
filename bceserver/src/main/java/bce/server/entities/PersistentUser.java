package bce.server.entities;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * BCE用户的持久化类
 *
 * @author robins
 *
 */
@Entity
@Table(name = "BCE_USER")
public class PersistentUser implements Serializable {

    private static final long serialVersionUID = 9220004511179825925L;

    /**
     * 用于Servlet中request对象中标识登录用户的key
     */
    public final static String ATTRIBUTE_KEY = "loginedUser";

    /**
     * 数据库自动分配的用户ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "USER_ID")
    private Integer userId;

    /**
     * 用户注册时使用的用户名，用户名不得重复
     */
    @Column(nullable = false, name = "USER_NAME")
    private String userName;

    /**
     * 用户输入的，他认为的密码的摘要值
     * 用SHA-1摘要，再用16进制表示为String形式，长度为40字节
     * 服务器端只需维护该摘要值，“密码”由用户维护
     */
    @Column(nullable = false, name = "PASSWORD", length = 40)
    private String password;

    /**
     * 用户email地址
     */
    @Column(nullable = false, name = "EMAIL")
    private String email;

    /**
     * 用户注册时间
     */
    @Column(nullable = false, name = "REG_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date regDate;

    public PersistentUser() {}

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getRegDate() {
        return regDate;
    }

    public void setRegDate(Date regDate) {
        this.regDate = regDate;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((email == null) ? 0 : email.hashCode());
        result = prime * result
                + ((password == null) ? 0 : password.hashCode());
        result = prime * result + ((regDate == null) ? 0 : regDate.hashCode());
        result = prime * result + ((userId == null) ? 0 : userId.hashCode());
        result = prime * result
                + ((userName == null) ? 0 : userName.hashCode());
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
        PersistentUser other = (PersistentUser) obj;
        if (email == null) {
            if (other.email != null)
                return false;
        } else if (!email.equals(other.email))
            return false;
        if (password == null) {
            if (other.password != null)
                return false;
        } else if (!password.equals(other.password))
            return false;
        if (regDate == null) {
            if (other.regDate != null)
                return false;
        } else if (!regDate.equals(other.regDate))
            return false;
        if (userId == null) {
            if (other.userId != null)
                return false;
        } else if (!userId.equals(other.userId))
            return false;
        if (userName == null) {
            if (other.userName != null)
                return false;
        } else if (!userName.equals(other.userName))
            return false;
        return true;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "PersistentUser [userId=" + userId + ", userName=" + userName
                + ", password=" + password + ", email=" + email + ", regDate="
                + regDate + "]";
    }

}
