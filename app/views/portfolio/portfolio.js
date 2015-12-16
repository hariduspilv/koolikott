define(['app'], function(app)
{
    app.controller('portfolioController', ['$scope', 'translationService', 'serverCallService', '$route', '$location', 'alertService', '$rootScope', 'authenticatedUserService', '$timeout',
      function($scope, translationService, serverCallService, $route, $location, alertService, $rootScope, authenticatedUserService, $timeout) {
          
    	  var increaseViewCountPromise;
    	  
    	  function init() {
              if ($rootScope.savedPortfolio) {
            	  setPortfolio($rootScope.savedPortfolio);
                  increaseViewCount();
              } else {
                  getPortfolio(getPortfolioSuccess, getPortfolioFail);
              }
              
              $scope.newComment = {};
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
                  increaseViewCount();
              }
          }
    
          function getPortfolioFail() {
              log('No data returned by getting portfolio.');
              alertService.setErrorAlert('ERROR_PORTFOLIO_NOT_FOUND');
              $location.url("/");
          }
    
          function increaseViewCount() {
        	  /*
        	   *  It is needed to have it in a timeout because of double call caused by using two different page structure.
        	   *  So we cancel it in case the page is destroyed so the new one that will be create makes the valid call.
        	   */
        	  increaseViewCountPromise = $timeout(function() {
        		  var portfolio = createPortfolio($scope.portfolio.id);
        		  serverCallService.makePost("rest/portfolio/increaseViewCount", portfolio, function success(){}, function fail(){});
        	  }, 1000);
          }
      
        $scope.addComment = function() {
            var url = "rest/comment/portfolio";

        	var portfolio = createPortfolio($scope.portfolio.id);
            var params = {
                'comment': $scope.newComment,
                'portfolio': portfolio
            };
            serverCallService.makePost(url, params, addCommentSuccess, addCommentFailed);
        };
            
        function addCommentSuccess() {
            $scope.newComment.text = "";
            
            getPortfolio(function(portfolio) {
                $scope.portfolio = portfolio;
            }, function() {
                log("Comment success, but failed to reload portfolio.");
            });
        }
      
        function addCommentFailed(){
            log('Adding comment failed.');
        }
        
        function setPortfolio(portfolio) {
        	$scope.portfolio = portfolio;
            $rootScope.savedPortfolio = portfolio;;
        }

        $scope.$on('$routeChangeStart', function() {
        	if (!$location.url().startsWith("/portfolio/edit?id=")) {
        		setPortfolio(null);
        	}
	    });
        
        $scope.$on('$destroy', function() {
        	if (increaseViewCountPromise) {
        		$timeout.cancel(increaseViewCountPromise);
        	}
	    });
        
        init();
    }]);
});
