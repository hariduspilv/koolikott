define([
    'app',
    'services/serverCallService',
    'ngInfiniteScroll',
    'directives/materialBox/materialBox',
    'directives/portfolioBox/portfolioBox',
], function (app) {
    app.directive('dopInfiniteSearchResult', ['serverCallService',
        function () {
            return {
                scope: {
                    params: '=',
                    url: '='
                },
                templateUrl: 'directives/infiniteSearchResult/infiniteSearchResult.html',
                controller: function ($scope, serverCallService) {
                    $scope.start = 0;
                    $scope.searching = false;
                    var hasInitated = false;
                    var searchCount = 0;

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
                        if (hasInitated) search();
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
                        serverCallService.makeGet($scope.url, $scope.params, searchSuccess, searchFail);
                    }


                    function searchSuccess(data) {
                        if (isEmpty(data)) {
                            searchFail();
                        } else {
                            $scope.items.push.apply($scope.items, data.items);
                            searchCount++;
                            $scope.totalResults = data.totalResults;
                            //if less results then queried and less then received max then ask for more?
                        }

                        $scope.searching = false;
                    }

                    function searchFail() {
                        console.log('Search failed.');
                        $scope.searching = false;
                    }

                    init();
                }
            };
        }]);
});
