package us.hexcoder.polyticks.controller.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import us.hexcoder.polyticks.controller.rest.model.ResponseRestModel;
import us.hexcoder.polyticks.exception.ResourceNotFoundException;
import us.hexcoder.polyticks.model.AnswerModel;
import us.hexcoder.polyticks.model.QuestionModel;
import us.hexcoder.polyticks.model.SurveyModel;
import us.hexcoder.polyticks.service.SurveyService;
import us.hexcoder.polyticks.service.UserService;

import java.util.List;
import java.util.UUID;

/**
 * Created by 67726e on 8/29/15.
 */
@RestController
@RequestMapping("/rest/surveys")
public class SurveyRestController extends AbstractRestController {
	@Autowired
	private UserService userService;
	@Autowired
	private SurveyService surveyService;

	@RequestMapping(value = "/{surveyId}", method = RequestMethod.GET)
	public SurveyModel getById(@PathVariable("surveyId") UUID surveyId) {
		return surveyService.findById(surveyId)
				.orElseThrow(ResourceNotFoundException::new);
	}

	@RequestMapping(value = "/{surveyId}/complete", method = RequestMethod.GET)
	public boolean getComplete(@PathVariable("surveyId") UUID surveyId,
							   @RequestParam("userId") UUID userId) {
		return surveyService.isComplete(surveyId, userId);
	}

	@RequestMapping(value = "/{surveyId}/questions/unanswered", method = RequestMethod.GET)
	public List<QuestionModel> getAllUnansweredQuestions(@PathVariable("surveyId") UUID surveyId,
														 @RequestParam("userId") UUID userId) {
		return surveyService.findUnansweredQuestionsBySurveyAndUser(surveyId, userId);
	}

	@RequestMapping(value = "/{surveyId}/answers/unanswered", method = RequestMethod.GET)
	public List<AnswerModel> getAllAnswersForUnansweredQuestions(@PathVariable("surveyId") UUID surveyId,
																 @RequestParam("userId") UUID userId) {
		return surveyService.findUnansweredAnswersBySurveyAndUser(surveyId, userId);
	}

	@RequestMapping(value = "/{surveyId}/response", method = RequestMethod.POST)
	public boolean postCreateResponse(@PathVariable("surveyId") UUID surveyId,
									  @RequestBody ResponseRestModel response) {
		UUID currentUserId = userService.findCurrentUserId()
				.orElseThrow(ResourceNotFoundException::new);

		response.setUserId(currentUserId);
		surveyService.insertResponse(response);

		return true;
	}
}
