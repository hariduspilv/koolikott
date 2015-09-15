define(['app'], function(app)
{
    app.controller('portfolioController', ['$scope', 'translationService', 'serverCallService', '$route', '$location', 'alertService',
        function($scope, translationService, serverCallService, $route, $location, alertService) {

	    	var portfolioId = $route.current.params.id;
	        serverCallService.makeGet("rest/portfolio?id=" + portfolioId, {}, getPortfolioSuccess, getPortfolioFail);
	        
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

	    	$scope.formatPortfolioCreatedDate = function(createdDate) {
	    		return formatDatetoDayMonthYear(createdDate);
            }
    	}
    ]);
});