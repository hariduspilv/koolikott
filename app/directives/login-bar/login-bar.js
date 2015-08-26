define(['app'], function(app)
{    
    app.directive('dopLoginBar', ['loginService',
     function(loginService) {
        return {
            scope: true,
            templateUrl: 'app/directives/login-bar/login-bar.html',
            controller: function ($scope) {

                $scope.idCardAuth = function() {
                    loginService.loginWithIdCard();
                };
                
            }
        };
    }]);
    
    return app;
});
