package bce.server.dao.hibernate;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

import bce.server.dao.AdminDAO;
import bce.server.entities.PersistentAdmin;

/**
 * 管理员实体类DAO的Hibernate实现
 * 
 * @author robins
 *
 */
public class AdminHibernateDAO implements AdminDAO {

	private SessionFactory factory;
	
	public void setFactory(SessionFactory factory) {
		this.factory = factory;
	}
	
	public AdminHibernateDAO() {}

	/*
	 * (non-Javadoc)
	 * @see bce.server.dao.AdminDAO#get(java.lang.Integer)
	 */
	@Override
	public PersistentAdmin get(Integer adminId) {
		
		Session session = factory.openSession();
		return (PersistentAdmin) session.get(PersistentAdmin.class, adminId);
	}

	/*
	 * (non-Javadoc)
	 * @see bce.server.dao.AdminDAO#get(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public PersistentAdmin get(String adminName) {
		
		Session session = factory.openSession();
		Criteria criteria = session.createCriteria(PersistentAdmin.class);
		criteria.add(Restrictions.eq("adminName", adminName));
		List<PersistentAdmin> resultList = (List<PersistentAdmin>) criteria.list();
		
		if (resultList.isEmpty())
			return null;
		
		return resultList.get(0);
	}

	/*
	 * (non-Javadoc)
	 * @see bce.server.dao.AdminDAO#add(bce.server.entities.PersistentAdmin)
	 */
	@Override
	public void add(PersistentAdmin admin) {
		
		Session session = factory.openSession();
		session.beginTransaction();
		session.save(admin);
		session.getTransaction().commit();
		
		if (session.isOpen())
			session.close();
	}

	/*
	 * (non-Javadoc)
	 * @see bce.server.dao.AdminDAO#update(bce.server.entities.PersistentAdmin)
	 */
	@Override
	public void update(PersistentAdmin admin) {
		
		Session session = factory.openSession();
		session.beginTransaction();
		session.update(session.merge(admin));
		session.getTransaction().commit();
		
		if (session.isOpen())
			session.close();
	}

	/*
	 * (non-Javadoc)
	 * @see bce.server.dao.AdminDAO#delete(bce.server.entities.PersistentAdmin)
	 */
	@Override
	public void delete(PersistentAdmin admin) {
		
		Session session = factory.openSession();
		session.beginTransaction();
		session.delete(session.merge(admin));
		session.getTransaction().commit();
		
		if (session.isOpen())
			session.close();
	}

}
