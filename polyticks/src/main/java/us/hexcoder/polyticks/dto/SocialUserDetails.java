package us.hexcoder.polyticks.dto;

import com.google.common.collect.Sets;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.social.security.SocialUser;
import us.hexcoder.polyticks.constant.Provider;
import us.hexcoder.polyticks.constant.Role;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Created by 67726e on 8/28/15.
 */
public class SocialUserDetails extends SocialUser {
	private UUID id;
	private String name;
	private Set<Role> roles;
	private Provider provider;

	protected SocialUserDetails(String username, String password, Collection<? extends GrantedAuthority> authorities) {
		super(username, password, authorities);
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

	public Provider getProvider() {
		return provider;
	}

	public void setProvider(Provider provider) {
		this.provider = provider;
	}

	public static class Builder {
		private UUID id;
		private String username;
		private String name;
		private Set<Role> roles = Sets.newHashSet();
		private Provider provider;

		public SocialUserDetails build() {
			SocialUserDetails details = new SocialUserDetails(this.username, "", this.roles.stream()
					.map(role -> new SimpleGrantedAuthority(role.toString()))
					.collect(Collectors.toSet()));

			details.setId(this.id);
			details.setName(this.name);
			details.setRoles(this.roles);
			details.setProvider(this.provider);

			return details;
		}

		public Builder withId(UUID id) {
			this.id = id;
			return this;
		}

		public Builder withUsername(String username) {
			this.username = username;
			return this;
		}

		public Builder withName(String name) {
			this.name = name;
			return this;
		}

		public Builder withRole(Role role) {
			this.roles.add(role);
			return this;
		}

		public Builder withRoles(Set<Role> roles) {
			this.roles.addAll(roles);
			return this;
		}

		public Builder withProvider(Provider provider) {
			this.provider = provider;
			return this;
		}
	}
}
