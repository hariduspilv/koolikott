define(['app'], function(app)
{
    
    app.directive('dopHeader', ['translationService', '$location', 'searchService', '$rootScope',
     function(translationService, $location, searchService, $rootScope) {
        return {
            scope: true,
            templateUrl: 'app/directives/header/header.html',
            controller: function ($scope, $location, $rootScope) {
                $scope.showLanguageSelection = false;
                $scope.showSearchBox = false;
                $scope.selectedLanguage = translationService.getLanguage();
                $rootScope.searchFields = {};
                $rootScope.searchFields.searchQuery = searchService.getQuery();

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
                    if (!isEmpty($rootScope.searchFields.searchQuery)) {
                        searchService.setSearch($rootScope.searchFields.searchQuery);
                        $location.url(searchService.getURL());
                    }
                };

            }
        };
    }]);
    
    return app;
});