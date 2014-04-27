package bce.java.entities;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Arrays;

import bce.java.exceptions.IllegalSymmKeyAttrSizeException;
import bce.java.exceptions.IllegalSymmKeyByteArraySizeException;
import bce.java.utils.BCEConstraints;
import bce.java.utils.MemoryUtil;

/**
 * 此类为BCE生成的对称密钥的面向对象封装，并提供了一些相关的I/O方法
 *
 * @author <a href="mailto:yingq.yuan@gmail.com">Yingquan Yuan</a>
 *
 */
public class BCESymmetricKey implements Serializable, BCEConstraints {

    private static final long serialVersionUID = 6458899786155936887L;

    /**
     * 对称密钥数据字段，字节数组
     */
    private byte[] symmetricKey;

    public BCESymmetricKey() {}

    /**
     * 直接将BCE对称密钥对象的密钥以字节的形式数据输出到内存
     * @return 输出的字节数组
     */
    public byte[] toBytes() {

        if (symmetricKey == null || symmetricKey.length != BCEConstraints.SYMMETRIC_KEY_LENGTH)
            throw new IllegalSymmKeyAttrSizeException("SymmetricKey toBytes(): symmetricKey null or wrong size");

        byte[] symmKey = new byte[symmetricKey.length];

        System.arraycopy(symmetricKey, 0, symmKey, 0, symmetricKey.length);

        return symmKey;
    }

    /**
     * 从字节数组构建BCE对称密钥对象
     * @param symmKey 包含对称密钥原生数据的字节数组
     * @return 构建好的BCE对称密钥对象
     */
    public static BCESymmetricKey fromBytes(byte[] symmKey) {

        if (symmKey == null)
            throw new IllegalSymmKeyByteArraySizeException("SymmtricKey buildSymmKeyFromBytes(): symmKey null");

        if (symmKey.length != BCEConstraints.SYMMETRIC_KEY_LENGTH)
            throw new IllegalSymmKeyByteArraySizeException("SymmtricKey buildSymmKeyFromBytes(): symmKey wrong size");

        BCESymmetricKey symmetricKey = new BCESymmetricKey();

        byte[] symmKeyBlock = new byte[BCEConstraints.SYMMETRIC_KEY_LENGTH];
        System.arraycopy(symmKey, 0, symmKeyBlock, 0, symmKeyBlock.length);

        symmetricKey.setSymmetricKey(symmKeyBlock);

        return symmetricKey;
    }

    /**
     * 安全擦除存放BCE对称密钥数据的内存
     */
    public void abort() {

        if (this.symmetricKey != null)
            MemoryUtil.immediateEraseBuffers(this.symmetricKey);
    }

    public byte[] getSymmetricKey() {
        return symmetricKey;
    }

    public void setSymmetricKey(byte[] symmetricKey) {
        this.symmetricKey = symmetricKey;
    }

    @Override
    public String toString() {
        return "BCESymmetricKey [symmetricKey=" + Arrays.toString(symmetricKey)
                + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(symmetricKey);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        BCESymmetricKey other = (BCESymmetricKey) obj;
        if (!Arrays.equals(symmetricKey, other.symmetricKey))
            return false;
        return true;
    }

    /**
     * <pre>
     * 将对称密钥持久化到输出流
     * 对称密钥长度：156字节
     * </pre>
     * @see see {@link bce.java.utils.BCEConstraints#writeExternal(OutputStream)}
     */
    @Override
    public void writeExternal(OutputStream out) throws IOException {

        byte[] symmKeyBuffer = new byte[SYMMETRIC_KEY_LENGTH];
        Arrays.fill(symmKeyBuffer, (byte) 0);

        if (this.symmetricKey != null && this.symmetricKey.length == SYMMETRIC_KEY_LENGTH)
            System.arraycopy(this.symmetricKey, 0, symmKeyBuffer, 0, SYMMETRIC_KEY_LENGTH);

        out.write(symmKeyBuffer);
        out.flush();
        MemoryUtil.asyncEraseBuffers(symmKeyBuffer);
    }

    /*
     * (non-Javadoc)
     * @see bce.java.utils.BCEConstraints#readExternal(java.io.InputStream)
     */
    @Override
    public void readExternal(InputStream in) throws IOException, ClassNotFoundException {

        byte[] buffer = new byte[SYMMETRIC_KEY_LENGTH];
        int size = in.read(buffer);

        if (size != buffer.length)
            throw new IOException("Not enough bytes for a symmetric key");
        this.symmetricKey = new byte[SYMMETRIC_KEY_LENGTH];
        System.arraycopy(buffer, 0, this.symmetricKey, 0, SYMMETRIC_KEY_LENGTH);

        MemoryUtil.asyncEraseBuffers(buffer);
    }

}
