define([
    'app',
    'services/serverCallService',
    'ngInfiniteScroll',
    'directives/materialBox/materialBox',
    'directives/portfolioBox/portfolioBox'
], function (app) {
    app.directive('dopInfiniteSearchResult', ['serverCallService',
        function () {
            return {
                scope: {
                    params: '=',
                    url: '=',
                    items: '=',
                    accessor: '='
                },
                templateUrl: 'directives/infiniteSearchResult/infiniteSearchResult.html',
                controller: function ($scope, serverCallService) {
                    $scope.start = 0;
                    $scope.searching = false;
                    $scope.accessor = {};

                    const FIRST_SEARCH_AMOUNT = 20; // on first load 20 items should be loaded

                    var isFirstLoad = true;
                    var hasInitated = false;
                    var searchCount = 0;
                    var expectedItemCount = FIRST_SEARCH_AMOUNT;

                    // Pagination variables
                    var maxResults = $scope.params ? $scope.params.maxResults || $scope.params.limit : null;
                    $scope.items = [];

                    function init() {
                        if (isEmpty($scope.url)) $scope.url = "rest/search";
                        if (!$scope.params) {
                            $scope.params = {};
                        }

                        search();
                    }

                    $scope.nextPage = function () {
                        if (hasInitated) {
                            search();
                        }
                        hasInitated = true;
                    };

                    $scope.allResultsLoaded = function () {
                        return allResultsLoaded();
                    };

                    function allResultsLoaded() {
                        return $scope.items.length >= $scope.totalResults;
                    }


                    function search() {
                        if ($scope.searching || allResultsLoaded()) return;
                        $scope.searching = true;

                        if ($scope.items.length === 0) {
                            $scope.start = 0;
                        } else {
                            // because first search is 20 items
                            // +5 has to be added to every search
                            $scope.start = searchCount * maxResults + 5;
                        }

                        if (isFirstLoad) {
                            $scope.params.limit = FIRST_SEARCH_AMOUNT;
                            $scope.params.maxResults = FIRST_SEARCH_AMOUNT;
                        } else {
                            $scope.params.limit = 15;
                            $scope.params.maxResults = 15;
                        }

                        $scope.params.start = $scope.start;

                        serverCallService.makeGet($scope.url, $scope.params, searchSuccess, searchFail);
                    }


                    function searchSuccess(data) {
                        if (!data || !data.items || data.items.length === 0) {
                            searchFail();
                        } else {
                            $scope.items.push.apply($scope.items, data.items);
                            searchCount++;
                            $scope.totalResults = data.totalResults;

                            $scope.searching = false;
                            $scope.accessor.ready = true;

                            isFirstLoad = false;

                            if ($scope.items.length < expectedItemCount) {
                                search();
                            } else {
                                expectedItemCount += maxResults;
                            }
                        }
                    }

                    function searchFail() {
                        console.log('Search failed.');
                        $scope.searching = false;
                        $scope.accessor.ready = true;
                    }

                    init();
                }
            };
        }]);
});
