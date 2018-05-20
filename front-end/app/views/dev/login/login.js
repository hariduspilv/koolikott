'use strict'

angular.module('koolikottApp')
.controller('devLoginController',
[
    'serverCallService', '$route', 'authenticatedUserService', '$location', 'authenticationService',
    function(serverCallService, $route, authenticatedUserService, $location, authenticationService) {
        const idCode = $route.current.params.idCode;
        serverCallService.makeGet("rest/dev/login/" + idCode, {}, loginSuccess, loginFail, finishLogin);

        function loginSuccess(userStatus) {
            authenticationService.loginSuccess(userStatus);
        }

        function loginFail() {
            authenticationService.loginFail()
        }

        function finishLogin() {
            $location.url('/' + authenticatedUserService.getAuthenticatedUser().user.username);
        }
    }
]);
