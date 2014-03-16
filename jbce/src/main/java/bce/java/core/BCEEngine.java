package bce.java.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import bce.java.entities.BCECiphertext;
import bce.java.entities.BCEClientSystem;
import bce.java.entities.BCEEncryptionProduct;
import bce.java.entities.BCEPrivateKey;
import bce.java.entities.BCESymmetricKey;
import bce.java.entities.BCESystem;
import bce.java.entities.BCETransientKey;
import bce.java.utils.BCEConstraints;
import bce.java.utils.MemoryUtil;
import bce.jni.bce.BCELibrary;

/**
 * 广播加密(BCE)的核心类，提供BCE相关的面向对象封装与方法，整个广播加密系统是由这个类所驱动的
 * 
 * @author <a href="mailto:yingq.yuan@gmail.com">Yingquan Yuan</a>
 */
public class BCEEngine {

	/**
	 * 在服务器端初始化一个BCE系统
	 * 
	 * @param system 一个实例化的BCE系统描述对象
	 * @return 当前系统初始化后返回的临时密钥，用完必须手动销毁该对象的密钥内容
	 */
	public static BCETransientKey setup(BCESystem system) {

		if (system == null)
			return null;
		
		byte[] transientKeyBytes = new byte[BCEConstraints.PRIVATE_KEY_LENGTH];

		int result = BCELibrary.setup(system.getCurveParamsURI().getBytes(),
				system.getUserNumber(), system.getServerSysParamsURI().getBytes(),
				system.getGlobalSysParamsURI().getBytes(), transientKeyBytes);
		if (result != 0)
			return null;

		BCETransientKey transientKey = new BCETransientKey();
		transientKey.setTransientKey(transientKeyBytes);
		return transientKey;
	}
	
	/**
	 * 为服务器端特定BCE系统批量生成私钥
	 * 
	 * @param system 当前BCE系统的描述对象
	 * @param transientKey 当前系统初始化后返回的临时密钥，用于协助生成BCE私钥，用完必须手动销毁该对象的密钥内容
	 * @return 批量生成后返回的BCE私钥对象集合
	 */
	public static List<BCEPrivateKey> genBCEPrivKeys(BCESystem system, BCETransientKey transientKey) {
		
		if (system == null)
			return null;
		
		if (transientKey == null)
			return null;
		
		byte[][] BCEPrivKeyOut = new byte[system.getKeyFetchSize()][BCEConstraints.USER_PRIVATE_KEY_SIZE];
		for (int i = 0; i < BCEPrivKeyOut.length; i++)
			BCEPrivKeyOut[i] = new byte[BCEConstraints.USER_PRIVATE_KEY_SIZE];
		
		int result = BCELibrary.genPrivateKeys(system.getServerSysParamsURI().getBytes(), 
				transientKey.getTransientKey(), system.getUserNumber(), system.getKeyFetchStartIndex(), 
				system.getKeyFetchSize(), BCEPrivKeyOut);
		
		if (result != 0)
			return null;
		
		List<BCEPrivateKey> privKeysList = new ArrayList<BCEPrivateKey>(system.getKeyFetchSize());
		for (byte[] bs : BCEPrivKeyOut) {
			BCEPrivateKey privKey = BCEPrivateKey.fromBytes(bs);
			privKey.setIsLegal(1);
			privKeysList.add(privKey);
		}
		
		// 异步擦出内存中驻留的私钥数据
		MemoryUtil.asyncEraseBuffers(BCEPrivKeyOut);
		
		return privKeysList;
	}
	
	/**
	 * 为服务器端特定BCE系统完成一次广播加密
	 * 
	 * @param system 当前BCE系统的描述对象
	 * @return 加密产品——BCE系统加密完成后生成的密文与对称密钥的封装器<br><b>注意: </b>这里的加密产品不是JNI中的encr_prod
	 */
	public static BCEEncryptionProduct encrypt(BCESystem system) {
		
		if (system == null)
			return null;
		
		byte[] CTC0Out = new byte[BCEConstraints.CT_C0_LENGTH];
		byte[] CTC1Out = new byte[BCEConstraints.CT_C1_LENGTH];
		byte[] symmetricKeyOut = new byte[BCEConstraints.SYMMETRIC_KEY_LENGTH];
		
		int result = BCELibrary.encrypt(system.getServerSysParamsURI().getBytes(), CTC0Out, CTC1Out, symmetricKeyOut);
		
		if (result != 0)
			return null;
		
		BCEEncryptionProduct encryptionProduct = new BCEEncryptionProduct();
		BCECiphertext ciphertext = BCECiphertext.fromBytes(CTC0Out, CTC1Out);
		BCESymmetricKey symmetricKey = BCESymmetricKey.fromBytes(symmetricKeyOut);
		encryptionProduct.setCiphertext(ciphertext);
		encryptionProduct.setSymmetricKey(symmetricKey);
		
		MemoryUtil.asyncEraseBuffers(CTC0Out, CTC1Out, symmetricKeyOut);
		
		return encryptionProduct;
	}
	
