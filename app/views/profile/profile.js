define(['app'], function(app)
{
    app.controller('profileController', ['$scope', '$route', 'loginService', 'serverCallService', '$location', 
        function($scope, $route, loginService, serverCallService, $location) {

    	function getUser() {
    		if (loginService.isAuthenticated()) {
    			var user = loginService.getUser()

    			if (user && $route.current.params.username === user.username) {
    				$scope.user = user;
    				$scope.myProfile = true;
    			}
    		}

            if (!$scope.user) {
                getUserFromRest();
            }
    	}

        function getUserFromRest() {
            var params = {
                'username': $route.current.params.username
            };
            var url = "rest/user";
            serverCallService.makeGet(url, params, getUserSuccess, getUserFail);
        }

        function getUserSuccess(user) {
            if (isEmpty(user)) {
                console.log('No user returned.');
                $location.url('/');
            } else {
                $scope.user = user;
            }
        }
        
        function getUserFail(data, status) {
            console.log('Getting user failed.')
        }

    	getUser();

    }]);
});