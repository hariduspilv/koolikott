define(['app'], function(app)
{
    app.controller('profileController', ['$scope', '$route', 'loginService', 
        function($scope, $route, loginService) {

    	function getUser() {
    		if (loginService.isAuthenticated()) {
    			var user = loginService.getUser()

    			if ($route.current.params.username === user.username) {
    				$scope.user = user;
    				$scope.myProfile = true;
    			}
    		}
    	}

    	getUser();

    }]);
});