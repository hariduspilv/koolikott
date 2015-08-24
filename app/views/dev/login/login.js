define(['app'], function(app)
{
    app.controller('loginController', ['$scope', 'serverCallService', '$route', 'loginService', '$location',
       function($scope, serverCallService, $route, loginService, $location) {
        var idCode = $route.current.params.idCode;
        var params = {};
        serverCallService.makeGet("rest/dev/login/" + idCode, params, loginSuccess, loginFail); 
        
        function loginSuccess(authenticatedUser) {
            if (isEmpty(authenticatedUser)) {
                log("No data returned by logging in with id code:" + idCode);
                $location.url('/');
            } else {
                loginService.setAuthenticatedUser(authenticatedUser);
                $location.url('/' + authenticatedUser.user.username);
            }
        }
        
        function loginFail(authenticatedUser, status) {
            log('Login failed.');
            $location.url('/');
        }
        
    }]);
});