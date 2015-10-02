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
                    var idCodeValid = validateIdCode();
                    var phoneNumberValid = validatePhoneNumber();

                    if (idCodeValid && phoneNumberValid) {
                        language = translationService.getLanguage();
                        authenticationService.loginWithMobileId($scope.mobileId.phoneNumber, $scope.mobileId.idCode, language,
                        mobileIdSuccess, mobileIdFail, mobileIdReceiveChallenge);
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

                function validateIdCode() {
                    $scope.validation.error.idCode = null;

                    if (isEmpty($scope.mobileId.idCode)) {
                        $scope.validation.error.idCode = "required";
                    } else {
                        var isValid = isIdCodeValid($scope.mobileId.idCode);

                        if (!isValid) {
                            $scope.validation.error.idCode = "invalid";
                        }
                    }

                    return isValid;
                }

                function validatePhoneNumber() {
                    $scope.validation.error.phoneNumber = null;

                    if (isEmpty($scope.mobileId.phoneNumber)) {
                        $scope.validation.error.phoneNumber = "required";
                    } else {
                        var isValid = isPhoneNumberEstonian($scope.mobileId.phoneNumber);

                        if (!isValid) {
                            $scope.validation.error.phoneNumber = "notEstonian";
                        }
                    }

                    return isValid;
                }

                function isPhoneNumberEstonian(phoneNumber) {
                    return !phoneNumber.startsWith("+") || phoneNumber.startsWith("+372");
                }
            }
        };
    }]);
    
    return app;
});
