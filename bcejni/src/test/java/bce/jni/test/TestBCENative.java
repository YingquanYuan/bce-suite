package bce.jni.test;

import java.io.File;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import bce.jni.bce.BCELibrary;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestBCENative implements BCETestConstants {

    private static byte[][] userKeys, userKeys1;
    private static byte[] privateKey;
    private static byte[] CT_C0, CT_C1;
    private static byte[] symmetricKey, symmetricKey1;
    private static byte[] decrProdBatch, decrProdBatch_out;

    private static int[] adds = { 5, 6, 7, 10, 11, 12, 13, 14, 15, 16 };
    private static int[] rems = { 5, 6, 7, 8, 10, 11, 12, 13, 14, 15, 16, 18, 19 };

    @BeforeClass
    public static void setUp() {

        privateKey = new byte[PRIVATE_KEY_LENGTH];

        userKeys = new byte[PRIVATE_KEY_GEN_BATCH_SIZE][USER_PRIVATE_KEY_SIZE];
        for (int i = 0; i < userKeys.length; i++) {
            userKeys[i] = new byte[USER_PRIVATE_KEY_SIZE];
        }

        userKeys1 = new byte[PRIVATE_KEY_GEN_BATCH_SIZE][USER_PRIVATE_KEY_SIZE];
        for (int i = 0; i < userKeys1.length; i++) {
            userKeys1[i] = new byte[USER_PRIVATE_KEY_SIZE];
        }

        CT_C0 = new byte[CT_C0_LENGTH];
        CT_C1 = new byte[CT_C1_LENGTH];
        symmetricKey = new byte[SYMMETRIC_KEY_LENGTH];
        symmetricKey1 = new byte[SYMMETRIC_KEY_LENGTH];
        decrProdBatch = new byte[CHANGE_DECR_PROD_BATCH_SIZE * PRK_DECR_PROD_LENGTH];
        decrProdBatch_out = new byte[CHANGE_DECR_PROD_BATCH_SIZE * PRK_DECR_PROD_LENGTH];

    }

    @Test
    public void test1BCESetup() {
        int retval = -1;
        retval = BCELibrary.setup(CURVE_FILE_NAME.getBytes(), USER_NUMBER,
                SYS_PARAMS_FILE_NAME.getBytes(), GLOBAL_PARAMS_FILE_NAME.getBytes(), privateKey);
        Assert.assertEquals(0, retval);
        Assert.assertTrue(new File(SYS_PARAMS_FILE_NAME).exists());
        Assert.assertTrue(new File(GLOBAL_PARAMS_FILE_NAME).exists());
    }

    @Test
    public void test2BCEGenPrivateKeys() {
        int retval = -1;
        retval = BCELibrary.genPrivateKeys(SYS_PARAMS_FILE_NAME.getBytes(),
                privateKey, USER_NUMBER, 1 + 64 * 0, PRIVATE_KEY_GEN_BATCH_SIZE, userKeys);
        Assert.assertEquals(0, retval);
        retval = BCELibrary.genPrivateKeys(SYS_PARAMS_FILE_NAME.getBytes(),
                privateKey, USER_NUMBER, 1 + 64 * 1, PRIVATE_KEY_GEN_BATCH_SIZE, userKeys1);
        Assert.assertEquals(0, retval);
    }

    @Test
    public void test3EncryptDecrypt() {
        int retval = -1;
        // encrypt
        retval = BCELibrary.encrypt(SYS_PARAMS_FILE_NAME.getBytes(),
                CT_C0, CT_C1, symmetricKey);
        Assert.assertEquals(0, retval);

        // decrypt
        retval = BCELibrary.decrypt(GLOBAL_PARAMS_FILE_NAME.getBytes(),
                userKeys[11], CT_C0, CT_C1, symmetricKey1);
        Assert.assertEquals(0, retval);

        Assert.assertArrayEquals(symmetricKey, symmetricKey1);
    }



    @Test
    public void test4ChangeEncryptDecryptProduction() {
        int retval = -1;

        // SYS_PARAMS_FILE_NAME changed
        retval = BCELibrary.changeEncryptionProduct(SYS_PARAMS_FILE_NAME.getBytes(),
                adds, adds.length, rems, rems.length);
        Assert.assertEquals(0, retval);

        // copy original userKeys to decrProdBatch for passing into native lib
        for (int i = 0; i < CHANGE_DECR_PROD_BATCH_SIZE; i++) {
            System.arraycopy(userKeys[i], USER_PRIVATE_KEY_SIZE - PRK_DECR_PROD_LENGTH,
                    decrProdBatch, i * PRK_DECR_PROD_LENGTH, PRK_DECR_PROD_LENGTH);
        }

        // change decrypt production based on the original ones
        retval = BCELibrary.changeDecryptionProduct(GLOBAL_PARAMS_FILE_NAME.getBytes(),
                1, 64, adds, adds.length, rems, rems.length, decrProdBatch, decrProdBatch_out);
        Assert.assertEquals(0, retval);

        // copy back the decrProdBatch_out
        for (int i = 0; i < CHANGE_DECR_PROD_BATCH_SIZE; i++) {
            System.arraycopy(decrProdBatch_out, i * PRK_DECR_PROD_LENGTH, userKeys[i],
                    USER_PRIVATE_KEY_SIZE - PRK_DECR_PROD_LENGTH, PRK_DECR_PROD_LENGTH);
        }
    }

    @Test
    public void test5EncryptDecryptAfterChange() {
        int retval = -1;

        // clean memory
        Arrays.fill(CT_C0, (byte) 0);
        Arrays.fill(CT_C1, (byte) 0);
        Arrays.fill(symmetricKey, (byte) 0);
        Arrays.fill(symmetricKey1, (byte) 0);

        // redo encrypt/decrypt
        retval = BCELibrary.encrypt(SYS_PARAMS_FILE_NAME.getBytes(), CT_C0, CT_C1, symmetricKey);
        Assert.assertEquals(0, retval);

        retval = BCELibrary.decrypt(GLOBAL_PARAMS_FILE_NAME.getBytes(), userKeys[11], CT_C0, CT_C1, symmetricKey1);
        Assert.assertEquals(0, retval);
    }

}
