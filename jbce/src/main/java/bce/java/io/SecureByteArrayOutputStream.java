package bce.java.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 * 该类重写了java.io.ByteArrayInputStream类，在close()方法中加入了安全擦出内存数据的功能
 * 
 * @author robins
 *
 */
public class SecureByteArrayOutputStream extends ByteArrayOutputStream {
	
	/*
	 * (non-Javadoc)
	 * @see java.io.ByteArrayOutputStream#close()
	 */
	@Override
	public void close() throws IOException {
		super.close();
		if (buf != null) {
			Arrays.fill(buf, (byte) 0);
		}
	}
}
