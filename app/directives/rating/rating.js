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
                // todo: add actual logic
                $scope.isLiked = false;
                $scope.isDisliked = false;
                
                function init() {
                	setRatings();
                }
                
                function setRatings() {
                	$scope.rating = {};
                	if($scope.portfolio) {
	                    $scope.rating.likes = $scope.portfolio.likes;
	                    $scope.rating.dislikes = $scope.portfolio.dislikes;
                	}
                }
                
                $scope.like = function() {
                    if ($scope.isLiked) {
                        showToast($translate.instant('RATING_LIKE_REMOVED'));
                    }
                    else {
                    	var portfolio = createPortfolio($scope.portfolio.id);
                  		serverCallService.makePost("rest/portfolio/like", portfolio, likePortfolioSuccess, function() {});
                    }
                }
                
                function likePortfolioSuccess() {
                	$scope.isDisliked = !$scope.isDisliked;
                    $scope.isDisliked = false;
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
                	$scope.isDisliked = !$scope.isDisliked;
                    $scope.isDisliked = false;
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