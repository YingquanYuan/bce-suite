package bce.client.servicers;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import javax.net.ssl.HttpsURLConnection;

public class ClientBCEServicer {
	
	private ClientBCEServicer() {
	}

	/**
	 * 客户端登录
	 * 
	 * @param userName 用户名
	 * @param password 密码
	 * @return 两个登录信息，[0]：登录成功/失败反馈信息，[1]：JSESSIONID
	 */
	public static String[] login(String userName, String password) {

		String[] loginInfo = new String[2];
		StringBuffer feedback = new StringBuffer();
		try {
			URL url = new URL("https://localhost:8443/bceserver/LoginServlet.sl");
			HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setUseCaches(false);
			conn.setInstanceFollowRedirects(false);
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			conn.connect();
			DataOutputStream out = new DataOutputStream(conn.getOutputStream());
			StringBuilder content = new StringBuilder();
			content.append(URLEncoder.encode("u", "UTF-8")).append("=")
					.append(URLEncoder.encode(userName, "UTF-8")).append("&")
					.append(URLEncoder.encode("p", "UTF-8")).append("=")
					.append(URLEncoder.encode(password, "UTF-8")).append("&")
					.append(URLEncoder.encode("flag", "UTF-8")).append("=")
					.append(URLEncoder.encode("1", "UTF-8"));
			out.writeBytes(content.toString());
			out.flush();
			out.close();
			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
			String tempBuf;
			while ((tempBuf = reader.readLine()) != null) {
				feedback.append(tempBuf);
				continue;
			}
			reader.close();
			loginInfo[0] = feedback.toString();
			String key = "";
			String sessionId = "";
			if (conn != null && feedback.toString().startsWith("Welcome")) {
				for (int i = 1; (key = conn.getHeaderFieldKey(i)) != null; i++) {
					if (key.equalsIgnoreCase("set-cookie")) {
						sessionId = conn.getHeaderField(key);
						sessionId = sessionId.substring(0, sessionId.indexOf(";"));
						break;
					}
				}
			}
			loginInfo[1] = sessionId;
			conn.disconnect();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return loginInfo;
	}
}
