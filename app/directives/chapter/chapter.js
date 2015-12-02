define(['app'], function(app)
{
    app.directive('dopChapter', ['translationService', '$rootScope',
     function(translationService, $rootScope) {
        return {
            scope: false,
            templateUrl: 'directives/chapter/chapter.html',
            link: function () {
            }
        };
    }]);

    return app;
});
