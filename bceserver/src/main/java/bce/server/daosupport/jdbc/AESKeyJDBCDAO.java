package bce.server.daosupport.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import bce.jni.utils.BCEUtils;
import bce.server.dao.AESKeyDAO;

/**
 * 处理AES密钥的DAO的JDBC实现
 *
 * @author robins
 *
 */
public class AESKeyJDBCDAO implements AESKeyDAO {

    /**
     * DB Provider
     */
    private DB db;

    public void setDb(DB db) {
        this.db = db;
    }

    public AESKeyJDBCDAO() {
    }

    /*
     * (non-Javadoc)
     * @see bce.server.dao.AESKeyDAO#get(int)
     */
    @Override
    public byte[] get(int matchedBCEId) {
        byte[] key = null;
        try {
            Connection connection = db.getConnection();
            StringBuilder query = new StringBuilder();
            query.append("SELECT * FROM BCE_AES_KEY WHERE KEY_ID = ?");
            PreparedStatement stmt = connection.prepareStatement(query.toString());
            stmt.setInt(1, matchedBCEId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                key = BCEUtils.unhex(rs.getString(2));
            }
            db.close(rs);
            db.close(stmt);
            db.close(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return key;
    }

    /*
     * (non-Javadoc)
     * @see bce.server.dao.AESKeyDAO#get(int, int)
     */
    @Override
    public List<byte[]> get(int offset, int length) {
        List<byte[]> resultList = new ArrayList<byte[]>(length);
        try {
            Connection connection = db.getConnection();
            StringBuilder query = new StringBuilder();
            query.append("SELECT * FROM BCE_AES_KEY WHERE KEY_ID >= ? AND KEY_ID < ?");
            PreparedStatement stmt = connection.prepareStatement(query.toString());
            stmt.setInt(1, offset);
            stmt.setInt(2, offset + length);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                resultList.add(BCEUtils.unhex(rs.getString(2)));
            }
            db.close(rs);
            db.close(stmt);
            db.close(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return resultList;
    }

    /*
     * (non-Javadoc)
     * @see bce.server.dao.AESKeyDAO#add(int, byte[])
     */
    @Override
    public void add(int matchedBCEId, byte[] aesKey) {
        try {
            Connection conn = db.getConnection();
            StringBuilder insert = new StringBuilder();
            insert.append("INSERT INTO BCE_AES_KEY (KEY_ID, AES_KEY) VALUES (?, ?)");
            PreparedStatement stmt = conn.prepareStatement(insert.toString());
            stmt.setInt(1, matchedBCEId);
            stmt.setString(2, BCEUtils.hex(aesKey));
            stmt.executeUpdate();
            db.close(stmt);
            db.close(conn);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /*
     * (non-Javadoc)
     * @see bce.server.dao.AESKeyDAO#add(java.util.List, int, int)
     */
    @Override
    public void add(List<byte[]> aesKeyList, int offset, int length) {
        if (aesKeyList.size() != length)
            return;
        try {
            Connection conn = db.getConnection();
            conn.setAutoCommit(false);
            StringBuilder insert = new StringBuilder();
            insert.append("INSERT INTO BCE_AES_KEY (KEY_ID, AES_KEY) VALUES (?, ?)");
            PreparedStatement stmt = conn.prepareStatement(insert.toString());
            for (int i = 0; i < length; i++) {
                stmt.setInt(1, offset + i);
                stmt.setString(2, BCEUtils.hex(aesKeyList.get(i)));
                stmt.addBatch();
            }
            stmt.executeBatch();
            conn.commit();
            db.close(stmt);
            db.close(conn);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /*
     * (non-Javadoc)
     * @see bce.server.dao.AESKeyDAO#update(int, byte[])
     */
    @Override
    public void update(int matchedBCEId, byte[] aesKey) {
        try {
            Connection conn = db.getConnection();
            StringBuilder update = new StringBuilder();
            update.append("UPDATE BCE_AES_KEY SET AES_KEY = ? WHERE KEY_ID = ?");
            PreparedStatement stmt = conn.prepareStatement(update.toString());
            stmt.setString(1, BCEUtils.hex(aesKey));
            stmt.setInt(2, matchedBCEId);
            stmt.executeUpdate();
            db.close(stmt);
            db.close(conn);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /*
     * (non-Javadoc)
     * @see bce.server.dao.AESKeyDAO#update(java.util.List, int, int)
     */
    @Override
    public void update(List<byte[]> aesKeyList, int offset, int length) {
        if (aesKeyList.size() != length)
            return;
        try {
            Connection conn = db.getConnection();
            conn.setAutoCommit(false);
            StringBuilder update = new StringBuilder();
            update.append("UPDATE BCE_AES_KEY SET AES_KEY = ? WHERE KEY_ID = ?");
            PreparedStatement stmt = conn.prepareStatement(update.toString());
            for (int i = 0; i < length; i++) {
                stmt.setString(1, BCEUtils.hex(aesKeyList.get(i)));
                stmt.setInt(2, offset + i);
                stmt.addBatch();
            }
            stmt.executeBatch();
            conn.commit();
            System.out.println(db.dbName);
            db.close(stmt);
            db.close(conn);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /*
     * (non-Javadoc)
     * @see bce.server.dao.AESKeyDAO#delete(int)
     */
    @Override
    public void delete(int matchedBCEId) {
        try {
            Connection conn = db.getConnection();
            StringBuilder delete = new StringBuilder();
            delete.append("DELETE FROM BCE_AES_KEY WHERE KEY_ID = ?");
            PreparedStatement stmt = conn.prepareStatement(delete.toString());
            stmt.setInt(1, matchedBCEId);
            stmt.executeUpdate();
            db.close(stmt);
            db.close(conn);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /*
     * (non-Javadoc)
     * @see bce.server.dao.AESKeyDAO#delete()
     */
    @Override
    public void delete() {
        try {
            Connection conn = db.getConnection();
            StringBuilder delete = new StringBuilder();
            delete.append("DELETE FROM BCE_AES_KEY");
            PreparedStatement stmt = conn.prepareStatement(delete.toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
