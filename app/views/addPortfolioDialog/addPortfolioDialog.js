define(['app'], function(app)
{
    app.controller('addPortfolioDialog', ['$scope', '$mdDialog', '$location', 'serverCallService', '$rootScope', 'portfolio',
        function($scope, $mdDialog, $location, serverCallService, $rootScope, portfolio) {
        
            function init() {
                $scope.portfolio = portfolio;

                if($scope.portfolio.id != null) {
                    $scope.isEditPortfolio = true;
                }
            }
            
            $scope.cancel = function() {
                $mdDialog.hide();
            };

            $scope.create = function() {
            	var url = "rest/portfolio/create";
                $scope.portfolio.picture = getPicture($scope.portfolio);

				serverCallService.makePost(url, $scope.portfolio, createPortfolioSuccess, createPortfolioFailed);
            };

            function getPicture(portfolio) {
                if (portfolio.picture) {
                    var base64Picture = portfolio.picture.$ngfDataUrl;
                }
                return base64Picture;
            }
            
            function createPortfolioSuccess(portfolio) {
            	if (isEmpty(portfolio)) {
            		createPortfolioFailed();
	            } else {
	            	$rootScope.portfolio = portfolio;
	            	$mdDialog.hide();
	                $location.url('/portfolio/edit?id=' + portfolio.id);
	            }
			}
			
			function createPortfolioFailed(){
				log('Creating portfolio failed.');
			}
            
            
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
                        };
                        
                        portfolioChapter.materials = [];
                        portfolioChapter.subchapters = [];
                        
                        list.push(chapterForm);

                    }
                }
                
                return list;
            }
            
            $scope.update = function() {
                var url = "rest/portfolio/update";
                var chapters = generateChapterForm($scope.portfolio.chapters);
                var taxon = $scope.portfolio.taxon;
                $scope.portfolio.taxon = null;
                $scope.portfolio.picture = getPicture($scope.portfolio);

                var params = {
                    'portfolio': $scope.portfolio,
                    'taxonId': taxon ? taxon.id : null,
                    'portfolioId': $scope.portfolio.id,
                    'chapters': chapters
                };

                serverCallService.makePost(url, params, updatePortfolioSuccess, createPortfolioFailed);
            }
            
            function updatePortfolioSuccess(portfolio) {
                if (isEmpty(portfolio)) {
                    createPortfolioFailed();
                } else {
                    $scope.portfolio.taxon = portfolio.taxon;
                    $scope.portfolio.chapters = portfolio.chapters;
                    $mdDialog.hide();
                }
            }
            
            init();
            
        }
    ]);
});
