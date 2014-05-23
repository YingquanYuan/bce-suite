package bce.client.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

/**
 * 单例的URL生成器
 *
 * @author robins
 *
 */
public class BCEURLProvider {

    /**
     * 唯一的实例对象
     */
    private static BCEURLProvider provider;

    static {
        if (provider == null)
            provider = new BCEURLProvider();
    }

    /**
     * 该单例提供的loginURL对象
     */
    private URL loginURL;

    /**
     * 该单例提供的processURL对象
     */
    private URL processURL;

    /**
     * 私有构造函数，用于收回对象实例化的控制权
     */
    private BCEURLProvider() {
        loadProperties();
    }

    /**
     * 获取唯一实例对象
     * @return 唯一的BCEURLProvider实例对象
     */
    public static BCEURLProvider getInstance() {
        return provider;
    }

    /**
     * 读取配置文件，载入配置的URL
     */
    private void loadProperties() {
        Properties prop = new Properties();
        InputStream in = null;
        try {
            in = BCEURLProvider.class.getResourceAsStream("/BCEURLProvider.properties");
            prop.load(in);
            this.loginURL = new URL(prop.getProperty("login"));
            this.processURL = new URL(prop.getProperty("process"));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取loginURL对象
     * @return loginURL对象
     */
    public URL getLoginURL() {
        if (this.loginURL == null)
            return null;
        return loginURL;
    }

    /**
     * 获取processURL对象
     * @return processURL对象
     */
    public URL getProcessURL() {
        if (this.processURL == null)
            return null;
        return processURL;
    }

}
