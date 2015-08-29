package us.hexcoder.polyticks.configuration;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.social.UserIdSource;
import org.springframework.social.config.annotation.ConnectionFactoryConfigurer;
import org.springframework.social.config.annotation.EnableSocial;
import org.springframework.social.config.annotation.SocialConfigurer;
import org.springframework.social.connect.*;
import org.springframework.social.connect.web.ConnectController;
import org.springframework.social.connect.web.ProviderSignInController;
import org.springframework.social.connect.web.SignInAdapter;
import org.springframework.social.facebook.connect.FacebookConnectionFactory;
import org.springframework.social.github.connect.GitHubConnectionFactory;
import org.springframework.social.security.AuthenticationNameUserIdSource;
import org.springframework.web.context.request.NativeWebRequest;
import us.hexcoder.polyticks.repository.JooqUsersConnectionRepository;
import us.hexcoder.polyticks.service.UserService;

import java.util.UUID;

/**
 * Created by 67726e on 8/28/15.
 */
@Configuration
@EnableSocial
public class SocialConfiguration implements SocialConfigurer {
	@Value("${social.facebook.publicKey}")
	private String facebookPublicKey;
	@Value("${social.facebook.privateKey}")
	private String facebookPrivateKey;

	@Value("${social.github.publicKey}")
	private String githubPublicKey;
	@Value("${social.github.privateKey}")
	private String githubPrivateKey;

	@Autowired
	private DSLContext context;
	@Autowired
	private UserService userService;

	@Override
	public void addConnectionFactories(ConnectionFactoryConfigurer connectionFactoryConfigurer, Environment environment) {
		connectionFactoryConfigurer.addConnectionFactory(new FacebookConnectionFactory(facebookPublicKey, facebookPrivateKey));
		connectionFactoryConfigurer.addConnectionFactory(new GitHubConnectionFactory(githubPublicKey, githubPrivateKey));
		// TODO: Add more social configurations?
	}

	@Override
	public UserIdSource getUserIdSource() {
		return new AuthenticationNameUserIdSource();
	}

	@Override
	public UsersConnectionRepository getUsersConnectionRepository(ConnectionFactoryLocator connectionFactoryLocator) {
		return new JooqUsersConnectionRepository(context, connectionFactoryLocator, connectionSignUp);
	}

	@Bean
	public ConnectController connectController(ConnectionFactoryLocator connectionFactoryLocator, ConnectionRepository connectionRepository) {
		return new ConnectController(connectionFactoryLocator, connectionRepository);
	}

	@Bean
	public ProviderSignInController providerSignInController(ConnectionFactoryLocator connectionFactoryLocator, UsersConnectionRepository usersConnectionRepository) {
		return new ProviderSignInController(connectionFactoryLocator, usersConnectionRepository, new SignInAdapter() {
			@Override
			public String signIn(String userId, Connection<?> connection, NativeWebRequest request) {
				userService.socialLogin(UUID.fromString(userId), connection);

				// TODO: Custom redirect logic, maybe?
				return "/";
			}
		});
	}

	private final ConnectionSignUp connectionSignUp = new ConnectionSignUp() {
		@Override
		public String execute(Connection<?> connection) {
			return String.valueOf(userService.socialSignup(connection).getId());
		}
	};
}
