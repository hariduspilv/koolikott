'use strict'

angular.module('koolikottApp')
.controller('devLoginController',
[
    'serverCallService', '$rootScope', '$route', 'authenticatedUserService', '$location', 'authenticationService',
    function(serverCallService, $rootScope, $route, authenticatedUserService, $location, authenticationService) {
        const idCode = $route.current.params.idCode;
        serverCallService.makeGet("rest/dev/login/" + idCode, {}, loginSuccess, loginFail);

        function loginSuccess(userStatus) {
            $rootScope.afterAuthRedirectURL = "/portfolios";
            authenticationService.loginSuccess(userStatus);
        }

        function loginFail() {
            authenticationService.loginFail()
            $location.url("/");
        }
    }
]);
