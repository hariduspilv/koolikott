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
        $rootScope.canCloseWaitingDialog = false
        $rootScope.rejectedPortfolios = [];

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
            console.log('hasemail')
            console.log(userStatus)
            userEmailService.hasEmailOnLogin(userStatus.token)
                .then(response => {
                    $rootScope.userHasEmailOnLogin = response.status === 200;

                    return $rootScope.userHasEmailOnLogin
                })
        }

        function showPleaseWaitDialog() {
            $mdDialog.show({
                templateUrl: 'views/pleaseWaitDialog/pleaseWaitDialog.html',
                controller: 'pleaseWaitDialogController',
                controllerAs: '$ctrl',
                clickOutsideToClose: $rootScope.canCloseWaitingDialog,
                escapeToClose: $rootScope.canCloseWaitingDialog})
        }

        function migrateLearningObjectsAndLogin(authenticatedUser) {
            $timeout(() => {
                showPleaseWaitDialog()
            }, 100)
            serverCallService.makePost('rest/user/migrateLearningObjectLicences', authenticatedUser.user)
                .then((response) => {
                    $rootScope.rejectedPortfolios = response.data
                    $rootScope.canCloseWaitingDialog = true
                    authenticateUser(authenticatedUser)
                })
        }

        function setAllLearningObjectsToPrivate(authenticatedUser) {
            $timeout(() => {
                showPleaseWaitDialog()
            }, 100)
            if (!$rootScope.previouslyDisagreed) {
                serverCallService.makePost('rest/user/setLearningObjectsPrivate', authenticatedUser.user)
                    .then((response) => {
                        $rootScope.rejectedPortfolios = response.data
                        $rootScope.canCloseWaitingDialog = true
                        authenticateUser(authenticatedUser)
                    })
            } else {
                authenticateUser(authenticatedUser)
            }
        }

        function createLicenceAgreementResponse(userId, agreed, disagreed) {
            let licenceAgreementResponse = {}
            licenceAgreementResponse.userId = userId
            licenceAgreementResponse.agreed = agreed
            licenceAgreementResponse.disagreed = disagreed
            return licenceAgreementResponse
        }

        function saveResponseAndMigrateLicences(authenticatedUser) {
            serverCallService.makePost('rest/userLicenceAgreement',
                createLicenceAgreementResponse(authenticatedUser.user.id, true, false))
                .then(() => {
                    migrateLearningObjectsAndLogin(authenticatedUser)
                })
        }

        function saveResponseAndSetLearningObjectsToPrivate(authenticatedUser) {
            setAllLearningObjectsToPrivate(authenticatedUser)
            serverCallService.makePost('rest/userLicenceAgreement',
                createLicenceAgreementResponse(authenticatedUser.user.id, false, true))
        }

        function saveResponseAndLogOut(authenticatedUser) {
            serverCallService.makePost('rest/userLicenceAgreement',
                createLicenceAgreementResponse(authenticatedUser.user.id, false, false))
            loginFail()
        }

        function checkMigrationAgreementResponse(migrationResponse, authenticatedUser) {
            if (migrationResponse.agreed) {
                saveResponseAndMigrateLicences(authenticatedUser)
            } else if (migrationResponse.disagreed) {
                saveResponseAndSetLearningObjectsToPrivate(authenticatedUser)
            } else {
                saveResponseAndLogOut(authenticatedUser)
            }
        }

        function showLicenceMigrationAgreementModal(authenticatedUser) {
            $mdDialog.show({
                templateUrl: 'views/agreement/migrationAgreementDialog.html',
                controller: 'migrationAgreementController',
                controllerAs: '$ctrl',
                escapeToClose: false
            }).then((migrationResponse) => {
                checkMigrationAgreementResponse(migrationResponse, authenticatedUser)
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
                else {
                    showLicenceMigrationAgreementModal(response.data)
                }
            })
        }

        function userHasRespondToLatestLicenceAgreement(userLatestResponse, latestLicenceAgreementVersion) {
            return userLatestResponse === latestLicenceAgreementVersion;
        }

        function checkLicencesAndAct(authenticatedUser) {
            serverCallService.makeGet('/rest/user/areLicencesAcceptable?id=' + authenticatedUser.user.id)
                .then((response) => {
                        if (response) {
                            if (response.data) {
                                authenticateUser(authenticatedUser)
                            } else {
                                showLicenceMigrationAgreementModal(authenticatedUser)
                            }
                        }
                    }
                )
        }

        function analyzeUserLatestResponse(response, authenticatedUser) {
            if (response.agreed) {
                authenticateUser(authenticatedUser)
            } else if (response.disagreed) {
                $rootScope.previouslyDisagreed = true
                $rootScope.canCloseWaitingDialog = true
                checkLicencesAndAct(authenticatedUser)
            }  else {
                showLicenceMigrationAgreementModal(authenticatedUser)
            }
        }

        function checkUserPreviousResponse(authenticatedUser) {
            serverCallService.makeGet('rest/userLicenceAgreement?id=' + authenticatedUser.user.id)
                .then((response => {
                    console.log(response)
                    if (!response.data.hasLearningObjects) {
                        return authenticateUser(authenticatedUser)
                    }
                    if (!response.data.userLicenceAgreement.licenceAgreement) {
                        showLicenceMigrationAgreementModal(authenticatedUser)
                    }
                    serverCallService.makeGet('rest/licenceAgreement/latest')
                        .then((res) => {
                            if (userHasRespondToLatestLicenceAgreement(response.data.userLicenceAgreement.licenceAgreement.version, res.data.version)) {
                                analyzeUserLatestResponse(response.data.userLicenceAgreement, authenticatedUser)
                            } else {
                                showLicenceMigrationAgreementModal(authenticatedUser)
                            }
                        })
                }))
        }

        function showGdprModalAndAct(userStatus) {
            hasEmail(userStatus)
            $rootScope.statusForDuplicateCheck = userStatus
            $mdDialog.show({
                templateUrl: 'views/agreement/agreementDialog.html',
                controller: 'agreementDialogController',
                controllerAs: '$ctrl',
                escapeToClose: false
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
                                checkUserPreviousResponse(response.data)
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

        function loginSuccess(userStatus) {
            const {token, statusOk, userTermsAgreement, gdprTermsAgreement, existingUser, loginFrom} = userStatus;
            console.log(userStatus)
            if (isEmpty(userStatus)) {
                loginFail();
            } else {
                if (userStatus.statusOk){
                    checkUserPreviousResponse(userStatus.authenticatedUser)
                } else {
                    const params = {
                        existingUser : existingUser ? existingUser : null,
                        statusOk,
                        token,
                        userTermsAgreement : userTermsAgreement ? userTermsAgreement.id : null,
                        gdprTermsAgreement : gdprTermsAgreement ? gdprTermsAgreement.id : null,
                        loginFrom
                    }
                    showGdprModalAndAct(params);
                }
            }
        }

        function enableLogin() {
            isAuthenticationInProgress = false;
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
                let loginOrigin = localStorage.getItem(LOGIN_ORIGIN)
                if (loginOrigin.contains('oppematerjalid') || loginOrigin.contains('kogumikud') || loginOrigin.contains('lemmikud')) {
                    $location.url(authenticatedUser.user.username + loginOrigin);
                } else {
                    $location.url(loginOrigin)
                }
            }
            enableLogin();

            localStorage.removeItem(LOGIN_ORIGIN);
            isOAuthAuthentication = false;
            $rootScope.afterAuthRedirectURL = null;
            toastService.show('LOGIN_SUCCESS');
            getLoginFrom(JSON.parse(localStorage.getItem('authenticatedUser')))

            if (mobileIdLoginSuccessCallback) {
                mobileIdLoginSuccessCallback();
            }

            if ($rootScope.rejectedPortfolios.length > 0 && !$rootScope.previouslyDisagreed) {
                $timeout(() => { $mdDialog.show({
                    templateUrl: 'views/notMigratedPortfoliosDialog/notMigratedPortfoliosDialog.html',
                    controller: 'notMigratedPortfoliosController',
                    controllerAs: '$ctrl',
                    clickOutsideToClose: false,
                    escapeToClose: false,
                    locals: { portfolios: $rootScope.rejectedPortfolios }
                    }).then(() => {
                        userLocatorService.getUserLocation().then((response) => {
                            if (response.data && $rootScope.showLocationDialog && (response.data !== $location.url())) {
                                showLocationDialog()
                                $rootScope.locationDialogIsOpen = true
                            }
                        });
                    })
                }, 100)
            } else {
                userLocatorService.getUserLocation().then((response) => {
                    if (response.data && $rootScope.showLocationDialog && (response.data !== $location.url())) {
                        showLocationDialog()
                        $rootScope.locationDialogIsOpen = true
                    }
                });
            }

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
                    $rootScope.afterAuthRedirectURL = '/';
                    $rootScope.$broadcast('logout:success');
                    enableLogin();
                })
        }

        return {

            loginSuccess: function (userStatus) {
                isOAuthAuthentication = true;
                loginSuccess(userStatus);
            },

            loginFail: function () {
                loginFail();
            },

            logout: function () {
                endSession('rest/user/logout')
            },

            terminate: function () {
                endSession('rest/user/terminateSession')
            },

            loginWithIdCard: function () {
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
                const {token, userConfirmed, statusOk, agreement, gdprAgreement, existingUser, eKoolUserMissingIdCode, stuudiumUserMissingIdCode, harIdUserMissingIdCode, loginFrom} = inputParams;
                console.log('2');
                console.log(inputParams);
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
                if (statusOk === 'true') {
                    serverCallService.makeGet('rest/login/getAuthenticatedUser?token=' + token)
                        .then(({data}) => {
                        checkUserPreviousResponse(data)
                        })
                } else {
                    const params = {
                        existingUser : existingUser ? existingUser : null,
                        userConfirmed : (userConfirmed === 'true'),
                        statusOk : (statusOk === 'true'),
                        token,
                        userTermsAgreement : agreement ? agreement : null,
                        gdprTermsAgreement : gdprAgreement ? gdprAgreement : null,
                        loginFrom
                    }
                    console.log(params)
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
            },

            authenticate: function(userData) {
                authenticateUser(userData)
            }

        };
    }
]);
