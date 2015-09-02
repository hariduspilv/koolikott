define(['app'], function(app)
{    
    app.directive('dopLoginBar', ['authenticationService', '$location', 
     function(authenticationService, $location) {
        return {
            scope: true,
            templateUrl: 'app/directives/login-bar/login-bar.html',
            controller: function ($scope) {

                $scope.idCardAuth = function() {
                    authenticationService.loginWithIdCard();
                };

                $scope.taatAuth = function() {
                    authenticationService.loginWithTaat();
                };
                
            }
        };
    }]);
    
    return app;
});
