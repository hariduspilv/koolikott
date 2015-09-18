define(['app'], function(app)
{

	app.directive('dopPortfolioBox', ['translationService', 'serverCallService', function(translationService, serverCallService) {
		return {
			scope: {
				portfolio: '='
			},
			templateUrl: 'app/directives/portfolioBox/portfolioBox.html',
			controller: function ($scope, $location, $rootScope) {

				$scope.savePortfolio = function(portfolio) {
					$rootScope.savedPortfolio = portfolio;
				}

				$scope.formatName = function(name) {
					return formatNameToInitials(name);
				}

				$scope.formatSurname = function(surname){
					return formatSurnameToInitialsButLast(surname);
				}

				$scope.formatDate = function(date) {
	    		return formatDateToDayMonthYear(date);
            	}
			}
		};
	}]);

return app;
});