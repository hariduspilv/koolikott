define(['app'], function(app)
{
    app.controller('portfolioController', ['$scope', 'translationService', 'serverCallService', '$route', '$location', 'alertService', '$rootScope',
        function($scope, translationService, serverCallService, $route, $location, alertService, $rootScope) {
            // TEST DATA

            $scope.comments = [{
                author: "Mari Maasikas",
                date: "24.11.2015 14:21",
                text: "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Curabitur vitae gravida velit, at aliquet neque. Duis at pellentesque leo"
            }, {
                author: "Ants Kaalikas",
                date: "24.11.2015 10:02",
                text: "Nunc pharetra eu velit ut scelerisque. Duis enim odio, consectetur ac accumsan id, mattis et justo."
            }];

            // END TEST DATA

        	if ($rootScope.savedPortfolio){
            	$scope.portfolio = $rootScope.savedPortfolio;
            	init();
        	} else {
            	var portfolioId = $route.current.params.id;
	        	serverCallService.makeGet("rest/portfolio?id=" + portfolioId, {}, getPortfolioSuccess, getPortfolioFail);
	        }

	        function getPortfolioSuccess(portfolio) {
	            if (isEmpty(portfolio)) {
	            	getPortfolioFail();
	            } else {
	                $scope.portfolio = portfolio;
	                init();
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

            function init() {
            	var params = {
					'type' : '.Portfolio',
                	'id': $scope.portfolio.id
            	};

            	serverCallService.makePost("rest/portfolio/increaseViewCount", params, function success(){}, function fail(){});
    		}
    	}
    ]);
});
