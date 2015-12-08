define(['app'], function(app)
{
    app.directive('dopPortfolioSummaryCard', ['translationService', '$location', '$mdSidenav', '$mdDialog', '$rootScope', 'authenticatedUserService', '$route',
        function(translationService, $location, $mdSidenav, $mdDialog, $rootScope, authenticatedUserService, $route) {
            return {
                scope: true,
                templateUrl: 'directives/portfolioSummaryCard/portfolioSummaryCard.html',
                controller: function ($scope, $location) {

                	 $scope.openMenu = function($mdOpenMenu) {
                     	 if(isShowEditButton()) {
                     		 $scope.showEditButton = true;
                     	 }
                     	 
                         $mdOpenMenu();     
                     };
                     
                    $scope.showEditPortfolioDialog = function($event) {

                    	if(isShowEditButton() && $rootScope.isEditPortforlioMode && !$rootScope.isViewPortforlioPage) {
	                        $mdDialog.show({
	                            controller: 'addPortfolioDialog',
	                            templateUrl: 'views/addPortfolioDialog/addPortfolioDialog.html',
	                            locals:{portfolio: $scope.portfolio}
	                        });
                    	}
                    	if(isShowEditButton() && $rootScope.isViewPortforlioPage) {
                    		var portfolioId = $route.current.params.id;
                    		$location.url("/portfolio/edit?id="+portfolioId);
                    	}
                    	
                    };
                    
                    function isShowEditButton() {
                    	var portfolio = $scope.portfolio;
                    	var user = authenticatedUserService.getUser();
                    	var authenticated = authenticatedUserService.isAuthenticated();
                    	var showEditButton = false;
						if(authenticated && user.id == portfolio.creator.id) {
							 showEditButton = true;
						}
	                	return showEditButton;
                    }
                    
                }
            };
        }]);

    return app;
});
