'use strict';

angular.module('koolikottApp')
.factory('authenticationService',
[
    '$location', '$rootScope', '$timeout', 'serverCallService', 'authenticatedUserService', '$mdDialog', 'toastService', 'userLocatorService', 'userSessionService', 'userEmailService',
    function($location, $rootScope, $timeout, serverCallService, authenticatedUserService, $mdDialog, toastService, userLocatorService, userSessionService, userEmailService) {
        var isAuthenticationInProgress;
        var isOAuthAuthentication = false;
        $rootScope.showLocationDialog = true;
        $rootScope.userFirstLogin = false;

        var mobileIdLoginSuccessCallback;
        var mobileIdLoginFailCallback;
        var mobileIdChallengeReceivedCallback;

        function authenticateUser(authenticatedUser) {
            if (isEmpty(authenticatedUser)) {
                loginFail();
            } else {
                $rootScope.justLoggedIn = true;
                authenticatedUserService.setAuthenticatedUser(authenticatedUser);
                serverCallService.makeGet("rest/user/role")
                    .then(({data}) => {
                        getRoleSuccess(data)
                    }, () => loginFail);
            }
        }

        function showLocationDialog() {
            $mdDialog.show({
                templateUrl: 'views/locationDialog/locationDialog.html',
                controller: 'locationDialogController',
                clickOutsideToClose: true
            })
            userLocatorService.stopTimer()
        }

        function hasEmail(userStatus) {
            userEmailService.hasEmailOnLogin(userStatus)
                .then(response => {
                    if (response.status === 200)
                        $rootScope.userHasEmailOnLogin = true
                    else
                        $rootScope.userHasEmailOnLogin = false

                    return $rootScope.userHasEmailOnLogin
                })
        }

        function showGdprModalAndAct(userStatus) {
            hasEmail(userStatus)
            $rootScope.statusForDuplicateCheck = userStatus
            $mdDialog.show({
                templateUrl: 'views/agreement/agreementDialog.html',
                controller: 'agreementDialogController',
                controllerAs: '$ctrl',
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
                            if ($rootScope.userHasEmailOnLogin) {
                                authenticateUser(response.data);
                            } else {
                                userEmailService.saveEmail($rootScope.email, response.data.user)
                                showEmailValidationModal(response)
                                $rootScope.userFromAuthentication = response.data.user
                            }
                            }, () => {
                                loginFail();
                            }
                        )
                }
            })
        }

        function showEmailValidationModal(response) {
            $mdDialog.show({
                templateUrl: 'views/emailValidation/emailValidationDialog.html',
                controller: 'emailValidationController',
                clickOutsideToClose: false,
                escapeToClose: false,
            }).then((res) => {
                if (!res)
                    loginFail()
                 else
                    authenticateUser(response.data);
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
            toastService.show('ERROR_LOGIN_FAILED');
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

        function idCodeLoginFail(msg) {
            console.log('Logging in failed.');
            $mdDialog.hide();
            toastService.show(msg, 35000, 'user-missing-id');
            enableLogin();
            authenticatedUserService.removeAuthenticatedUser();

            localStorage.removeItem(LOGIN_ORIGIN);
            $location.url('/');

        }

        function finishLogin(authenticatedUser) {
            authenticatedUserService.setAuthenticatedUser(authenticatedUser);
            if ($rootScope.afterAuthRedirectURL) {

                $location.url('/' + authenticatedUser.user.username + $rootScope.afterAuthRedirectURL);
            } else if (authenticatedUser.firstLogin) {
                $location.url('/profiil');
                $rootScope.userFirstLogin = true
            } else if (isOAuthAuthentication) {
                $location.url(authenticatedUser.user.username + localStorage.getItem(LOGIN_ORIGIN));
            }
            enableLogin();

            localStorage.removeItem(LOGIN_ORIGIN);
            isOAuthAuthentication = false;
            $rootScope.afterAuthRedirectURL = null;
            toastService.show('LOGIN_SUCCESS');

            if (mobileIdLoginSuccessCallback) {
                mobileIdLoginSuccessCallback();
            }

            userLocatorService.getUserLocation().then((response) => {
                if (response.data && $rootScope.showLocationDialog && (response.data !== $location.url())) {
                    showLocationDialog()
                    $rootScope.locationDialogIsOpen = true
                }
            });

            $rootScope.justLoggedIn = true;
            $timeout(() =>
                $rootScope.$broadcast('login:success')
            )

            switch ($rootScope.authenticationOption) {
                case 'idCard':
                    gTagCaptureEvent('login', 'user', 'ID-Card')
                    break;
                case 'ekool':
                    gTagCaptureEvent('login', 'user', 'ekool.eu')
                    break;
                case 'stuudium':
                    gTagCaptureEvent('login', 'user', 'stuudium.com')
                    break;
                case 'harID':
                    gTagCaptureEvent('login', 'user', 'HarID')
                    break;
                case 'mID':
                    gTagCaptureEvent('login', 'user', 'Mobile-ID')
                    break;
            }
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

        function endSession(url) {
            userLocatorService.saveUserLocation();
            userLocatorService.stopTimer();
            $rootScope.showLocationDialog = true;
            userSessionService.stopTimer();
            serverCallService.makePost(url)
                .then(() => {
                    authenticatedUserService.removeAuthenticatedUser();
                    $rootScope.$broadcast('logout:success');
                    enableLogin();
                });
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
                endSession('rest/user/logout')
            },

            terminate: function() {
                endSession('rest/user/terminateSession')
            },

            loginWithIdCard: function() {
                if (isAuthenticationInProgress) {
                    return;
                }

                disableLogin();
                serverCallService.makeGet("rest/login/idCard", {}, loginSuccess, loginFail);
            },

            loginWithEkool : function() {
                loginWithOAuth("/rest/login/ekool");
            },

            loginWithStuudium : function() {
                loginWithOAuth("/rest/login/stuudium");
            },

            loginWithHarid: function() {
                loginWithOAuth('/rest/login/harid');
            },

            authenticateUsingOAuth: function(inputParams) {
                const {token, agreement, existingUser, eKoolUserMissingIdCode, stuudiumUserMissingIdCode, harIdUserMissingIdCode, loginFrom} = inputParams;
                if (eKoolUserMissingIdCode) {
                    idCodeLoginFail('ERROR_LOGIN_FAILED_EKOOL');
                    return;
                }

                if (stuudiumUserMissingIdCode) {
                    idCodeLoginFail('ERROR_LOGIN_FAILED_STUUDIUM');
                    return;
                }

                if (harIdUserMissingIdCode) {
                    idCodeLoginFail('ERROR_LOGIN_FAILED_HARID');
                    return;
                }

                isOAuthAuthentication = true;
                if (!(agreement || existingUser)){
                    serverCallService.makeGet("rest/login/getAuthenticatedUser", {token}, authenticateUser, loginFail);
                } else {
                    const params = {
                        token,
                        agreementId : agreement,
                        existingUser,
                        loginFrom
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
