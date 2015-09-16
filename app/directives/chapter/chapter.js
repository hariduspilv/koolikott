define(['app'], function(app)
{    
    app.directive('dopChapter', ['translationService',
     function(translationService) {
        return {
            scope: {
            	chapter: '=' 
            },
            templateUrl: 'app/directives/chapter/chapter.html',
            link: function () {
            }
        };
    }]);
    
    return app;
});
