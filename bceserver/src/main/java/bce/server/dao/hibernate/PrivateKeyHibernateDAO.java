package bce.server.dao.hibernate;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.hibernate.CacheMode;
import org.hibernate.Criteria;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.hibernate.jdbc.Work;

import bce.java.io.SecureByteArrayInputStream;
import bce.java.io.SecureByteArrayOutputStream;
import bce.java.utils.BCECapsule;
import bce.java.utils.BCECapsuleAESImpl;
import bce.server.dao.AESKeyDAO;
import bce.server.dao.PrivateKeyDAO;
import bce.server.daosupport.jdbc.AESKeySupport;
import bce.server.entities.PersistentPrivateKey;
import bce.server.entities.PersistentUser;
import bce.server.util.SpringUtil;

/**
 * BCE私钥实体类DAO的Hibernate实现
 * 
 * @author robins
 *
 */
public class PrivateKeyHibernateDAO implements PrivateKeyDAO {
	
	private SessionFactory factory;
	
	private AESKeyDAO aesKeyDAO;
	
	public void setFactory(SessionFactory factory) {
		this.factory = factory;
	}
	
	public void setAesKeyDAO(AESKeyDAO aesKeyDAO) {
		this.aesKeyDAO = aesKeyDAO;
	}
	
	/**
	 * 构造函数
	 */
	public PrivateKeyHibernateDAO() {}
	
	/*
	 * (non-Javadoc)
	 * @see bce.server.dao.PrivateKeyDAO#get(java.lang.Integer)
	 */
	@Override
	public PersistentPrivateKey get(Integer privateKeyId) {
		Session session = factory.openSession();
		PersistentPrivateKey privateKey = (PersistentPrivateKey) session.get(PersistentPrivateKey.class, privateKeyId);
		SecureByteArrayInputStream buffer = new SecureByteArrayInputStream(privateKey.getPrivateKeyField());
		BCECapsule capsule = new BCECapsuleAESImpl();
		capsule.setKey(aesKeyDAO.get(privateKeyId));
		try {
			capsule.readExternal(buffer);
			Arrays.fill(privateKey.getPrivateKeyField(), (byte) 0);
			byte[] newBuffer = new byte[capsule.getData().length];
			System.arraycopy(capsule.getData(), 0, newBuffer, 0, newBuffer.length);
			privateKey.setPrivateKeyField(newBuffer);
			buffer.close();
			capsule.abort();
			
		} catch (IOException e) {
			capsule.abort();
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			capsule.abort();
			e.printStackTrace();
		}
		return privateKey;
	}

