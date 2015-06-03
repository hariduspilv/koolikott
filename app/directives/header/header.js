define(['app'], function(app)
{
    app.directive('dopHeader', function($translate) {
        return {
            scope: true,
            templateUrl: 'app/directives/header/header.html',
            controller: function ($scope) {
	            $scope.changeLanguage = function(langKey) {
	            	$translate.use(langKey);
			    };
	        }
        }
    });
    
    return app;
});