define(['app'], function(app)
{    
    app.directive('dopDetailedSearch', [ 
     function() {
        return {
            scope: true,
            templateUrl: 'app/directives/detailedSearch/detailedSearch.html',
            controller: function ($scope) {
                
            }
        };
    }]);
    
    return app;
});
