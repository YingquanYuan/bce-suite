package bce.server.dao;

import bce.server.entities.PersistentAdmin;

/**
 * 管理员实体类DAO的接口声明
 * 
 * @author robins
 *
 */
public interface AdminDAO {
	/**
	 * 根据管理员主键ID检索一个管理员
	 * 
	 * @param userId 管理员主键ID
	 * @return 一个检索出的管理员实体对象
	 */
	public PersistentAdmin get(Integer adminId);

	/**
	 * 根据管理员名检索一个管理员
	 * 
	 * @param userName 管理员名
	 * @return 一个检索出的管理员实体对象
	 */
	public PersistentAdmin get(String adminName);

	/**
	 * 添加一条管理员记录，用于管理员注册时使用
	 * 
	 * @param user 新增的管理员持久化对象
	 */
	public void add(PersistentAdmin admin);

	/**
	 * 更新一条特定管理员的记录
	 * 
	 * @param user 管理员持久化对象
	 */
	public void update(PersistentAdmin admin);

	/**
	 * 删除一条特定管理员的记录
	 * 
	 * @param user 待删除的管理员持久化对象
	 */
	public void delete(PersistentAdmin admin);
}
