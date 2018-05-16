'use strict'

angular.module('koolikottApp')
.controller('devLoginController',
[
    '$scope', 'serverCallService', '$route', 'authenticatedUserService', '$location', '$rootScope', 'dialogService',
    function($scope, serverCallService, $route, authenticatedUserService, $location, $rootScope, dialogService) {
        var idCode = $route.current.params.idCode;
        var params = {};
        serverCallService.makeGet("rest/dev/login/" + idCode, params, loginSuccess, loginFail);

        var authenticatedUser;

        function loginSuccess(authUser) {
            if (isEmpty(authUser)) {
                log("No data returned by logging in with id code:" + idCode);
                $location.url('/');
            } else {
                if (authUser.status){
                    authenticatedUser = authUser.authenticatedUser;
                    $rootScope.justLoggedIn = true;
                    authenticatedUserService.setAuthenticatedUser(authenticatedUser);
                    serverCallService.makeGet("rest/user/role", {}, getRoleSuccess, loginFail);
                } else {
                    dialogService.showConfirmationDialog(
                        'MATERIAL_CONFIRM_DELETE_DIALOG_TITLE',
                        'MATERIAL_CONFIRM_DELETE_DIALOG_CONTENT',
                        'ALERT_CONFIRM_POSITIVE',
                        'ALERT_CONFIRM_NEGATIVE',
                        () => {
                            authUser.userConfirmed = true;
                            serverCallService.makePost('rest/login/finalizeLogin', authUser)
                                .then((response) => {
                                    authenticatedUser = response.data;
                                    $rootScope.justLoggedIn = true;
                                    authenticatedUserService.setAuthenticatedUser(authenticatedUser);
                                    serverCallService.makeGet("rest/user/role", {}, getRoleSuccess, loginFail);
                                }
                            )
                        },
                        ()=>{
                            loginFail();
                        });
                }
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
