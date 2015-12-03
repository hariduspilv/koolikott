define(['app'], function(app)
{
    app.directive('dopRecommend', ['translationService',
     function(translationService) {
        return {
            templateUrl: 'directives/recommend/recommend.html'
        };
    }]);

    return app;
});