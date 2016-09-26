define([
    'app',
    'services/serverCallService',
    'services/authenticatedUserService'
], function (app) {
    app.directive('dopFavorite', function (serverCallService, authenticatedUserService) {
        return {
            scope: {
                learningObject: '='
            },
            templateUrl: 'directives/favorite/favorite.html',
            controller: function ($scope, serverCallService, authenticatedUserService) {
                $scope.hasFavorited = false;

                if(isLoggedIn()) {
                    serverCallService.makeGet("rest/learningObject/favorite", {'id': $scope.learningObject.id}, getFavoriteSuccess, getFavoriteFail);
                }

                function getFavoriteSuccess(data) {
                    if (data && data.id) {
                        $scope.hasFavorited = true;
                    }
                }

                function getFavoriteFail() {
                    console.log("Getting info if the user has favorited failed");
                }

                $scope.favorite = function ($event) {
                    $event.preventDefault();
                    $event.stopPropagation();

                    if (isLoggedIn()) {
                        serverCallService.makePost("rest/learningObject/favorite", $scope.learningObject, addFavoriteSuccess, addFavoriteFail);
                        $scope.hasFavorited = true;
                    }
                };

                function addFavoriteSuccess(data) {
                    if (data && data.id) {
                        $scope.hasFavorited = true;
                    }
                }

                function addFavoriteFail() {
                    console.log("Adding as favorite failed");
                    $scope.hasFavorited = false;
                }

                $scope.removeFavorite = function ($event) {
                    $event.preventDefault();
                    $event.stopPropagation();

                    if (isLoggedIn() && $scope.hasFavorited) {
                        serverCallService.makeDelete("rest/learningObject/favorite", {'id': $scope.learningObject.id}, function () {
                        }, function () {
                        });
                        $scope.hasFavorited = false;
                    }
                };

                function isLoggedIn() {
                    return authenticatedUserService.isAuthenticated();
                }
            }
        };
    });
});
