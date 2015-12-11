define(['app'], function(app)
{
    app.directive('dopPortfolioSummaryCard', ['translationService', '$location', '$mdSidenav', '$mdDialog', '$rootScope', 'authenticatedUserService', '$route',
        function(translationService, $location, $mdSidenav, $mdDialog, $rootScope, authenticatedUserService, $route) {
            return {
                scope: true,
                templateUrl: 'directives/portfolioSummaryCard/portfolioSummaryCard.html',
                controller: function ($scope, $location) {

                	$scope.isOwner = function() {
                		if (!authenticatedUserService.isAuthenticated()) {
                			return false;
                		}
                		
                		var creatorId = $scope.portfolio.creator.id;
                		var userId = authenticatedUserService.getUser().id;
                		return creatorId === userId;
                	}
 
                    $scope.editPortfolio = function() {
	                    var portfolioId = $route.current.params.id;
	                    $location.url("/portfolio/edit?id="+portfolioId);
	                    $rootScope.savedPortfolio = $scope.portfolio;
                    };

                    $scope.showEditMetadataoDialog = function() {
                        $mdDialog.show({
                            controller: 'addPortfolioDialog',
                            templateUrl: 'views/addPortfolioDialog/addPortfolioDialog.html',
                            locals:{portfolio: $scope.portfolio}
                        });
                    };
                }
            };
        }]);

    return app;
});
