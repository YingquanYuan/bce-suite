package bce.java.utils;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;

/**
 * 安全清理内存的工具类
 * @author <a href="mailto:yingq.yuan@gmail.com">Yingquan Yuan</a>
 */
public class MemoryUtil {

    /**
     * 在新的线程中安全擦除内存块，这个方法在执行后会快速返回<br>
     * 此方法与在新线程中执行immediateSecureBuffers方法等效
     * @param buffers 待擦除的内存块
     */
    public final static void asyncEraseBuffers(byte[] ... buffers) {
        new EraseBufferThread(buffers).start();
    }

    /**
     * 立刻安全擦除内存块
     * @param buffers 待擦除的内存块buffers
     */
    public final static void immediateEraseBuffers(byte[] ... buffers) {

        BigInteger i = new BigInteger(Long.toHexString(System.nanoTime()), 16);
        Random random = new SecureRandom(i.toByteArray());
        for (byte[] buffer : buffers) {
            if (buffer != null)
                random.nextBytes(buffer);
        }
    }

}

final class EraseBufferThread extends Thread {

    private byte[][] buffers;

    public EraseBufferThread(byte[] ... buffers) {
        this.buffers = buffers;
    }

    @Override
    public void run() {
        MemoryUtil.immediateEraseBuffers(this.buffers);
    }
}
