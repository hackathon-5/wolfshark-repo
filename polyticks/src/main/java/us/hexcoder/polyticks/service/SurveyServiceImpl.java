package us.hexcoder.polyticks.service;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import us.hexcoder.polyticks.controller.rest.model.ResponseRestModel;
import us.hexcoder.polyticks.jooq.tables.records.ResponsesRecord;
import us.hexcoder.polyticks.model.AnswerModel;
import us.hexcoder.polyticks.model.QuestionModel;
import us.hexcoder.polyticks.model.SurveyModel;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static us.hexcoder.polyticks.jooq.Tables.*;

/**
 * Created by 67726e on 8/29/15.
 */
@Service
@Transactional(readOnly = true)
public class SurveyServiceImpl implements SurveyService {
	@Autowired
	private DSLContext context;

	@Override
	public Optional<SurveyModel> findById(UUID surveyId) {
		return Optional.ofNullable(context.selectFrom(SURVEYS)
				.where(SURVEYS.ID.eq(surveyId))
				.fetchOne())
				.map(record -> record.into(SurveyModel.class));
	}

	@Override
	public List<QuestionModel> findUnansweredQuestionsBySurveyAndUser(UUID surveyId, UUID userId) {
		return context.select(QUESTIONS.fields()).from(SURVEYS)
				.join(QUESTIONS).on(QUESTIONS.SURVEY_ID.eq(SURVEYS.ID))
				.where(SURVEYS.ID.eq(surveyId))
				.and(QUESTIONS.ID.notIn(
						context.select(RESPONSES.QUESTION_ID).from(RESPONSES).where(RESPONSES.USER_ID.eq(userId))
				))
				.fetchInto(QuestionModel.class);
	}

	@Override
	public List<AnswerModel> findUnansweredAnswersBySurveyAndUser(UUID surveyId, UUID userId) {
		return context.select(ANSWERS.fields()).from(SURVEYS)
				.join(QUESTIONS).on(QUESTIONS.SURVEY_ID.eq(SURVEYS.ID))
				.join(ANSWERS).on(ANSWERS.QUESTION_ID.eq(QUESTIONS.ID))
				.where(SURVEYS.ID.eq(surveyId))
				.and(QUESTIONS.ID.notIn(
						context.select(RESPONSES.QUESTION_ID).from(RESPONSES).where(RESPONSES.USER_ID.eq(userId))
				))
				.fetchInto(AnswerModel.class);
	}

	@Override
	@Transactional(readOnly = false)
	public void insertResponse(ResponseRestModel response) {
		ResponsesRecord record = context.newRecord(RESPONSES, response);
		record.insert();
	}

	@Override
	public boolean isComplete(UUID surveyId, UUID userId) {
		// We cannot complete a non-existent survey, can we?
		if (!findById(surveyId).isPresent())
			return false;

		Integer questionCount = context.selectCount().from(QUESTIONS)
				.join(QUESTIONS).on(QUESTIONS.SURVEY_ID.eq(SURVEYS.ID))
				.where(SURVEYS.ID.eq(surveyId))
				.fetchOneInto(Integer.class);

		Integer responseCount = context.selectCount().from(RESPONSES)
				.join(QUESTIONS).on(QUESTIONS.ID.eq(RESPONSES.QUESTION_ID))
				.join(QUESTIONS).on(QUESTIONS.SURVEY_ID.eq(SURVEYS.ID))
				.where(SURVEYS.ID.eq(surveyId))
				.and(RESPONSES.USER_ID.eq(userId))
				.fetchOneInto(Integer.class);

		return Objects.equals(questionCount, responseCount);

	}
}
