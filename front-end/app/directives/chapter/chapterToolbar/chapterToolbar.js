define([
    'app',
    'angularAMD',
    'services/translationService',
    'directives/validate/validateUrl'
], function(app, angularAMD) {
    app.directive('dopChapterToolbar', ['translationService', '$mdDialog', '$rootScope', 'storageService', 'serverCallService', '$filter', '$anchorScroll',
    function(translationService, $mdDialog, $rootScope, storageService, serverCallService, $filter, $anchorScroll) {
        return {
            scope: {
                chapter: '=',
                isSub: '='
            },
            templateUrl: 'directives/chapter/chapterToolbar/chapterToolbar.html',
            controller: function($scope) {
                $scope.isEditable = $rootScope.isEditPortfolioMode;

                $scope.addMaterial = function() {
                    var addMaterialScope = $scope.$new(true);

                    addMaterialScope.uploadMode = true;
                    addMaterialScope.material = {};
                    addMaterialScope.isChapterMaterial = true;
                    storageService.setMaterial(null);

                    $mdDialog.show(angularAMD.route({
                        templateUrl: 'addMaterialDialog.html',
                        controllerUrl: 'views/addMaterialDialog/addMaterialDialog',
                        scope: addMaterialScope
                    })).then(closeDialog);
                };

                function closeDialog(material) {
                    if (material) {
                        $scope.chapter.materials.push(material);
                    }
                }

                $scope.addNewSubChapter = function() {
                    var subChapters = $scope.chapter.subchapters;

                    subChapters.push({
                        title: $filter('translate')('PORTFOLIO_DEFAULT_NEW_SUBCHAPTER_TITLE'),
                        materials: [],
                        openCloseChapter: true
                    });
                };

                $scope.openMenu = function($mdOpenMenu, ev) {
                    $mdOpenMenu(ev);
                };

                $scope.openDetailedSearch = function () {
                    $rootScope.$broadcast("detailedSearch:open");
                    $anchorScroll();
                };
            }
        }
    }]);
});
