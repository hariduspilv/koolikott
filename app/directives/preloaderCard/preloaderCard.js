define(['app'], function(app)
{
    app.directive('dopPreloaderCard', function() {
        return {
            scope: false,
            templateUrl: 'directives/preloaderCard/preloaderCard.html'
        };
    });

    return app;
});
