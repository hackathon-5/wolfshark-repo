package us.hexcoder.polyticks.service;

import us.hexcoder.polyticks.controller.rest.model.ResponseRestModel;
import us.hexcoder.polyticks.model.AnswerModel;
import us.hexcoder.polyticks.model.QuestionModel;
import us.hexcoder.polyticks.model.ResponseModel;
import us.hexcoder.polyticks.model.SurveyModel;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by 67726e on 8/29/15.
 */
public interface SurveyService {
	Optional<SurveyModel> findById(UUID surveyId);
	List<SurveyModel> findAll();
	List<QuestionModel> findQuestionsBySurvey(UUID surveyId);
	List<QuestionModel> findUnansweredQuestionsBySurveyAndUser(UUID surveyId, UUID userId);
	List<AnswerModel> findAnswersBySurvey(UUID surveyId);
	List<AnswerModel> findUnansweredAnswersBySurveyAndUser(UUID surveyId, UUID userId);
	List<ResponseModel> findResponsesBySurveyAndUser(UUID surveyId, UUID userId);
	void insertResponse(ResponseRestModel response);
	boolean isComplete(UUID surveyId, UUID userId);
}
