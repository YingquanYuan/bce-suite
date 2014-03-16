package bce.server.util;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 该类提供一些与Spring操作相关的工具方法
 * 
 * @author robins
 *
 */
public class SpringUtil {
	
	/**
	 * 从Spring BeanFactory中获取定制的单例Bean
	 * 
	 * @param name bean的名字
	 * @return 单例bean
	 */
	public static Object getBean(String name) {
		ApplicationContext application = new ClassPathXmlApplicationContext("applicationContext.xml");
		return application.getBean(name);
	}
}
