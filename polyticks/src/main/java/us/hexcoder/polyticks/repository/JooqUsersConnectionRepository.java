package us.hexcoder.polyticks.repository;

import org.jooq.DSLContext;
import org.springframework.social.connect.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static us.hexcoder.polyticks.jooq.Tables.*;

/**
 * Created by 67726e on 8/28/15.
 */
public class JooqUsersConnectionRepository implements UsersConnectionRepository {
	private DSLContext context;
	private ConnectionFactoryLocator connectionFactoryLocator;
	private ConnectionSignUp connectionSignUp;

	public JooqUsersConnectionRepository(DSLContext context, ConnectionFactoryLocator connectionFactoryLocator,
										 ConnectionSignUp connectionSignUp) {
		this.context = context;
		this.connectionFactoryLocator = connectionFactoryLocator;
		this.connectionSignUp = connectionSignUp;
	}

	@Override
	public List<String> findUserIdsWithConnection(Connection<?> connection) {
		ConnectionKey key = connection.getKey();

		List<String> userIds = context.select(USERS_CONNECTIONS.USER_ID).from(USERS_CONNECTIONS)
				.where(USERS_CONNECTIONS.PROVIDER_ID.eq(key.getProviderId()))
				.and(USERS_CONNECTIONS.PROVIDER_USER_ID.eq(key.getProviderUserId()))
				.fetch()
				.map(record -> record.getValue(USERS_CONNECTIONS.USER_ID).toString());

		if (userIds.size() == 0) {
			// Create a new user account from the connection, and associate the connection with the user
			String userId = connectionSignUp.execute(connection);
			createConnectionRepository(userId).addConnection(connection);
			userIds.add(userId);
		}

		return userIds;
	}

	@Override
	public Set<String> findUserIdsConnectedTo(String providerId, Set<String> providerUserIds) {
		return new HashSet<>(context.select(USERS_CONNECTIONS.USER_ID).from(USERS_CONNECTIONS)
				.where(USERS_CONNECTIONS.PROVIDER_ID.eq(providerId))
				.and(USERS_CONNECTIONS.PROVIDER_USER_ID.in(providerUserIds))
				.fetch()
				.map(record -> record.getValue(USERS_CONNECTIONS.USER_ID).toString()));
	}

	@Override
	public ConnectionRepository createConnectionRepository(String userId) {
		if (userId == null) throw new IllegalArgumentException("userId cannot be null");

		return new JooqConnectionRepository(context, userId, connectionFactoryLocator);
	}

}
