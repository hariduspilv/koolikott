'use strict';

angular.module('koolikottApp')
.factory('authenticationService',
[
    '$location', '$rootScope', '$timeout', 'serverCallService', 'authenticatedUserService', 'alertService', '$mdDialog',
    function($location, $rootScope, $timeout, serverCallService, authenticatedUserService, alertService, $mdDialog) {
        var isAuthenticationInProgress;
        var isOAuthAuthentication = false;

        var mobileIdLoginSuccessCallback;
        var mobileIdLoginFailCallback;
        var mobileIdChallengeReceivedCallback;

        function authenticateUser(authenticatedUser) {
            if (isEmpty(authenticatedUser)) {
                loginFail();
            } else {
                $rootScope.justLoggedIn = true;
                authenticatedUserService.setAuthenticatedUser(authenticatedUser);
                serverCallService.makeGet("rest/user/role", {}, getRoleSuccess, loginFail);
            }
        }

        function showGdprModalAndAct(userStatus) {
            $mdDialog.show({
                templateUrl: 'views/agreement/agreementDialog.html',
                controller: 'agreementDialogController',
            }).then((res)=>{
                if (!res){
                    if (userStatus.existingUser){
                        serverCallService.makePost('rest/login/rejectAgreement', userStatus)
                            .then(() => loginFail())
                    } else {
                        loginFail();
                    }
                } else {
                    userStatus.userConfirmed = true;
                    serverCallService.makePost('rest/login/finalizeLogin', userStatus)
                        .then((response) => {
                                authenticateUser(response.data);
                            }, () => {
                                loginFail();
                            }
                        )
                }
            })
        }

        function loginSuccess(userStatus) {
            if (isEmpty(userStatus)) {
                loginFail();
            } else {
                if (userStatus.statusOk){
                    authenticateUser(userStatus.authenticatedUser);
                } else {
                    showGdprModalAndAct(userStatus);
                }
            }
        }

        function loginFail() {
            console.log('Logging in failed.');
            $mdDialog.hide();
            alertService.setErrorAlert('ERROR_LOGIN_FAILED');
            enableLogin();
            authenticatedUserService.removeAuthenticatedUser();

            if (isOAuthAuthentication) {
                localStorage.removeItem(LOGIN_ORIGIN);
                $location.url('/');
            }

            isOAuthAuthentication = false;

            if (mobileIdLoginFailCallback) {
                mobileIdLoginFailCallback();
            }
        }

        function eKoolLoginFail() {
            console.log('Logging in failed.');
            $mdDialog.hide();
            alertService.setErrorAlert15s('ERROR_LOGIN_FAILED_EKOOL');
            enableLogin();
            authenticatedUserService.removeAuthenticatedUser();

            if (isOAuthAuthentication) {
                localStorage.removeItem(LOGIN_ORIGIN);
                $location.url('/');
            }
        }

        function idCodeLoginFail(msg) {
            console.log('Logging in failed.');
            $mdDialog.hide();
            alertService.setErrorAlert15s(msg);
            enableLogin();
            authenticatedUserService.removeAuthenticatedUser();

            if (isOAuthAuthentication) {
                localStorage.removeItem(LOGIN_ORIGIN);
                $location.url('/');
            }
        }

        function finishLogin(authenticatedUser) {
            authenticatedUserService.setAuthenticatedUser(authenticatedUser);

            if ($rootScope.afterAuthRedirectURL) {
                $location.url('/' + authenticatedUser.user.username + $rootScope.afterAuthRedirectURL);
            } else if (authenticatedUser.firstLogin) {
                $location.url('/' + authenticatedUser.user.username);
            } else if (isOAuthAuthentication) {
                $location.url(localStorage.getItem(LOGIN_ORIGIN));
            }

            enableLogin();
            localStorage.removeItem(LOGIN_ORIGIN);
            isOAuthAuthentication = false;
            $rootScope.afterAuthRedirectURL = null;
            alertService.setErrorAlert('LOGIN_SUCCESS');

            if (mobileIdLoginSuccessCallback) {
                mobileIdLoginSuccessCallback();
            }

            $rootScope.justLoggedIn = true;
            $timeout(() =>
                $rootScope.$broadcast('login:success')
            )
        }

        function logoutSuccess(data) {
            authenticatedUserService.removeAuthenticatedUser();
            $rootScope.$broadcast('logout:success');
            enableLogin();
        }

        function logoutFail(data, status) {
            //ignore
        }

        function disableLogin() {
            isAuthenticationInProgress = true;
        }

        function enableLogin() {
            isAuthenticationInProgress = false;
        }

        function loginWithMobileIdSuccess(mobileIDSecurityCodes) {
            if (isEmpty(mobileIDSecurityCodes)) {
                loginFail();
            } else {
                mobileIdChallengeReceivedCallback(mobileIDSecurityCodes.challengeId);

                const params = {
                    'token': mobileIDSecurityCodes.token
                };

                serverCallService.makeGet("rest/login/mobileId/isValid", params, loginSuccess, loginFail);
            }
        }

        function loginWithOAuth(path) {
            localStorage.removeItem(LOGIN_ORIGIN);
            localStorage.setItem(LOGIN_ORIGIN, $rootScope.afterAuthRedirectURL ? $rootScope.afterAuthRedirectURL : $location.$$url);
            window.location = path;
        }

        function getRoleSuccess(data) {
            if (isEmpty(data)) {
                loginFail();
            } else {
                let authenticatedUser = authenticatedUserService.getAuthenticatedUser();
                authenticatedUser.user.role = data;
                finishLogin(authenticatedUser);
            }
        }

        return {

            loginSuccess: function (userStatus) {
                isOAuthAuthentication = true;
                loginSuccess(userStatus);
            },

            loginFail: function () {
                loginFail();
            },

            logout: function() {
                serverCallService.makePost("rest/logout", {}, logoutSuccess, logoutFail);
            },

            loginWithIdCard: function() {
                if (isAuthenticationInProgress) {
                    return;
                }

                disableLogin();
                serverCallService.makeGet("rest/login/idCard", {}, loginSuccess, loginFail);
            },

            /*loginWithTaat: function() {
                loginWithOAuth("/rest/login/taat");
            },*/

            loginWithEkool : function() {
                loginWithOAuth("/rest/login/ekool");
            },

            loginWithStuudium : function() {
                loginWithOAuth("/rest/login/stuudium");
            },

            authenticateUsingOAuth: function(inputParams) {
                const {token, agreement, existingUser, eKoolLoginMissingIdCode, stuudiumLoginMissingIdCode} = inputParams;
                if (eKoolLoginMissingIdCode) {
                    idCodeLoginFail('ERROR_LOGIN_FAILED_EKOOL');
                    return;
                }

                if (stuudiumLoginMissingIdCode) {
                    idCodeLoginFail('ERROR_LOGIN_FAILED_STUUDIUM');
                    return;
                }

                isOAuthAuthentication = true;
                if (!(agreement || existingUser)){
                    serverCallService.makeGet("rest/login/getAuthenticatedUser", {token}, authenticateUser, loginFail);
                } else {
                    const params = {
                        token,
                        agreementId : agreement,
                        existingUser
                    }
                    showGdprModalAndAct(params);
                }
            },

            loginWithMobileId: function(phoneNumber, idCode, language, successCallback, failCallback, challengeReceivedCallback) {
                if (isAuthenticationInProgress) {
                    return;
                }

                mobileIdLoginSuccessCallback = successCallback;
                mobileIdLoginFailCallback = failCallback;
                mobileIdChallengeReceivedCallback = challengeReceivedCallback;

                const params = {
                    'phoneNumber': phoneNumber,
                    'idCode': idCode,
                    'language': language
                };

                disableLogin();
                serverCallService.makeGet("rest/login/mobileId", params, loginWithMobileIdSuccess, loginFail);
            }

        };
    }
]);
