package bce.java.entities;

import java.io.Serializable;
import java.util.Arrays;

import bce.java.utils.MemoryUtil;

/**
 * <pre>
 * 此类为BCE系统setup()之后返回的临时密钥的封装，用于传递临时密钥参数，需要及时销毁
 * 注意：使用此类要非常小心，在BCE系统genPrivateKeys()全部完成后，必须手动擦除该类的密钥字段
 * </pre>
 *
 * @author <a href="mailto:yingq.yuan@gmail.com">Yingquan Yuan</a>
 *
 */
public class BCETransientKey implements Serializable {

    private static final long serialVersionUID = 798748625960321832L;

    private byte[] transientKey;

    public BCETransientKey() {}

    /**
     * 安全擦除存放BCE临时密钥的内存
     */
    public void abort() {
        if (this.transientKey != null)
            MemoryUtil.immediateEraseBuffers(this.transientKey);
    }

    public byte[] getTransientKey() {
        return transientKey;
    }

    public void setTransientKey(byte[] transientKey) {
        this.transientKey = transientKey;
    }

    @Override
    public String toString() {
        return "BCETransientKey [transientKey=" + Arrays.toString(transientKey)
                + "]";
    }

}
