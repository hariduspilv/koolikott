define(['app'], function(app)
{
    
    app.directive('dopHeader', function($translate) {
        return {
            scope: true,
            templateUrl: 'app/directives/header/header.html',
            controller: function ($scope) {
                $scope.showLanguageSelection = false;
	        
                $scope.languageSelectClick = function() {
                    $scope.showLanguageSelection = !$scope.showLanguageSelection; 
	        };

            }
        };
    });
    
    return app;
});