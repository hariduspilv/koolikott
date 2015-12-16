define(['app'], function(app)
{
    app.directive('dopPortfolioSummaryCard', ['translationService', '$location', '$mdSidenav', '$mdDialog', '$rootScope', 'authenticatedUserService', '$route',
        function(translationService, $location, $mdSidenav, $mdDialog, $rootScope, authenticatedUserService, $route) {
            return {
            	scope: {
                    portfolio: '=',
                    comment: '=',
                    submitClick: "&"
                },
                templateUrl: 'directives/portfolioSummaryCard/portfolioSummaryCard.html',
                controller: function ($scope, $location) {
                    if($scope.portfolio) {
                        $scope.newTaxon = Object.create($scope.portfolio.taxon);
                    }

                	$scope.isViewPortforlioPage = $rootScope.isViewPortforlioPage;
                	$scope.isEditPortfolioMode = $rootScope.isEditPortfolioMode;

                    $scope.getEducationalContext = function() {
                        return $rootScope.taxonUtils.getEducationalContext($scope.newTaxon)
                            .name.toUpperCase();
                    };

                    $scope.isOwner = function() {
                		if (!authenticatedUserService.isAuthenticated()) {
                			return false;
                		}

                        if($scope.portfolio && $scope.portfolio.creator) {
                            var creatorId = $scope.portfolio.creator.id;
                            var userId = authenticatedUserService.getUser().id;
                            return creatorId === userId;
                        }
                    };
 
                    $scope.editPortfolio = function() {
	                    var portfolioId = $route.current.params.id;
	                    $location.url("/portfolio/edit?id="+portfolioId);
	                    $rootScope.savedPortfolio = $scope.portfolio;
                    };

                    $scope.showEditMetadataDialog = function() {
                        $mdDialog.show({
                            controller: 'addPortfolioDialog',
                            templateUrl: 'views/addPortfolioDialog/addPortfolioDialog.html',
                            locals:{portfolio: $scope.portfolio}
                        });
                    };
                    
                    $scope.addComment = function() {
                        $scope.submitClick();
                    }
                }
            };
        }]);

    return app;
});
