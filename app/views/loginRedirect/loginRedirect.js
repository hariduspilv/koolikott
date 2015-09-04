define(['app'], function(app)
{
    app.controller('loginRedirectController', ['$scope', '$route', 'serverCallService', 'authenticatedUserService', '$location',
       function($scope, $route, serverCallService, authenticatedUserService, $location) {
        var url = localStorage.getItem(LOGIN_ORIGIN);
        localStorage.removeItem(LOGIN_ORIGIN);
        var params = {
            'token': $route.current.params.token
        };

        serverCallService.makeGet("rest/authenticatedUser/getAuthenticatedUser", params, getSuccess, getFail); 
        
        function getSuccess(authenticatedUser) {
            if (isEmpty(authenticatedUser)) {
                log("No data returned by logging in");
                $location.url('/');
            } else {
                authenticatedUserService.setAuthenticatedUser(authenticatedUser);
                console.log(authenticatedUser);
                console.log(authenticatedUser.firstLogin)
                if (authenticatedUser.firstLogin) {
                    $location.url('/' + authenticatedUser.user.username);
                } else {
                    $location.url(url);
                }
            }
        }
        
        function getFail(authenticatedUser, status) {
            log('Login failed.');
            $location.url('/');
        }
    }]);
});