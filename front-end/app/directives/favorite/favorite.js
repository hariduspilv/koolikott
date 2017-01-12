'use strict'

angular.module('koolikottApp')
.directive('dopFavorite',
[
    'serverCallService', 'authenticatedUserService', 'toastService', '$timeout', 'materialService', 'portfolioService',
    function (serverCallService, authenticatedUserService, toastService, $timeout, materialService, portfolioService) {
        return {
            scope: {
                learningObject: '='
            },
            templateUrl: 'directives/favorite/favorite.html',
            controller: ['$scope', '$rootScope', function ($scope, $rootScope) {
                $scope.isEditPortfolioMode = $rootScope.isEditPortfolioMode;
                $scope.isViewMaterialOrPortfolioPage = $rootScope.isViewMaterialOrPortfolioPage;

                $timeout(function () {
                    if ($scope.learningObject && isLoggedIn()) {
                        if ($scope.learningObject.favorite) {
                            $scope.hasFavorited = true;
                        } else if ($scope.learningObject.favorite == null) {
                            serverCallService.makeGet("rest/learningObject/favorite", {'id': $scope.learningObject.id}, getFavoriteSuccess, getFavoriteFail);
                        }
                    }
                }, 0);

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

                        if (isPortfolio($scope.learningObject.type)) {
                            portfolioService.getPortfolioById($scope.learningObject.id)
                                .then(data => {
                                    serverCallService.makePost("rest/learningObject/favorite", data, addFavoriteSuccess, addFavoriteFail);
                                });
                        } else if (isMaterial($scope.learningObject.type)) {
                            materialService.getMaterialById($scope.learningObject.id)
                                .then(data => {
                                    serverCallService.makePost("rest/learningObject/favorite", data, addFavoriteSuccess, addFavoriteFail);
                                });
                        }


                        $scope.hasFavorited = true;
                    }
                };

                function addFavoriteSuccess(data) {
                    if (data && data.id) {
                        toastService.show("ADDED_TO_FAVORITES");
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
                        toastService.show("REMOVED_FROM_FAVORITES");

                    }
                };

                function isLoggedIn() {
                    return authenticatedUserService.isAuthenticated();
                }
            }]
        };
    }
]);
