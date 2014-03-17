package bce.server.junit;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import bce.java.core.BCEEngine;
import bce.java.entities.BCECiphertext;
import bce.java.entities.BCEClientSystem;
import bce.java.entities.BCEEncryptionProduct;
import bce.java.entities.BCEPrivateKey;
import bce.java.entities.BCESymmetricKey;
import bce.java.entities.BCESystem;
import bce.java.entities.BCETransientKey;
import bce.java.utils.BCEConstraints;
import bce.server.dao.BCESystemDAO;
import bce.server.dao.PrivateKeyDAO;
import bce.server.entities.PersistentBCESystem;
import bce.server.entities.PersistentPrivateKey;
import bce.server.util.BCEObjectConverter;
import bce.server.util.SpringUtil;

public class TestBCEServer {

//	@Test
	public void testJBCE() {
		
///////////////////////////////////////init settings///////////////////////////////////////////////////////////////
		int[] adds = { 5, 6, 7, 10, 11, 12, 13, 14, 15, 16 };
		int nAdds = 10;
		int[] rems = { 5, 6, 7, 8, 10, 11, 12, 13, 14, 15, 16, 18, 19 };
		int nRems = 13;

		BCESystem system = new BCESystem();
		system.setCurveParamsURI(BCEConstraints.CURVE_FILE_NAME);
		system.setServerSysParamsURI(BCEConstraints.SYS_PARAMS_FILE_NAME);
		system.setGlobalSysParamsURI(BCEConstraints.GLOBAL_PARAMS_FILE_NAME);
		system.setUserNumber(BCEConstraints.USER_NUMBER);
		system.setKeyFetchStartIndex(1);
		system.setKeyFetchSize(BCEConstraints.PRIVATE_KEY_GEN_BATCH_SIZE);
		system.setAdds(adds);
		system.setnAdds(nAdds);
		system.setRems(rems);
		system.setnRems(nRems);
		system.setChangeDecrProdStartIndex(1);
		system.setChangeDecrProdBatchSize(BCEConstraints.CHANGE_DECR_PROD_BATCH_SIZE);
		BCETransientKey transientKey = BCEEngine.setup(system);

		System.out.println(transientKey);
///////////////////////////////////////init settings///////////////////////////////////////////////////////////////

		
///////////////////////////////////////gen privKeys////////////////////////////////////////////////////////////////
		List<BCEPrivateKey> list1 = BCEEngine.genBCEPrivKeys(system, transientKey);
		system.setKeyFetchStartIndex(65);
		List<BCEPrivateKey> list2 = BCEEngine.genBCEPrivKeys(system, transientKey);
		transientKey.abort();
///////////////////////////////////////gen privKeys////////////////////////////////////////////////////////////////

		
///////////////////////////////////////BCE encryption//////////////////////////////////////////////////////////////
		BCEEncryptionProduct encryptionProduct = BCEEngine.encrypt(system);
		BCECiphertext ciphertext = encryptionProduct.getCiphertext();
		BCESymmetricKey symmetricKey = encryptionProduct.getSymmetricKey();

		System.out.println(ciphertext);
		System.out.println(symmetricKey);
///////////////////////////////////////BCE encryption//////////////////////////////////////////////////////////////

		
///////////////////////////////////////BCE decryption//////////////////////////////////////////////////////////////
		BCEClientSystem clientSystem = new BCEClientSystem();
		clientSystem.setGlobalSysParamsURI(BCEConstraints.GLOBAL_PARAMS_FILE_NAME);

		for (int i = 0; i < system.getKeyFetchSize(); i++) {
			BCESymmetricKey symmetricKeyDec = BCEEngine.decrypt(clientSystem, list1.get(i), ciphertext);
			System.out.println("[" + (i + 1) + "]" + ": " + symmetricKeyDec);
		}
		for (int i = 0; i < system.getKeyFetchSize(); i++) {
			BCESymmetricKey symmetricKeyDec = BCEEngine.decrypt(clientSystem, list2.get(i), ciphertext);
			System.out.println("[" + (64 + i + 1) + "]" + ": " + symmetricKeyDec);
		}
///////////////////////////////////////BCE decryption//////////////////////////////////////////////////////////////

		
///////////////////////////////////////changeEncrProd//////////////////////////////////////////////////////////////
		int result = BCEEngine.changeEncryptionProduct(system);
		System.out.println("changeEncryptionProduct: " + result);
///////////////////////////////////////changeEncrProd//////////////////////////////////////////////////////////////

		
///////////////////////////////////////changeDecrProd//////////////////////////////////////////////////////////////
		result = BCEEngine.changeDecryptionProduct(system, list1);
		System.out.println("changeDecryptionProduct: " + result);
		system.setChangeDecrProdStartIndex(65);
		result = BCEEngine.changeDecryptionProduct(system, list2);
		System.out.println("changeDecryptionProduct: " + result);
///////////////////////////////////////changeDecrProd//////////////////////////////////////////////////////////////

		
///////////////////////////////////////BCE encryption//////////////////////////////////////////////////////////////
		BCEEncryptionProduct encryptionProductAfterChange = BCEEngine.encrypt(system);
		BCECiphertext ciphertextAfterChange = encryptionProductAfterChange.getCiphertext();
		BCESymmetricKey symmetricKeyAfterChange = encryptionProductAfterChange.getSymmetricKey();

		System.out.println(ciphertextAfterChange);
		System.out.println(symmetricKeyAfterChange);
///////////////////////////////////////BCE encryption//////////////////////////////////////////////////////////////

		
///////////////////////////////////////BCE decryption//////////////////////////////////////////////////////////////
		BCEClientSystem newClientSystem = new BCEClientSystem();
		newClientSystem.setGlobalSysParamsURI(BCEConstraints.GLOBAL_PARAMS_FILE_NAME);

		for (int i = 0; i < system.getKeyFetchSize(); i++) {
			BCESymmetricKey symmetricKeyDecAfterChange = BCEEngine.decrypt(newClientSystem, list1.get(i), ciphertextAfterChange);
			System.out.println("[" + (i + 1) + "]" + ": " + symmetricKeyDecAfterChange);
		}
		for (int i = 0; i < system.getKeyFetchSize(); i++) {
			BCESymmetricKey symmetricKeyDecAfterChange = BCEEngine.decrypt(newClientSystem, list2.get(i), ciphertextAfterChange);
			System.out.println("[" + (64 + i + 1) + "]" + ": " + symmetricKeyDecAfterChange);
		}
		// System.out.println(symmetricKeyAfterChange);
///////////////////////////////////////BCE decryption//////////////////////////////////////////////////////////////
		
	}
	
//	@Test
	public void testDB() {
		int[] adds = { 5, 6, 7, 10, 11, 12, 13, 14, 15, 16 };
		int nAdds = 10;
		int[] rems = { 5, 6, 7, 8, 10, 11, 12, 13, 14, 15, 16, 18, 19 };
		int nRems = 13;

		BCESystem system = new BCESystem();
		system.setCurveParamsURI(BCEConstraints.CURVE_FILE_NAME);
		system.setServerSysParamsURI(BCEConstraints.SYS_PARAMS_FILE_NAME);
		system.setGlobalSysParamsURI(BCEConstraints.GLOBAL_PARAMS_FILE_NAME);
		system.setUserNumber(BCEConstraints.USER_NUMBER);
		system.setKeyFetchStartIndex(1);
		system.setKeyFetchSize(BCEConstraints.PRIVATE_KEY_GEN_BATCH_SIZE);
		system.setAdds(adds);
		system.setnAdds(nAdds);
		system.setRems(rems);
		system.setnRems(nRems);
		system.setChangeDecrProdStartIndex(1);
		system.setChangeDecrProdBatchSize(BCEConstraints.CHANGE_DECR_PROD_BATCH_SIZE);
		BCETransientKey transientKey = BCEEngine.setup(system);

		System.out.println(transientKey);
///////////////////////////////////////init settings///////////////////////////////////////////////////////////////

		
///////////////////////////////////////gen privKeys////////////////////////////////////////////////////////////////
		List<BCEPrivateKey> list1 = BCEEngine.genBCEPrivKeys(system, transientKey);
		system.setKeyFetchStartIndex(65);
		List<BCEPrivateKey> list2 = BCEEngine.genBCEPrivKeys(system, transientKey);
		transientKey.abort();
///////////////////////////////////////gen privKeys////////////////////////////////////////////////////////////////

		BCESystemDAO systemDAO = (BCESystemDAO) SpringUtil.getBean("bceSystemDAO");
		PersistentBCESystem persistentBCESystem = BCEObjectConverter.transform(system);
		systemDAO.add(persistentBCESystem);
		
		List<PersistentPrivateKey> list3 = new ArrayList<PersistentPrivateKey>(64);
		List<PersistentPrivateKey> list4 = new ArrayList<PersistentPrivateKey>(64);
		
		for (int i = 0; i < list1.size(); i++) {
			PersistentPrivateKey persistentPrivateKey = BCEObjectConverter.transform(list1.get(i));
			persistentPrivateKey.setBelongedBCESystem(persistentBCESystem);
			list3.add(persistentPrivateKey);
		}
		for (int i = 0; i < list2.size(); i++) {
			PersistentPrivateKey persistentPrivateKey = BCEObjectConverter.transform(list2.get(i));
			persistentPrivateKey.setBelongedBCESystem(persistentBCESystem);
			list4.add(persistentPrivateKey);
		}
		
		PrivateKeyDAO privateKeyDAO = (PrivateKeyDAO) SpringUtil.getBean("privateKeyDAO");
//		privateKeyDAO.addJDBCBatch(list3, 1, list3.size());
//		privateKeyDAO.addJDBCBatch(list4, 1 + list3.size(), list4.size());
		privateKeyDAO.addBatch(list3, 1, list3.size());
		privateKeyDAO.addBatch(list4, 1 + list3.size(), list4.size());
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("success");
	}
	
}
