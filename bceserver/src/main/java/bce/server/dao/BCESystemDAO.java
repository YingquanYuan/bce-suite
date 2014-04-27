package bce.server.dao;

import bce.server.entities.PersistentBCESystem;

/**
 * 该接口提供了操作不同BCE系统实例的DAO层方法。BCE服务器中可能维护着多个BCE系统的实例，这些实例分别由各自的系统参数文件与全局参数文件驱动，参数文件的路径维护在数据库中，每个BCE系统被分配了一个systemId
 * @author robins
 *
 */
public interface BCESystemDAO {

    /**
     * 根据主键systemId从数据库中检索对应的URI
     * @param systemId BCE系统ID
     * @return 检索出的BCE系统实例
     */
    public PersistentBCESystem get(Integer systemId);

    /**
     * 新建一个BCE系统实例后，将参数文件对象加入数据库
     * @param system 新的系统实例
     */
    public void add(PersistentBCESystem system);

    /**
     * 当某个特定BCE系统参数文件存储路径发生变化时，更新一个BCE系统实例
     * @param system 改变的系统实例
     */
    public void update(PersistentBCESystem system);

    /**
     * 删除数据库中维护的某个特定BCE系统实例
     * @param system 待删除的BCE系统实例
     */
    public void delete(PersistentBCESystem system);

}
