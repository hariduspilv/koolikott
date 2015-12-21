define(['app'], function(app) {

	var instance;
	var toast;

    app.factory('toastService',['$mdToast', '$filter', '$rootScope', 
        function($mdToast, $filter, $rootScope) {

        	$rootScope.$on('$routeChangeSuccess', function() {
        		if (toast) {
        			instance.show(toast);
        			toast = null;
        		}
	        });

            instance = {
                show: function(content) {
                	$mdToast.show($mdToast.simple().position('right top').content($filter('translate')(content)));
                },

                showOnRouteChange: function(content) {
                	toast = content;
                }
            };

            return instance;
        }
    ]);
});
