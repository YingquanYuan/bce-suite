package bce.server.daosupport.jdbc;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;

/**
 * 该类用于生成长为48字节的随机字节串，作为AES密钥的原始密钥，该原始并不是最终用于AES加密的密钥
 *
 * @author Yingquan Yuan
 *
 */
public class AESKeySupport {

    /**
     * 工具方法，生成AES原始密钥
     *
     * @return AES原始密钥
     */
    public static byte[] generateKey() {
        BigInteger i = new BigInteger(Long.toHexString(System.nanoTime()), 16);
        Random random = new SecureRandom(i.toByteArray());
        byte[] key = new byte[48];
        random.nextBytes(key);
        return key;
    }

}
