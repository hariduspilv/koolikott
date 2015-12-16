define(['app'], function(app)
{
    app.directive('dopColumnLayout', [
     function() {
        return {
            scope: true,
            templateUrl: 'directives/pageStructure/columnLayout/columnLayout.html',
            controller: function () {
            }
        };
    }]);

    return app;
});
