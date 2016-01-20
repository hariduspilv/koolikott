define(['app'], function(app)
{
    app.directive('dopRecommend', ['translationService', 'authenticatedUserService', 'serverCallService',
     function(translationService, authenticatedUserService, serverCallService) {
        return {
        	scope: {
                material: '=',
            },
            templateUrl: 'directives/recommend/recommend.html',
        	controller: function ($scope, $location) {
        		
            	$scope.recommend = function() {
            		var url = "rest/material/recommend";
            		if($scope.material && $scope.isAdmin()) {
                        serverCallService.makePost(url, $scope.material, querySuccess, queryFail);
            		}
            	}
            	
            	$scope.removeRecommendation = function() {
            		var url = "rest/material/removeRecommendation";
            		if($scope.material && $scope.isAdmin()) {
                        serverCallService.makePost(url, $scope.material, querySuccess, queryFail);
            		}
            	}
                
            	function querySuccess(data) {
            		$scope.material.recommended = !$scope.material.recommended ;
            		$scope.isRecommended = !$scope.isRecommended;
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
                
            }
        };
    }]);

    return app;
});