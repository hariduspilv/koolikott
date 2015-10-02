define(['app'], function(app)
{    
    app.directive('dopLoginBar', ['authenticationService', '$location', 'translationService', 
     function(authenticationService, $location, translationService) {
        return {
            scope: true,
            templateUrl: 'app/directives/login-bar/login-bar.html',
            controller: function ($scope) {

                $scope.mobileId = {};
                $scope.validation = {
                    error: {}
                };

                $scope.idCardAuth = function() {
                    authenticationService.loginWithIdCard();
                };

                $scope.taatAuth = function() {
                    authenticationService.loginWithTaat();
                };

                $scope.mobileIdAuth = function() {
                    $scope.validation.error.idCode = null;
                    $scope.validation.error.phoneNumber = null;

                    var idCodeValid = isIdCodeValid($scope.mobileId.idCode);
                    var phoneNumberEstonian = isPhoneNumberEstonian($scope.mobileId.phoneNumber);

                    if (idCodeValid && phoneNumberEstonian) {
                        language = translationService.getLanguage();
                        authenticationService.loginWithMobileId($scope.mobileId.phoneNumber, $scope.mobileId.idCode, language,
                        mobileIdSuccess, mobileIdFail, mobileIdReceiveChallenge);
                    } else {
                        showValidationErrors(idCodeValid, phoneNumberEstonian);
                    }
                };

                function mobileIdSuccess() {
                    $scope.mobileIdChallenge = null;
                    $scope.mobileId.idCode = null;
                    $scope.mobileId.phoneNumber = null;
                }

                function mobileIdFail() {
                    $scope.mobileIdChallenge = null;
                }

                function mobileIdReceiveChallenge(challenge) {
                    $scope.mobileIdChallenge = challenge;
                }

                function isPhoneNumberEstonian(phoneNumber) {
                    if (!phoneNumber) {
                        return false;
                    }
                    return !phoneNumber.startsWith("+") || phoneNumber.startsWith("+372");
                }

                function showValidationErrors(idCodeValid, phoneNumberEstonian) {
                    if (!idCodeValid) {
                        if (isEmpty($scope.mobileId.idCode)) {
                            $scope.validation.error.idCode = "required";
                        } else {
                            $scope.validation.error.idCode = "invalid";
                        }
                    }

                    if (!phoneNumberEstonian) {
                        if (isEmpty($scope.mobileId.phoneNumber)) {
                            $scope.validation.error.phoneNumber = "required";
                        } else {
                            $scope.validation.error.phoneNumber = "notEstonian";
                        }
                    }
                }
            }
        };
    }]);
    
    return app;
});
