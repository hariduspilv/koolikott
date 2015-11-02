define(['app'], function(app)
{

    app.directive('dopAddPortfolio', ['translationService', 
        function(translationService) {
            return {
                scope: true,
                templateUrl: 'app/directives/addPortfolio/addPortfolio.html',
                controller: function ($scope, $location) {


                }
            };
        }
    ]);

    return app;
});