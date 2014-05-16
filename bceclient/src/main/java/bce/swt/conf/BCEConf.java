package bce.swt.conf;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class BCEConf {

	public static void main(String[] args) {
		Properties prop = new Properties();
		prop.setProperty("login", "https://localhost:8443/bceserver/LoginServlet.sl");
		prop.setProperty("process", "https://localhost:8443/bceserver/MainPageServlet.sl");
		try {
			prop.store(new FileOutputStream(new File("src/bce/swt/conf/BCEURLProvider.properties")), null);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
