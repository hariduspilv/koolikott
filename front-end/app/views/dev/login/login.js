define([
    'app',
    'services/serverCallService',
    'services/authenticatedUserService'
], function(app) {
    return ['$scope', 'serverCallService', '$route', 'authenticatedUserService', '$location', function($scope, serverCallService, $route, authenticatedUserService, $location) {
        var idCode = $route.current.params.idCode;
        var params = {};
        serverCallService.makeGet("rest/dev/login/" + idCode, params, loginSuccess, loginFail);

        var authenticatedUser;

        function loginSuccess(authUser) {
            if (isEmpty(authUser)) {
                log("No data returned by logging in with id code:" + idCode);
                $location.url('/');
            } else {
                authenticatedUser = authUser;
                getRole();
            }
        }

        function loginFail() {
            log('Login failed.');
            authenticatedUserService.removeAuthenticatedUser();
            $location.url('/');
        }

        function finishLogin() {
            authenticatedUserService.setAuthenticatedUser(authenticatedUser);
            $location.url('/' + authenticatedUser.user.username);
        }

        function getRole() {
            authenticatedUserService.setAuthenticatedUser(authenticatedUser);
            serverCallService.makeGet("rest/user/role", {}, getRoleSuccess, loginFail);
        }

        function getRoleSuccess(data) {
            if (isEmpty(data)) {
                loginFail();
            } else {
                authenticatedUserService.removeAuthenticatedUser();
                authenticatedUser.user.role = data;
                finishLogin();
            }
        }
    }];
});
