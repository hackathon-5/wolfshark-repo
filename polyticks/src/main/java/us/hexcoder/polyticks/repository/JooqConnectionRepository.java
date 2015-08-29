package us.hexcoder.polyticks.repository;

import com.google.common.collect.Lists;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.impl.DSL;
import org.springframework.social.connect.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import us.hexcoder.polyticks.constant.Constants;
import us.hexcoder.polyticks.constant.Provider;
import us.hexcoder.polyticks.jooq.tables.records.UsersConnectionsRecord;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static us.hexcoder.polyticks.jooq.Tables.USERS_CONNECTIONS;

/**
 * Created by 67726e on 8/28/15.
 */
public class JooqConnectionRepository implements ConnectionRepository {
	private DSLContext context;
	private UUID userId;
	private ConnectionFactoryLocator connectionFactoryLocator;

	public JooqConnectionRepository(DSLContext context, String userId, ConnectionFactoryLocator connectionFactoryLocator) {
		this.context = context;
		this.userId = UUID.fromString(userId);
		this.connectionFactoryLocator = connectionFactoryLocator;
	}

	@Override
	public MultiValueMap<String, Connection<?>> findAllConnections() {
		MultiValueMap<String, Connection<?>> connections = new LinkedMultiValueMap<>();

		// Pre-fill the connections map with empty lists
		connectionFactoryLocator.registeredProviderIds().stream()
				.forEach(providerId -> connections.put(providerId, Lists.newLinkedList()));

		context.selectFrom(USERS_CONNECTIONS)
				.where(USERS_CONNECTIONS.USER_ID.eq(userId))
				.orderBy(USERS_CONNECTIONS.PROVIDER_ID, USERS_CONNECTIONS.RANK)
				.fetch()
				.map(this::intoConnection)
				.stream()
				.forEach(connection -> connections.add(connection.getKey().getProviderId(), connection));

		return connections;
	}

