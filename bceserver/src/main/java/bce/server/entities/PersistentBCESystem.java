package bce.server.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 用于标识服务器端BCE系统的持久化类
 * 
 * @author robins
 *
 */
@Entity
@Table(name = "BCE_SYSTEM")
public class PersistentBCESystem implements Serializable {

	private static final long serialVersionUID = 8900465741261561781L;
	
	/**
	 * 服务器端可能运行多个BCE系统，每个BCE系统对应1个系统参数文件和1个全局参数文件
	 * 该字段作为主键，表示服务器端的不同BCE系统ID
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "BCE_SYSTEM_ID")
	private Integer bceSystemId;
	
	/**
	 * BCE系统椭圆参数文件URI
	 */
	@Column(nullable = false, name = "CURVE_FILE_PARAMS_URI")
	private String curveParamsURI;
	
	/**
	 * 服务器端系统参数文件URI
	 */
	@Column(nullable = false, name = "SERVER_SYS_PARAMS_URI")
	private String serverSysParamsURI;
	
	/**
	 * 服务器端全局参数文件URI
	 */
	@Column(nullable = false, name = "GLOBAL_SYS_PARAMS_URI")
	private String globalSysParamsURI;
	
	/**
	 * 当前BCE系统能容纳的总人数，包含失效用户
	 */
	@Column(nullable = false, name = "BCE_USER_NUMBER")
	private Integer userNumber;
	
	/**
	 * <pre>
	 * 在服务器执行genPrivKeys()方法时，用于设置BCE私钥批量回送策略
	 * 该字段用于设置当前批次私钥的批量大小
	 * </pre>
	 */
	@Column(nullable = false, name = "BCE_KEY_FETCH_SIZE")
	private Integer keyFetchSize;
	
	/**
	 * <pre>
	 * 在服务器执行changeDecryptionProduct()方法时，用于设置BCE解密产品批量回送策略
	 * 该字段用于设置当前批次返回的解密产品的批量大小
	 * </pre>
	 */
	@Column(nullable = false, name = "BCE_CHANGE_DECR_PROD_BATCH_SIZE")
	private Integer changeDecrProdBatchSize;
	
	public PersistentBCESystem() {}

	public Integer getBceSystemId() {
		return bceSystemId;
	}

	public void setBceSystemId(Integer bceSystemId) {
		this.bceSystemId = bceSystemId;
	}

	public String getCurveParamsURI() {
		return curveParamsURI;
	}

	public void setCurveParamsURI(String curveParamsURI) {
		this.curveParamsURI = curveParamsURI;
	}

	public String getServerSysParamsURI() {
		return serverSysParamsURI;
	}

	public void setServerSysParamsURI(String serverSysParamsURI) {
		this.serverSysParamsURI = serverSysParamsURI;
	}

	public String getGlobalSysParamsURI() {
		return globalSysParamsURI;
	}

	public void setGlobalSysParamsURI(String globalSysParamsURI) {
		this.globalSysParamsURI = globalSysParamsURI;
	}

	public Integer getUserNumber() {
		return userNumber;
	}

	public void setUserNumber(Integer userNumber) {
		this.userNumber = userNumber;
	}

	public Integer getKeyFetchSize() {
		return keyFetchSize;
	}

	public void setKeyFetchSize(Integer keyFetchSize) {
		this.keyFetchSize = keyFetchSize;
	}

	public Integer getChangeDecrProdBatchSize() {
		return changeDecrProdBatchSize;
	}

	public void setChangeDecrProdBatchSize(Integer changeDecrProdBatchSize) {
		this.changeDecrProdBatchSize = changeDecrProdBatchSize;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((bceSystemId == null) ? 0 : bceSystemId.hashCode());
		result = prime * result
				+ ((changeDecrProdBatchSize == null) ? 0 : changeDecrProdBatchSize.hashCode());
		result = prime * result
				+ ((curveParamsURI == null) ? 0 : curveParamsURI.hashCode());
		result = prime * result
				+ ((globalSysParamsURI == null) ? 0 : globalSysParamsURI.hashCode());
		result = prime * result
				+ ((keyFetchSize == null) ? 0 : keyFetchSize.hashCode());
		result = prime * result
				+ ((serverSysParamsURI == null) ? 0 : serverSysParamsURI.hashCode());
		result = prime * result
				+ ((userNumber == null) ? 0 : userNumber.hashCode());
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
		PersistentBCESystem other = (PersistentBCESystem) obj;
		if (bceSystemId == null) {
			if (other.bceSystemId != null)
				return false;
		} else if (!bceSystemId.equals(other.bceSystemId))
			return false;
		if (changeDecrProdBatchSize == null) {
			if (other.changeDecrProdBatchSize != null)
				return false;
		} else if (!changeDecrProdBatchSize
				.equals(other.changeDecrProdBatchSize))
			return false;
		if (curveParamsURI == null) {
			if (other.curveParamsURI != null)
				return false;
		} else if (!curveParamsURI.equals(other.curveParamsURI))
			return false;
		if (globalSysParamsURI == null) {
			if (other.globalSysParamsURI != null)
				return false;
		} else if (!globalSysParamsURI.equals(other.globalSysParamsURI))
			return false;
		if (keyFetchSize == null) {
			if (other.keyFetchSize != null)
				return false;
		} else if (!keyFetchSize.equals(other.keyFetchSize))
			return false;
		if (serverSysParamsURI == null) {
			if (other.serverSysParamsURI != null)
				return false;
		} else if (!serverSysParamsURI.equals(other.serverSysParamsURI))
			return false;
		if (userNumber == null) {
			if (other.userNumber != null)
				return false;
		} else if (!userNumber.equals(other.userNumber))
			return false;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "PersistentBCESystem [bceSystemId=" + bceSystemId
				+ ", curveParamsURI=" + curveParamsURI
				+ ", serverSysParamsURI=" + serverSysParamsURI
				+ ", globalSysParamsURI=" + globalSysParamsURI
				+ ", userNumber=" + userNumber + ", keyFetchSize="
				+ keyFetchSize + ", changeDecrProdBatchSize="
				+ changeDecrProdBatchSize + "]";
	}
	
}
