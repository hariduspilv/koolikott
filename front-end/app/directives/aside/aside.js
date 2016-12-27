'use strict';

angular.module('koolikottApp').directive('dopAside',
    function () {
        return {
            scope: {
                learningObject: '=',
            },
            templateUrl: 'directives/aside/aside.html',
            controller: ['$scope', 'serverCallService', '$location', 'searchService', '$timeout',
                function ($scope, serverCallService, $location, searchService, $timeout) {
                    const SIDE_ITEMS_AMOUNT = 5;
                    $scope.similar = [{}, {}, {}, {}, {}];
                    $scope.recommendations = [{}, {}, {}, {}, {}];

                    $timeout(init());

                    function init() {
                        loadRecommendations();
                    }

                    $scope.$watch('learningObject', function (newValue) {
                        if (newValue && newValue.id) loadSimilar()
                    }, true);

                    function getObjectIds(objects) {
                        let result = [];

                        if (objects) {
                            objects.forEach(function (object) {
                                result.push(object.id);
                            })
                        }

                        return result;
                    }

                    function loadSimilar() {
                        let q = "";

                        if ($scope.learningObject.tags) {
                            $scope.learningObject.tags.forEach(function (tag) {
                                q = q.concat('tag:\"' + tag + '\" ')
                            });
                        }

                        let params = {
                            q: q,
                            isORSearch: "true",
                            excluded: $scope.learningObject.id,
                            taxon: getObjectIds($scope.learningObject.taxons),
                            targetGroup: $scope.learningObject.targetGroups,
                            crossCurricularTheme: getObjectIds($scope.learningObject.crossCurricularThemes),
                            keyCompetence: getObjectIds($scope.learningObject.keyCompetences),
                            start: 0,
                            limit: SIDE_ITEMS_AMOUNT
                        };

                        serverCallService.makeGet("rest/search", params, getSimilarSuccess, getSimilarFail, true);
                    }

                    function getSimilarSuccess(data) {
                        if (isEmpty(data)) {
                            console.log('No data returned by similar item search.');
                        } else {
                            $scope.similar = data.items;
                        }
                    }

                    function getSimilarFail() {
                        console.log('Getting similar objects failed.')
                    }

                    function loadMostLiked() {
                        if (isSearchResultPage()) {
                            loadMostLikedOrderedByLikes();
                        } else {
                            let params = {
                                maxResults: SIDE_ITEMS_AMOUNT
                            };

                            serverCallService.makeGet("rest/search/mostLiked", params, getMostLikedSuccess, getMostLikedFail);
                        }
                    }

                    function isSearchResultPage() {
                        return $location.url().startsWith('/' + searchService.getSearchURLbase());
                    }

                    function loadRecommendations() {

                        let params = {
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

                    function getRecommendationsFail() {
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
                        let params = {
                            limit: SIDE_ITEMS_AMOUNT
                        };

                        let originalSort = searchService.getSort();
                        let originalSortDirection = searchService.getSortDirection();
                        searchService.setSort('like_score');
                        searchService.setSortDirection('desc');
                        let searchUrl = searchService.getQueryURL(true);
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
                }]
        }
    }
);
