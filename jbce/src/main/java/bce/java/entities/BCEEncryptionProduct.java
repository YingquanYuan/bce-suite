package bce.java.entities;

import java.io.Serializable;

/**
 * 此类用于封装BCE系统encrypt()之后在服务器端生成的密文和对称密钥，仅仅是个封装器
 *
 * @author <a href="mailto:yingq.yuan@gmail.com">Yingquan Yuan</a>
 *
 */
public class BCEEncryptionProduct implements Serializable {

    private static final long serialVersionUID = -2883922545590528628L;

    private BCECiphertext ciphertext;

    private BCESymmetricKey symmetricKey;

    public BCEEncryptionProduct() {}

    public BCECiphertext getCiphertext() {
        return ciphertext;
    }

    public void setCiphertext(BCECiphertext ciphertext) {
        this.ciphertext = ciphertext;
    }

    public BCESymmetricKey getSymmetricKey() {
        return symmetricKey;
    }

    public void setSymmetricKey(BCESymmetricKey symmetricKey) {
        this.symmetricKey = symmetricKey;
    }

    @Override
    public String toString() {
        return "BCEEncryptionProduct [ciphertext=" + ciphertext
                + ", symmetricKey=" + symmetricKey + "]";
    }
}
