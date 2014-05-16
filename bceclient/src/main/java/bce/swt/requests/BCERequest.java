package bce.swt.requests;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import bce.swt.util.BCEHandler;

public abstract class BCERequest implements Runnable {

	protected BCEHandler handler;
	
	protected URL url;

	protected String sessionId;
	
	public BCERequest(BCEHandler handler, URL url, String sessionId) {
		this.handler = handler;
		this.url = url;
		this.sessionId = sessionId;
	}
	
	public abstract byte[] getRequestContent();
	
	private void connect() throws IOException{
		HttpsURLConnection conn = (HttpsURLConnection) this.url.openConnection();
		conn.setDoInput(true);
		conn.setDoOutput(true);
		conn.setRequestMethod("POST");
		if (this.sessionId != null) {
			conn.setRequestProperty("Set-Cookie", this.sessionId);
		}
		conn.setUseCaches(false);
		conn.setInstanceFollowRedirects(false);
		conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		conn.connect();
		OutputStream out = conn.getOutputStream();
		out.write(getRequestContent());
		out.flush();
		out.close();
		InputStream in = conn.getInputStream();
		byte[] buf = new byte[16];
		int len;
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		while ((len = in.read(buf)) > 0) {
			buffer.write(buf, 0, len);
		}
		buffer.flush();
		in.close();
		byte[] responseData = buffer.toByteArray();
		buffer.close();
		conn.disconnect();
		this.handler.handleResponse(responseData);
	}
	
	@Override
	public final void run() {
		try {
			connect();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
