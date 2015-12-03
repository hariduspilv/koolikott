define(['app'], function(app)
{
    app.controller('portfolioController', ['$scope', 'translationService', 'serverCallService', '$route', '$location', 'alertService', '$rootScope', 'authenticatedUserService',
      function($scope, translationService, serverCallService, $route, $location, alertService, $rootScope, authenticatedUserService) {
          function init() {
              if ($rootScope.savedPortfolio) {
            	  $rootScope.portfolio = $rootScope.savedPortfolio;
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
            	  $rootScope.portfolio = portfolio;
                  increaseViewCount();
              }
          }
    
          function getPortfolioFail() {
              log('No data returned by getting portfolio.');
              alertService.setErrorAlert('ERROR_PORTFOLIO_NOT_FOUND');
              $location.url("/");
          }
    
          function increaseViewCount() {
              var params = {
                  'type' : '.Portfolio',
                  'id': $scope.portfolio.id
              };
    
              serverCallService.makePost("rest/portfolio/increaseViewCount", params, function success(){}, function fail(){});
          }
      
        $scope.addComment = function() {
            var url = "rest/comment/portfolio";
            var params = {
                'comment': $scope.newComment,
                'portfolio': {
                  'type' : '.Portfolio',
                  'id': $scope.portfolio.id
                }
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
      
        init();
    }]);
});
