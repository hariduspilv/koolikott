define(['app'], function(app)
{
    
    app.directive('dopMaterialBox', ['translationService', function(translationService) {
        return {
            scope: true,
            templateUrl: 'app/directives/materialBox/materialBox.html',
            controller: function ($scope, $location) {
            }
        };
    }]);
    
    return app;
});