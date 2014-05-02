package bce.java.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import bce.jni.utils.BCEConstants;

/**
 * 提供BCE系统需要的一些常量和通用I/O接口的声明
 *
 * @author <a href="mailto:yingq.yuan@gmail.com">Yingquan Yuan</a>
 *
 */
public interface BCEIOSpec extends BCEConstants {

    /**
     * 将实例持久化到输出流
     * @param out 输出流
     * @throws IOException 发生IO异常
     */
    void writeExternal(OutputStream out) throws IOException;

    /**
     * 从输入流中读取实例数据
     * @param in 输入流
     * @throws IOException 发生IO异常
     * @throws ClassNotFoundException 读取的数据无法作为类的数据
     */
    void readExternal(InputStream in) throws IOException, ClassNotFoundException;
}
