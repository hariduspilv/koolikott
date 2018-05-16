'use strict'

angular.module('koolikottApp')
.controller('devLoginController',
[
    '$scope', 'serverCallService', '$route', 'authenticatedUserService', '$location', '$rootScope',
    function($scope, serverCallService, $route, authenticatedUserService, $location, $rootScope) {
        var idCode = $route.current.params.idCode;
        var params = {};
        serverCallService.makeGet("rest/dev/login/" + idCode, params, loginSuccess, loginFail);

        var authenticatedUser;

        function loginSuccess(authUser) {
            if (isEmpty(authUser)) {
                log("No data returned by logging in with id code:" + idCode);
                $location.url('/');
            } else {
                if (authUser.authenticatedUser){
                    authenticatedUser = authUser.authenticatedUser;
                } else {
                    authenticatedUser = authUser;
                }
                $rootScope.justLoggedIn = true;
                authenticatedUserService.setAuthenticatedUser(authenticatedUser);
                serverCallService.makeGet("rest/user/role", {}, getRoleSuccess, loginFail);
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
            $rootScope.$broadcast('login:success')
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
    }
]);