	/*
	 * (non-Javadoc)
	 * @see bce.server.dao.PrivateKeyDAO#get(bce.server.entities.PersistentUser)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<PersistentPrivateKey> get(PersistentUser belongedUser) {
		Session session = factory.openSession();
		Criteria criteria = session.createCriteria(PersistentPrivateKey.class);
		criteria.add(Restrictions.eq("belongedUser", belongedUser));
		List<PersistentPrivateKey> resultList = (List<PersistentPrivateKey>) criteria.list();
		
		for (int i = 0; i < resultList.size(); i++) {
			byte[] aesKey = aesKeyDAO.get(resultList.get(i).getPrivateKeyId());
			
			SecureByteArrayInputStream buffer = new SecureByteArrayInputStream(resultList.get(i).getPrivateKeyField());
			BCECapsule capsule = new BCECapsuleAESImpl();
			capsule.setKey(aesKey);
			try {
				capsule.readExternal(buffer);
				Arrays.fill(resultList.get(i).getPrivateKeyField(), (byte) 0);
				byte[] newBuffer = new byte[capsule.getData().length];
				System.arraycopy(capsule.getData(), 0, newBuffer, 0, newBuffer.length);
				resultList.get(i).setPrivateKeyField(newBuffer);
				buffer.close();
				capsule.abort();
				
			} catch (IOException e) {
				capsule.abort();
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				capsule.abort();
				e.printStackTrace();
			}
		}
		
		return resultList;
	}
	
	/*
	 * (non-Javadoc)
	 * @see bce.server.dao.PrivateKeyDAO#get(java.lang.Integer, java.lang.Integer)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<PersistentPrivateKey> get(Integer offset, Integer length) {
		Session session = factory.openSession();
		Integer endIndex = offset + length;
		List<PersistentPrivateKey> resultList = (List<PersistentPrivateKey>) session.createQuery("from PersistentPrivateKey where privateKeyId >= " + offset + " and privateKeyId < " + endIndex).list();
		List<byte[]> aesKeyList = aesKeyDAO.get(offset, length);
		if (aesKeyList.size() != resultList.size())
			return null;
		for (int i = 0; i < resultList.size(); i++) {
			SecureByteArrayInputStream buffer = new SecureByteArrayInputStream(resultList.get(i).getPrivateKeyField());
			BCECapsule capsule = new BCECapsuleAESImpl();
			capsule.setKey(aesKeyList.get(i));
			try {
				capsule.readExternal(buffer);
				Arrays.fill(resultList.get(i).getPrivateKeyField(), (byte) 0);
				byte[] newBuffer = new byte[capsule.getData().length];
				System.arraycopy(capsule.getData(), 0, newBuffer, 0, newBuffer.length);
				resultList.get(i).setPrivateKeyField(newBuffer);
				buffer.close();
				capsule.abort();
				
			} catch (IOException e) {
				capsule.abort();
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				capsule.abort();
				e.printStackTrace();
			}
		}
		return resultList;
	}
	
	/*
	 * (non-Javadoc)
	 * @see bce.server.dao.PrivateKeyDAO#getFirstAvailable()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public PersistentPrivateKey getFirstAvailable() {
		Session session = factory.openSession();
		Criteria criteria = session.createCriteria(PersistentPrivateKey.class);
		criteria.add(Restrictions.isNull("belongedUser")).add(Restrictions.sqlRestriction("PRIVATE_KEY_ID % 8 <> 1"));
		criteria.setMaxResults(1);
		List<PersistentPrivateKey> resultList = (List<PersistentPrivateKey>) criteria.list();
		if (resultList.isEmpty() || resultList == null)
			return null;
		PersistentPrivateKey persistentPrivateKey = resultList.get(0);
		
		byte[] aesKey = aesKeyDAO.get(persistentPrivateKey.getPrivateKeyId());
		
		SecureByteArrayInputStream buffer = new SecureByteArrayInputStream(persistentPrivateKey.getPrivateKeyField());
		BCECapsule capsule = new BCECapsuleAESImpl();
		capsule.setKey(aesKey);
		try {
			capsule.readExternal(buffer);
			Arrays.fill(persistentPrivateKey.getPrivateKeyField(), (byte) 0);
			byte[] newBuffer = new byte[capsule.getData().length];
			System.arraycopy(capsule.getData(), 0, newBuffer, 0, newBuffer.length);
			persistentPrivateKey.setPrivateKeyField(newBuffer);
			capsule.abort();
			buffer.close();
		} catch (IOException e) {
			capsule.abort();
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			capsule.abort();
			e.printStackTrace();
		}
		
//		Iterator<PersistentPrivateKey> iter = resultList.iterator();
//		while (iter.hasNext())
//			iter.next().abort();
		
//		persistentPrivateKey.setPrivateKeyId(BCEUtils.bytesToInt(persistentPrivateKey.getPrivateKeyField()));
		
		return persistentPrivateKey;
	}
	
	/*
	 * (non-Javadoc)
	 * @see bce.server.dao.PrivateKeyDAO#getIllegal()
	 */
	public Set<Integer> getIllegal() {
		
		final Set<Integer> set = new HashSet<Integer>();
		Session session = factory.openSession();
		
		Work work = new Work() {
			
			@Override
			public void execute(Connection connection) throws SQLException {
				
				PreparedStatement stmt = connection.prepareStatement("SELECT PRIVATE_KEY_ID FROM BCE_PRIVATE_KEY WHERE IS_LEGAL = 0 AND BELONGED_USER_ID IS NOT NULL");
				ResultSet rs = stmt.executeQuery();
				while (rs.next()) {
					set.add(rs.getInt(1));
				}
			}
		};
		
		session.doWork(work);
		
		return set;
	}
	
	/*
	 * (non-Javadoc)
	 * @see bce.server.dao.PrivateKeyDAO#getLegal()
	 */
	public Set<Integer> getLegal() {
		
		final Set<Integer> set = new HashSet<Integer>();
		Session session = factory.openSession();
		
		Work work = new Work() {
			
			@Override
			public void execute(Connection connection) throws SQLException {
				
				PreparedStatement stmt = connection.prepareStatement("SELECT PRIVATE_KEY_ID FROM BCE_PRIVATE_KEY WHERE IS_LEGAL = 1 AND BELONGED_USER_ID IS NOT NULL");
				ResultSet rs = stmt.executeQuery();
				while (rs.next()) {
					set.add(rs.getInt(1));
				}
			}
		};
		
		session.doWork(work);
		
		return set;
	}
	
