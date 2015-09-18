define(['app'], function(app)
{
    app.controller('portfolioController', ['$scope', 'translationService', 'serverCallService', '$route', '$location', 'alertService', '$rootScope',
        function($scope, translationService, serverCallService, $route, $location, alertService, $rootScope) {

        	if ($rootScope.savedPortfolio){
            	$scope.portfolio = $rootScope.savedPortfolio;
        	} else {
            	var portfolioId = $route.current.params.id;
	        	serverCallService.makeGet("rest/portfolio?id=" + portfolioId, {}, getPortfolioSuccess, getPortfolioFail);
	        }
	    	
	        function getPortfolioSuccess(portfolio) {
	            if (isEmpty(portfolio)) {
	            	getPortfolioFail();
	            } else {
	                $scope.portfolio = portfolio;
	            }
	    	}
	    	
	    	function getPortfolioFail() {
	            log('No data returned by getting portfolio.');
	            alertService.setErrorAlert('ERROR_PORTFOLIO_NOT_FOUND');
	            $location.url("/");
	    	}

	    	$scope.formatDate = function(date) {
	    		return formatDateToDayMonthYear(date);
            }
    	}
    ]);
});