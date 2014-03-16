package bce.server.dao.hibernate;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import bce.server.dao.BCESystemDAO;
import bce.server.entities.PersistentBCESystem;

/**
 * 服务器端BCE系统DAO的Hibernate实现
 * 
 * @author robins
 *
 */
public class BCESystemHibernateDAO implements BCESystemDAO {

	SessionFactory factory;
	
	public void setFactory(SessionFactory factory) {
		this.factory = factory;
	}
	
	public BCESystemHibernateDAO() {}

	/*
	 * (non-Javadoc)
	 * @see bce.server.dao.BCESystemDAO#get(java.lang.Integer)
	 */
	@Override
	public PersistentBCESystem get(Integer systemId) {
		
		Session session = factory.openSession();
		return (PersistentBCESystem) session.get(PersistentBCESystem.class, systemId);
	}

	/*
	 * (non-Javadoc)
	 * @see bce.server.dao.BCESystemDAO#add(bce.server.entities.PersistentBCESystem)
	 */
	@Override
	public void add(PersistentBCESystem system) {
		
		Session session = factory.openSession();
		session.beginTransaction();
		session.save(system);
		session.getTransaction().commit();
		
		if (session.isOpen())
			session.close();
	}

	/*
	 * (non-Javadoc)
	 * @see bce.server.dao.BCESystemDAO#update(bce.server.entities.PersistentBCESystem)
	 */
	@Override
	public void update(PersistentBCESystem system) {
		
		Session session = factory.openSession();
		session.beginTransaction();
		session.update(session.merge(system));
		session.getTransaction().commit();
		
		if (session.isOpen())
			session.close();
	}

	/*
	 * (non-Javadoc)
	 * @see bce.server.dao.BCESystemDAO#delete(bce.server.entities.PersistentBCESystem)
	 */
	@Override
	public void delete(PersistentBCESystem system) {
		
		Session session = factory.openSession();
		session.beginTransaction();
		session.delete(session.merge(system));
		session.getTransaction().commit();
		
		if (session.isOpen())
			session.close();
	}

}
