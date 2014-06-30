package bce.server.util;

import bce.java.entities.BCEPrivateKey;
import bce.java.entities.BCESystem;
import bce.server.entities.PersistentBCESystem;
import bce.server.entities.PersistentPrivateKey;
import bce.server.exception.NullAttributeException;
import bce.server.exception.NullObjectException;

/**
 * 工具类，对象转换器，提供了一些BCE持久化对象与业务对象之间互相转换的工具方法
 *
 * @author <a href="mailto:yingq.yuan@gmail.com">Yingquan Yuan</a>
 *
 */
public final class BCEObjectConverter {

    /**
     * 构造函数
     */
    private BCEObjectConverter() {
    }

    /**
     * <pre>
     * 将BCE私钥业务对象转换为BCE私钥持久化对象，该方法仅填充BCE持久化对象中涉及业务对象的私钥数据部分，
     * 转换出的BCE持久化对象是不完整的，该对象的其他字段需要另做填充才能构建完整的BCE持久化对象
     * 注意：该方法不负责销毁BCE私钥业务对象的数据字段，如果不再使用该BCE私钥业务对象，需在方法调用返回后手动调用abort()方法
     * </pre>
     * @param privateKey BCE私钥业务对象
     * @return BCE私钥持久化对象
     */
    public final static PersistentPrivateKey transform(BCEPrivateKey privateKey) {

        ensureNotNull(privateKey);
        PersistentPrivateKey persistentPrivateKey = new PersistentPrivateKey();
        persistentPrivateKey.setPrivateKeyField(privateKey.toBytes());
        persistentPrivateKey.setIsLegal(privateKey.getIsLegal());
        return persistentPrivateKey;
    }

    /**
     * <pre>
     * 将BCE私钥持久化对象转换为BCE私钥业务对象，由于BCE持久化对象包含的数据内容要多于BCE私钥业务对象
     * 所以该方法转换出的是一个完整的BCE私钥业务对象
     * 注意：该方法不负责销毁BCE私钥持久化对象的数据字段，如果不再使用该BCE私钥持久化对象，需在方法调用返回后主动销毁该对象
     * </pre>
     * @param persistentPrivateKey BCE私钥持久化对象
     * @return BCE私钥业务对象
     */
    public final static BCEPrivateKey transform(PersistentPrivateKey persistentPrivateKey) {

        ensureNotNull(persistentPrivateKey);
        BCEPrivateKey privateKey = BCEPrivateKey.fromBytes(persistentPrivateKey.getPrivateKeyField());
        privateKey.setIsLegal(persistentPrivateKey.getIsLegal());
        return privateKey;
    }

    /**
     * <pre>
     * 将BCE系统业务对象转换为BCE系统持久化对象，由于BCE系统业务对象的数据信息多于BCE系统持久化对象
     * 所以该方法转换出的是一个完整的BCE系统持久化对象
     * 注意：该方法不负责销毁BCE系统业务对象的数据字段
     * </pre>
     * @param system BCE系统业务对象
     * @return BCE系统持久化对象
     */
    public final static PersistentBCESystem transform(BCESystem system) {

        ensureNotNull(system);
        PersistentBCESystem persistentBCESystem = new PersistentBCESystem();
        persistentBCESystem.setCurveParamsURI(system.getCurveParamsURI());
        persistentBCESystem.setServerSysParamsURI(system.getServerSysParamsURI());
        persistentBCESystem.setGlobalSysParamsURI(system.getGlobalSysParamsURI());
        persistentBCESystem.setUserNumber(system.getUserNumber());
        persistentBCESystem.setKeyFetchSize(system.getKeyFetchSize());
        persistentBCESystem.setChangeDecrProdBatchSize(system.getChangeDecrProdBatchSize());
        return persistentBCESystem;
    }

    /**
     * <pre>
     * 将BCE系统持久化对象转换为BCE系统业务对象，该方法仅填充BCE系统业务对象中受持久化维护的字段
     * BCE系统业务对象涉及JNI交互策略的字段需要在使用时进行另外的设置
     * 注意：该方法不负责销毁BCE系统持久化对象的数据字段
     * </pre>
     * @param persistentBCESystem BCE系统持久化对象
     * @return BCE系统业务对象
     */
    public final static BCESystem transform(PersistentBCESystem persistentBCESystem) {

        ensureNotNull(persistentBCESystem);
        BCESystem system = new BCESystem();
        system.setCurveParamsURI(persistentBCESystem.getCurveParamsURI());
        system.setServerSysParamsURI(persistentBCESystem.getServerSysParamsURI());
        system.setGlobalSysParamsURI(persistentBCESystem.getGlobalSysParamsURI());
        system.setUserNumber(persistentBCESystem.getUserNumber());
        system.setKeyFetchSize(persistentBCESystem.getKeyFetchSize());
        system.setChangeDecrProdBatchSize(persistentBCESystem.getChangeDecrProdBatchSize());
        return system;
    }

