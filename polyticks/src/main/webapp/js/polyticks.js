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
				.state("survey", {
					url: "/survey/:surveyId",
					templateUrl: "/html/survey.html"
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
						if (!parameters) {
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
		.controller("RootController", function($scope, $state, $location, RESTFactory) {
			var userService = RESTFactory("users");

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
		.controller("SurveyController", function ($scope, $stateParams, RESTFactory) {
			var surveyService = RESTFactory("surveys");

			var initializeSurvey = function() {};

			surveyService.getForResource("/" + $stateParams["surveyId"])
				.then(function(response) {
					$scope.survey = response.data;
					initializeSurvey();
				});
			surveyService.getForResource("/" + $stateParams["surveyId"] + "/questions/unanswered?userId" + $scope.currentUser.user.id)
				.then(function(response) {
					$scope.questions = response.data;
					initializeSurvey();
				});
			surveyService.getForResource("/" + $stateParams["surveyId"] + "/answers/unanswered?userId" + $scope.currentUser.user.id)
				.then(function (response) {
					$scope.answers = response.data;
					initializeSurvey();
				});
		})
		;
})(angular, _);
