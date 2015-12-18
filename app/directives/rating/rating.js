define(['app'], function(app)
{
    app.directive('dopRating', function(translationService, $mdToast, $translate, serverCallService) {
        return {
            scope: {
            	material: '=',
            	portfolio: '=',
                likeMessage: '@',
                dislikeMessage: '@'
            },
            templateUrl: 'directives/rating/rating.html',
            controller: function ($scope, $mdToast, $translate, serverCallService) {

                function init() {
                	$scope.allowRequests = false;
                    $scope.likeFunction = function() {}   
                    $scope.dislikeFunction = function() {}
                    $scope.rating = {};
                    
                    if($scope.portfolio) {
	                	$scope.rating.likes = $scope.portfolio.likes;
	                	$scope.rating.dislikes = $scope.portfolio.dislikes;
	                	
	                	$scope.entity = $scope.portfolio;
	                	$scope.url = "rest/portfolio/";
                    }
                    if($scope.material) {
                    	log($scope.material);
	                	$scope.rating.likes = $scope.material.likes;
	                	$scope.rating.dislikes = $scope.material.dislikes;
	                	
	                	$scope.entity = $scope.material;
	                	$scope.url = "rest/material/";  	
                    }
                    
                    if($scope.entity && $scope.entity.type) {
                    	getUserLike();
                    }
                }

                $scope.like = function() {
                	if($scope.allowRequests) {
                		$scope.allowRequests = false;
                		stateMachine();
                		$scope.likeFunction();
                	}
                }   
                
                $scope.dislike = function() {
                	if($scope.allowRequests) {
                		$scope.allowRequests = false;
                		stateMachine();
                		$scope.dislikeFunction();
                	}
                }
                
                function getUserLike() {
                	log($scope.material);
                	log($scope.entity);
                	serverCallService.makePost($scope.url+"getUserLike", $scope.entity, getUserLikeSuccess, function() {});
                }
                
                function getUserLikeSuccess(userlike) {
                	if(userlike) {
	            		if(userlike.liked) {
	            			$scope.isLiked = true;
	            		} else {
	                        $scope.isDisliked = true;
	            		}
                	}
            		requestSuccessful();
                }
         
                function like() {
                  	serverCallService.makePost($scope.url+"like", $scope.entity, requestSuccessful, requestFailed);
                  	$scope.isLiked = true;
                    $scope.rating.likes += 1;
                }
                
                function dislike() {
                  	serverCallService.makePost($scope.url+"dislike", $scope.entity, requestSuccessful, requestFailed);
                    $scope.isDisliked = true;
                    $scope.rating.dislikes += 1;
                }
                
                function removeLike() {
                  	serverCallService.makePost($scope.url+"removeUserLike", $scope.entity, requestSuccessful, requestFailed);      	
                    $scope.isLiked = false;
                	$scope.isDisliked = false;
                	$scope.rating.likes -= 1;
                }
                
                function removeDislike() {
                  	serverCallService.makePost($scope.url+"removeUserLike", $scope.entity, requestSuccessful, requestFailed);
                    $scope.isLiked = false;
                	$scope.isDisliked = false;
                	$scope.rating.dislikes -= 1;
                }
                
                function switchRating() {
                	if($scope.isLiked) {
                		serverCallService.makePost($scope.url+"dislike", $scope.entity, requestSuccessful, requestFailed);
                		$scope.isLiked = false;
                		$scope.isDisliked = true;
                		$scope.rating.likes -= 1;
                		$scope.rating.dislikes += 1;
                	} else {
                		serverCallService.makePost($scope.url+"like", $scope.entity, requestSuccessful, requestFailed);
                		$scope.isLiked = true;
                		$scope.isDisliked = false;
                		$scope.rating.likes += 1;
                		$scope.rating.dislikes -= 1;
                	} 
                }

                
                function stateMachine() {
                	if($scope.isLiked) {
                		$scope.likeFunction = removeLike;
                		$scope.dislikeFunction = switchRating;
                	} else if($scope.isDisliked) {
                		$scope.likeFunction = switchRating;
                		$scope.dislikeFunction = removeDislike;
                	} else {
                		$scope.likeFunction = like;
                		$scope.dislikeFunction = dislike;
                	}
                }
                
                function requestSuccessful() {    	
                	$scope.allowRequests = true;
                }
                
                function requestFailed() {
                	$scope.allowRequests = true;
                }
                
                init();
            }
        };
    });

    return app;
});