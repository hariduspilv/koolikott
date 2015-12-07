define(['app'], function(app)
{
    app.run(['$anchorScroll', function($anchorScroll) {
      $anchorScroll.yOffset = 50;
    }]);

    app.controller('editPortfolioController', ['$scope', 'translationService', 'serverCallService', '$route', '$location', 'alertService', '$rootScope', 'authenticatedUserService', 'dialogService', 'toastService',
        function($scope, translationService, serverCallService, $route, $location, alertService, $rootScope, authenticatedUserService, dialogService, toastService) {

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

            function generateMaterialsList(materials) {
                var materialList = [];
                if(materials) {
                    for(var i=0; i<materials.length; i++) {
                        var material = materials[i];
                        materialList.push(material.id);
                    }
                }
                return materialList;
            }
            
            function generateChapterForm(chapters) {
                var list = [];
                
                if(chapters) {
                    for(var i=0; i<chapters.length; i++) {
                        
                        var portfolioChapter = chapters[i];        
                        var materialList = generateMaterialsList(chapters[i].materials);
                        var subchapters = generateChapterForm(portfolioChapter.subchapters);

                        var chapterForm = {
                            'chapter': portfolioChapter,
                            'materials': materialList,
                            'subchapters': subchapters
                        }
                        
                        portfolioChapter.materials = [];
                        portfolioChapter.subchapters = [];
                        
                        list.push(chapterForm);

                    }
                }
                
                return list;
            }
            
            $scope.savePortfolio = function() {
            	var url = "rest/portfolio/update";
                var chapters = generateChapterForm($scope.portfolio.chapters);
                var taxon = $scope.portfolio.taxon;
                $scope.portfolio.taxon = null;

                var params = {
                    'portfolio': $scope.portfolio,
                    'taxonId': taxon ? taxon.id : null,
                    'portfolioId': $scope.portfolio.id,
                    'chapters': chapters
                };

                serverCallService.makePost(url, params, updatePortfolioSuccess, updatePortfolioFailed);
            };
            
            function updatePortfolioSuccess(portfolio) {
                if (isEmpty(portfolio)) {
                    createPortfolioFailed();
                } else {
                    $scope.portfolio.taxon = portfolio.taxon;
                    $scope.portfolio.chapters = portfolio.chapters;
                    toastService.show("PORTFOLIO_SAVED");
                }
            }
            
            function updatePortfolioFailed(){
				log('Updagint portfolio failed.');
			}
            
            init();
    	}
    ]);
});