	/**
	 * 在客户端为特定用户完成一次BCE解密
	 * 
	 * @param clientSystem 客户端BCE系统实例化对象
	 * @param privateKey 用户的BCE私钥对象
	 * @param ciphertext 用户接收到的密文对象
	 * @return 解密后得到的对称密钥，即消息加密密钥
	 */
	public static BCESymmetricKey decrypt(BCEClientSystem clientSystem, BCEPrivateKey privateKey, BCECiphertext ciphertext) {
		
		if (clientSystem == null)
			return null;
		if (privateKey == null)
			return null;
		if (ciphertext == null)
			return null;
		
		byte[] symmetricKeyOut = new byte[BCEConstraints.SYMMETRIC_KEY_LENGTH];
		
		int result = BCELibrary.decrypt(clientSystem.getGlobalSysParamsURI().getBytes(), 
				privateKey.toBytes(), ciphertext.ctC0ToBytes(), ciphertext.ctC1ToBytes(), symmetricKeyOut);
		
		if (result != 0)
			return null;
		
		BCESymmetricKey symmetricKey = BCESymmetricKey.fromBytes(symmetricKeyOut);
		
		MemoryUtil.asyncEraseBuffers(symmetricKeyOut);
		
		return symmetricKey;
	}
	
	/**
	 * <pre>
	 * 当用户合法性发生变化时，在服务器端为特定BCE系统修改加密产品
	 * 这里的加密产品要区别于BCE加密方法返回的加密产物
	 * 该方法通过JNI调用，在底层修改了BCE系统的参数配置文件
	 * </pre>
	 * 
	 * @param system 当前BCE系统的实例化对象
	 * @return 返回0：执行成功；其它：错误码
	 */
	public static int changeEncryptionProduct(BCESystem system) {
		
		if (system == null)
			return -100;
		
		int result = BCELibrary.changeEncryptionProduct(system.getServerSysParamsURI().getBytes(), 
				system.getAdds(), system.getnAdds(), system.getRems(), system.getnRems());
		
		if (result != 0)
			return -90;
		
		return result;
	}
	
	/**
	 * <pre>
	 * 当用户合法性发生变化，且服务器端已经修改了加密产品的情况下，
	 * 该方法在服务器端为特定BCE系统中批量的用户私钥修改解密产品字段
	 * </pre>
	 * 
	 * @param system 当前BCE系统的实例化对象
	 * @param privateKeysList 当前批次批量BCE私钥对象的集合
	 * @return 返回0：执行成功；其它：错误码
	 */
	public static int changeDecryptionProduct(BCESystem system, List<BCEPrivateKey> privateKeysList) {
		
		if (system == null)
			return -100;
		if (privateKeysList == null)
			return -90;
		if (privateKeysList.size() != system.getChangeDecrProdBatchSize())
			return -80;
		
		byte[] decrProdBatch = new byte[system.getChangeDecrProdBatchSize() * BCEConstraints.PRK_DECR_PROD_LENGTH];
		byte[] decrProdBatchOut = new byte[system.getChangeDecrProdBatchSize() * BCEConstraints.PRK_DECR_PROD_LENGTH];
		for (int i = 0; i < privateKeysList.size(); i++){
			System.arraycopy(privateKeysList.get(i).getDecr_prod(), 0, decrProdBatch, i * BCEConstraints.PRK_DECR_PROD_LENGTH, BCEConstraints.PRK_DECR_PROD_LENGTH);
		}
		int result = BCELibrary.changeDecryptionProduct(system.getGlobalSysParamsURI().getBytes(), 
				system.getChangeDecrProdStartIndex(), system.getChangeDecrProdBatchSize(), system.getAdds(), 
				system.getnAdds(), system.getRems(), system.getnRems(), decrProdBatch, decrProdBatchOut);
		
		if (result != 0)
			return -70;
		
		for (int i =0; i < privateKeysList.size(); i++) {
			System.arraycopy(decrProdBatchOut, i * BCEConstraints.PRK_DECR_PROD_LENGTH, privateKeysList.get(i).getDecr_prod(), 0, BCEConstraints.PRK_DECR_PROD_LENGTH);
			if (system.getRems() != null && isInSet(system.getRems(), privateKeysList.get(i).getIntegerIndex()))
				privateKeysList.get(i).setIsLegal(0);
			if (system.getAdds() != null && isInSet(system.getAdds(), privateKeysList.get(i).getIntegerIndex()))
				privateKeysList.get(i).setIsLegal(1);
		}
		
		// TODO Optimization above
		
		MemoryUtil.immediateEraseBuffers(decrProdBatchOut);
		MemoryUtil.asyncEraseBuffers(decrProdBatch);
		
		return result;
	}
	
	private static boolean isInSet(int[] set, int index) {
		Arrays.sort(set);
		int retPos = Arrays.binarySearch(set, index);
		if (retPos >= 0)
			return true;
		else
			return false;
	}
}
