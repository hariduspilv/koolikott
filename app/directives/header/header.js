define(['app'], function(app)
{
    
    app.directive('dopHeader', function(translationService) {
        return {
            scope: true,
            templateUrl: 'app/directives/header/header.html',
            controller: function ($scope) {
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
                    $scope.selectedLanguage = translationService.getLanguage();
                    $scope.showLanguageSelection = false;
                }
            }
        };
    });
    
    return app;
});