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
				});
		});

	polyticksModule
		.controller("RootController", function($scope) {

		})
		.controller("IndexController", function($scope) {

		});
})(angular, _);
