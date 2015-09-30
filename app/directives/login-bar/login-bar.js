define(['app'], function(app)
{    
    app.directive('dopLoginBar', ['authenticationService', '$location', 'translationService', 
     function(authenticationService, $location, translationService) {
        return {
            scope: true,
            templateUrl: 'app/directives/login-bar/login-bar.html',
            controller: function ($scope) {

                $scope.mobileId = [];

                $scope.idCardAuth = function() {
                    authenticationService.loginWithIdCard();
                };

                $scope.taatAuth = function() {
                    authenticationService.loginWithTaat();
                };

                $scope.mobileIdAuth = function() {
                    language = translationService.getLanguage();

                    if (validIdCode($scope.mobileId.idCode)) {
                        authenticationService.loginWithMobileId($scope.mobileId.phoneNumber, $scope.mobileId.idCode, language, 
                        mobileIdSuccess, mobileIdFail, mobileIdReceiveChallenge);
                    } else {
                        log("Invalid id code.");
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
