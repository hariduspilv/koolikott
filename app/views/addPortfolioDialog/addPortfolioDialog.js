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
            	var taxon = $scope.portfolio.taxon;
            	$scope.portfolio.taxon = null;
            	
				var params = {
					'taxonId': taxon ? taxon.id : null,
					'portfolio': $scope.portfolio
				};
				serverCallService.makePost(url, params, createPortfolioSuccess, createPortfolioFailed);
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
            
            $scope.update = function() {
                var url = "rest/portfolio/update";
                var taxon = $scope.portfolio.taxon;
                $scope.portfolio.taxon = null;

                var params = {
                    'taxonId': taxon ? taxon.id : null,
                    'portfolio': $scope.portfolio
                };
                serverCallService.makePost(url, params, updatePortfolioSuccess, createPortfolioFailed);
            }
            
            function updatePortfolioSuccess(portfolio) {
                if (isEmpty(portfolio)) {
                    createPortfolioFailed();
                } else {
                    $scope.portfolio.taxon = portfolio.taxon;
                    $mdDialog.hide();
                }
            }
            
            init();
            
        }
    ]);
});
