define([
    'angularAMD',
    'services/serverCallService',
    'services/authenticationService',
    'services/searchService',
    'services/translationService',
    'services/suggestService'
], function (angularAMD, mod1, mod2, mod3, mod4, mod5, $http) {
    angularAMD.directive('dopHeader', ['translationService', '$location', 'searchService', 'authenticationService', 'authenticatedUserService', '$timeout', '$mdDialog', 'suggestService', '$http',
        function (translationService, $location, searchService, authenticationService, authenticatedUserService, $timeout, $mdDialog, suggestService, $http) {
            return {
                scope: true,
                templateUrl: 'directives/header/header.html',
                controller: function ($scope, $location, authenticationService, authenticatedUserService, $rootScope) {
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
                        if (!isEmpty($scope.searchFields.searchQuery)) {
                            searchService.setSearch($scope.searchFields.searchQuery);
                            searchService.clearFieldsNotInSimpleSearch();
                            $location.url(searchService.getURL());
                        }
                    };

                    $scope.openDetailedSearch = function () {
                        $scope.detailedSearch.isVisible = true;
                        $scope.detailedSearch.queryIn = $scope.searchFields.searchQuery;
                        $scope.searchFields.searchQuery = $scope.detailedSearch.mainField;
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

                    $scope.suggest.doSuggest = function () {
                        suggestService.setSuggest($scope.searchFields.searchQuery);
                        $http.get(suggestService.getURL()).then(function (result) {
                            $scope.suggest.suggestions = result.data.alternatives;
                        });
                    };

                    $scope.searchFieldEnterPressed = function () {
                        if ($scope.detailedSearch.isVisible) {
                            $scope.detailedSearch.accessor.search();
                        } else {
                            $scope.search();
                        }
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

                    $('#myDiv').on('classChange', function() {
                        alert("selected item");
                    });
                }
            };
        }
    ]);
});
