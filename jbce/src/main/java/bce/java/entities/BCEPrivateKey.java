package bce.java.entities;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.security.PrivateKey;
import java.util.Arrays;

import bce.java.core.BCEIOSpec;
import bce.java.exceptions.IllegalPrivKeyAttrSizeException;
import bce.java.exceptions.IllegalPrivKeyByteArraySizeException;
import bce.java.utils.MemoryUtil;
import bce.jni.utils.BCEUtils;

/**
 * 此类为BCE私钥的面向对象封装，并提供了一些相关的I/O方法
 *
 * @author <a href="mailto:yingq.yuan@gmail.com">Yingquan Yuan</a>
 *
 */
public class BCEPrivateKey implements Serializable, BCEIOSpec, PrivateKey {

    private static final long serialVersionUID = 8389196035857169080L;

    private byte[] index;

    private byte[] g_i_gamma; // G1组

    private byte[] g_i; // G1组

    private byte[] h_i; // G2组

    private byte[] decr_prod; // G1组

    private int isLegal;

    public BCEPrivateKey() {}

    /**
     * 将BCE私钥各字段拼接，直接以字节数组的形式输出到内存
     *
     * @return 输出的BCE私钥字节数组
     */
    public byte[] toBytes() {

        if (getIntegerIndex() < 1)
            throw new IllegalPrivKeyAttrSizeException("PrivateKey toBytes(): user index out of range");

        if (g_i_gamma == null || g_i_gamma.length != PRK_G_I_GAMMA_LENGTH)
            throw new IllegalPrivKeyAttrSizeException("PrivateKey toBytes(): g_i_gamma null or wrong size");

        if (g_i == null || g_i.length != PRK_G_I_LENGTH)
            throw new IllegalPrivKeyAttrSizeException("PrivateKey toBytes(): g_i null or wrong size");

        if (h_i == null || h_i.length != PRK_H_I_LENGTH)
            throw new IllegalPrivKeyAttrSizeException("PrivateKey toBytes(): h_i null or wrong size");

        if (decr_prod == null || decr_prod.length != PRK_DECR_PROD_LENGTH)
            throw new IllegalPrivKeyAttrSizeException("PrivateKey toBytes(): decr_prod null or wrong size");

        byte[] privateKeyByteBlock = new byte[index.length + g_i_gamma.length + g_i.length + h_i.length + decr_prod.length];

        System.arraycopy(index, 0, privateKeyByteBlock, 0, index.length);
        System.arraycopy(g_i_gamma, 0, privateKeyByteBlock, index.length, g_i_gamma.length);
        System.arraycopy(g_i, 0, privateKeyByteBlock, index.length + g_i_gamma.length, g_i.length);
        System.arraycopy(h_i, 0, privateKeyByteBlock, index.length + g_i_gamma.length + g_i.length, h_i.length);
        System.arraycopy(decr_prod, 0, privateKeyByteBlock, index.length + g_i_gamma.length + g_i.length + h_i.length, decr_prod.length);

        return privateKeyByteBlock;
    }

    /**
     * 从私钥字节数组直接构建BCE私钥对象
     *
     * @param privateKeyByteBlock 私钥各字段按序拼接的字节数组
     * @return 构建好的BCE私钥对象
     */
    public static BCEPrivateKey fromBytes(byte[] privateKeyByteBlock) {

        if (privateKeyByteBlock == null)
            throw new IllegalPrivKeyByteArraySizeException("PrivateKey buildPRKFromBytes(): src byte array null");

        if (privateKeyByteBlock.length != PRK_INDEX_LENGTH + PRK_G_I_GAMMA_LENGTH + PRK_G_I_LENGTH + PRK_H_I_LENGTH + PRK_DECR_PROD_LENGTH)
            throw new IllegalPrivKeyByteArraySizeException("PrivateKey buildPRKFromBytes(): src byte array wrong size");

        BCEPrivateKey privateKey = new BCEPrivateKey();
        byte[] indexBuffer = new byte[PRK_INDEX_LENGTH];
        byte[] g_i_gammaBuffer = new byte[PRK_G_I_GAMMA_LENGTH];
        byte[] g_iBuffer = new byte[PRK_G_I_LENGTH];
        byte[] h_iBuffer = new byte[PRK_H_I_LENGTH];
        byte[] decr_prodBuffer = new byte[PRK_DECR_PROD_LENGTH];

        System.arraycopy(privateKeyByteBlock, 0, indexBuffer, 0, indexBuffer.length);
        System.arraycopy(privateKeyByteBlock, indexBuffer.length, g_i_gammaBuffer, 0, g_i_gammaBuffer.length);
        System.arraycopy(privateKeyByteBlock, indexBuffer.length + g_i_gammaBuffer.length, g_iBuffer, 0, g_iBuffer.length);
        System.arraycopy(privateKeyByteBlock, indexBuffer.length + g_i_gammaBuffer.length + g_iBuffer.length, h_iBuffer, 0, h_iBuffer.length);
        System.arraycopy(privateKeyByteBlock, indexBuffer.length + g_i_gammaBuffer.length + g_iBuffer.length + h_iBuffer.length, decr_prodBuffer, 0, decr_prodBuffer.length);

        privateKey.setIndex(indexBuffer);
        privateKey.setG_i_gamma(g_i_gammaBuffer);
        privateKey.setG_i(g_iBuffer);
        privateKey.setH_i(h_iBuffer);
        privateKey.setDecr_prod(decr_prodBuffer);

        return privateKey;
    }

