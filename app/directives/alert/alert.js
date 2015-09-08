define(['app'], function(app)
{
    app.directive('dopAlert', ['translationService', '$rootScope',
    function(translationService, $rootScope) {
        return {
            scope: true,
            templateUrl: 'app/directives/alert/alert.html',
            controller: function ($rootScope) {
                
            }
        };
    }]);
    
    return app;
});
