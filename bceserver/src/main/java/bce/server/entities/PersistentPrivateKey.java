package bce.server.entities;

import java.io.Serializable;
import java.util.Arrays;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

/**
 * 该持久化类用于存储受保护的私钥数据，该类的持久化对象由非持久化的私钥业务对象构建，其存储的私钥数据是经过AES256加密的
 * @author robins
 *
 */
@Entity
@Table(name = "BCE_PRIVATE_KEY")
public class PersistentPrivateKey implements Serializable {

	private static final long serialVersionUID = -2996386472418967944L;
	
	/**
	 * 私钥ID
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "PRIVATE_KEY_ID")
	private Integer privateKeyId;
	
	/**
	 * 外键，当前私钥实例所属的用户
	 */
	@ManyToOne(optional = true)
	@JoinColumn(name = "BELONGED_USER_ID")
	private PersistentUser belongedUser;
	
	/**
	 * 外键，当前私钥实例所属的BCE系统
	 */
	@ManyToOne(optional = false)
	@JoinColumn(name = "BELONGED_SYSTEM_ID")
	private PersistentBCESystem belongedBCESystem;
	
	/**
	 * <pre>
	 * 私钥字段块，由私钥非持久化类中私钥各字段拼接成，由于使用了序列化处理，该字段还包含一部分加密后的对象信息
	 * 拼接顺序：
	 * index, g_i_gamma, g_i, h_i, decr_prod(变化字段)
	 * 长度：316 bytes
	 * </pre>
	 */
	@Lob
	@Basic(fetch = FetchType.LAZY, optional = false)
	@Column(name = "BCE_PRIV_KEY_FIELD")
	private byte[] privateKeyField;
	
	/**
	 * 该字段用于标识当前私钥在BCE系统中是否合法
	 */
	@Column(name = "IS_LEGAL", nullable = false)
	private Integer isLegal;
	
	/**
	 * 应用于私钥实体的Hibernate乐观锁
	 */
	@Version
	@Column(name="OPTLOCK")
	private Integer version;
	
	public PersistentPrivateKey() {
		this.isLegal = 1;
	}

	public Integer getPrivateKeyId() {
		return privateKeyId;
	}

	public void setPrivateKeyId(Integer privateKeyId) {
		this.privateKeyId = privateKeyId;
	}

	public PersistentUser getBelongedUser() {
		return belongedUser;
	}

	public void setBelongedUser(PersistentUser belongedUser) {
		this.belongedUser = belongedUser;
	}

	public PersistentBCESystem getBelongedBCESystem() {
		return belongedBCESystem;
	}

	public void setBelongedBCESystem(PersistentBCESystem belongedBCESystem) {
		this.belongedBCESystem = belongedBCESystem;
	}

	public byte[] getPrivateKeyField() {
		return privateKeyField;
	}

	public void setPrivateKeyField(byte[] privateKeyField) {
		this.privateKeyField = privateKeyField;
	}

	public Integer getIsLegal() {
		return isLegal;
	}

	public void setIsLegal(Integer isLegal) {
		this.isLegal = isLegal;
	}
	
	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((belongedBCESystem == null) ? 0 : belongedBCESystem
						.hashCode());
		result = prime * result
				+ ((belongedUser == null) ? 0 : belongedUser.hashCode());
		result = prime * result + ((isLegal == null) ? 0 : isLegal.hashCode());
		result = prime * result + Arrays.hashCode(privateKeyField);
		result = prime * result
				+ ((privateKeyId == null) ? 0 : privateKeyId.hashCode());
		result = prime * result + ((version == null) ? 0 : version.hashCode());
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
		PersistentPrivateKey other = (PersistentPrivateKey) obj;
		if (belongedBCESystem == null) {
			if (other.belongedBCESystem != null)
				return false;
		} else if (!belongedBCESystem.equals(other.belongedBCESystem))
			return false;
		if (belongedUser == null) {
			if (other.belongedUser != null)
				return false;
		} else if (!belongedUser.equals(other.belongedUser))
			return false;
		if (isLegal == null) {
			if (other.isLegal != null)
				return false;
		} else if (!isLegal.equals(other.isLegal))
			return false;
		if (!Arrays.equals(privateKeyField, other.privateKeyField))
			return false;
		if (privateKeyId == null) {
			if (other.privateKeyId != null)
				return false;
		} else if (!privateKeyId.equals(other.privateKeyId))
			return false;
		if (version == null) {
			if (other.version != null)
				return false;
		} else if (!version.equals(other.version))
			return false;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "PersistentPrivateKey [privateKeyId=" + privateKeyId
				+ ", belongedUser=" + belongedUser + ", belongedBCESystem="
				+ belongedBCESystem + ", privateKeyField="
				+ Arrays.toString(privateKeyField) + ", isLegal=" + isLegal
				+ ", version=" + version + "]";
	}

	/**
	 * 该方法用于销毁BCE私钥持久化对象中的私钥数据信息
	 */
	public void abort() {
		 
		if (this.privateKeyField != null)
			Arrays.fill(this.privateKeyField, (byte) 0);
	}

}