    /**
     * 安全擦除存放BCE私钥数据的内存
     */
    public void abort() {

        if (this.index != null)
            MemoryUtil.immediateEraseBuffers(this.index);
        if (this.g_i_gamma != null)
            MemoryUtil.immediateEraseBuffers(this.g_i_gamma);
        if (this.g_i != null)
            MemoryUtil.immediateEraseBuffers(this.g_i);
        if (this.h_i != null)
            MemoryUtil.immediateEraseBuffers(this.h_i);
        if (this.decr_prod != null)
            MemoryUtil.immediateEraseBuffers(this.decr_prod);
    }

    public int getIntegerIndex() {
        return BCEUtils.bytesToInt(index);
    }

    public byte[] getIndex() {
        return index;
    }

    public void setIndex(byte[] index) {
        this.index = index;
    }

    public byte[] getG_i_gamma() {
        return g_i_gamma;
    }

    public void setG_i_gamma(byte[] g_i_gamma) {
        this.g_i_gamma = g_i_gamma;
    }

    public byte[] getG_i() {
        return g_i;
    }

    public void setG_i(byte[] g_i) {
        this.g_i = g_i;
    }

    public byte[] getH_i() {
        return h_i;
    }

    public void setH_i(byte[] h_i) {
        this.h_i = h_i;
    }

    public byte[] getDecr_prod() {
        return decr_prod;
    }

    public void setDecr_prod(byte[] decr_prod) {
        this.decr_prod = decr_prod;
    }

    public int getIsLegal() {
        return isLegal;
    }

    public void setIsLegal(int isLegal) {
        this.isLegal = isLegal;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(decr_prod);
        result = prime * result + Arrays.hashCode(g_i);
        result = prime * result + Arrays.hashCode(g_i_gamma);
        result = prime * result + Arrays.hashCode(h_i);
        result = prime * result + Arrays.hashCode(index);
        result = prime * result + isLegal;
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
        BCEPrivateKey other = (BCEPrivateKey) obj;
        if (!Arrays.equals(decr_prod, other.decr_prod))
            return false;
        if (!Arrays.equals(g_i, other.g_i))
            return false;
        if (!Arrays.equals(g_i_gamma, other.g_i_gamma))
            return false;
        if (!Arrays.equals(h_i, other.h_i))
            return false;
        if (!Arrays.equals(index, other.index))
            return false;
        if (isLegal != other.isLegal)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "BCEPrivateKey [index=" + Arrays.toString(index)
                + ", g_i_gamma=" + Arrays.toString(g_i_gamma) + ", g_i="
                + Arrays.toString(g_i) + ", h_i=" + Arrays.toString(h_i)
                + ", decr_prod=" + Arrays.toString(decr_prod) + ", isLegal="
                + isLegal + "]";
    }

    @Override
    public String getAlgorithm() {
        return "BCE";
    }

