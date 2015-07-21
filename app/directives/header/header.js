define(['app'], function(app)
{
    
    app.directive('dopHeader', function(translationService) {
        return {
            scope: true,
            templateUrl: 'app/directives/header/header.html',
            controller: function ($scope, $location, $rootScope) {
                $scope.showLanguageSelection = false;
                $scope.selectedLanguage = translationService.getLanguage();
	        
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
                
                $rootScope.searchFields = {};
                
                $scope.search = function() {
                	 if (!isEmpty($rootScope.searchFields.searchQuery)) {
                         $location.url("search/result?q=" + $rootScope.searchFields.searchQuery)
                     }
                }

            }
        };
    });
    
    return app;
});