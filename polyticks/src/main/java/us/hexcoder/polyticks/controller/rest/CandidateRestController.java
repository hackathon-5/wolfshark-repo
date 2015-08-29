package us.hexcoder.polyticks.controller.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import us.hexcoder.polyticks.model.CandidateModel;
import us.hexcoder.polyticks.model.CandidateResponseModel;
import us.hexcoder.polyticks.service.CandidateService;

import java.util.List;
import java.util.UUID;

/**
 * Created by 67726e on 8/29/15.
 */
@RestController
@RequestMapping("/rest/candidates")
public class CandidateRestController extends AbstractRestController {
	@Autowired
	private CandidateService candidateService;

	@RequestMapping(method = RequestMethod.GET)
	public List<CandidateModel> getCandidatesBySurvey(@RequestParam("surveyId") UUID surveyId) {
		return candidateService.findCandidatesBySurvey(surveyId);
	}

	@RequestMapping(value = "/responses", method = RequestMethod.GET)
	public List<CandidateResponseModel> getCandidateResponsesBySurvey(@RequestParam("surveyId") UUID surveyId) {
		return candidateService.findCandidateResponsesBySurvey(surveyId);
	}
}
