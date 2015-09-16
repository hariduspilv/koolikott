define(['app'], function(app)
{

	app.directive('dopPortfolioBox', ['translationService', 'serverCallService', function(translationService, serverCallService) {
		return {
			scope: {
				portfolio: '='
			},
			templateUrl: 'app/directives/portfolioBox/portfolioBox.html',
			controller: function ($scope, $location, $rootScope) {
			}
		};
	}]);

return app;
});