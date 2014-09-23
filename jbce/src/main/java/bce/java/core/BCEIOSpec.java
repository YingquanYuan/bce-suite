package bce.java.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import bce.jni.utils.BCEConstants;

/**
 * The I/O serialization specifications of BCE system
 *
 * @author <a href="mailto:yingq.yuan@gmail.com">Yingquan Yuan</a>
 *
 */
public interface BCEIOSpec extends BCEConstants {

    /**
     * Serialize the current instance to some output stream
     * @param out Any output stream, such as a file, database or network
     * @throws IOException
     */
    void writeExternal(OutputStream out) throws IOException;

    /**
     * Deserialize the instance from some input stream
     * @param in Any input stream
     * @throws IOException
     * @throws ClassNotFoundException
     * This exception is thrown when the data read cannot be deserailized to an instance
     */
    void readExternal(InputStream in) throws IOException, ClassNotFoundException;
}
