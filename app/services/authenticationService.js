define([
  'angularAMD',
  'services/alertService'
], function(angularAMD) {
    var instance;
    var isAuthenticationInProgress;
    var isOAuthAuthentication = false;

    var mobileIdLoginSuccessCallback;
    var mobileIdLoginFailCallback;
    var mobileIdChallengeReceivedCallback;

    angularAMD.factory('authenticationService', ['$location', '$rootScope', 'serverCallService', 'authenticatedUserService', 'alertService', '$mdDialog',
        function($location, $rootScope, serverCallService, authenticatedUserService, alertService, $mdDialog) {

            function loginSuccess(authenticatedUser) {
                if (isEmpty(authenticatedUser)) {
                    loginFail();
                } else {
                    authenticatedUserService.setAuthenticatedUser(authenticatedUser);

                    if (authenticatedUser.firstLogin) {
                        $location.url('/' + authenticatedUser.user.username);
                    } else if (isOAuthAuthentication) {
                        var url = localStorage.getItem(LOGIN_ORIGIN);
                        $location.url(url);
                    }

                    enableLogin();
                    localStorage.removeItem(LOGIN_ORIGIN);
                    isOAuthAuthentication = false;

                    if (mobileIdLoginSuccessCallback) {
                        mobileIdLoginSuccessCallback();
                    }
                }
            }

            function loginFail() {
                log('Logging in failed.');
                $mdDialog.hide();
                alertService.setErrorAlert('ERROR_LOGIN_FAILED');
                enableLogin();

                if (isOAuthAuthentication) {
                    localStorage.removeItem(LOGIN_ORIGIN);
                    $location.url('/');
                }

                isOAuthAuthentication = false

                if (mobileIdLoginFailCallback) {
                    mobileIdLoginFailCallback();
                }
            }

            function logoutSuccess(data) {
                authenticatedUserService.removeAuthenticatedUser();
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

                    var params = {
                        'token': mobileIDSecurityCodes.token
                    };

                    serverCallService.makeGet("rest/login/mobileId/isValid", params, loginSuccess, loginFail);
                }
            }

            return {

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

                loginWithTaat: function() {
                    localStorage.removeItem(LOGIN_ORIGIN);
                    localStorage.setItem(LOGIN_ORIGIN, $location.url());
                    window.location = "/rest/login/taat";
                },

                authenticateUsingOAuth: function(token) {
                    var params = {
                        'token': token
                    };

                    serverCallService.makeGet("rest/login/getAuthenticatedUser", params, loginSuccess, loginFail);
                    isOAuthAuthentication = true;
                },

                loginWithMobileId: function(phoneNumber, idCode, language, successCallback, failCallback, challengeReceivedCallback) {
                    if (isAuthenticationInProgress) {
                        return;
                    }

                    mobileIdLoginSuccessCallback = successCallback;
                    mobileIdLoginFailCallback = failCallback;
                    mobileIdChallengeReceivedCallback = challengeReceivedCallback;

                    var params = {
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
});