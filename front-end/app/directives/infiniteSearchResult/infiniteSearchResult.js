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

                    var isFirstLoad = true;
                    var hasInitated = false;
                    var searchCount = 0;
                    var expectedItemCount = 15; // on first load 15 items should be loaded

                    // Pagination variables
                    var maxResults = $scope.params.maxResults || $scope.params.limit;
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
                            $scope.start = searchCount * maxResults;
                        }
                        $scope.params.start = $scope.start;

                        if (isFirstLoad) {
                            $scope.params.limit = 20;
                        } else {
                            $scope.params.limit = 15;
                        }
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
