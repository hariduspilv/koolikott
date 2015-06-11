define(['app'], function(app)
{
    
    app.directive('dopHeader', function($translate) {
        return {
            scope: true,
            templateUrl: 'app/directives/header/header.html',
            controller: function ($scope, $rootScope) {
                $scope.showLanguageSelection = false;
	        
                $scope.languageSelectClick = function() {
                    $scope.showLanguageSelection = !$scope.showLanguageSelection; 
                };

                $scope.closeLanguageSelection = function() {
                	$scope.$apply(function() {
	                	$scope.showLanguageSelection = false;
                	});
                }

                 $scope.pickLanguage = function(lang) {
                    $rootScope.language = lang;
                    $scope.showLanguageSelection = false;
                }
            }
        };
    });
    
    return app;
});