    @Override
    public String getFormat() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public byte[] getEncoded() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * <pre>
     * 写入顺序：
     * index，4字节
     * g_i_gamma，G1组，52字节
     * g_i，G1组，52字节
     * h_i，G2组，156字节
     * decr_prod，G1组，52字节
     * </pre>
     * @see see {@link bce.java.utils.BCEIOSpec#writeExternal(OutputStream)}
     */
    @Override
    public void writeExternal(OutputStream out) throws IOException {
        byte[] encoded = getEncoded();
        if (encoded != null) {
            out.write(encoded);
            out.flush();
            return;
        }

        // 不编码，直接将私钥写入输出流
        byte[] iBuffer = new byte[PRK_INDEX_LENGTH];
        Arrays.fill(iBuffer, (byte) 0);
        if (this.index != null && this.index.length == PRK_INDEX_LENGTH)
            System.arraycopy(this.index, 0, iBuffer, 0, this.index.length);
        out.write(iBuffer);

        byte[] gIGammaBuffer = new byte[PRK_G_I_GAMMA_LENGTH];
        Arrays.fill(gIGammaBuffer, (byte) 0);
        if (this.g_i_gamma != null && this.g_i_gamma.length == PRK_G_I_GAMMA_LENGTH)
            System.arraycopy(this.g_i_gamma, 0, gIGammaBuffer, 0, this.g_i_gamma.length);
        out.write(gIGammaBuffer);

        byte[] gIBuffer = new byte[PRK_G_I_LENGTH];
        Arrays.fill(gIBuffer, (byte) 0);
        if (this.g_i != null && this.g_i.length == PRK_G_I_LENGTH)
            System.arraycopy(this.g_i, 0, gIBuffer, 0, this.g_i.length);
        out.write(gIBuffer);

        byte[] hIBuffer = new byte[PRK_H_I_LENGTH];
        Arrays.fill(hIBuffer, (byte) 0);
        if (this.h_i != null && this.h_i.length == PRK_H_I_LENGTH)
            System.arraycopy(this.h_i, 0, hIBuffer, 0, this.h_i.length);
        out.write(hIBuffer);

        byte[] decrProdBuffer = new byte[PRK_DECR_PROD_LENGTH];
        Arrays.fill(decrProdBuffer, (byte) 0);
        if (this.decr_prod != null && this.decr_prod.length == PRK_DECR_PROD_LENGTH)
            System.arraycopy(this.decr_prod, 0, decrProdBuffer, 0, this.decr_prod.length);
        out.write(decrProdBuffer);

        out.flush();
        MemoryUtil.asyncEraseBuffers(iBuffer, hIBuffer);
        MemoryUtil.immediateEraseBuffers(gIGammaBuffer, gIBuffer, decrProdBuffer);
    }

    /*
     * (non-Javadoc)
     * @see bce.java.utils.BCEConstraints#readExternal(java.io.InputStream)
     */
    @Override
    public void readExternal(InputStream in) throws IOException, ClassNotFoundException {

        // TODO 判断私钥是否是编码过的
        byte[] buffer = new byte[PRK_INDEX_LENGTH + PRK_G_I_GAMMA_LENGTH + PRK_G_I_LENGTH + PRK_H_I_LENGTH + PRK_DECR_PROD_LENGTH];

        int keySize = in.read(buffer);
        if (keySize != buffer.length)
            throw new IOException("Not enough bytes for a PrivateKey");
        this.index = new byte[PRK_INDEX_LENGTH];
        this.g_i_gamma = new byte[PRK_G_I_GAMMA_LENGTH];
        this.g_i = new byte[PRK_G_I_LENGTH];
        this.h_i = new byte[PRK_H_I_LENGTH];
        this.decr_prod = new byte[PRK_DECR_PROD_LENGTH];

        System.arraycopy(buffer, 0, this.index, 0, PRK_INDEX_LENGTH);
        System.arraycopy(buffer, PRK_INDEX_LENGTH, this.g_i_gamma, 0, PRK_G_I_GAMMA_LENGTH);
        System.arraycopy(buffer, PRK_INDEX_LENGTH + PRK_G_I_GAMMA_LENGTH, this.g_i, 0, PRK_G_I_LENGTH);
        System.arraycopy(buffer, PRK_INDEX_LENGTH + PRK_G_I_GAMMA_LENGTH + PRK_G_I_LENGTH, this.h_i, 0, PRK_H_I_LENGTH);
        System.arraycopy(buffer, PRK_INDEX_LENGTH + PRK_G_I_GAMMA_LENGTH + PRK_G_I_LENGTH + PRK_H_I_LENGTH, this.decr_prod, 0, PRK_DECR_PROD_LENGTH);

        MemoryUtil.asyncEraseBuffers(buffer);
    }

}
