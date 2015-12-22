define(['app'], function(app) {
  
    app.run(['$anchorScroll', function($anchorScroll) {
        $anchorScroll.yOffset = 50;
    }]);

    app.controller('editPortfolioController', ['$scope', 'translationService', 'serverCallService', '$route', '$location', 'alertService', '$rootScope', 'authenticatedUserService', 'dialogService', 'toastService', 'searchService', '$mdDialog', '$interval',
        function($scope, translationService, serverCallService, $route, $location, alertService, $rootScope, authenticatedUserService, dialogService, toastService, searchService, $mdDialog, $interval) {
            var isAutoSaving = false;
            var autoSaveInterval;

            function init() {
                if ($rootScope.savedPortfolio) {
                	if(checkAuthorized($rootScope.savedPortfolio)) {
	                    setPortfolio($rootScope.savedPortfolio);
	                    checkPortfolioVisibility($rootScope.savedPortfolio);
                	}
                } else {
                    getPortfolio(getPortfolioSuccess, getPortfolioFail);
                }
                
                searchService.setType("material");
                searchService.setTargetGroups([]);
                
                startAutosave();
            }
            
            function checkPortfolioVisibility(portfolio) {
                if (portfolio.visibility === 'PRIVATE') return;
                
                showWarning();
            }

            function getPortfolio(success, fail) {
                var portfolioId = $route.current.params.id;
                serverCallService.makeGet("rest/portfolio?id=" + portfolioId, {}, success, fail);
            }

	    	function getPortfolioFail() {
	            log('No data returned by getting portfolio.');
	            alertService.setErrorAlert('ERROR_PORTFOLIO_NOT_FOUND');
	            $location.url("/");
	    	}

            function getPortfolioSuccess(portfolio) {
                if (isEmpty(portfolio)) {
                    getPortfolioFail();
                } else if(checkAuthorized(portfolio)) {              
                    setPortfolio(portfolio);
                    checkPortfolioVisibility(portfolio);
                    searchService.setTargetGroups(portfolio.targetGroups);
                }
            }

            $scope.toggleSidenav = function(menuId) {
                $mdSidenav(menuId).toggle();
            };

            $scope.showEditPortfolioDialog = function() {
                $mdDialog.show({
                    controller: 'addPortfolioDialog',
                    templateUrl: 'views/addPortfolioDialog/addPortfolioDialog.html',
                    locals: {
                        portfolio: portfolio
                    }
                });
            };

            $scope.onDeleteChapter = function(chapter) {
                var deleteChapter = function() {
                    $scope.portfolio.chapters.splice($scope.portfolio.chapters.indexOf(chapter), 1);
                };

                dialogService.showDeleteConfirmationDialog(
                    'PORTFOLIO_DELETE_CHAPTER_CONFIRM_TITLE',
                    'PORTFOLIO_DELETE_CHAPTER_CONFIRM_MESSAGE',
                    deleteChapter);
            };

            $scope.savePortfolio = function() {
              isAutoSaving = false;
              
              updatePortfolio();
            }
            
            function updatePortfolio() {
                var url = "rest/portfolio/update";
                serverCallService.makePost(url, $scope.portfolio, updatePortfolioSuccess, updatePortfolioFailed);
            }

            function updatePortfolioSuccess(portfolio) {
                if (isEmpty(portfolio)) {
                    createPortfolioFailed();
                } else {
                    if(!isAutoSaving) setPortfolio(portfolio);
                    
                    var message = isAutoSaving ? 'PORTFOLIO_AUTOSAVED' : 'PORTFOLIO_SAVED';
                    toastService.show(message);
                }
            }

            function updatePortfolioFailed() {
                log('Updating portfolio failed.');
            }

            function setPortfolio(portfolio) {
                $scope.portfolio = portfolio;
                $rootScope.savedPortfolio = portfolio;
            }
            
            function showWarning() {
                var setPrivate = function() {
                    $scope.savedPortfolio.visibility = 'PRIVATE';
                    
                    updatePortfolio();
                }
                
                dialogService.showConfirmationDialog(
                     "{{'PORTFOLIO_MAKE_PRIVATE' | translate}}",
                    "{{'PORTFOLIO_WARNING' | translate}}",
                    "{{'PORTFOLIO_YES' | translate}}",
                    "{{'PORTFOLIO_NO' | translate}}",
                    setPrivate);
            }
            
            function checkAuthorized(portfolio) {
            	if(authenticatedUserService.getUser().id != portfolio.creator.id) {
            		$location.url("/");
            		return false;
            	}
            	return true;
            }
            
            function startAutosave() {                
                autoSaveInterval = $interval(function() {
                    isAutoSaving = true;
                    
                    updatePortfolio(); 
                }, 20000);
            }
            
            $scope.$on('$destroy', function() {
                $interval.cancel(autoSaveInterval);
            });

            init();
        }
    ]);
});
