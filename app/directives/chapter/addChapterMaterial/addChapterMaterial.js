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
                    var addMaterialScope = $scope.$new(true);

                    addMaterialScope.material ={};
                    addMaterialScope.material.url = $scope.chapter.resourcePermalink;
                    $mdDialog.show({
                        controller: 'addMaterialDialog',
                        templateUrl: 'views/addMaterialDialog/addMaterialDialog.html',
                        scope: addMaterialScope
                    });
                }
            }
        };
    }]);

    return app;
});