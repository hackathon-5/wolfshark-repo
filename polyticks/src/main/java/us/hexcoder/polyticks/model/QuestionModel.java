package us.hexcoder.polyticks.model;

import java.util.UUID;

/**
 * Created by 67726e on 8/29/15.
 */
public class QuestionModel {
	private UUID id;
	private UUID surveyId;
	private UUID categoryId;
	private String text;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public UUID getSurveyId() {
		return surveyId;
	}

	public void setSurveyId(UUID surveyId) {
		this.surveyId = surveyId;
	}

	public UUID getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(UUID categoryId) {
		this.categoryId = categoryId;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
}
