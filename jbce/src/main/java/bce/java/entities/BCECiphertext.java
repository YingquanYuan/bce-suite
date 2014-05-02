package bce.java.entities;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Arrays;

import bce.java.core.BCEIOSpec;
import bce.java.exceptions.IllegalCTAttrSizeException;
import bce.java.exceptions.IllegalCTByteArraySizeException;
import bce.java.utils.MemoryUtil;

/**
 * 此类为BCE系统密文头的面向对象封装，并提供了一些相关的I/O方法
 *
 * @author <a href="mailto:yingq.yuan@gmail.com">Yingquan Yuan</a>
 *
 */
public class BCECiphertext implements Serializable, BCEIOSpec {

    private static final long serialVersionUID = 6323148422446026311L;

    /**
     * BCE密文头第一段数据C0
     */
    private byte[] CT_C0;

    /**
     * BCE密文头第二段数据C1
     */
    private byte[] CT_C1;

    public BCECiphertext() {}

    /**
     * 将BCE密文头第一段直接转换为字节数组输出到内存
     *
     * @return 输出的C0字节数组
     */
    public byte[] ctC0ToBytes() {

        if (CT_C0 == null || CT_C0.length != BCEIOSpec.CT_C0_LENGTH)
            throw new IllegalCTAttrSizeException("Ciphertext ctC0ToBytes(): CT_C0 null or wrong size");

        byte[] ctC0ByteBlock = new byte[CT_C0.length];

        System.arraycopy(CT_C0, 0, ctC0ByteBlock, 0, CT_C0.length);

        return ctC0ByteBlock;
    }

    /**
     * 将BCE密文头第二段直接转换为字节数组输出到内存
     *
     * @return 输出的C1字节数组
     */
    public byte[] ctC1ToBytes() {

        if (CT_C1 == null || CT_C1.length != BCEIOSpec.CT_C1_LENGTH)
            throw new IllegalCTAttrSizeException("Ciphertext ctC1ToBytes(): CT_C1 null or wrong size");

        byte[] ctC1ByteBlock = new byte[CT_C1.length];

        System.arraycopy(CT_C1, 0, ctC1ByteBlock, 0, CT_C1.length);

        return ctC1ByteBlock;
    }

    /**
     * 从字节数组构建BCE密文对象
     *
     * @param ctC0 密文头第一段原生数据
     * @param ctC1 密文头第二段原生数据
     * @return 构建好的BCE密文对象
     */
    public static BCECiphertext fromBytes(byte[] ctC0, byte[] ctC1) {

        if (ctC0 == null)
            throw new IllegalCTByteArraySizeException("Ciphertext buildCTFromBytes(): src byte array ctC0 null");

        if (ctC0.length != BCEIOSpec.CT_C0_LENGTH)
            throw new IllegalCTByteArraySizeException("Ciphertext buildCTFromBytes(): src byte array ctC0 wrong size");

        if (ctC1 == null)
            throw new IllegalCTByteArraySizeException("Ciphertext buildCTFromBytes(): src byte array ctC1 null");

        if (ctC1.length != BCEIOSpec.CT_C1_LENGTH)
            throw new IllegalCTByteArraySizeException("Ciphertext buildCTFromBytes(): src byte array ctC1 wrong size");

        BCECiphertext ct = new BCECiphertext();

        byte[] c0 = new byte[BCEIOSpec.CT_C0_LENGTH];
        byte[] c1 = new byte[BCEIOSpec.CT_C1_LENGTH];

        System.arraycopy(ctC0, 0, c0, 0, c0.length);
        System.arraycopy(ctC1, 0, c1, 0, c1.length);

        ct.setCT_C0(c0);
        ct.setCT_C1(c1);

        return ct;
    }

    /**
     * 安全擦除BCE存放密文数据的内存
     */
    public void abort() {

        if (this.CT_C0 != null)
            MemoryUtil.immediateEraseBuffers(this.CT_C0);
        if (this.CT_C1 != null)
            MemoryUtil.immediateEraseBuffers(this.CT_C1);
    }

    public byte[] getCT_C0() {
        return CT_C0;
    }

    public void setCT_C0(byte[] cT_C0) {
        CT_C0 = cT_C0;
    }

    public byte[] getCT_C1() {
        return CT_C1;
    }

    public void setCT_C1(byte[] cT_C1) {
        CT_C1 = cT_C1;
    }

    @Override
    public String toString() {
        return "BCECiphertext [CT_C0=" + Arrays.toString(CT_C0) + ", CT_C1="
                + Arrays.toString(CT_C1) + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(CT_C0);
        result = prime * result + Arrays.hashCode(CT_C1);
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
        BCECiphertext other = (BCECiphertext) obj;
        if (!Arrays.equals(CT_C0, other.CT_C0))
            return false;
        if (!Arrays.equals(CT_C1, other.CT_C1))
            return false;
        return true;
    }

    /**
     * <pre>
     * 将密文持久化输出流
     * 密文长度：208
     * </pre>
     * @see see {@link bce.java.utils.BCEIOSpec#writeExternal(OutputStream)}
     */
    @Override
    public void writeExternal(OutputStream out) throws IOException {

        byte[] c0Buffer = new byte[CT_C0_LENGTH];
        Arrays.fill(c0Buffer, (byte) 0);
        if (this.CT_C0 != null && this.CT_C0.length == CT_C0_LENGTH)
            System.arraycopy(this.CT_C0, 0, c0Buffer, 0, CT_C0_LENGTH);
        out.write(c0Buffer);

        byte[] c1Buffer = new byte[CT_C1_LENGTH];
        Arrays.fill(c1Buffer, (byte) 0);
        if (this.CT_C1 != null && this.CT_C1.length == CT_C1_LENGTH)
            System.arraycopy(this.CT_C1, 0, c1Buffer, 0, CT_C1_LENGTH);
        out.write(c1Buffer);

        out.flush();
        MemoryUtil.asyncEraseBuffers(c1Buffer);
        MemoryUtil.immediateEraseBuffers(c0Buffer);
    }

    /*
     * (non-Javadoc)
     * @see bce.java.utils.BCEConstraints#readExternal(java.io.InputStream)
     */
    @Override
    public void readExternal(InputStream in) throws IOException, ClassNotFoundException {

        byte[] buffer = new byte[CT_C0_LENGTH + CT_C1_LENGTH];
        int size = in.read(buffer);

        if (size != buffer.length)
            throw new IOException("Not enough bytes for a Ciphertext");

        this.CT_C0 = new byte[CT_C0_LENGTH];
        System.arraycopy(buffer, 0, this.CT_C0, 0, CT_C0_LENGTH);
        this.CT_C1 = new byte[CT_C1_LENGTH];
        System.arraycopy(buffer, CT_C0_LENGTH, this.CT_C1, 0, CT_C1_LENGTH);

        MemoryUtil.asyncEraseBuffers(buffer);
    }

}
