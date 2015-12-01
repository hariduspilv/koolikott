define(['app'], function(app)
{
    app.directive('dopRating', function(translationService, $mdToast, $translate) {
        return {
            scope: {
                rating: '=',
                likeMessage: '@',
                dislikeMessage: '@'
            },
            templateUrl: 'directives/rating/rating.html',
            controller: function ($scope, $mdToast, $translate) {
                // todo: add actual logic
                $scope.isLiked = false;
                $scope.isDisliked = false;
                
                $scope.rating = {};
                $scope.rating.likes = 13;
                $scope.rating.dislikes = 4;
                
                $scope.like = function() {
                    if ($scope.isLiked) {
                        showToast($translate.instant('RATING_LIKE_REMOVED'));
                    } else {
                        showToast($scope.likeMessage);
                    }
                    
                    $scope.isLiked = !$scope.isLiked;
                    $scope.isDisliked = false;
                }
                
                $scope.dislike = function() {
                    if ($scope.isDisliked) {
                        showToast($translate.instant('RATING_DISLIKE_REMOVED'));
                    } else {
                        showToast($scope.dislikeMessage);
                    }
                    
                    $scope.isDisliked = !$scope.isDisliked;
                    $scope.isLiked = false;
                }
                
                function showToast(message) {
                    $mdToast.show($mdToast.simple().position('right top').content(message));
                }
            }
        };
    });

    return app;
});