	@Override
	public List<Connection<?>> findConnections(String providerId) {
		return context.selectFrom(USERS_CONNECTIONS)
				.where(USERS_CONNECTIONS.USER_ID.eq(userId))
				.and(USERS_CONNECTIONS.PROVIDER_ID.eq(providerId))
				.orderBy(USERS_CONNECTIONS.RANK)
				.fetch()
				.map(this::intoConnection);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <A> List<Connection<A>> findConnections(Class<A> apiType) {
		List<?> connections = findConnections(toProviderId(apiType));

		return (List<Connection<A>>) connections;
	}

	@Override
	public MultiValueMap<String, Connection<?>> findConnectionsToUsers(MultiValueMap<String, String> providerUserIds) {
		if (providerUserIds == null || providerUserIds.isEmpty()) {
			throw new IllegalArgumentException("Unable to query for no provider user IDs");
		}

		// Group into (providerId='' AND providerUserId in ('')) clauses and chain with ORs
		Condition providerClause = providerUserIds.entrySet().stream()
				.map(entry -> USERS_CONNECTIONS.PROVIDER_ID.eq(entry.getKey())
						.and(USERS_CONNECTIONS.PROVIDER_USER_ID.in(entry.getValue())))
				.reduce((accumulator, condition) -> {
					if (accumulator == null) return condition;
					else return accumulator.or(condition);
				})
				.orElseThrow(IllegalStateException::new);

		MultiValueMap<String, Connection<?>> connectionsByUser = new LinkedMultiValueMap<>();

		context.selectFrom(USERS_CONNECTIONS)
				.where(USERS_CONNECTIONS.USER_ID.eq(userId))
				.and(providerClause)
				.fetch()
				.map(this::intoConnection)
				.stream()
				.forEach(connection -> {
					String providerId = connection.getKey().getProviderId();
					List<String> userIds = providerUserIds.get(providerId);
					List<Connection<?>> connections = connectionsByUser.get(providerId);

					// Pre-fill the list for the providerId with null entries equal to the number of providerUserIds
					if (connections == null) {
						connections = new ArrayList<>(userIds.size());
						for (int i = 0; i < userIds.size(); i++) connections.add(null);
						connectionsByUser.put(providerId, connections);
					}

					// Place the connection corresponding to the userId at the respective index
					String providerUserId = connection.getKey().getProviderUserId();
					connections.set(userIds.indexOf(providerUserId), connection);
				});

		return connectionsByUser;
	}

	@Override
	public Connection<?> getConnection(ConnectionKey connectionKey) {
		return intoConnection(Optional.ofNullable(context.selectFrom(USERS_CONNECTIONS)
				.where(USERS_CONNECTIONS.USER_ID.eq(userId))
				.and(USERS_CONNECTIONS.PROVIDER_ID.eq(connectionKey.getProviderId()))
				.and(USERS_CONNECTIONS.PROVIDER_USER_ID.eq(connectionKey.getProviderUserId()))
				.fetchOne())
				.orElseThrow(() -> new NoSuchConnectionException(connectionKey)));
	}

	@Override
	@SuppressWarnings("unchecked")
	public <A> Connection<A> getConnection(Class<A> apiType, String providerUserId) {
		return (Connection<A>) getConnection(new ConnectionKey(toProviderId(apiType), providerUserId));
	}

	@Override
	public <A> Connection<A> getPrimaryConnection(Class<A> apiType) {
		return Optional.ofNullable(findPrimaryConnection(apiType))
				.orElseThrow(() -> new NotConnectedException(toProviderId(apiType)));
	}

	@Override
	@SuppressWarnings("unchecked")
	public <A> Connection<A> findPrimaryConnection(Class<A> apiType) {
		return (Connection<A>) Optional.of(context.selectFrom(USERS_CONNECTIONS)
				.where(USERS_CONNECTIONS.USER_ID.eq(userId))
				.and(USERS_CONNECTIONS.PROVIDER_ID.eq(toProviderId(apiType)))
				.orderBy(USERS_CONNECTIONS.RANK)
				.fetchOne())
				.map(this::intoConnection)
				.orElse(null);
	}

	@Override
	public void addConnection(Connection<?> connection) {
		ConnectionData data = connection.createData();

		// Check if this connection already exists in the DB
		int connectionCount = Optional.ofNullable(context.selectCount().from(USERS_CONNECTIONS)
				.where(USERS_CONNECTIONS.USER_ID.eq(userId))
				.and(USERS_CONNECTIONS.PROVIDER_ID.eq(data.getProviderId()))
				.and(USERS_CONNECTIONS.PROVIDER_USER_ID.eq(data.getProviderUserId()))
				.fetchOneInto(Integer.class)).orElse(0);

		// If so, throw this exception
		if (connectionCount > 0) {
			throw new DuplicateConnectionException(connection.getKey());
		}

		int rank = context.select(
				DSL.coalesce(USERS_CONNECTIONS.RANK.max().plus(1), 1))
				.from(USERS_CONNECTIONS)
				.where(USERS_CONNECTIONS.USER_ID.eq(userId))
				.and(USERS_CONNECTIONS.PROVIDER_ID.eq(data.getProviderId()))
				.fetchOneInto(Integer.class);

		UsersConnectionsRecord record = context.newRecord(USERS_CONNECTIONS, data);
		record.setUserId(userId);
		record.setRank(rank);
		record.setImageUrl(getImageUrl(data).orElse(Constants.DEFAULT_PROFILE_URL));
		record.insert();
	}

	@Override
	public void updateConnection(Connection<?> connection) {
		ConnectionData data = connection.createData();

		context.update(USERS_CONNECTIONS)
				.set(USERS_CONNECTIONS.DISPLAY_NAME, data.getDisplayName())
				.set(USERS_CONNECTIONS.PROFILE_URL, data.getProfileUrl())
				.set(USERS_CONNECTIONS.IMAGE_URL, getImageUrl(data).orElse(Constants.DEFAULT_PROFILE_URL))
				.set(USERS_CONNECTIONS.ACCESS_TOKEN, data.getAccessToken())
				.set(USERS_CONNECTIONS.SECRET, data.getSecret())
				.set(USERS_CONNECTIONS.REFRESH_TOKEN, data.getRefreshToken())
				.set(USERS_CONNECTIONS.EXPIRE_TIME, data.getExpireTime())
				.where(USERS_CONNECTIONS.USER_ID.eq(userId))
				.and(USERS_CONNECTIONS.PROVIDER_ID.eq(data.getProviderId()))
				.and(USERS_CONNECTIONS.PROVIDER_USER_ID.eq(data.getProviderUserId()))
				.execute();
	}

	@Override
	public void removeConnections(String providerId) {
		context.delete(USERS_CONNECTIONS)
				.where(USERS_CONNECTIONS.USER_ID.eq(userId))
				.and(USERS_CONNECTIONS.PROVIDER_ID.eq(providerId))
				.execute();
	}

	@Override
	public void removeConnection(ConnectionKey connectionKey) {
		context.delete(USERS_CONNECTIONS)
				.where(USERS_CONNECTIONS.USER_ID.eq(userId))
				.and(USERS_CONNECTIONS.PROVIDER_ID.eq(connectionKey.getProviderId()))
				.and(USERS_CONNECTIONS.PROVIDER_USER_ID.eq(connectionKey.getProviderUserId()))
				.execute();
	}

	private Connection<?> intoConnection(Record record) {
		ConnectionData connectionData = record.into(USERS_CONNECTIONS).into(ConnectionData.class);

		return connectionFactoryLocator.getConnectionFactory(connectionData.getProviderId()).createConnection(connectionData);
	}

	private <A> String toProviderId(Class<A> apiType) {
		return connectionFactoryLocator.getConnectionFactory(apiType).getProviderId();
	}

	private Optional<String> getImageUrl(ConnectionData connectionData) {
		try {
			Optional<Provider> provider = Provider.fromString(connectionData.getProviderId());

			if (provider.isPresent()) {
				switch (provider.get()) {
					default:
						return Optional.ofNullable(connectionData.getImageUrl());
				}
			}
		} catch (Exception ignored) {}

		return Optional.ofNullable(connectionData.getImageUrl());
	}
}
