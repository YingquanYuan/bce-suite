package bce.jni.test;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import bce.jni.bce.BCELibrary;

public class TestBCENative {

    @Test
    public void test() {
        byte[] privateKey = new byte[BCETestConstants.PRIVATE_KEY_LENGTH];
        byte[][] userKeys = new byte[BCETestConstants.PRIVATE_KEY_GEN_BATCH_SIZE][BCETestConstants.USER_PRIVATE_KEY_SIZE];
        byte[][] userKeys1 = new byte[BCETestConstants.PRIVATE_KEY_GEN_BATCH_SIZE][BCETestConstants.USER_PRIVATE_KEY_SIZE];

        byte[] CT_C0 = new byte[BCETestConstants.CT_C0_LENGTH];
        byte[] CT_C1 = new byte[BCETestConstants.CT_C1_LENGTH];
        byte[] symmetricKey = new byte[BCETestConstants.SYMMETRIC_KEY_LENGTH];
        byte[] symmetricKey1 = new byte[BCETestConstants.SYMMETRIC_KEY_LENGTH];
        byte[] decrProdBatch = new byte[BCETestConstants.CHANGE_DECR_PROD_BATCH_SIZE * BCETestConstants.PRK_DECR_PROD_LENGTH];
        byte[] decrProdBatch_out = new byte[BCETestConstants.CHANGE_DECR_PROD_BATCH_SIZE * BCETestConstants.PRK_DECR_PROD_LENGTH];

        for (int i = 0; i < userKeys.length; i++) {
            userKeys[i] = new byte[BCETestConstants.USER_PRIVATE_KEY_SIZE];
            userKeys1[i] = new byte[BCETestConstants.USER_PRIVATE_KEY_SIZE];
        }
        int retval = BCELibrary.setup(BCETestConstants.CURVE_FILE_NAME.getBytes(), BCETestConstants.USER_NUMBER, BCETestConstants.SYS_PARAMS_FILE_NAME.getBytes(), BCETestConstants.GLOBAL_PARAMS_FILE_NAME.getBytes(), privateKey);
        Assert.assertEquals(0, retval);

        retval = BCELibrary.genPrivateKeys(BCETestConstants.SYS_PARAMS_FILE_NAME.getBytes(), privateKey, BCETestConstants.USER_NUMBER, 1 + 64 * 0, BCETestConstants.PRIVATE_KEY_GEN_BATCH_SIZE, userKeys);
        BCELibrary.genPrivateKeys(BCETestConstants.SYS_PARAMS_FILE_NAME.getBytes(), privateKey, BCETestConstants.USER_NUMBER, 1 + 64 * 1, BCETestConstants.PRIVATE_KEY_GEN_BATCH_SIZE, userKeys1);
        Assert.assertEquals(0, retval);

        retval = BCELibrary.encrypt(BCETestConstants.SYS_PARAMS_FILE_NAME.getBytes(), CT_C0, CT_C1, symmetricKey);
        Assert.assertEquals(0, retval);

        retval = BCELibrary.decrypt(BCETestConstants.GLOBAL_PARAMS_FILE_NAME.getBytes(), userKeys[11], CT_C0, CT_C1, symmetricKey1);
        Assert.assertEquals(0, retval);
        Assert.assertArrayEquals(symmetricKey, symmetricKey1);

        int[] adds = { 5, 6, 7, 10, 11, 12, 13, 14, 15, 16 };
        int nAdds = 10;
        int[] rems = { 5, 6, 7, 8, 10, 11, 12, 13, 14, 15, 16, 18, 19 };
        int nRems = 13;

        retval = BCELibrary.changeEncryptionProduct(BCETestConstants.SYS_PARAMS_FILE_NAME.getBytes(), adds, nAdds, rems, nRems);
        System.out.println("changeEncryptionProduct returns: " + retval);

        for (int i = 0; i < BCETestConstants.CHANGE_DECR_PROD_BATCH_SIZE; i++) {
            System.arraycopy(userKeys[i], BCETestConstants.USER_PRIVATE_KEY_SIZE - BCETestConstants.PRK_DECR_PROD_LENGTH, decrProdBatch, i * BCETestConstants.PRK_DECR_PROD_LENGTH, BCETestConstants.PRK_DECR_PROD_LENGTH);
        }
        retval = BCELibrary.changeDecryptionProduct(BCETestConstants.GLOBAL_PARAMS_FILE_NAME.getBytes(), 1, 64, adds, nAdds, rems, nRems, decrProdBatch, decrProdBatch_out);
        System.out.println("changeDecryptionProduct returns: " + retval);

        for (int i = 0; i < BCETestConstants.CHANGE_DECR_PROD_BATCH_SIZE; i++) {
            System.arraycopy(decrProdBatch_out, i * BCETestConstants.PRK_DECR_PROD_LENGTH, userKeys[i], BCETestConstants.USER_PRIVATE_KEY_SIZE - BCETestConstants.PRK_DECR_PROD_LENGTH, BCETestConstants.PRK_DECR_PROD_LENGTH);
        }

        Arrays.fill(CT_C0, (byte) 0);
        Arrays.fill(CT_C1, (byte) 0);
        Arrays.fill(symmetricKey, (byte) 0);
        Arrays.fill(symmetricKey1, (byte) 0);
        retval = BCELibrary.encrypt(BCETestConstants.SYS_PARAMS_FILE_NAME.getBytes(), CT_C0, CT_C1, symmetricKey);
        System.out.println("encrypt1 returns: " + retval);

        retval = BCELibrary.decrypt(BCETestConstants.GLOBAL_PARAMS_FILE_NAME.getBytes(), userKeys[11], CT_C0, CT_C1, symmetricKey1);
        System.out.println("decrypt1 returns: " + retval);
    }

}
