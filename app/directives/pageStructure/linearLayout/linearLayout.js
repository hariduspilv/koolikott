define(['app'], function(app)
{
    app.directive('dopLinearLayout', [
     function() {
        return {
            scope: true,
            templateUrl: 'directives/pageStructure/linearLayout/linearLayout.html',
            controller: function () {
            	
            }
        };
    }]);

    return app;
});
