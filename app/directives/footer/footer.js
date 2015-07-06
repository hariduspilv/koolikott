define(['app'], function(app)
{
    
    app.directive('dopFooter', function(translationService) {
        return {
            scope: true,
            templateUrl: 'app/directives/footer/footer.html',
            controller: function ($scope, $location) {
                
            }
        };
    });
    
    return app;
});