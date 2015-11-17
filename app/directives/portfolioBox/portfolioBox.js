define(['app'], function(app)
{

	app.directive('dopPortfolioBox', ['translationService', 'serverCallService', function(translationService, serverCallService) {
		return {
			scope: {
				portfolio: '='
			},
			templateUrl: 'directives/portfolioBox/portfolioBox.html',
			controller: function ($scope, $location, $rootScope) {
                $scope.navigateTo = function(portfolio) {
					$rootScope.savedPortfolio = portfolio;

                    $location.path('/portfolio').search({id:  portfolio.id});
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
