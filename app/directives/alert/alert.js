define(['app'], function(app)
{
    
    app.directive('dopAlert', ['translationService', '$rootScope', 'authenticationService', 'authenticatedUserService',
     function(translationService, $rootScope, authenticationService, authenticatedUserService) {
        return {
            scope: true,
            templateUrl: 'app/directives/alert/alert.html',
            controller: function ($scope, $rootScope, authenticationService, authenticatedUserService) {
                
            }
        };
    }]);
    
    return app;
});
