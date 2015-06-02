define(['app'], function(app)
{
    app.directive('dopHeader', function() {
        return {
            scope: true,
            templateUrl: 'app/directives/header/header.html',
            controller: function ($scope) {
	            
	        }
        }
    });
    
    return app;
});