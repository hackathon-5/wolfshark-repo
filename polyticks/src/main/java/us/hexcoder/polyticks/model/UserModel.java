package us.hexcoder.polyticks.model;

import java.util.UUID;

/**
 * Created by 67726e on 8/28/15.
 */
public class UserModel {
	private UUID id;
	private String email;
	private String username;
	private String fullName;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
}
