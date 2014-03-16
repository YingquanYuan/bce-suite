package bce.java.io;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 * 该类重写了java.io.ByteArrayInputStream类，在close()方法中加入了安全擦出内存数据的功能
 * 
 * @author robins
 *
 */
public class SecureByteArrayInputStream extends ByteArrayInputStream {

	/**
	 * @see see {@link java.io.ByteArrayInputStream#ByteArrayInputStream(byte buf[])}
	 * @param buf
	 */
	public SecureByteArrayInputStream(byte[] buf) {
		super(buf);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.io.ByteArrayInputStream#close()
	 */
	@Override
	public void close() throws IOException {
		super.close();
		if (buf != null) {
			Arrays.fill(buf, (byte) 0);
		}
	}

}