	/*
	 * (non-Javadoc)
	 * @see bce.server.dao.PrivateKeyDAO#getNotUsed()
	 */
	public Set<Integer> getNotUsed() {
		
		final Set<Integer> set = new HashSet<Integer>();
		Session session = factory.openSession();

		Work work = new Work() {
			
			@Override
			public void execute(Connection connection) throws SQLException {
				
				PreparedStatement stmt = connection.prepareStatement("SELECT PRIVATE_KEY_ID FROM BCE_PRIVATE_KEY WHERE BELONGED_USER_ID IS NULL");
				ResultSet rs =stmt.executeQuery();
				while (rs.next()) {
					set.add(rs.getInt(1));
				}
			}
		};
		
		session.doWork(work);
		
		return set;
	}

	/*
	 * (non-Javadoc)
	 * @see bce.server.dao.PrivateKeyDAO#add(bce.server.entities.PersistentPrivateKey, java.lang.Integer)
	 */
	@Override
	public void add(PersistentPrivateKey privateKey, Integer index) {
		Session session = factory.openSession();
		session.beginTransaction();
		
		byte[] aesKey = AESKeySupport.generateKey();
		
		aesKeyDAO.add(index, aesKey);
		Arrays.fill(aesKey, (byte) 0);

		BCECapsule capsule = new BCECapsuleAESImpl();
		capsule.protect(privateKey.getPrivateKeyField());
		byte[] aesKeyCopy = new byte[aesKey.length];
		System.arraycopy(aesKey, 0, aesKeyCopy, 0, aesKeyCopy.length);
		capsule.setKey(aesKeyCopy);
		SecureByteArrayOutputStream buffer = new SecureByteArrayOutputStream();
		try {
			capsule.writeExternal(buffer);
			Arrays.fill(privateKey.getPrivateKeyField(), (byte) 0);
			privateKey.setPrivateKeyField(buffer.toByteArray());
			buffer.reset();
			buffer.close();
			capsule.abort();
		} catch (IOException e) {
			capsule.abort();
			e.printStackTrace();
		}
		
		session.save(privateKey);
		session.getTransaction().commit();
		if (session.isOpen())
			session.close();
	}

	/*
	 * (non-Javadoc)
	 * @see bce.server.dao.PrivateKeyDAO#addBatch(java.util.List, java.lang.Integer, java.lang.Integer)
	 */
	@Override
	public void addBatch(List<PersistentPrivateKey> privateKeys, Integer offset, Integer length) {
		if (length != privateKeys.size())
			return;
		// 打开Session
		Session session = factory.openSession();
		// 开始事务
		Transaction tx = session.beginTransaction();
		
		// AES key Thread
		List<byte[]> aesKeyList = new ArrayList<byte[]>(length);
		for (int i = 0; i < length; i++) {
			aesKeyList.add(AESKeySupport.generateKey());
		}
		
		// 另起一个线程用于存储AES密钥
		AESKeyStoreThread thread = new AESKeyStoreThread(aesKeyList, offset, length);
		thread.start();
		
		for (int i = 0; i < length; i++) {
			
			BCECapsule capsule = new BCECapsuleAESImpl();
			capsule.protect(privateKeys.get(i).getPrivateKeyField());
			byte[] aesKey = new byte[aesKeyList.get(i).length];
			System.arraycopy(aesKeyList.get(i), 0, aesKey, 0, aesKey.length);
			capsule.setKey(aesKey);
			SecureByteArrayOutputStream buffer = new SecureByteArrayOutputStream();
			
			try {
				capsule.writeExternal(buffer);
				// 这一步是否要做？
				Arrays.fill(privateKeys.get(i).getPrivateKeyField(), (byte) 0);
				privateKeys.get(i).setPrivateKeyField(buffer.toByteArray());
				buffer.reset();
				buffer.close();
				capsule.abort();
			} catch (IOException e) {
				capsule.abort();
				e.printStackTrace();
			} finally {
			}
			
			// 在Session级别缓存私钥实例
			session.save(privateKeys.get(i));
			//每当累加器是20的倍数时，将Session中的数据刷入数据库，并清空Session缓存
			if (i % 32 == 0) {
				session.flush();
				session.clear();
				tx.commit();
				tx = session.beginTransaction();
			}
		}
		
		// 提交事务
		tx.commit();
		
		// 关闭事务
		if (session.isOpen())
			session.close();
	}

