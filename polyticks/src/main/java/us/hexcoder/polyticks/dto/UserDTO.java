package us.hexcoder.polyticks.dto;

import us.hexcoder.polyticks.model.UserConnectionModel;
import us.hexcoder.polyticks.model.UserModel;

/**
 * Created by 67726e on 8/28/15.
 */
public class UserDTO {
	private UserModel user;
	private UserConnectionModel connection;

	public UserModel getUser() {
		return user;
	}

	public void setUser(UserModel user) {
		this.user = user;
	}

	public UserConnectionModel getConnection() {
		return connection;
	}

	public void setConnection(UserConnectionModel connection) {
		this.connection = connection;
	}
}
