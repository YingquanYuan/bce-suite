package bce.server.dao;

import bce.server.entities.PersistentUser;

/**
 * BCE用户实体类DAO的接口声明
 * 
 * @author robins
 *
 */
public interface UserDAO {
	
	/**
	 * 根据用户主键ID检索一个用户
	 * @param userId 用户主键ID
	 * @return 一个检索出的用户实体对象
	 */
	public PersistentUser get(Integer userId);
	
	/**
	 * 根据用户名检索一个用户
	 * @param userName 用户名
	 * @return 一个检索出的用户实体对象
	 */
	public PersistentUser get(String userName);
	
	/**
	 * 根据邮箱号检索一个用户
	 * @param email 邮箱号
	 * @return 一个检索出的用户实体对象
	 */
	public PersistentUser getByEmail(String email);
	
	/**
	 * 添加一条用户记录，用于用户注册时使用
	 * @param user 新增的用户持久化对象
	 */
	public void add(PersistentUser user);
	
	/**
	 * 更新一条特定用户的记录
	 * @param user 用户持久化对象
	 */
	public void update(PersistentUser user);
	
	/**
	 * 删除一条特定用户的记录
	 * @param user 待删除的用户持久化对象
	 */
	public void delete(PersistentUser user);
}
