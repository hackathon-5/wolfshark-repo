package us.hexcoder.polyticks.model;

import java.util.UUID;

/**
 * Created by 67726e on 8/29/15.
 */
public class CategoryModel {
	private UUID id;
	private String name;

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
}
