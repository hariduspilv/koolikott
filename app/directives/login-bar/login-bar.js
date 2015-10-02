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

                    if (idCodeValid && !isEmpty($scope.mobileId.phoneNumber)) {
                        language = translationService.getLanguage();
                        authenticationService.loginWithMobileId($scope.mobileId.phoneNumber, $scope.mobileId.idCode, language,
                        mobileIdSuccess, mobileIdFail, mobileIdReceiveChallenge);
                    } else {
                        if (!idCodeValid) {
                            if (isEmpty($scope.mobileId.idCode)) {
                                $scope.validation.error.idCode = "required";
                            } else {
                                $scope.validation.error.idCode = "invalid";
                            }
                        }

                        if (isEmpty($scope.mobileId.phoneNumber)) {
                            $scope.validation.error.phoneNumber = "required";
                        }
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
            }
        };
    }]);
    
    return app;
});
