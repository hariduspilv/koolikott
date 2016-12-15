'use strict'

angular.module('koolikottApp')
.directive('dopSidebar',
[
    'serverCallService', '$location', 'searchService',
    function () {
        return {
            scope: true,
            templateUrl: 'directives/sidebar/sidebar.html',
            controller: function ($scope, serverCallService, $location, searchService, $timeout) {
                var SIDE_ITEMS_AMOUNT = 5;

                $timeout(init());

                function init() {
                    loadRecommendations();
                    loadMostLiked();
                }

                function loadMostLiked() {
                    if (isSearchResultPage()) {
                        loadMostLikedOrderedByLikes();
                    } else {
                        var params = {
                            maxResults: SIDE_ITEMS_AMOUNT
                        };

                        serverCallService.makeGet("rest/search/mostLiked", params, getMostLikedSuccess, getMostLikedFail);
                    }
                }

                function isSearchResultPage() {
                    return $location.url().startsWith('/' + searchService.getSearchURLbase());
                }

                function loadRecommendations() {

                    var params = {
                        q: 'recommended:true',
                        start: 0,
                        sort: 'recommendation_timestamp',
                        sortDirection: 'desc',
                        limit: SIDE_ITEMS_AMOUNT
                    };

                    serverCallService.makeGet("rest/search", params, getRecommendationsSuccess, getRecommendationsFail);
                }

                function getRecommendationsSuccess(data) {
                    if (isEmpty(data)) {
                        log('No data returned by recommended item search.');
                    } else {
                        $scope.recommendations = data.items;
                    }
                }

                function getRecommendationsFail(data, status) {
                    console.log('Session search failed.')
                }

                function getMostLikedSuccess(data) {
                    if (isEmpty(data)) {
                        getMostLikedFail();
                    } else {
                        $scope.mostLikedList = data;
                    }
                }

                function getMostLikedFail() {
                    console.log('Most liked search failed.')
                }

                function searchMostLikedSuccess(data) {
                    if (isEmpty(data)) {
                        getMostLikedFail();
                    } else {
                        $scope.mostLikedList = data.items;
                    }
                }

                $scope.showMoreRecommendations = function () {
                    searchService.setSearch('recommended:true');
                    searchService.clearFieldsNotInSimpleSearch();
                    searchService.setSort('recommendation_timestamp');
                    searchService.setSortDirection('desc');

                    $location.url('/' + searchService.getSearchURLbase() + searchService.getQueryURL());
                };

                function loadMostLikedOrderedByLikes() {
                    var params = {
                        limit: SIDE_ITEMS_AMOUNT
                    };

                    var originalSort = searchService.getSort();
                    var originalSortDirection = searchService.getSortDirection();
                    searchService.setSort('like_score');
                    searchService.setSortDirection('desc');
                    var searchUrl = searchService.getQueryURL(true);
                    searchService.setSort(originalSort);
                    searchService.setSortDirection(originalSortDirection);

                    serverCallService.makeGet("rest/search?" + searchUrl, params, searchMostLikedSuccess, getMostLikedFail);
                }

                $scope.$watch(function () {
                    return $location.search();
                }, function (newValue, oldValue) {
                    if (newValue !== oldValue && newValue.q && isSearchResultPage()) {
                        loadMostLiked();
                    }

                }, true);
            }
        }
    }
]);
