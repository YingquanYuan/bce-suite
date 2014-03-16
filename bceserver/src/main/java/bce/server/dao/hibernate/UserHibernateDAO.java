package bce.server.dao.hibernate;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;
import org.hibernate.criterion.Restrictions;

import bce.java.utils.Hash;
import bce.jni.utils.BCEUtils;
import bce.server.dao.UserDAO;
import bce.server.entities.PersistentUser;

/**
 * BCE用户实体类DAO的Hibernate实现
 * 
 * @author robins
 *
 */
public class UserHibernateDAO implements UserDAO {
	
	SessionFactory factory;

	public void setFactory(SessionFactory factory) {
		this.factory = factory;
	}
	
	public UserHibernateDAO() {}

	/*
	 * (non-Javadoc)
	 * @see bce.server.dao.UserDAO#get(java.lang.Integer)
	 */
	@Override
	public PersistentUser get(Integer userId) {
		
		Session session = factory.openSession();
		return (PersistentUser) session.get(PersistentUser.class, userId);
	}

	/*
	 * (non-Javadoc)
	 * @see bce.server.dao.UserDAO#get(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public PersistentUser get(String userName) {
		
		Session session = factory.openSession();
		Criteria criteria = session.createCriteria(PersistentUser.class);
		criteria.add(Restrictions.eq("userName", userName));
		List<PersistentUser> resultsList = (List<PersistentUser>) criteria.list();
		
		if (resultsList.isEmpty())
			return null;
		
		return resultsList.get(0);
	}
	
	/*
	 * (non-Javadoc)
	 * @see bce.server.dao.UserDAO#getByEmail(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public PersistentUser getByEmail(String email) {
		Session session = factory.openSession();
		Criteria criteria = session.createCriteria(PersistentUser.class);
		criteria.add(Restrictions.eq("email", email));
		List<PersistentUser> resultList = (List<PersistentUser>) criteria.list();
		
		if (resultList.isEmpty())
			return null;
		
		return resultList.get(0);
	}

	/*
	 * (non-Javadoc)
	 * @see bce.server.dao.UserDAO#add(bce.server.entities.PersistentUser)
	 */
	@Override
	public void add(PersistentUser user) {
		
		Session session = factory.openSession();
		session.beginTransaction();
		user.getPassword().getBytes();
		user.setPassword(BCEUtils.hex(Hash.sha1(user.getPassword())));
		session.save(user);
		session.getTransaction().commit();
		
		if (session.isOpen())
			session.close();
	}

	/*
	 * (non-Javadoc)
	 * @see bce.server.dao.UserDAO#update(bce.server.entities.PersistentUser)
	 */
	@Override
	public void update(PersistentUser user) {
		
		Session session = factory.openSession();
		session.beginTransaction();
		session.update(session.merge(user));
		session.getTransaction().commit();
		
		if (session.isOpen())
			session.close();
	}

	/*
	 * (non-Javadoc)
	 * @see bce.server.dao.UserDAO#delete(bce.server.entities.PersistentUser)
	 */
	@Override
	public void delete(PersistentUser user) {
		
		Session session = factory.openSession();
		session.beginTransaction();
		session.delete(session.merge(user));
		session.getTransaction().commit();
		
		if (session.isOpen())
			session.close();
	}

}
