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
    
    app.directive('dopHeader', ['translationService', '$location', 'searchService', 'authenticationService', 'authenticatedUserService',
     function(translationService, $location, searchService, authenticationService, authenticatedUserService) {
        return {
            scope: true,
            templateUrl: 'app/directives/header/header.html',
            controller: function ($scope, $location, authenticationService, authenticatedUserService) {
                $scope.showLanguageSelection = false;
                $scope.showSearchBox = false;
                $scope.selectedLanguage = translationService.getLanguage();
                $scope.searchFields = {};
                $scope.searchFields.searchQuery = searchService.getQuery();

                $scope.languageSelectClick = function() {
                    $scope.showLanguageSelection = !$scope.showLanguageSelection; 
                };
                
                $scope.showSearchBoxClick = function() {
                    $scope.showSearchBox = !$scope.showSearchBox;
                    jQuery('<div class="modal-backdrop fade in hidden-md hidden-lg"></div>').appendTo(document.body);
                };
                
                $scope.closeLanguageSelection = function () {
                    $scope.$apply(function() {
                        $scope.showLanguageSelection = false;
                    });
                };

                $scope.closeSearchBox = function () {
                    $scope.showSearchBox = false;
                    jQuery('.modal-backdrop').fadeOut();
                };
                
                $scope.setLanguage = function(language) {
                    translationService.setLanguage(language);
                    $scope.selectedLanguage = language;
                    $scope.showLanguageSelection = false;
                };
                
                $scope.search = function() {
                    $scope.closeSearchBox();
                    if (!isEmpty($scope.searchFields.searchQuery)) {
                        searchService.setSearch($scope.searchFields.searchQuery);
                        $location.url(searchService.getURL());
                    }
                };

                $scope.logout = function() {
                    authenticationService.logout();
                    $('#userMenu').dropdown('toggle');
                    $location.url('/');
                };

                $scope.$watch(function () {
                        return authenticatedUserService.getUser();
                    }, function(user) {
                        $scope.user = user;
                        $('#dropdowned').collapse('hide');
                }, true);

                $scope.$watch(function () {
                        return searchService.getQuery();
                    }, function(query) {
                        $scope.searchFields.searchQuery = query;
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
