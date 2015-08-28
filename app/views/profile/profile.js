define(['app'], function(app)
{
    app.controller('profileController', ['$scope', '$route', 'authenticatedUserService', 'serverCallService', '$location', 
        function($scope, $route, authenticatedUserService, serverCallService, $location) {

    	function init() {
    		isMyProfilePage();

            if (!$scope.user) {
                getUser();
            }

            getUsersMaterials();
    	}

        function isMyProfilePage() {
            if (authenticatedUserService.isAuthenticated()) {
                var user = authenticatedUserService.getUser()

                if (user && $route.current.params.username === user.username) {
                    $scope.user = user;
                    $scope.myProfile = true;
                }
            }
        }

        function getUser() {
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

        function getUsersMaterials() {
            var params = {
                'username': $route.current.params.username
            };
            var url = "rest/material/getByCreator";
            serverCallService.makeGet(url, params, getUsersMaterialsSuccess, getUsersMaterialsFail);
        }

        function getUsersMaterialsSuccess(data) {
            if (isEmpty(data)) {
                log('No material data returned.');
            } else {
                $scope.materials = data;
            }
        }
        
        function getUsersMaterialsFail(data, status) {
            console.log('Failed to get materials. ')
        }

    	init();

    }]);
});