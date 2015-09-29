define(['app'], function(app)
{    
    app.directive('dopLoginBar', ['authenticationService', '$location', 'translationService', 
     function(authenticationService, $location, translationService) {
        return {
            scope: true,
            templateUrl: 'app/directives/login-bar/login-bar.html',
            controller: function ($scope) {

                var ESTONIAN_CALLING_CODE = "+372";

                $scope.mobileId = [];

                $scope.idCardAuth = function() {
                    authenticationService.loginWithIdCard();
                };

                $scope.taatAuth = function() {
                    authenticationService.loginWithTaat();
                };

                $scope.mobileIdAuth = function() {
                    language = translationService.getLanguage();

                    // Check if user did not enter a calling code
                    if ($scope.mobileId.phoneNumber.indexOf("+") !== 0) {
                        $scope.mobileId.phoneNumber = ESTONIAN_CALLING_CODE + $scope.mobileId.phoneNumber;
                    }

                    authenticationService.loginWithMobileId($scope.mobileId.phoneNumber, $scope.mobileId.idCode, language, 
                        mobileIdSuccess, mobileIdFail, mobileIdReceiveChallenge);
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
