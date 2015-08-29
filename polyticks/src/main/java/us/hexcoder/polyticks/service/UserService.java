package us.hexcoder.polyticks.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.social.connect.Connection;
import org.springframework.social.security.SocialUserDetailsService;
import us.hexcoder.polyticks.dto.UserDTO;
import us.hexcoder.polyticks.model.UserModel;

import java.util.Optional;
import java.util.UUID;

/**
 * Created by 67726e on 8/28/15.
 */
public interface UserService extends UserDetailsService, SocialUserDetailsService {
	Optional<UUID> findCurrentUserId();
	Optional<UserDTO> findCurrentUserDTO();
	UserModel socialSignup(Connection<?> connection);
	void socialLogin(UUID userId, Connection<?> connection);
	void login(UserModel user);
}
