define(['app'], function(app)
{
    app.directive('showFocus', function($timeout) {
        return function(scope, element, attrs) {
          scope.$watch(attrs.showFocus,
            function (newValue) {
                $timeout(function() {
                    newValue && elementt[0].focus();
                });
            },true);
        };
    });

    app.directive('dopHeader', ['translationService', '$location', 'searchService', 'authenticationService', 'authenticatedUserService', '$timeout', '$mdDialog',
     function(translationService, $location, searchService, authenticationService, authenticatedUserService, $timeout, $mdDialog) {
        return {
            scope: true,
            templateUrl: 'directives/header/header.html',
            controller: function ($scope, $location, authenticationService, authenticatedUserService) {
                $scope.showLanguageSelection = false;
                $scope.selectedLanguage = translationService.getLanguage();
                $scope.searchFields = {};
                $scope.searchFields.searchQuery = searchService.getQuery();
                $scope.detailedSearch = {};
                $scope.showSearch = searchService.queryExists();

                $scope.setLanguage = function(language) {
                    translationService.setLanguage(language);
                    $scope.selectedLanguage = language;
                };

                $scope.search = function() {
                    if (!isEmpty($scope.searchFields.searchQuery)) {
                        searchService.setSearch($scope.searchFields.searchQuery);
                        searchService.clearFieldsNotInSimpleSearch();
                        $location.url(searchService.getURL());
                    }
                };

                $scope.logout = function() {
                    authenticationService.logout();
                    $location.url('/');
                };

                $scope.showLogin = function(ev) {
			  		$mdDialog.show({
						controller: 'loginController',
                    	templateUrl: 'views/loginDialog/loginDialog.html',
                    	targetEvent: ev,
                  	})
                };

                $scope.toggleDetailSearch = function() {
                    $scope.showSearch = true;
                    $scope.detailedSearch.isVisible = !$scope.detailedSearch.isVisible;

                    if ($scope.detailedSearch.isVisible) {
                        $scope.detailedSearch.queryIn = $scope.searchFields.searchQuery;
                    } else {
                        $scope.searchFields.searchQuery = $scope.detailedSearch.queryOut;
                        $scope.detailedSearch.queryIn = null;
                    }
                }

                $scope.$watch(function () {
                        return authenticatedUserService.getUser();
                    }, function(user) {
                        $scope.user = user;
                }, true);

                $scope.$watch(function () {
                        return searchService.getQuery();
                    }, function(query) {
                        // Search query is not shown in simple search box when detailed search is open
                        if (!query || !$scope.detailedSearch.isVisible) {
                            $scope.searchFields.searchQuery = query;
                        }
                }, true);

                $scope.$watch(function () {
                        return translationService.getLanguage();
                    }, function(language) {
                        $scope.setLanguage(language);
                }, true);
            }
        };
    }]);

    return app;
});
