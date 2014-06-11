package jbce.jni.test;

import java.util.Arrays;
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
import bce.jni.bce.BCELibrary;
import bce.jni.utils.BCEUtils;

public class TestJbce implements BCETestConstants {

//    @Test
    public void testGeneric() {
        byte[] privateKey = new byte[PRIVATE_KEY_LENGTH];
        byte[][] userKeys = new byte[PRIVATE_KEY_GEN_BATCH_SIZE][USER_PRIVATE_KEY_SIZE];
        byte[][] userKeys1 = new byte[PRIVATE_KEY_GEN_BATCH_SIZE][USER_PRIVATE_KEY_SIZE];

        byte[] CT_C0 = new byte[CT_C0_LENGTH];
        byte[] CT_C1 = new byte[CT_C1_LENGTH];
        byte[] symmetricKey = new byte[SYMMETRIC_KEY_LENGTH];
        byte[] symmetricKey1 = new byte[SYMMETRIC_KEY_LENGTH];
        byte[] decrProdBatch = new byte[CHANGE_DECR_PROD_BATCH_SIZE * PRK_DECR_PROD_LENGTH];
        byte[] decrProdBatch_out = new byte[CHANGE_DECR_PROD_BATCH_SIZE * PRK_DECR_PROD_LENGTH];

        for (int i = 0; i < userKeys.length; i++) {
            userKeys[i] = new byte[USER_PRIVATE_KEY_SIZE];
            userKeys1[i] = new byte[USER_PRIVATE_KEY_SIZE];
        }
        int retval = BCELibrary.setup(CURVE_FILE_NAME.getBytes(), USER_NUMBER, SYS_PARAMS_FILE_NAME.getBytes(), GLOBAL_PARAMS_FILE_NAME.getBytes(), privateKey);
        System.out.println("setup returns: " + retval);
        BCEUtils.printHex(privateKey);

        retval = BCELibrary.genPrivateKeys(SYS_PARAMS_FILE_NAME.getBytes(), privateKey, USER_NUMBER, 1 + 64 * 0, PRIVATE_KEY_GEN_BATCH_SIZE, userKeys);
        BCELibrary.genPrivateKeys(SYS_PARAMS_FILE_NAME.getBytes(), privateKey, USER_NUMBER, 1 + 64 * 1, PRIVATE_KEY_GEN_BATCH_SIZE, userKeys1);
        System.out.println("genPrivateKeys returns: " + retval);
        byte[] src = new byte[4];
        System.arraycopy(userKeys1[63], 0, src, 0, 4);
        System.out.println(BCEUtils.bytesToInt(src));

        retval = BCELibrary.encrypt(SYS_PARAMS_FILE_NAME.getBytes(), CT_C0, CT_C1, symmetricKey);
        System.out.println("encrypt returns: " + retval);

        retval = BCELibrary.decrypt(GLOBAL_PARAMS_FILE_NAME.getBytes(), userKeys[16], CT_C0, CT_C1, symmetricKey1);
        System.out.println("decrypt returns: " + retval);

        int[] adds = { 5, 6, 7, 10, 11, 12, 13, 14, 15, 16 };
        int nAdds = 10;
        int[] rems = { 5, 6, 7, 8, 10, 11, 12, 13, 14, 15, 16, 18, 19 };
        int nRems = 13;

        retval = BCELibrary.changeEncryptionProduct(SYS_PARAMS_FILE_NAME.getBytes(), adds, nAdds, rems, nRems);
        System.out.println("changeEncryptionProduct returns: " + retval);

        for (int i = 0; i < CHANGE_DECR_PROD_BATCH_SIZE; i++) {
            System.arraycopy(userKeys[i], USER_PRIVATE_KEY_SIZE - PRK_DECR_PROD_LENGTH, decrProdBatch, i * PRK_DECR_PROD_LENGTH, PRK_DECR_PROD_LENGTH);
        }
        retval = BCELibrary.changeDecryptionProduct(GLOBAL_PARAMS_FILE_NAME.getBytes(), 1, 64, adds, nAdds, rems, nRems, decrProdBatch, decrProdBatch_out);
        System.out.println("changeDecryptionProduct returns: " + retval);

        for (int i = 0; i < CHANGE_DECR_PROD_BATCH_SIZE; i++) {
            System.arraycopy(decrProdBatch_out, i * PRK_DECR_PROD_LENGTH, userKeys[i], USER_PRIVATE_KEY_SIZE - PRK_DECR_PROD_LENGTH, PRK_DECR_PROD_LENGTH);
        }

        Arrays.fill(CT_C0, (byte) 0);
        Arrays.fill(CT_C1, (byte) 0);
        Arrays.fill(symmetricKey, (byte) 0);
        Arrays.fill(symmetricKey1, (byte) 0);
        retval = BCELibrary.encrypt(SYS_PARAMS_FILE_NAME.getBytes(), CT_C0, CT_C1, symmetricKey);
        System.out.println("encrypt1 returns: " + retval);

        retval = BCELibrary.decrypt(GLOBAL_PARAMS_FILE_NAME.getBytes(), userKeys[16], CT_C0, CT_C1, symmetricKey1);
        System.out.println("decrypt1 returns: " + retval);
    }

//    @Test
    public void testOOP() {

/////////////////////////////////////////init settings///////////////////////////////////////////////////////////////
        int[] adds = { 5, 6, 7, 10, 11, 12, 13, 14, 15, 16 };
        int nAdds = 10;
        int[] rems = { 5, 6, 7, 8, 10, 11, 12, 13, 14, 15, 16, 18, 19 };
        int nRems = 13;

        BCESystem system = new BCESystem();
        system.setCurveParamsURI(CURVE_FILE_NAME);
        system.setServerSysParamsURI(SYS_PARAMS_FILE_NAME);
        system.setGlobalSysParamsURI(GLOBAL_PARAMS_FILE_NAME);
        system.setUserNumber(USER_NUMBER);
        system.setKeyFetchStartIndex(1);
        system.setKeyFetchSize(PRIVATE_KEY_GEN_BATCH_SIZE);
        system.setAdds(adds);
        system.setnAdds(nAdds);
        system.setRems(rems);
        system.setnRems(nRems);
        system.setChangeDecrProdStartIndex(1);
        system.setChangeDecrProdBatchSize(CHANGE_DECR_PROD_BATCH_SIZE);
        BCETransientKey transientKey = BCEEngine.setup(system);

/////////////////////////////////////////init settings///////////////////////////////////////////////////////////////


/////////////////////////////////////////gen privKeys////////////////////////////////////////////////////////////////
        List<BCEPrivateKey> list1 = BCEEngine.genBCEPrivKeys(system, transientKey);
        system.setKeyFetchStartIndex(65);
        List<BCEPrivateKey> list2 = BCEEngine.genBCEPrivKeys(system, transientKey);
        transientKey.abort();
/////////////////////////////////////////gen privKeys////////////////////////////////////////////////////////////////


/////////////////////////////////////////BCE encryption//////////////////////////////////////////////////////////////
        BCEEncryptionProduct encryptionProduct = BCEEngine.encrypt(system);
        BCECiphertext ciphertext = encryptionProduct.getCiphertext();
        BCESymmetricKey symmetricKey = encryptionProduct.getSymmetricKey();

        System.out.println(ciphertext);
        System.out.println(symmetricKey);
/////////////////////////////////////////BCE encryption//////////////////////////////////////////////////////////////

/////////////////////////////////////////BCE decryption//////////////////////////////////////////////////////////////
        BCEClientSystem clientSystem = new BCEClientSystem();
        clientSystem.setGlobalSysParamsURI(GLOBAL_PARAMS_FILE_NAME);

        for (int i = 0; i < system.getKeyFetchSize(); i++) {
            BCESymmetricKey symmetricKeyDec = BCEEngine.decrypt(clientSystem, list1.get(i), ciphertext);
            // System.out.print("[" + (i + 1) + "] ");
            System.out.println("[" + (i + 1) + "]" + ": " + symmetricKeyDec);
        }
        System.out.println();
        for (int i = 0; i < system.getKeyFetchSize(); i++) {
            BCESymmetricKey symmetricKeyDec = BCEEngine.decrypt(clientSystem, list2.get(i), ciphertext);
            // System.out.print("[" + (i + 1) + "] ");
            System.out.println("[" + (64 + i + 1) + "]" + ": " + symmetricKeyDec);
        }
        System.out.println();
/////////////////////////////////////////BCE decryption//////////////////////////////////////////////////////////////


/////////////////////////////////////////changeEncrProd//////////////////////////////////////////////////////////////
        int result = BCEEngine.changeEncryptionProduct(system);
        System.out.println("changeEncryptionProduct: " + result);
/////////////////////////////////////////changeEncrProd//////////////////////////////////////////////////////////////


/////////////////////////////////////////changeDecrProd//////////////////////////////////////////////////////////////
        result = BCEEngine.changeDecryptionProduct(system, list1);
        System.out.println("changeDecryptionProduct: " + result);
        system.setChangeDecrProdStartIndex(65);
        result = BCEEngine.changeDecryptionProduct(system, list2);
        System.out.println("changeDecryptionProduct: " + result);
/////////////////////////////////////////changeDecrProd//////////////////////////////////////////////////////////////


/////////////////////////////////////////BCE encryption//////////////////////////////////////////////////////////////
        BCEEncryptionProduct encryptionProductAfterChange = BCEEngine.encrypt(system);
        BCECiphertext ciphertextAfterChange = encryptionProductAfterChange.getCiphertext();
        BCESymmetricKey symmetricKeyAfterChange = encryptionProductAfterChange.getSymmetricKey();

        System.out.println(ciphertextAfterChange);
        System.out.println(symmetricKeyAfterChange);
/////////////////////////////////////////BCE encryption//////////////////////////////////////////////////////////////


/////////////////////////////////////////BCE decryption//////////////////////////////////////////////////////////////
        BCEClientSystem newClientSystem = new BCEClientSystem();
        newClientSystem.setGlobalSysParamsURI(GLOBAL_PARAMS_FILE_NAME);

        for (int i = 0; i < system.getKeyFetchSize(); i++) {
            BCESymmetricKey symmetricKeyDecAfterChange = BCEEngine.decrypt(newClientSystem, list1.get(i), ciphertextAfterChange);
            System.out.println("[" + (i + 1) + "]" + ": " + symmetricKeyDecAfterChange);
        }
        System.out.println();
        for (int i = 0; i < system.getKeyFetchSize(); i++) {
            BCESymmetricKey symmetricKeyDecAfterChange = BCEEngine.decrypt(newClientSystem, list2.get(i), ciphertextAfterChange);
            System.out.println("[" + (64 + i + 1) + "]" + ": " + symmetricKeyDecAfterChange);
        }
        System.out.println();
//		System.out.println(symmetricKeyAfterChange);
/////////////////////////////////////////BCE decryption//////////////////////////////////////////////////////////////
    }
}
