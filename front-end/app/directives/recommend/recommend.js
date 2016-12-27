'use strict'

angular.module('koolikottApp')
.directive('dopRecommend',
[
    'translationService', 'authenticatedUserService', 'serverCallService',
    function(translationService, authenticatedUserService, serverCallService) {
        return {
            scope: {
                material: '=',
                portfolio: '='
            },
            templateUrl: 'directives/recommend/recommend.html',
            controller: ['$scope', '$location', function($scope, $location) {

                $scope.recommend = function() {
                    if (authenticatedUserService.isAdmin()) {
                        if ($scope.material) {
                            var url = "rest/material/recommend";
                            serverCallService.makePost(url, $scope.material, querySuccess, queryFail);
                        } else if ($scope.portfolio) {
                            var url = "rest/portfolio/recommend";
                            serverCallService.makePost(url, $scope.portfolio, querySuccess, queryFail);
                        }
                    }
                }

                $scope.removeRecommendation = function() {
                    if (authenticatedUserService.isAdmin()) {
                        if ($scope.material) {
                            var url = "rest/material/removeRecommendation";
                            serverCallService.makePost(url, $scope.material, querySuccess, queryFail);
                        } else if ($scope.portfolio) {
                            var url = "rest/portfolio/removeRecommendation";
                            serverCallService.makePost(url, $scope.portfolio, querySuccess, queryFail);
                        }
                    }
                }

                function querySuccess(recommendation) {
                    if (isEmpty(recommendation)) {
                        recommendation = null;
                    }

                    if ($scope.material) {
                        $scope.material.recommendation = recommendation;
                    } else if ($scope.portfolio) {
                        $scope.portfolio.recommendation = recommendation;
                    }
                }

                function queryFail() {
                    log("Request failed");
                }

            }]
        };
    }
]);
