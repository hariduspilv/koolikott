define([
    'angularAMD',
    'services/serverCallService',
    'services/authenticationService',
    'services/searchService',
    'services/translationService',
    'services/suggestService',
    'services/serverCallService',
    'services/toastService',
    'directives/copyPermalink/copyPermalink'
], function (angularAMD, $http) {
    angularAMD.directive('dopHeader', ['translationService', '$location', 'searchService', 'authenticationService', 'authenticatedUserService', '$timeout', '$mdDialog', 'suggestService', 'serverCallService', 'toastService', '$route','$http',
        function (translationService, $location, searchService, authenticationService, authenticatedUserService, $timeout, $mdDialog, suggestService, serverCallService, toastService, $route, $http) {
            return {
                scope: true,
                templateUrl: 'directives/header/header.html',
                controller: function ($scope, $location, authenticationService, authenticatedUserService, $rootScope, $anchorScroll) {

                    $scope.detailedSearch = {};
                    $scope.detailedSearch.isVisible = false;
                    $scope.showLanguageSelection = false;
                    $scope.selectedLanguage = translationService.getLanguage();
                    $scope.searchFields = {};
                    $scope.searchFields.searchQuery = searchService.getQuery();
                    $scope.detailedSearch = {};
                    $scope.suggest = {};
                    $scope.suggest.suggestions = null;
                    $scope.suggest.selectedItem = null;
                    var dontSearch = false;

                    $scope.detailedSearch.accessor = {
                        clearSimpleSearch: function () {
                            $scope.searchFields.searchQuery = '';
                        }
                    };

                    $scope.setLanguage = function (language) {
                        translationService.setLanguage(language);
                        $scope.selectedLanguage = language;
                    };

                    $scope.logout = function () {
                        authenticationService.logout();
                        $location.url('/');
                    };

                    $scope.showLogin = function (ev) {
                        $mdDialog.show(angularAMD.route({
                            templateUrl: 'views/loginDialog/loginDialog.html',
                            controllerUrl: 'views/loginDialog/loginDialog',
                            targetEvent: ev
                        }));
                    };

                    $scope.search = function () {
                        searchService.setSearch($scope.searchFields.searchQuery);
                        searchService.clearFieldsNotInSimpleSearch();
                        searchService.setType('all');
                        $location.url(searchService.getURL());
                    };

                    $scope.openDetailedSearch = function () {
                        $scope.detailedSearch.isVisible = true;
                        $scope.detailedSearch.queryIn = $scope.searchFields.searchQuery;
                        broadcastSearchOpen();
                        $anchorScroll();
                    };

                    function broadcastSearchOpen() {
                        $scope.$broadcast("detailedSearch:open");
                    }

                    $scope.$on('detailedSearch:open', function () {
                        $scope.detailedSearch.isVisible = true;
                    });

                    $scope.closeDetailedSearch = function () {
                        $timeout(function () {
                            $scope.clearTaxonSelector();
                            $scope.detailedSearch.accessor.clear();
                        }, 500);
                        dontSearch = true;
                        $scope.detailedSearch.isVisible = false;
                        $scope.detailedSearch.queryIn = null;
                    };

                    $scope.suggest.doSuggest = function (query) {
                        return suggestService.suggest(query, suggestService.getSuggestURLbase());
                    };

                    $scope.clickOutside = function () {
                        if ($rootScope.dontCloseSearch) {
                            $rootScope.dontCloseSearch = false;
                        }
                    };

                    $scope.$watch('detailedSearch.mainField', function (newValue, oldValue) {
                        if (newValue != oldValue) {
                            $scope.searchFields.searchQuery = newValue || "";
                        }
                    }, true);


                    $scope.$watch('searchFields.searchQuery', function (newValue, oldValue) {
                        $scope.searchFields.searchQuery = newValue || "";
                        if (newValue !== oldValue && !$scope.detailedSearch.isVisible && !dontSearch) {
                            $scope.search();
                        } else if ($scope.detailedSearch.isVisible) {
                            $scope.detailedSearch.queryIn = $scope.searchFields.searchQuery;
                        }

                        if (dontSearch) dontSearch = false;
                    }, true);

                    $scope.$watch(function () {
                        return authenticatedUserService.getUser();
                    }, function (user) {
                        $scope.user = user;
                    }, true);

                    $scope.$watch(function () {
                        return searchService.getQuery();
                    }, function (query) {
                        // Search query is not updated from search service while detailed search is open
                        if (!query || !$scope.detailedSearch.isVisible) {
                            $scope.searchFields.searchQuery = query;
                        }
                    }, true);

                    $scope.$watch(function () {
                        return translationService.getLanguage();
                    }, function (language) {
                        $scope.setLanguage(language);
                    }, true);

                    $scope.isAdmin = function () {
                        return authenticatedUserService.isAdmin();
                    };

                    $scope.isModerator = function () {
                        return authenticatedUserService.isModerator();
                    };

                    $scope.getShareUrl = buildShareUrl();

                    function buildShareUrl() {
                        var protocol = $location.protocol();
                        var host = $location.host();
                        var path = '/portfolio';
                        var params = $location.search();

                        return protocol + '://' + host + path + '?id=' + params.id;
                    }

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

                    $scope.clearTaxonSelector = function () {
                        $rootScope.$broadcast('taxonSelector:clear', null);
                    };

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

                    function updatePortfolioFailed() {
                        log('Updating portfolio failed.');
                    }

                    $scope.$watch(function(){ return $location.path() }, function(params){
                        if(params.indexOf("/portfolio") !== -1 || params.indexOf("/material") !== -1) {
                            $scope.detailedSearch.isVisible = false;
                        }
                    });
                }
            };
        }
    ]);
});