	/*
	 * (non-Javadoc)
	 * @see bce.server.dao.PrivateKeyDAO#addJDBCBatch(java.util.List, java.lang.Integer, java.lang.Integer)
	 */
	@Override
	public void addJDBCBatch(List<PersistentPrivateKey> privateKeys, Integer offset, Integer length) {
		
		if (length != privateKeys.size())
			return;
		
		Session session = factory.openSession();
		
		final List<PersistentPrivateKey> privateKeysList = privateKeys;
		
		final List<byte[]> aesKeyList = new ArrayList<byte[]>(length);
		for (int i = 0; i < length; i++) {
			aesKeyList.add(AESKeySupport.generateKey());
		}
		AESKeyStoreThread thread = new AESKeyStoreThread(aesKeyList, offset, length);
		thread.start();
		
		Work work = new Work() {

			@Override
			public void execute(Connection connection) throws SQLException {
				
				connection.setAutoCommit(false);
				
				PreparedStatement stmt = connection.prepareStatement("INSERT INTO BCE_PRIVATE_KEY (BCE_PRIV_KEY_FIELD, BELONGED_SYSTEM_ID, IS_LEGAL, OPTLOCK) VALUES (?, ?, ?, ?)");
				List<SecureByteArrayInputStream> inBufferList = new ArrayList<SecureByteArrayInputStream>();
				for (int i = 0; i < privateKeysList.size(); i ++) {
					BCECapsule capsule = new BCECapsuleAESImpl();
					capsule.protect(privateKeysList.get(i).getPrivateKeyField());
					byte[] aesKey = new byte[aesKeyList.get(i).length];
					System.arraycopy(aesKeyList.get(i), 0, aesKey, 0, aesKey.length);
					capsule.setKey(aesKey);
					try {
						SecureByteArrayOutputStream buffer = new SecureByteArrayOutputStream();
						capsule.writeExternal(buffer);
						// 这个InputSream是否需要关闭？
						SecureByteArrayInputStream inBuffer = new SecureByteArrayInputStream(buffer.toByteArray());
						stmt.setBlob(1, inBuffer);
						buffer.reset();
						buffer.close();
						capsule.abort();
						inBufferList.add(inBuffer);
					} catch (IOException e) {
						capsule.abort();
						e.printStackTrace();
					} finally {
					}
					
					stmt.setInt(2, privateKeysList.get(i).getBelongedBCESystem().getBceSystemId());
					// 这一步是为了使JDBC操作与Hibernate乐观锁兼容
					stmt.setInt(3, 1);
					stmt.setInt(4, 0);
					stmt.addBatch();
				}
				stmt.executeBatch();
				
				connection.commit();
				
				for (int i = 0; i < inBufferList.size(); i++) {
					try {
						inBufferList.get(i).close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		};
		
		session.doWork(work);
		if (session.isOpen())
			session.close();
	}

	/*
	 * (non-Javadoc)
	 * @see bce.server.dao.PrivateKeyDAO#updateBatch(java.util.List, java.lang.Integer, java.lang.Integer)
	 */
	@Override
	public void updateBatch(List<PersistentPrivateKey> privateKeys, Integer offset, Integer length) {
		
		if (privateKeys.size() != length)
			return;
		
		List<byte[]> aesKeyList = aesKeyDAO.get(offset, length);
		Integer endIndex = offset + length;
		
		Session session = factory.openSession();
		Transaction tx = session.beginTransaction();
		ScrollableResults results = session.createQuery("from PersistentPrivateKey where privateKeyId >= " + offset + " and privateKeyId < " + endIndex).setCacheMode(CacheMode.IGNORE).scroll(ScrollMode.FORWARD_ONLY);
		
		int i = 0;
		while (results.next()) {
			BCECapsule capsule = new BCECapsuleAESImpl();
			capsule.protect(privateKeys.get(i).getPrivateKeyField());
			capsule.setKey(aesKeyList.get(i));
			SecureByteArrayOutputStream buffer = new SecureByteArrayOutputStream();
			try {
				capsule.writeExternal(buffer);
				Arrays.fill(privateKeys.get(i).getPrivateKeyField(), (byte) 0);
				privateKeys.get(i).setPrivateKeyField(buffer.toByteArray());
				buffer.reset();
				buffer.close();
				capsule.abort();
			} catch (IOException e) {
				capsule.abort();
				e.printStackTrace();
			}
			PersistentPrivateKey privateKey = (PersistentPrivateKey) results.get(0);
			Arrays.fill(privateKey.getPrivateKeyField(), (byte) 0);
			privateKey.setPrivateKeyField(privateKeys.get(i).getPrivateKeyField());
			privateKey.setIsLegal(privateKeys.get(i).getIsLegal());
			if (++i % 32 == 0) {
				session.flush();
				session.clear();
			}
		}
		
		tx.commit();
		
		if (session.isOpen())
			session.close();
		
		Iterator<byte[]> iter = aesKeyList.iterator();
		while (iter.hasNext()) {
			Arrays.fill(iter.next(), (byte) 0);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see bce.server.dao.PrivateKeyDAO#updateJDBCBatch(java.util.List, java.lang.Integer, java.lang.Integer)
	 */
	@Override
	public void updateJDBCBatch(List<PersistentPrivateKey> privateKeys, Integer offset, Integer length) {
		
		if (privateKeys.size() != length)
			return;
		
		final List<byte[]> aesKeyList = aesKeyDAO.get(offset, length);
		
		final List<PersistentPrivateKey> privateKeyList = privateKeys;
		final int off = offset;
		final int len = length;
		Session session = factory.openSession();
		
		Work work = new Work() {
			
			@Override
			public void execute(Connection connection) throws SQLException {
				
				connection.setAutoCommit(false);
				
				PreparedStatement stmt = connection.prepareStatement("UPDATE BCE_PRIVATE_KEY SET BCE_PRIV_KEY_FIELD=?, IS_LEGAL=? WHERE PRIVATE_KEY_ID=?");
				for (int i = 0; i < len; i++) {
					BCECapsule capsule = new BCECapsuleAESImpl();
					capsule.protect(privateKeyList.get(i).getPrivateKeyField());
					capsule.setKey(aesKeyList.get(i));
					SecureByteArrayOutputStream buffer = new SecureByteArrayOutputStream();
					try {
						capsule.writeExternal(buffer);
						Arrays.fill(privateKeyList.get(i).getPrivateKeyField(), (byte) 0);
						privateKeyList.get(i).setPrivateKeyField(buffer.toByteArray());
						buffer.reset();
						buffer.close();
						capsule.abort();
					} catch (IOException e) {
						capsule.abort();
						e.printStackTrace();
					}
					stmt.setBlob(1, new SecureByteArrayInputStream(privateKeyList.get(i).getPrivateKeyField()));
					stmt.setInt(2, privateKeyList.get(i).getIsLegal());
					stmt.setInt(3, off + i);
					stmt.addBatch();
				}
				stmt.executeBatch();
				
				
				connection.commit();
			}
		};
		
		session.doWork(work);
		if (session.isOpen())
			session.close();
		
		Iterator<byte[]> iter = aesKeyList.iterator();
		while (iter.hasNext()) {
			Arrays.fill(iter.next(), (byte) 0);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see bce.server.dao.PrivateKeyDAO#update(bce.server.entities.PersistentPrivateKey)
	 */
	@Override
	public void update(PersistentPrivateKey privateKey) {
		Session session = factory.openSession();
		session.beginTransaction();
		
		BCECapsule capsule = new BCECapsuleAESImpl();
		capsule.protect(privateKey.getPrivateKeyField());
		capsule.setKey(aesKeyDAO.get(privateKey.getPrivateKeyId()));
		SecureByteArrayOutputStream buffer = new SecureByteArrayOutputStream();
		try {
			capsule.writeExternal(buffer);
			Arrays.fill(privateKey.getPrivateKeyField(), (byte) 0);
			privateKey.setPrivateKeyField(buffer.toByteArray());
			buffer.reset();
			buffer.close();
			capsule.abort();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		session.update(session.merge(privateKey));
		session.getTransaction().commit();
		if (session.isOpen())
			session.close();
	}

	/*
	 * (non-Javadoc)
	 * @see bce.server.dao.PrivateKeyDAO#delete(bce.server.entities.PersistentPrivateKey)
	 */
	@Override
	public void delete(PersistentPrivateKey privateKey) {
		Session session = factory.openSession();
		session.beginTransaction();
		session.delete(session.merge(privateKey));
		session.getTransaction().commit();
		if (session.isOpen())
			session.close();
	}

}

final class AESKeyStoreThread extends Thread {
	
	private List<byte[]> aesKeyList;
	
	private Integer offset;
	
	private Integer length;
	
	public AESKeyStoreThread(List<byte[]> aesKeyList, Integer off, Integer len) {
		this.aesKeyList = aesKeyList;
		this.offset = off;
		this.length = len;
	}
	
	@Override
	public void run() {
		AESKeyDAO dao = (AESKeyDAO) SpringUtil.getBean("aesKeyDAO");
		dao.add(aesKeyList, offset, length);
		for (byte[] bs : this.aesKeyList) {
			Arrays.fill(bs, (byte) 0);
		}
	}
}
