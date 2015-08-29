package us.hexcoder.polyticks.model;

import java.util.UUID;

/**
 * Created by 67726e on 8/29/15.
 */
public class AnswerModel {
	private UUID id;
	private UUID questionId;
	private boolean conservative;
	private String text;

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

	public boolean isConservative() {
		return conservative;
	}

	public void setConservative(boolean conservative) {
		this.conservative = conservative;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
}
