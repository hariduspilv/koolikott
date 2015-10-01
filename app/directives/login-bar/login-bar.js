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

                    if (isIdCodeValid($scope.mobileId.idCode)) {
                        language = translationService.getLanguage();
                        authenticationService.loginWithMobileId($scope.mobileId.phoneNumber, $scope.mobileId.idCode, language,
                        mobileIdSuccess, mobileIdFail, mobileIdReceiveChallenge);
                    } else {
                        if (isEmpty($scope.mobileId.idCode)) {
                            $scope.validation.error.idCode = "required";
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
