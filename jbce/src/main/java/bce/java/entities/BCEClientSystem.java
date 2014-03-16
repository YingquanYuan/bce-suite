package bce.java.entities;

import java.io.Serializable;

/**
 * 此类用于封装客户端BCE系统的各类参数，通过此类可以完成了一些与底层JNI交互的策略设置
 * 
 * @author <a href="mailto:yingq.yuan@gmail.com">Yingquan Yuan</a>
 *
 */
public class BCEClientSystem implements Serializable {

	private static final long serialVersionUID = 6814363082639563221L;
	
	/**
	 * 客户端全局参数文件URI
	 */
	private String globalSysParamsURI;
	
	public BCEClientSystem() {}

	public String getGlobalSysParamsURI() {
		return globalSysParamsURI;
	}

	public void setGlobalSysParamsURI(String globalSysParamsURI) {
		this.globalSysParamsURI = globalSysParamsURI;
	}

}
