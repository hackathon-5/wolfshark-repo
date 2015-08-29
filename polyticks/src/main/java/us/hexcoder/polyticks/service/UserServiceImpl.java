package us.hexcoder.polyticks.service;

import com.google.common.collect.Sets;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.RecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.UserProfile;
import org.springframework.stereotype.Service;
import us.hexcoder.polyticks.constant.Provider;
import us.hexcoder.polyticks.constant.Role;
import us.hexcoder.polyticks.dto.SocialUserDetails;
import us.hexcoder.polyticks.dto.UserDTO;
import us.hexcoder.polyticks.jooq.tables.records.UsersRecord;
import us.hexcoder.polyticks.model.UserConnectionModel;
import us.hexcoder.polyticks.model.UserModel;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static us.hexcoder.polyticks.jooq.Tables.USERS;
import static us.hexcoder.polyticks.jooq.Tables.USERS_CONNECTIONS;

/**
 * Created by 67726e on 8/28/15.
 */
@Service
public class UserServiceImpl implements UserService {
	@Autowired
	private DSLContext context;

	@Override
	public UserModel socialSignup(Connection<?> connection) {
		UserProfile profile = connection.fetchUserProfile();

		String username = connection.createData().getProviderUserId();

		UsersRecord userRecord = context.selectFrom(USERS)
				.where(USERS.USERNAME.eq(username))
				.fetchOne();

		if (userRecord == null) {
			userRecord = context.newRecord(USERS);
			userRecord.setUsername(username);
			userRecord.setFullName(profile.getName());
			userRecord.insert();
		}

		return userRecord.into(UserModel.class);
	}

	@Override
	public void socialLogin(UUID userId, Connection<?> connection) {
		Optional<UserModel> user = Optional.ofNullable(context.select(USERS.fields()).from(USERS)
				.join(USERS_CONNECTIONS).on(USERS_CONNECTIONS.USER_ID.eq(USERS.ID))
				.where(USERS.ID.eq(userId))
				.and(USERS_CONNECTIONS.PROVIDER_ID.eq(connection.getKey().getProviderId()))
				.and(USERS_CONNECTIONS.PROVIDER_USER_ID.eq(connection.getKey().getProviderUserId()))
				.fetchOne())
				.map(record -> record.into(USERS).into(UserModel.class));

		if (user.isPresent()) {
			login(user.get());
		}
	}

	@Override
	public void login(UserModel user) {
		refreshContext(createDetails(user));
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		UsersRecord record = context.selectFrom(USERS)
				.where(USERS.USERNAME.eq(username))
				.fetchOne();

		if (record == null) {
			throw new UsernameNotFoundException("No user found for " + username);
		}

		return createDetails(record.into(UserModel.class));
	}

	@Override
	public org.springframework.social.security.SocialUserDetails loadUserByUserId(String userId) throws UsernameNotFoundException, DataAccessException {
		return (org.springframework.social.security.SocialUserDetails) loadUserByUsername(userId);
	}

	private SocialUserDetails createDetails(UserModel user) {
		// Provide default roles for the user
		Set<Role> roles = Sets.newHashSet();
		roles.add(Role.ROLE_SYSTEM_USER);

		return new SocialUserDetails.Builder()
				.withId(user.getId())
				.withUsername(user.getUsername())
				.withName(user.getFullName())
				.withRoles(roles)
				.withProvider(Provider.TWITTER)
				.build();
	}

	private void refreshContext(SocialUserDetails details) {
		Authentication authentication = new UsernamePasswordAuthenticationToken(details, null, details.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(authentication);
	}

	private static final RecordMapper<Record, UserDTO> USER_DTO_MAPPER = record -> {
		UserDTO user = new UserDTO();

		user.setUser(record.into(USERS).into(UserModel.class));
		user.setConnection(record.into(USERS_CONNECTIONS).into(UserConnectionModel.class));

		return user;
	};
}
