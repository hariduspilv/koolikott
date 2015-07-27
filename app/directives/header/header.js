define(['app'], function(app)
{
    
    app.directive('dopHeader', ['translationService', '$location', function(translationService, $location) {
        return {
            scope: true,
            templateUrl: 'app/directives/header/header.html',
            controller: function ($scope, $location, $rootScope) {
                $scope.showLanguageSelection = false;
                $scope.selectedLanguage = translationService.getLanguage();
                
                $scope.searchQuery = "";
                var searchObject = $location.search();
                if (searchObject.q) {
                    $scope.searchQuery = searchObject.q;
                }
	        
                $scope.languageSelectClick = function() {
                    $scope.showLanguageSelection = !$scope.showLanguageSelection; 
                };

                $scope.closeLanguageSelection = function () {
                    $scope.$apply(function() {
                        $scope.showLanguageSelection = false;
                    });
                }
                
                $scope.setLanguage = function(language) {
                    translationService.setLanguage(language);
                    $scope.selectedLanguage = language;
                    $scope.showLanguageSelection = false;
                }
                
                $scope.search = function() {
                    if (!isEmpty($scope.searchQuery)) {
                        $location.url("search/result?q=" + $scope.searchQuery)
                    }
                }
            }
        };
    }]);
    
    return app;
});