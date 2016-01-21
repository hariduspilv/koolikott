define(['app'], function(app)
{
    app.directive('dopRecommend', ['translationService', 'authenticatedUserService', 'serverCallService',
     function(translationService, authenticatedUserService, serverCallService) {
        return {
        	scope: {
                material: '=',
                portfolio: '='
            },
            templateUrl: 'directives/recommend/recommend.html',
        	controller: function ($scope, $location) {
        		
            	$scope.recommend = function() {
            		if($scope.material && $scope.isAdmin()) {
            			var url = "rest/material/recommend";
                        serverCallService.makePost(url, $scope.material, querySuccess, queryFail);
            		}
            		if($scope.portfolio && $scope.isAdmin()) {
            			var url = "rest/portfolio/recommend";
                        serverCallService.makePost(url, $scope.portfolio, querySuccess, queryFail);
            		}
            	}
            	
            	$scope.removeRecommendation = function() {	
            		if($scope.material && $scope.isAdmin()) {
            			var url = "rest/material/removeRecommendation";
                        serverCallService.makePost(url, $scope.material, querySuccess, queryFail);
            		}
            		if($scope.portfolio && $scope.isAdmin()) {
            			var url = "rest/portfolio/removeRecommendation";
                        serverCallService.makePost(url, $scope.portfolio, querySuccess, queryFail);
            		}
            	}
                
            	function querySuccess(data) {
            		if($scope.material && $scope.material.recommended != null) {
	            		$scope.material.recommended = !$scope.material.recommended ;
	            		$scope.isRecommended = !$scope.isRecommended;
            		}
            		if($scope.portfolio && $scope.portfolio.recommended != null) {
	            		$scope.portfolio.recommended = !$scope.portfolio.recommended ;
	            		$scope.isRecommended = !$scope.isRecommended;
            		}
            	}
            	
            	function queryFail() {
            		log("Request failed");
            	}
            	
            	$scope.isAdmin = function() {
                    return authenticatedUserService.getUser() && authenticatedUserService.getUser().role === 'ADMIN';
                };
                
                $scope.$watch("material", function(newValue, oldValue) {
                	if(newValue && newValue.recommended != null) {
	                	$scope.material = newValue;
	                	$scope.isRecommended = $scope.material.recommended;
                	}
                });
                
                $scope.$watch("portfolio", function(newValue, oldValue) {
                	log(newValue);
                	if(newValue && newValue.recommended != null) {
	                	$scope.portfolio = newValue;
	                	$scope.isRecommended = $scope.portfolio.recommended;
                	}
                });
                
            }
        };
    }]);

    return app;
});