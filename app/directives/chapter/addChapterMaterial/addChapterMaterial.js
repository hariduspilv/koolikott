define(['app'], function(app)
{
    app.directive('dopAddChapterMaterial', ['translationService', '$mdDialog',
     function(translationService, $mdDialog) {
        return {
            scope: {
                chapter: '='
            },
            templateUrl: 'directives/chapter/addChapterMaterial/addChapterMaterial.html',
            controller: function ($scope) {
                $scope.addMaterialFromPermalink = function() {
                    $mdDialog.show({
                        controller: 'addMaterialDialog',
                        templateUrl: 'views/addMaterialDialog/addMaterialDialog.html'
                    });
                }
            }
        };
    }]);

    return app;
});