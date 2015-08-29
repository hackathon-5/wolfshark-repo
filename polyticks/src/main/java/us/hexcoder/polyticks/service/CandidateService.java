package us.hexcoder.polyticks.service;

import us.hexcoder.polyticks.model.CandidateModel;
import us.hexcoder.polyticks.model.CandidateResponseModel;

import java.util.List;
import java.util.UUID;

/**
 * Created by 67726e on 8/29/15.
 */
public interface CandidateService {
	List<CandidateModel> findCandidatesBySurvey(UUID surveyId);
	List<CandidateResponseModel> findCandidateResponsesBySurvey(UUID surveyId);
}
