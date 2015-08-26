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
                
                jQuery('#dropdowned').on('show.bs.collapse', function () {
                    jQuery('#loginButton').removeClass('collapsed');
                })
                
                jQuery('#dropdowned').on('hide.bs.collapse', function () {
                    jQuery('#loginButton').addClass('collapsed');
                })
            }
        };
    }]);
    
    return app;
});
