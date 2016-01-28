define([
    'app',
    'angularAMD',
    'services/translationService'
], function(app, angularAMD) {
    app.directive('dopAddChapterMaterial', ['translationService', '$mdDialog', '$rootScope', function(translationService, $mdDialog, $rootScope) {
        return {
            scope: {
                chapter: '='
            },
            templateUrl: 'directives/chapter/addChapterMaterial/addChapterMaterial.html',
            controller: function($scope) {
                $scope.isEditable = $rootScope.isEditPortfolioMode;

                $scope.addMaterialFromPermalink = function() {
                    if ($scope.resourcePermalinkForm.$valid) {
                        var addMaterialScope = $scope.$new(true);

                        addMaterialScope.material = {};
                        addMaterialScope.material.url = $scope.chapter.resourcePermalink;
                        addMaterialScope.isChapterMaterial = true;

                        $mdDialog.show(angularAMD.route({
                          templateUrl: 'views/addMaterialDialog/addMaterialDialog.html',
                          controllerUrl: 'views/addMaterialDialog/addMaterialDialog',
                          scope: addMaterialScope
                        })).then(closeDialog);

                        function closeDialog(material) {
                            if (material) {
                                $scope.chapter.materials.push(material);
                            }
                        }
                    }
                };
            }
        }
    }]);
});