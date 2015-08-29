/*global _: false, angular: false */


(function(angular, _) {
	"use strict";

	var polyticksModule = angular.module("Polyticks", ["ui.router"]);

	polyticksModule
		.config(function($stateProvider, $urlRouterProvider) {
			$urlRouterProvider.otherwise("/");
			$stateProvider
				.state("index", {
					url: "/",
					templateUrl: "/html/index.html"
				})
				.state("login", {
					url: "/login",
					templateUrl: "/html/login.html"
				})
				.state("surveys", {
					url: "/surveys/",
					templateUrl: "/html/surveys.html"
				})
				.state("survey", {
					   url: "/surveys/:surveyId",
					   templateUrl: "/html/survey.html"
					   })
				.state("results", {
					url: "/surveys/:surveyId/results",
					templateUrl: "/html/results.html"
				})
				.state("candidates", {
					   url: "/surveys/:surveyId/candidates",
					   templateUrl: "/html/candidates.html"
			    })
				.state("issues", {
					   url: "/surveys/:surveyId/issues",
					   templateUrl: "/html/issues.html"
 			    })
				;
		});

	polyticksModule
		.factory("RESTFactory", function ($http) {
			return function (resource) {
				var base = "/rest/" + resource;

				var errorRedirect = function (content, status) {
					if (status === 403) {
						location.reload();
					}
				};

				return {
					// Methods for custom REST endpoints
					getForResource: function (resource) {
						return $http.get(base + resource)
							.error(errorRedirect);
					},
					deleteForResource: function (resource) {
						return $http.delete(base + resource)
							.error(errorRedirect);
					},
					postForResource: function (resource, parameters) {
						if (parameters) {
							return $http.post(base + resource, JSON.stringify(parameters))
								.error(errorRedirect);
						} else {
							return $http.post(base + resource, undefined)
								.error(errorRedirect);
						}
					}
				};
			};
		});

	polyticksModule
		.controller("RootController", function($scope, $rootScope, $state, $location, RESTFactory) {
			var userService = RESTFactory("users");

			$scope.bodyclass = '';
			$rootScope.$on('$stateChangeStart', function(event, toState, toParams, fromState, fromParams){
			   $scope.bodyclass = toState.name;
		    });
					
			userService.getForResource("/current")
				.error(function(content, response) {
					if (response === 404 && $location.url() !== "/login") {
						$state.go("login");
					}
				})
				.then(function(response) {
					$scope.currentUser = response.data;
				});
		})
		.controller("IndexController", function($scope) {

		})
		.controller("LoginController", function($scope) {

		})
		.controller("SurveyController", function ($scope, $state, $stateParams, RESTFactory) {
			var surveyService = RESTFactory("surveys");

			var initializeSurvey = function() {
				if ($scope.survey && $scope.questions && $scope.answers) {
					if ($scope.questions.length === 0) {
						$state.go("results", {surveyId: $stateParams["surveyId"]});
					}

					$scope.responses = new Array($scope.questions.length);
				}
			};
			var completeSurvey = function() {
				if ($scope.questionIndex >= $scope.questions.length) {
					$state.go("results", {surveyId: $stateParams["surveyId"]});
				}
			};

			$scope.questionIndex = 0;
			$scope.answerQuestion = function(answer) {
				var response = {
					userId: $scope.currentUser.user.id,
					answerId: answer.id,
					questionId: answer.questionId
				};

				$scope.responses[$scope.questionIndex] = answer.text;
				$scope.questionIndex++;

				surveyService.postForResource("/" + $stateParams["surveyId"] + "/response", response)
					.then(function() {
						completeSurvey();
					});
			};

			surveyService.getForResource("/" + $stateParams["surveyId"])
				.then(function(response) {
					$scope.survey = response.data;
					initializeSurvey();
				});
			surveyService.getForResource("/" + $stateParams["surveyId"] + "/questions/unanswered?userId=" + $scope.currentUser.user.id)
				.then(function(response) {
					$scope.questions = response.data;
					initializeSurvey();
				});
			surveyService.getForResource("/" + $stateParams["surveyId"] + "/answers/unanswered?userId=" + $scope.currentUser.user.id)
				.then(function (response) {
					$scope.answers = response.data;
					$scope.answersByQuestion = _.groupBy(response.data, "questionId");
					initializeSurvey();
				});
		})
		.controller("ResultsController", function($scope, $stateParams, RESTFactory) {
			var surveyService = RESTFactory("surveys");
			var candidateService = RESTFactory("candidates");

			var initializeResults = function() {
				if ($scope.responses && $scope.candidates && $scope.candidateResponses) {
					var answersByCandidate = _.object(_.map(_.groupBy($scope.candidateResponses, "candidateId"),
						function(responses, candidateId) {
						return [candidateId, responses.map(function(response) {
							return response.answerId;
						})];
					}));

					var answerIds = _.map($scope.responses, function(response) {
						return response.answerId;
					});

					// TODO: Put me into the scope and do something!!!!
					var comparisonByCandidate = _.object(_.map(answersByCandidate, function(candidateAnswerIds, candidateId) {
						var matches = _.intersection(answerIds, candidateAnswerIds).length;
						var percent = Math.ceil((matches / answerIds.length) * 100);

						return [candidateId, {
							matches: matches,
							percent: percent
						}];
					}));
				}
			};

			surveyService.getForResource("/" + $stateParams["surveyId"] + "/responses?userId=" + $scope.currentUser.user.id)
				.then(function(response) {
					$scope.responses = response.data;
					initializeResults();
				});
			candidateService.getForResource("?surveyId=" + $stateParams["surveyId"])
				.then(function(response) {
					$scope.candidates = response.data;
					initializeResults();
				});
			candidateService.getForResource("/responses?surveyId=" + $stateParams["surveyId"])
				.then(function(response) {
					$scope.candidateResponses = response.data;
					initializeResults();
				});
		})
		.controller("CandidatesController", function($scope, $stateParams, RESTFactory) {
			 // TODO: Get survey/user responses
			 // TODO: Get candidates
			 // TODO: Get candidate responses
			 
			 // TODO: Calculate comparison to candidates vs. user
		})
		.controller("IssuesController", function($scope, $stateParams, RESTFactory) {
			 // TODO: Get survey/user responses
			 // TODO: Get candidates
			 // TODO: Get candidate responses
			 
			 // TODO: Calculate comparison to candidates vs. user
		})
		;
})(angular, _);
