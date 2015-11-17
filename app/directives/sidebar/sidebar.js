define(['app'], function(app)
{
	app.directive('dopSidebar', ['translationService', '$location',
    	function(translationService, $location) {
        	return {
            	scope: true,
            	templateUrl: 'directives/sidebar/sidebar.html',
            }
    	}]
	);

    return app;
});
