package bce.server.dao;

import java.util.List;

/**
 * AESKeyDB数据库的DAO的接口声明
 * 
 * @author robins
 *
 */
public interface AESKeyDAO {
	
	/**
	 * 取一条AES原始密钥数据，返回字节流之前须完成16进制解码
	 * 
	 * @param matchedBCEId 该AES密钥所属的用户ID
	 * @return AES原始密钥
	 */
	public byte[] get(int matchedBCEId);
	
	/**
	 * 批量取出AES原始密钥数据，返回字节流之前须完成16进制解码
	 * 
	 * @param offset matchedBCEId起始位置
	 * @param length 当前批量大小
	 * @return 批量的AES原始密钥
	 */
	
	public List<byte[]> get(int offset, int length);
	
	/**
	 * 新增一条AES原始密钥数据，在方法内完成16进制编码
	 * 
	 * @param matchedBCEId 该AES密钥所属的用户ID
	 * @param aesKey AES原始密钥
	 */
	public void add(int matchedBCEId, byte[] aesKey);
	
	/**
	 * 批量添加AES原始密钥，在方法内完成16进制编码
	 * 
	 * @param aesKeyList AES原始密钥批量数据
	 * @param offset 起始位置，对应到服务器用户ID
	 * @param length 批量大小
	 */
	public void add(List<byte[]> aesKeyList, int offset, int length);
	
	/**
	 * 更新一条AES原始密钥数据，在方法内完成16进制编码
	 * 
	 * @param matchedBCEId 该AES密钥所属的用户ID
	 * @param aesKey AES原始密钥
	 */
	public void update(int matchedBCEId, byte[] aesKey);
	
	/**
	 * 批量更新AES原始密钥数据，在方法内完成16进制编码
	 * 
	 * @param aesKeyList AES原始密钥批量数据
	 * @param offset 起始位置，对应到服务器用户ID
	 * @param length 批量大小
	 */
	public void update(List<byte[]> aesKeyList, int offset, int length);
	
	/**
	 * 删除一条AES原始密钥数据
	 * 
	 * @param matchedBCEId 该AES密钥所属的用户ID
	 */
	public void delete(int matchedBCEId);
	
	/**
	 * 删除数据库中所有AES原始密钥数据，用于定期更新用户密钥
	 */
	public void delete();
}
