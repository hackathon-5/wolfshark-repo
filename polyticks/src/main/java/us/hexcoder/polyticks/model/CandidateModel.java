package us.hexcoder.polyticks.model;

import java.util.UUID;

/**
 * Created by 67726e on 8/29/15.
 */
public class CandidateModel {
	private UUID id;
	private UUID surveyId;
	private String fullName;

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

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
}
