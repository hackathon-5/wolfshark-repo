package us.hexcoder.polyticks.configuration;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.social.security.SpringSocialConfigurer;
import us.hexcoder.polyticks.constant.Role;
import us.hexcoder.polyticks.service.UserService;

/**
 * Created by 67726e on 8/28/15.
 */
@Configuration
@EnableWebSecurity
@ComponentScan({"us.hexcoder.polyticks.service"})
// See: http://codehustler.org/blog/spring-security-tutorial-form-login-java-config/
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
	@Autowired
	private UserService userService;

	@Override
	public void configure(WebSecurity webSecurity) throws Exception {
		webSecurity
				.ignoring()
				.antMatchers("/css/**")
				.antMatchers("/html/**")
				.antMatchers("/images/**")
				.antMatchers("/js/**")
				.antMatchers("/resources/**");
	}

	@Override
	protected void configure(HttpSecurity httpSecurity) throws Exception {
		httpSecurity
				.exceptionHandling()
				.accessDeniedHandler(ACCESS_DENIED_HANDLER)
				.and()
				.logout()
				.deleteCookies("JSESSIONID")
				.logoutUrl("/logout")
				.logoutSuccessUrl("/")
				.invalidateHttpSession(true)
				.and()
				.authorizeRequests()
				.antMatchers(
						"/",
						"/auth/**",
						"/rest/users/current",
						"/signin/**"
				).permitAll()
				.antMatchers("/**").hasAuthority(Role.ROLE_SYSTEM_USER.toString())
				.and()
				.csrf()
				.disable()
				.apply(new SpringSocialConfigurer());
	}

	@Override
	protected void configure(AuthenticationManagerBuilder authenticationBuilder) throws Exception {
		authenticationBuilder
				.userDetailsService(userService)
				.passwordEncoder(new BCryptPasswordEncoder(10));
	}

	private static final AccessDeniedHandler ACCESS_DENIED_HANDLER = (request, response, accessDeniedException) -> {
		if (StringUtils.startsWith(request.getRequestURI(), "/rest/")) {
			response.setStatus(401);
		} else {
			response.sendRedirect("/");
		}
	};

}
