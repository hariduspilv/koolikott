'use strict'

angular.module('koolikottApp')
.controller('devLoginController',
[
    '$scope', 'serverCallService', '$route', 'authenticatedUserService', '$location', '$rootScope', 'alertService', '$mdDialog',
    function($scope, serverCallService, $route, authenticatedUserService, $location, $rootScope, alertService, $mdDialog) {
        var idCode = $route.current.params.idCode;
        var params = {};
        serverCallService.makeGet("rest/dev/login/" + idCode, params, loginSuccess, loginFail);

        function authenticateUser(authenticatedUser) {
            $rootScope.justLoggedIn = true;
            authenticatedUserService.setAuthenticatedUser(authenticatedUser);
            serverCallService.makeGet("rest/user/role", {}, getRoleSuccess, loginFail);
        }

        function loginSuccess(userStatus) {
            if (isEmpty(userStatus)) {
                log("No data returned by logging in with id code:" + idCode);
                $location.url('/');
            } else {
                if (userStatus.statusOk){
                    authenticateUser(userStatus.authenticatedUser);
                } else {
                    $mdDialog.show({
                        templateUrl: 'views/agreement/agreementDialog.html',
                        controller: 'agreementDialogController',
                    }).then((res)=>{
                        if (!res){
                            loginFail();
                        } else {
                            userStatus.userConfirmed = true;
                            serverCallService.makePost('rest/login/finalizeLogin', userStatus)
                                .then((response) => {
                                        authenticateUser(response.data);
                                    }
                                )
                        }
                    })
                }
            }
        }

        function loginFail() {
            console.log('Login failed.');
            alertService.setErrorAlert('ERROR_LOGIN_FAILED');
            authenticatedUserService.removeAuthenticatedUser();
            $location.url('/');
        }

        function finishLogin(authenticatedUser) {
            authenticatedUserService.setAuthenticatedUser(authenticatedUser);
            $location.url('/' + authenticatedUser.user.username);
            $rootScope.$broadcast('login:success')
        }

        function getRoleSuccess(data) {
            if (isEmpty(data)) {
                loginFail();
            } else {
                const authenticatedUser = authenticatedUserService.getAuthenticatedUser();
                authenticatedUser.user.role = data;
                finishLogin(authenticatedUser);
            }
        }
    }
]);
