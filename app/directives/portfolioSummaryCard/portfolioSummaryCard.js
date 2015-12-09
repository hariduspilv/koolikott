define(['app'], function(app)
{
    app.directive('dopPortfolioSummaryCard', ['translationService', '$location', '$mdSidenav', '$mdDialog', '$rootScope', 'authenticatedUserService', '$route',
        function(translationService, $location, $mdSidenav, $mdDialog, $rootScope, authenticatedUserService, $route) {
            return {
                scope: true,
                templateUrl: 'directives/portfolioSummaryCard/portfolioSummaryCard.html',
                controller: function ($scope, $location) {

                	var user = authenticatedUserService.getUser();
                	var authenticated = authenticatedUserService.isAuthenticated();
                	
                	 $scope.openMenu = function($mdOpenMenu) {
                     	 if(isShowEditMetadataButton()) {
                     		 $scope.showEditButton = true;
                     	 }
                     	 if(isShowRedirectButton()) {
                    		 $scope.showRedirectButton = true;
                    	 }
                     	
                         $mdOpenMenu();     
                     };
                     
                    $scope.redirectToEditMode = function() {
	                    var portfolioId = $route.current.params.id;
	                    $location.url("/portfolio/edit?id="+portfolioId);
                    };
                     
                    $scope.showEditMetadataoDialog = function() {
                        $mdDialog.show({
                            controller: 'addPortfolioDialog',
                            templateUrl: 'views/addPortfolioDialog/addPortfolioDialog.html',
                            locals:{portfolio: $scope.portfolio}
                        });
                    };
                    
                    function isShowEditMetadataButton() {
                    	var portfolio = $scope.portfolio;
						if(authenticated && user.id == portfolio.creator.id && !isPortfolioViewMode()) {
							 return true;
						}
	                	return false;
                    }
                    
                    function isShowRedirectButton() {
                    	var portfolio = $scope.portfolio;
						if(authenticated && user.id == portfolio.creator.id && isPortfolioViewMode()) {
							return true;
						}
	                	return false;
                    }
                    
                    function isPortfolioViewMode() {
                    	return $rootScope.isViewPortforlioPage;
                    }
                    
                }
            };
        }]);

    return app;
});
