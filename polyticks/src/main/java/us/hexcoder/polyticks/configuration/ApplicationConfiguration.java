package us.hexcoder.polyticks.configuration;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.EnvironmentStringPBEConfig;
import org.jasypt.spring31.properties.EncryptablePropertyPlaceholderConfigurer;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.UrlBasedViewResolver;
import us.hexcoder.twirl.view.TwirlView;

/**
 * Created by 67726e on 8/28/15.
 */
@Configuration
@EnableWebMvc
@Import({PersistenceConfiguration.class})
@ComponentScan({"us.hexcoder.polyticks"})
public class ApplicationConfiguration extends WebMvcConfigurerAdapter {
	private static final String APPLICATION_ENVIRONMENT_VARIABLE = "APP_ENV";
	private static final String PROPERTY_ENCRYPTION_ALGORITHM = "PBEWithMD5AndDES";
	private static final String PROPERTY_ENCRYPTION_VARIABLE = "POLYTICKS_PROPERTY_PASSWORD";

	@Bean
	public static EnvironmentStringPBEConfig setupEnvironmentStringPBEConfig(Environment environment) {
		EnvironmentStringPBEConfig config = new EnvironmentStringPBEConfig();
		config.setAlgorithm(PROPERTY_ENCRYPTION_ALGORITHM);
		config.setPassword(environment.getRequiredProperty(PROPERTY_ENCRYPTION_VARIABLE));

		return config;
	}

	@Bean
	public static StandardPBEStringEncryptor setupStandardPBEStringEncryptor(EnvironmentStringPBEConfig config) {
		StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
		encryptor.setConfig(config);

		return encryptor;
	}

	@Bean
	public static PropertyPlaceholderConfigurer setupPropertyPlaceholderConfigurer(Environment environment, StandardPBEStringEncryptor encryptor) {
		EncryptablePropertyPlaceholderConfigurer configurer = new EncryptablePropertyPlaceholderConfigurer(encryptor);
		configurer.setLocation(new DefaultResourceLoader().getResource(String.format("classpath:%s.properties",
				environment.getRequiredProperty(APPLICATION_ENVIRONMENT_VARIABLE))));
		configurer.setIgnoreResourceNotFound(false);
		configurer.setIgnoreUnresolvablePlaceholders(false);

		return configurer;
	}

	@Bean
	public ViewResolver setupViewResolver() {
		UrlBasedViewResolver viewResolver = new UrlBasedViewResolver();

		viewResolver.setOrder(0);
		viewResolver.setViewClass(TwirlView.class);

		return viewResolver;
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/resources/**")
				.addResourceLocations("/resources/");

		registry.addResourceHandler("/css/**").addResourceLocations("/css/");
		registry.addResourceHandler("/html/**").addResourceLocations("/html/");
		registry.addResourceHandler("/images/**").addResourceLocations("/images/");
		registry.addResourceHandler("/js/**").addResourceLocations("/js/");
	}
}
