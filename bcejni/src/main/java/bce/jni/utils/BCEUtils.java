package bce.jni.utils;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class BCEUtils {

    private BCEUtils() {}

    /**
     * int转为byte[4]
     * @param i
     * @return
     */
    public final static byte[] intToBytes(int i) {
        byte[] bytes = new byte[4];
        bytes[0] = (byte) (0xff & i);
        bytes[1] = (byte) ((0xff00 & i) >> 8);
        bytes[2] = (byte) ((0xff0000 & i) >> 16);
        bytes[3] = (byte) ((0xff000000 & i) >> 24);
        return bytes;
    }

    /**
     * 将4字节的数组转换成int
     * @param bytes 要转换的字节数组
     * @return 转换后的int
     */
    public final static int bytesToInt(byte[] bytes) {
        return bytesToInt(bytes ,0);
    }

    /**
     * byte[4]转为int
     * @param bytes
     * @return
     */
    public final static int bytesToInt(byte[] bytes, int offset) {
        if (bytes == null || offset + 4 > bytes.length)
            throw new IllegalArgumentException("invalid byte array to convert!");
        int addr = bytes[0 + offset] & 0xFF;
        addr |= ((bytes[1 + offset] << 8) & 0xFF00);
        addr |= ((bytes[2 + offset] << 16) & 0xFF0000);
        addr |= ((bytes[3 + offset] << 24) & 0xFF000000);
        return addr;
    }

    /**
     * 将64位的long转换成8个字节的数组
     * @param l 要转换的long
     * @return 转换后的数组
     */
    public final static byte[] longToBytes(long l) {
        byte[] buffer = new byte[8];
        buffer[0] = (byte) (0xff & (l >> 56));
        buffer[1] = (byte) (0xff & (l >> 48));
        buffer[2] = (byte) (0xff & (l >> 40));
        buffer[3] = (byte) (0xff & (l >> 32));
        buffer[4] = (byte) (0xff & (l >> 24));
        buffer[5] = (byte) (0xff & (l >> 16));
        buffer[6] = (byte) (0xff & (l >> 8));
        buffer[7] = (byte) (0xff & l);
        return buffer;
    }

    /**
     * 将8个字节组装成一个long
     * @param bytes 要转换的字节数组
     * @return 组装后的long
     */
    public final static long bytesToLong(byte[] bytes) {
        return bytesToLong(bytes, 0);
    }

    /**
     * 将8个字节组装成一个long
     * @param bytes 要转换的字节数组
     * @param offset 数组中开始读取的偏移量
     * @return 组装后的long
     */
    public final static long bytesToLong(byte[] bytes, int offset) {
        if (bytes == null || offset + 8 > bytes.length)
            throw new IllegalArgumentException("invalid byte array to convert!");
        return (((long) (bytes[0 + offset] & 0xff) << 56) |
                ((long) (bytes[1 + offset] & 0xff) << 48) |
                ((long) (bytes[2 + offset] & 0xff) << 40) |
                ((long) (bytes[3 + offset] & 0xff) << 32) |
                ((long) (bytes[4 + offset] & 0xff) << 24) |
                ((long) (bytes[5 + offset] & 0xff) << 16) |
                ((long) (bytes[6 + offset] & 0xff) <<  8) |
                ((long) (bytes[7 + offset] & 0xff)));
    }

    /**
     * 打印字节数组的16进制符
     * @param bytes
     */
    public final static void printHex(byte[] bytes) {
        for (int i = 0; i < bytes.length; i++)
            System.out.printf("%02x ", bytes[i]);
        System.out.println();
    }

    /**
     * 使用十六进制编码数据
     * @param data 要编码的数据
     * @return 编码后的字符串
     */
    public final static String hex(byte[] data) {
        return hex(data, 0, data.length);
    }

    /**
     * 使用十六进制编码数据
     * @param data 要编码的数据
     * @param offset 其实偏移 0开始
     * @param count 编码字节的数量
     * @return 编码后的字符串
     */
    public final static String hex(byte[] data, int offset, int count) {
        if (offset < 0)
            throw new IllegalArgumentException(new StringBuilder("invalid offset:").append(offset).toString());
        if (count < 0)
            throw new IllegalArgumentException(new StringBuilder("invalid count:").append(offset).toString());
        if (offset > data.length - count)
            throw new ArrayIndexOutOfBoundsException(offset + count);
        ByteArrayOutputStream out = new ByteArrayOutputStream(data.length + data.length);
        PrintStream writer = new PrintStream(out);
        for (int i = offset; i < offset + count; i++) {
            writer.printf("%02x", data[i]);
        }
        return out.toString();
    }

    /**
     * 将十六进制编码的数据还原
     * @param hex 要还原的字符串
     * @return 还原后的数据
     * @throws NumberFormatException 字符串中包含无法格式化为数字的字符
     */
    public final static byte[] unhex(String hex) {
        char[] tb = hex.toUpperCase().toCharArray();
        List<Byte> bs = new ArrayList<Byte>();
        for (int i = 0; i < tb.length; i += 2) {
            String t = new String();
            t += tb[i];
            t += tb[i + 1];
            int safe = Integer.parseInt(t, 16);
            if (safe > 127)
                safe -= 256;
            bs.add(Byte.parseByte(String.valueOf(safe)));
        }
        byte[] raw = new byte[bs.size()];
        for (int i = 0; i < bs.size(); i++)
            raw[i] = bs.get(i);
        return raw;
    }

}
