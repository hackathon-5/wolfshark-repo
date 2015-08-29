package us.hexcoder.polyticks.configuration;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Created by 67726e on 8/28/15.
 */
@Component
public class ApplicationContext implements ApplicationContextAware {
	private static org.springframework.context.ApplicationContext applicationContext;

	@Override
	public void setApplicationContext(org.springframework.context.ApplicationContext applicationContext) throws BeansException {
		ApplicationContext.applicationContext = applicationContext;
	}

	public static <T> T getBean(Class<T> beanClass) {
		return applicationContext.getBean(beanClass);
	}
}