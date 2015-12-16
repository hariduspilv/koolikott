define(['app'], function(app)
{
    app.directive('dopRating', function(translationService, $mdToast, $translate, serverCallService) {
        return {
            scope: {
            	portfolio: '=',
                likeMessage: '@',
                dislikeMessage: '@'
            },
            templateUrl: 'directives/rating/rating.html',
            controller: function ($scope, $mdToast, $translate, serverCallService) {

                function init() {
                	setRatings();
                	getUserLike();
                }
                
                function getUserLike() {
                	var portfolio = createPortfolio($scope.portfolio.id);
                	serverCallService.makePost("rest/portfolio/getUserLike", portfolio, getUserLikeSuccess, function() {});
                }
                
                function getUserLikeSuccess(userlike) {
            		if(userlike.liked) {
            			$scope.isLiked = true;
            		} else {
                        $scope.isDisliked = true;
            		}
                }
                
                function setRatings() {
                	$scope.rating = {};
                	if($scope.portfolio) {
	                    $scope.rating.likes = $scope.portfolio.likes;
	                    $scope.rating.dislikes = $scope.portfolio.dislikes;
                	}
                }
                
                $scope.like = function() {
                	var portfolio = createPortfolio($scope.portfolio.id);
                    if ($scope.isLiked) {
                        serverCallService.makePost("rest/portfolio/removeUserLike", portfolio, removeUserLikePortfolioSuccess, function() {});
                    }
                    else {
                  		serverCallService.makePost("rest/portfolio/like", portfolio, likePortfolioSuccess, function() {});
                    }
                }
                
                function removeUserLikePortfolioSuccess() {
                	$scope.isLiked = false;
                    $scope.portfolio.likes -= 1;
                    setRatings();
                	showToast($translate.instant('RATING_LIKE_REMOVED'));
                }
                
                function likePortfolioSuccess() {
                    $scope.isLiked = true;
                    $scope.portfolio.likes += 1;
                    setRatings();
                    showToast($scope.likeMessage);
                }
                
                $scope.dislike = function() {
                    if ($scope.isDisliked) {
                        showToast($translate.instant('RATING_DISLIKE_REMOVED'));
                    } else {
	                    var portfolio = createPortfolio($scope.portfolio.id);
	              		serverCallService.makePost("rest/portfolio/dislike", portfolio, dislikePortfolioSuccess, function() {});
                    }
                    $scope.isDisliked = !$scope.isDisliked;
                    $scope.isLiked = false;
                }
                
                function dislikePortfolioSuccess() {
                    $scope.isDisliked = true;
                    $scope.portfolio.dislikes += 1;
                    setRatings();
                    showToast($scope.dislikeMessage);
                }

                function showToast(message) {
                    $mdToast.show($mdToast.simple().position('right top').content(message));
                }
                
                init();
            }
        };
    });

    return app;
});