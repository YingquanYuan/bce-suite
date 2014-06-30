package bce.server.dao;

import java.util.List;
import java.util.Set;

import bce.server.entities.PersistentPrivateKey;
import bce.server.entities.PersistentUser;

/**
 * BCE私钥实体类DAO的接口声明
 *
 * @author robins
 *
 */
public interface PrivateKeyDAO {

    /**
     * 根据该私钥关联的用户ID检索出该用户的所有私钥，初步实现中可认为用户与私钥是一对一关系，这里返回集合类型在设计上保留了可扩展性
     * @param belongedUserId 私钥关联的用户ID，非BCE系统中的ID
     * @return 检索出的对应用户的私钥集合
     */
    public List<PersistentPrivateKey> get(PersistentUser belongedUser);

    /**
     * 根据私钥主键ID检索一条私钥
     * @param privateKeyId
     * @return 检索出的私钥持久化对象
     */
    public PersistentPrivateKey get(Integer privateKeyId);

    /**
     * 批量取出BCE私钥，用于更新BCE私钥中decr_prod时
     *
     * @param offset 当前批次的起始位置
     * @param length 当前批次的批量
     * @return 批量的BCE私钥
     */
    public List<PersistentPrivateKey> get(Integer offset, Integer length);

    /**
     * <pre>
     * BCE在初始化完成后，存入数据库的私钥数据都是未关联到具体用户的
     * 每当新用户注册，BCE会在数据库中为其用户记录关联上一条私钥数据
     * 该方法用于检索私钥表中第一条关联用户字段为NULL的记录，并以私钥持久化对象的形式返回
     * </pre>
     *
     * @return 构建好的私钥持久化对象
     */
    public PersistentPrivateKey getFirstAvailable();

    /**
     * 从数据库中检索非法用户持久化私钥的索引集合
     *
     * @return 非法用户持久化私钥的索引集合
     */
    public Set<Integer> getIllegal();

    /**
     * 从数据库中检索合法用户持久化私钥的索引集合
     *
     * @return 合法用户持久化私钥的索引集合
     */
    public Set<Integer> getLegal();

    /**
     * 从数据库中检索还未分配持久化私钥的索引集合
     *
     * @return 还未分配持久化私钥的索引集合
     */
    public Set<Integer> getNotUsed();

    /**
     * 向数据库中批量插入BCE一次生成的所有系统私钥对象(经过AES256加密处理的)，该方法为批量操作，一次事务完成32个私钥的存储，利用累加器循环提交事务，该方法执行完成后，所有私钥对象将被导入数据库
     * @param privateKeys 批量私钥对象的集合
     * @param offset 当前批次的起始位置
     * @param length 当前批次批量
     */
    public void addBatch(List<PersistentPrivateKey> privateKeys, Integer offset, Integer length);

    /**
     * 在Hibernate中使用原生的JDBC批处理插入数据操作，以提高交互效率
     * @param privateKeys 批量用户私钥对象的集合
     * @param offset 当前批次的起始位置
     * @param length 当前批次批量
     */
    public void addJDBCBatch(List<PersistentPrivateKey> privateKeys, Integer offset, Integer length);

    /**
     * 增加一条私钥数据
     *
     * @param privateKey 待插入的私钥对象
     * @param index 该记录对应索引
     */
    public void add(PersistentPrivateKey privateKey, Integer index);

    /**
     * 当BCE系统中的用户合法性变化时，所有用户的私钥数据中decr_prod字段都将改变，该方法用于在这种情况下，更新数据库中维护的所有私钥数据的decr_prod字段
     *
     * @param privateKeys 批量用户私钥对象的集合
     * @param offset 当前批次的起始位置
     * @param length 当前批次批量
     */
    public void updateBatch(List<PersistentPrivateKey> privateKeys, Integer offset, Integer length);

    /**
     * 在Hibernate中使用原生的JDBC批处理更新数据操作，以提高交互效率
     * @param privateKeys 批量用户私钥对象的集合
     * @param offset 当前批次的起始位置
     * @param length 当前批次批量
     */
    public void updateJDBCBatch(List<PersistentPrivateKey> privateKeys, Integer offset, Integer length);

    /**
     * 更新一条私钥
     * @param privateKey 私钥对象
     */
    public void update(PersistentPrivateKey privateKey);

    /**
     * 该方法为设计上的保留方法，因为BCE系统对私钥的维护不许要执行删除私钥的操作，该方法目前仅作为DAO设计的保留方法
     * @param privateKey 待删除的私钥对象
     */
    public void delete(PersistentPrivateKey privateKey);

}
