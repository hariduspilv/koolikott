define([
    'angularAMD',
    'services/translationService',
    'services/serverCallService',
    'services/searchService',
    'services/toastService',
    'services/suggestService',
    'directives/copyPermalink/copyPermalink'
], function (angularAMD) {
    angularAMD.directive('dopEditPortfolioModeHeader', ['translationService', '$location', '$mdSidenav', '$mdDialog', '$rootScope', 'serverCallService', 'searchService', 'toastService', '$timeout', '$route', 'suggestService', '$http',
        function (translationService, $location, $mdSidenav, $mdDialog, $rootScope, serverCallService, searchService, toastService, $timeout, $route, suggestService, $http) {
            return {
                scope: true,
                templateUrl: 'directives/editPortfolioModeHeader/editPortfolioModeHeader.html',
                controller: function ($scope, $location, $rootScope) {
                    $scope.suggest = {};
                    $scope.suggest.suggestions = null;
                    $scope.suggest.selectedItem = null;
                    var dontSearch = false;

                    $scope.toggleSidenav = function () {
                        $mdSidenav('left').toggle();
                    };

                    $scope.makePublic = function () {
                        $rootScope.savedPortfolio.visibility = 'PUBLIC';
                        updatePortfolio();

                        toastService.show('PORTFOLIO_HAS_BEEN_MADE_PUBLIC');
                    };

                    $scope.makeNotListed = function () {
                        $rootScope.savedPortfolio.visibility = 'NOT_LISTED';
                        updatePortfolio();
                    };

                    $scope.makePrivate = function () {
                        $rootScope.savedPortfolio.visibility = 'PRIVATE';
                        updatePortfolio();
                    };

                    $scope.getShareUrl = buildShareUrl();

                    function buildShareUrl() {
                        var protocol = $location.protocol();
                        var host = $location.host();
                        var path = '/portfolio'
                        var params = $location.search();

                        return protocol + '://' + host + path + '?id=' + params.id;
                    }

                    function updatePortfolio() {
                        var url = "rest/portfolio/update";
                        serverCallService.makePost(url, $rootScope.savedPortfolio, updatePortfolioSuccess, updatePortfolioFailed);
                    }

                    function updatePortfolioSuccess(portfolio) {
                        if (isEmpty(portfolio)) {
                            updatePortfolioFailed();
                        } else {
                            log('Portfolio updated.');
                        }
                    }

                    function updatePortfolioFailed() {
                        log('Updating portfolio failed.');
                    }

                    // Search

                    $scope.searchFields = {};
                    $scope.searchFields.searchQuery = "";
                    $scope.detailedSearch = {};
                    $scope.detailedSearch.accessor = {
                        clearSimpleSearch: function () {
                            $scope.searchFields.searchQuery = '';
                        }
                    };

                    $scope.search = function () {
                        searchService.setSearch($scope.searchFields.searchQuery);
                        searchService.clearFieldsNotInSimpleSearch();
                        searchService.setType('material');
                        $location.url(searchService.getURL());
                        $route.reload();

                    };

                    $scope.openDetailedSearch = function () {
                        $scope.detailedSearch.isVisible = true;
                        // $scope.detailedSearch.queryIn = $scope.searchFields.searchQuery;
                    };

                    $scope.closeDetailedSearch = function () {
                        $timeout(function () {
                            $scope.detailedSearch.accessor.clear();
                        }, 500);
                        dontSearch = true;
                        $scope.detailedSearch.isVisible = false;
                        $scope.detailedSearch.queryIn = null;
                    };

                    $scope.saveAndExitPortfolio = function () {
                        var url = "rest/portfolio/update";
                        serverCallService.makePost(url, $rootScope.savedPortfolio, saveAndExitPortfolioSuccess, updatePortfolioFailed);
                    };


                    function saveAndExitPortfolioSuccess(portfolio) {
                        if (!isEmpty(portfolio)) {
                            toastService.show('PORTFOLIO_SAVED');
                            $rootScope.savedPortfolio = null;
                            $location.url('/portfolio?id=' + portfolio.id);
                            $route.reload();
                        }
                    }

                    $scope.clickOutside = function () {
                        if ($scope.detailedSearch.isVisible && !$rootScope.dontCloseSearch) {
                            $scope.closeDetailedSearch();
                        } else if ($rootScope.dontCloseSearch) {
                            $rootScope.dontCloseSearch = false;
                        }
                    };

                    $scope.suggest.doSuggest = function (query) {
                        if (query == null) {
                            return [];
                        }
                        return $http.get(suggestService.getURL(query), {cache: true}).then(function (response) {
                            return response.data.alternatives || [];
                        });
                    };

                    $scope.$watch('detailedSearch.mainField', function (newValue, oldValue) {
                        if (newValue != oldValue) {
                            $scope.searchFields.searchQuery = newValue || "";
                        }
                    }, true);

                    $scope.$watch('searchFields.searchQuery', function (newValue, oldValue) {
                        $scope.searchFields.searchQuery = newValue || "";
                        if (newValue != oldValue && !$scope.detailedSearch.isVisible && !dontSearch) {
                            $scope.search();
                        } else if ($scope.detailedSearch.isVisible) {
                            $scope.detailedSearch.queryIn = $scope.searchFields.searchQuery;
                        }

                        if (dontSearch) dontSearch = false;
                    }, true);

                }
            };
        }]);
});