    /**
     * 确保BCE私钥业务对象及其各字段不为空
     * @param privateKey BCE私钥业务对象
     */
    private final static void ensureNotNull(BCEPrivateKey privateKey) {

        if (privateKey == null)
            throw new NullObjectException("BCEPrivateKey: obj is null!");
        if (privateKey.getIndex() == null)
            throw new NullAttributeException("BCEPrivateKey: attribute index is null!");
        if (privateKey.getG_i_gamma() == null)
            throw new NullAttributeException("BCEPrivateKey: attribute g_i_gamma is null!");
        if (privateKey.getG_i() == null)
            throw new NullAttributeException("BCEPrivateKey: attribute g_i is null!");
        if (privateKey.getH_i() == null)
            throw new NullAttributeException("BCEPrivateKey: attribute h_i is null!");
        if (privateKey.getDecr_prod() == null)
            throw new NullAttributeException("BCEPrivateKey: attribute decr_prod is null!");
    }

    /**
     * 确保BCE持久化对象及其私钥数据字段不为空
     * @param persistentPrivateKey BCE持久化对象
     */
    private final static void ensureNotNull(PersistentPrivateKey persistentPrivateKey) {

        if (persistentPrivateKey == null)
            throw new NullObjectException("PersistentPrivateKey: obj is null!");
        if (persistentPrivateKey.getPrivateKeyField() == null)
            throw new NullAttributeException("PersistentPrivateKey: attribute privateKeyField is null!");
    }

    /**
     * 确保BCE系统业务对象及其各必需字段不为空且取值正确
     * @param system BCE系统业务对象
     */
    private final static void ensureNotNull(BCESystem system) {

        if (system == null)
            throw new NullObjectException("BCESystem: obj is null!");
        if (system.getCurveParamsURI() == null)
            throw new NullAttributeException("BCESystem: attribute curveParamsURI is null!");
        if (system.getServerSysParamsURI() == null)
            throw new NullAttributeException("BCESystem: attribute serverSysParamsURI is null!");
        if (system.getGlobalSysParamsURI() == null)
            throw new NullAttributeException("BCESystem: attribute globalSysParamsURI is null!");
        if (system.getUserNumber() < 0 || system.getUserNumber() % 8 != 0)
            throw new NullAttributeException("BCESystem: attribute userNumber is invalid size");
        if (system.getKeyFetchSize() < 0 || system.getKeyFetchSize() > system.getUserNumber())
            throw new NullAttributeException("BCESystem: attribute keyFetchSize is out of range!");
        if (system.getChangeDecrProdBatchSize() < 0 || system.getChangeDecrProdBatchSize() > system.getUserNumber())
            throw new NullAttributeException("BCESystem: attribute changeDecrProdBatchSize is out of range!");
    }

    /**
     * 确保BCE持久化对象及其各字段不为空且取值正确
     * @param persistentBCESystem BCE持久化对象
     */
    private final static void ensureNotNull(PersistentBCESystem persistentBCESystem) {

        if (persistentBCESystem == null)
            throw new NullObjectException("PersistentBCESystem: obj is null!");
        if (persistentBCESystem.getCurveParamsURI() == null)
            throw new NullAttributeException("PersistentBCESystem: attribute curveParamsURI is null!");
        if (persistentBCESystem.getServerSysParamsURI() == null)
            throw new NullAttributeException("PersistentBCESystem: attribute serverSysParamsURI is null!");
        if (persistentBCESystem.getGlobalSysParamsURI() == null)
            throw new NullAttributeException("PersistentBCESystem: attribute globalSysParamsURI is null!");
        if (persistentBCESystem.getUserNumber() < 0 || persistentBCESystem.getUserNumber() % 8 != 0)
            throw new NullAttributeException("PersistentBCESystem: attribute userNumber is invalid size");
        if (persistentBCESystem.getKeyFetchSize() < 0 || persistentBCESystem.getKeyFetchSize() > persistentBCESystem.getUserNumber())
            throw new NullAttributeException("PersistentBCESystem: attribute keyFetchSize is out of range!");
        if (persistentBCESystem.getChangeDecrProdBatchSize() < 0 || persistentBCESystem.getChangeDecrProdBatchSize() > persistentBCESystem.getUserNumber())
            throw new NullAttributeException("PersistentBCESystem: attribute changeDecrProdBatchSize is out of range!");
    }
}
