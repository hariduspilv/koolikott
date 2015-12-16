define(['app'], function(app)
{
    app.run(['$anchorScroll', function($anchorScroll) {
      $anchorScroll.yOffset = 50;
    }]);

    app.controller('editPortfolioController', ['$scope', 'translationService', 'serverCallService', '$route', '$location', 'alertService', '$rootScope', 'authenticatedUserService', 'dialogService', 'toastService', 'searchService',
        function($scope, translationService, serverCallService, $route, $location, alertService, $rootScope, authenticatedUserService, dialogService, toastService, searchService) {

            function init() {
            	if ($rootScope.savedPortfolio) {
					setPortfolio($rootScope.savedPortfolio);
				} else {
					getPortfolio(getPortfolioSuccess, getPortfolioFail);
				}
            	
				searchService.setType("material");
				searchService.setTargetGroups([]);
			}

			function getPortfolio(success, fail) {
				var portfolioId = $route.current.params.id;
				serverCallService.makeGet("rest/portfolio?id=" + portfolioId, {}, success, fail);
			}

	        function getPortfolioSuccess(portfolio) {
	            if (isEmpty(portfolio)) {
	            	getPortfolioFail();
	            } else {
	            	setPortfolio(portfolio);    
	                searchService.setTargetGroups(portfolio.targetGroups);
	            }
	    	}

	    	function getPortfolioFail() {
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
            	var url = "rest/portfolio/update";
                serverCallService.makePost(url, $scope.portfolio, updatePortfolioSuccess, updatePortfolioFailed);
            };
            
            function updatePortfolioSuccess(portfolio) {
                if (isEmpty(portfolio)) {
                    createPortfolioFailed();
                } else {
                	setPortfolio(portfolio);
                    toastService.show("PORTFOLIO_SAVED");
                }
            }
            
            function updatePortfolioFailed(){
				log('Updating portfolio failed.');
			}
            
            function setPortfolio(portfolio) {
            	$scope.portfolio = portfolio;
                $rootScope.savedPortfolio = portfolio;
            }
            
            init();
    	}
    ]);
});
