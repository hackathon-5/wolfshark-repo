package us.hexcoder.polyticks.service;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import us.hexcoder.polyticks.model.CandidateModel;
import us.hexcoder.polyticks.model.CandidateResponseModel;

import java.util.List;
import java.util.UUID;

import static us.hexcoder.polyticks.jooq.Tables.*;

/**
 * Created by 67726e on 8/29/15.
 */
@Service
@Transactional(readOnly = true)
public class CandidateServiceImpl implements CandidateService {
	@Autowired
	private DSLContext context;

	@Override
	public List<CandidateModel> findCandidatesBySurvey(UUID surveyId) {
		return context.selectFrom(CANDIDATES)
				.where(CANDIDATES.SURVEY_ID.eq(surveyId))
				.fetchInto(CandidateModel.class);
	}

	@Override
	public List<CandidateResponseModel> findCandidateResponsesBySurvey(UUID surveyId) {
		return context.select(CANDIDATES_RESPONSES.fields()).from(CANDIDATES)
				.join(CANDIDATES_RESPONSES).on(CANDIDATES_RESPONSES.CANDIDATE_ID.eq(CANDIDATES.ID))
				.where(CANDIDATES.SURVEY_ID.eq(surveyId))
				.fetchInto(CandidateResponseModel.class);
	}
}
