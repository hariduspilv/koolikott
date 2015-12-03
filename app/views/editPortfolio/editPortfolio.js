define(['app'], function(app)
{
    app.run(['$anchorScroll', function($anchorScroll) {
      $anchorScroll.yOffset = 50;
    }]);

    app.controller('editPortfolioController', ['$scope', 'translationService', 'serverCallService', '$route', '$location', 'alertService', '$rootScope', 'authenticatedUserService', 'dialogService',
        function($scope, translationService, serverCallService, $route, $location, alertService, $rootScope, authenticatedUserService, dialogService) {

            function init() {
				if ($rootScope.savedPortfolio) {
					$scope.portfolio = $rootScope.savedPortfolio;
				} else {
					getPortfolio(getPortfolioSuccess, getPortfolioFail);
				}
				$rootScope.isEditPortforlioMode = true;
			}

			function getPortfolio(success, fail) {
				var portfolioId = $route.current.params.id;
				serverCallService.makeGet("rest/portfolio?id=" + portfolioId, {}, success, fail);
			}

	        function getPortfolioSuccess(portfolio) {
	            if (isEmpty(portfolio)) {
	            	getPortfolioFail();
	            } else {
	                $scope.portfolio = portfolio;
	            }
	    	}

	    	function getPortfolioFail() {
                $rootScope.isEditPortforlioMode = false;
	            log('No data returned by getting portfolio.');
	            alertService.setErrorAlert('ERROR_PORTFOLIO_NOT_FOUND');
	            $location.url("/");
	    	}

            $scope.toggleSidenav = function (menuId) {
                $mdSidenav(menuId).toggle();
            };

            $scope.showEditPortfolioDialog = function() {
                $mdDialog.show({
                    controller: 'addPortfolioDialog',
                    templateUrl: 'views/addPortfolioDialog/addPortfolioDialog.html',
                    locals:{portfolio: portfolio}
                });
            };
            
            $scope.onDeleteChapter = function(chapter) {
                var deleteChapter = function() {
                    $scope.portfolio.chapters.splice($scope.portfolio.chapters.indexOf(chapter), 1);
                };
                    
                var confirm = dialogService.showDeleteConfirmationDialog(
                'PORTFOLIO_DELETE_CHAPTER_CONFIRM_TITLE', 
                'PORTFOLIO_DELETE_CHAPTER_CONFIRM_MESSAGE',
                deleteChapter);
            };

            $scope.savePortfolio = function() {
                $mdToast.show($mdToast.simple().position('right top').content('Portfolio saved!'));
            };
            
            init();
    	}
    ]);
});
