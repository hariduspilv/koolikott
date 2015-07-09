define(['app'], function(app)
{
    
    app.directive('dopHeader', function(translationService) {
        return {
            scope: true,
            templateUrl: 'app/directives/header/header.html',
            controller: function ($scope, $location) {
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

                    // get different translation for about or help page
                    if ($scope.isStaticPage) {
                    	console.log("setlanguage " + language);
                    	$scope.getPage(language); 
                    }
                }
                
                $scope.search = function() {
                    if (!isEmpty($scope.searchQuery)) {
                        $location.url("search/result?q=" + $scope.searchQuery)
                    }
                }
            }
        };
    });
    
    return app;
});