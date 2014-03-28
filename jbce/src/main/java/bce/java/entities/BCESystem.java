package bce.java.entities;

import java.io.Serializable;

/**
 * 此类用于封装服务器端特定BCE系统的各类参数，通过此类可以完成了一些与底层JNI交互的策略设置
 *
 * @author <a href="mailto:yingq.yuan@gmail.com">Yingquan Yuan</a>
 *
 */
public class BCESystem implements Serializable {

    private static final long serialVersionUID = 257103367362208821L;

    /**
     * 服务器端可能运行多个BCE系统，每个BCE系统对应1个系统参数文件和1个全局参数文件
     * 该字段作为主键，表示服务器端的不同BCE系统ID
     */
//	private int bceSystemId;

    /**
     * 椭圆参数文件URI
     */
    private String curveParamsURI;

    /**
     * 服务器端系统参数文件URI
     */
    private String serverSysParamsURI;

    /**
     * 服务器端全局参数文件URI
     */
    private String globalSysParamsURI;

    /**
     * 当前BCE系统能容纳的总人数，包含失效用户
     */
    private int userNumber;

    /**
     * <pre>
     * 在服务器执行genPrivKeys()方法时，用于设置BCE私钥批量回送策略
     * 该字段用于设置当前批次私钥中第一个私钥对应的用户索引
     * </pre>
     */
    private int keyFetchStartIndex;

    /**
     * <pre>
     * 在服务器执行genPrivKeys()方法时，用于设置BCE私钥批量回送策略
     * 该字段用于设置当前批次私钥的批量大小
     * </pre>
     */
    private int keyFetchSize;

    /**
     * 新增用户队列，用于设这BCE-->change方法中的adds参数，使用该功能时可向系统对象传入一个开辟好的int数组
     */
    private int[] adds;

    /**
     * 新增用户数目，用于设这BCE-->change方法中的nAdds参数，其值应该等于adds.length，这里作为参数传入是为了底层JNI的内存分配校验
     */
    private int nAdds;

    /**
     * 失效用户队列，用于设这BCE-->change方法中的rems参数，使用该功能时可向系统对象传入一个开辟好的int数组
     */
    private int[] rems;

    /**
     * 失效用户数目，用于设这BCE-->change方法中的nRems参数，其值应该等于rems.length，这里作为参数传入是为了底层JNI的内存分配校验
     */
    private int nRems;

    /**
     * <pre>
     * 在服务器执行changeDecryptionProduct()方法时，用于设置BCE解密产品批量回送策略
     * 该字段用于设置当前批次返回的第一个解密产品对应的用户索引
     * </pre>
     */
    private int changeDecrProdStartIndex;

    /**
     * <pre>
     * 在服务器执行changeDecryptionProduct()方法时，用于设置BCE解密产品批量回送策略
     * 该字段用于设置当前批次返回的解密产品的批量大小
     * </pre>
     */
    private int changeDecrProdBatchSize;

    public BCESystem() {}

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

    public int getUserNumber() {
        return userNumber;
    }

    public void setUserNumber(int userNumber) {
        this.userNumber = userNumber;
    }

    public int getKeyFetchStartIndex() {
        return keyFetchStartIndex;
    }

    public void setKeyFetchStartIndex(int keyFetchStartIndex) {
        this.keyFetchStartIndex = keyFetchStartIndex;
    }

    public int getKeyFetchSize() {
        return keyFetchSize;
    }

    public void setKeyFetchSize(int keyFetchSize) {
        this.keyFetchSize = keyFetchSize;
    }

    public int[] getAdds() {
        return adds;
    }

    public void setAdds(int[] adds) {
        this.adds = adds;
    }

    public int getnAdds() {
        return nAdds;
    }

    public void setnAdds(int nAdds) {
        this.nAdds = nAdds;
    }

    public int[] getRems() {
        return rems;
    }

    public void setRems(int[] rems) {
        this.rems = rems;
    }

    public int getnRems() {
        return nRems;
    }

    public void setnRems(int nRems) {
        this.nRems = nRems;
    }

    public int getChangeDecrProdStartIndex() {
        return changeDecrProdStartIndex;
    }

    public void setChangeDecrProdStartIndex(int changeDecrProdStartIndex) {
        this.changeDecrProdStartIndex = changeDecrProdStartIndex;
    }

    public int getChangeDecrProdBatchSize() {
        return changeDecrProdBatchSize;
    }

    public void setChangeDecrProdBatchSize(int changeDecrProdBatchSize) {
        this.changeDecrProdBatchSize = changeDecrProdBatchSize;
    }

}
