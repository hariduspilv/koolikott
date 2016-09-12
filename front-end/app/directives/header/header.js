define([
    'angularAMD',
    'services/serverCallService',
    'services/authenticationService',
    'services/searchService',
    'services/translationService',
    'services/suggestService',
    'services/serverCallService'
], function (angularAMD, $http) {
    angularAMD.directive('dopHeader', ['translationService', '$location', 'searchService', 'authenticationService', 'authenticatedUserService', '$timeout', '$mdDialog', 'suggestService', 'serverCallService', '$http',
        function (translationService, $location, searchService, authenticationService, authenticatedUserService, $timeout, $mdDialog, suggestService, serverCallService, $http) {
            return {
                scope: true,
                templateUrl: 'directives/header/header.html',
                controller: function ($scope, $location, authenticationService, authenticatedUserService, $rootScope) {
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
                        $location.url(searchService.getURL());
                    };

                    $scope.openDetailedSearch = function () {
                        $scope.detailedSearch.isVisible = true;
                    };

                    $scope.closeDetailedSearch = function () {
                        $timeout(function () {
                            $scope.detailedSearch.accessor.clear();
                        }, 500);

                        $scope.detailedSearch.isVisible = false;
                        $scope.searchFields.searchQuery = (($scope.searchFields.searchQuery || "") + " " + $scope.detailedSearch.queryOut).trim();
                        $scope.detailedSearch.queryIn = null;
                        $scope.searchFields.searchQuery = "";
                        $scope.detailedSearch.mainField = "";
                    };

                    $scope.detailedSearch.doSearch = function () {
                        var query = ($scope.searchFields.searchQuery || "") + " " + $scope.detailedSearch.queryOut;
                        searchService.setSearch(query.trim());
                        $location.url(searchService.getURL());
                    };

                    $scope.suggest.doSuggest = function (query) {
                        if (query == null) {
                            return [];
                        }
                        return $http.get(suggestService.getURL(query)).then(function (response) {
                            return response.data.alternatives || [];
                        });
                    };

                    $scope.clickOutside = function () {
                        if ($scope.detailedSearch.isVisible && !$rootScope.dontCloseSearch) {
                            $scope.closeDetailedSearch();
                        } else if ($rootScope.dontCloseSearch) {
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
                        if (newValue !== oldValue && !$scope.detailedSearch.isVisible) {
                            $scope.search();
                        } else if ($scope.detailedSearch.isVisible) {
                            $scope.detailedSearch.queryIn = $scope.searchFields.searchQuery;
                        }
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
                }
            };
        }
    ]);
});
