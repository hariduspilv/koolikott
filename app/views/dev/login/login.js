define(['app'], function(app)
{
    app.controller('devLoginController', ['$scope', 'serverCallService', '$route', 'authenticatedUserService', '$location',
       function($scope, serverCallService, $route, authenticatedUserService, $location) {
        var idCode = $route.current.params.idCode;
        var params = {};
        serverCallService.makeGet("rest/dev/login/" + idCode, params, loginSuccess, loginFail); 
        
        function loginSuccess(authenticatedUser) {
            if (isEmpty(authenticatedUser)) {
                log("No data returned by logging in with id code:" + idCode);
                $location.url('/');
            } else {
                authenticatedUserService.setAuthenticatedUser(authenticatedUser);
                $location.url('/' + authenticatedUser.user.username);
            }
        }
        
        function loginFail(authenticatedUser, status) {
            log('Login failed.');
            $location.url('/');
        }
    }]);
});