package us.hexcoder.polyticks.controller.rest.model;

import java.util.UUID;

/**
 * Created by 67726e on 8/29/15.
 */
public class ResponseRestModel {
	private UUID id;
	private UUID questionId;
	private UUID answerId;
	private UUID userId;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public UUID getQuestionId() {
		return questionId;
	}

	public void setQuestionId(UUID questionId) {
		this.questionId = questionId;
	}

	public UUID getAnswerId() {
		return answerId;
	}

	public void setAnswerId(UUID answerId) {
		this.answerId = answerId;
	}

	public UUID getUserId() {
		return userId;
	}

	public void setUserId(UUID userId) {
		this.userId = userId;
	}
}
