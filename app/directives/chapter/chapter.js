define(['app'], function(app)
{
    app.directive('dopChapter', ['translationService',
     function(translationService) {
        return {
            scope: {
            	chapter: '='
            },
            templateUrl: 'directives/chapter/chapter.html',
            link: function () {
            }
        };
    }]);

    return app;
});
