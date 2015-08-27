define(['app'], function(app)
{    
    app.directive('dopLoginBar', ['authenticationService',
     function(authenticationService) {
        return {
            scope: true,
            templateUrl: 'app/directives/login-bar/login-bar.html',
            controller: function ($scope) {

                $scope.idCardAuth = function() {
                    authenticationService.loginWithIdCard();
                };
                
            }
        };
    }]);
    
    return app;
});
