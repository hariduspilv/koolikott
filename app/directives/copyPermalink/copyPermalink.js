define(['app', 'clipboard'], function(app, Clipboard)
{
    app.directive('dopCopyPermalink', ['translationService',
     function(translationService) {
        return {
            scope: false,
            templateUrl: 'directives/copyPermalink/copyPermalink.html',
            link: function(scope, element) {
                var button = element.find('button');
                var _id = button.attr('id');
                if (!_id) {
                    button.attr('id', 'ngclipboard' + Date.now());
                    _id = button.attr('id');
                }

                new Clipboard('#' + _id);
            },
            controller: function($scope, $location) {
                $scope.absUrl = $location.absUrl();
            }
        };
    }]);

    return app;
});
