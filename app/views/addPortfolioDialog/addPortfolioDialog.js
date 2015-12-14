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
	            	$rootScope.savedPortfolio = portfolio;
	            	$mdDialog.hide();
	                $location.url('/portfolio/edit?id=' + portfolio.id);
	            }
			}
			
			function createPortfolioFailed(){
				log('Creating portfolio failed.');
			}
			
            $scope.update = function() {
                var url = "rest/portfolio/update";
                serverCallService.makePost(url, $scope.portfolio, updatePortfolioSuccess, createPortfolioFailed);
            }
            
            function updatePortfolioSuccess(portfolio) {
                if (isEmpty(portfolio)) {
                    createPortfolioFailed();
                } else {
                    $rootScope.savedPortfolio = portfolio;
                    $scope.portfolio = portfolio;
                    $mdDialog.hide();
                }
            }
            
            init();
        }
    ]);
});
