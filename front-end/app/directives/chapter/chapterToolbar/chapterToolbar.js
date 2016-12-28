'use strict'

angular.module('koolikottApp')
.directive('dopChapterToolbar',
[
    'translationService', '$mdDialog', '$rootScope', 'storageService', 'serverCallService', '$filter', '$anchorScroll',
    function(translationService, $mdDialog, $rootScope, storageService, serverCallService, $filter, $anchorScroll) {
        return {
            scope: {
                chapter: '=',
                isSub: '=',
                index: '='
            },
            templateUrl: 'directives/chapter/chapterToolbar/chapterToolbar.html',
            controller: ['$scope', '$timeout', function($scope, $timeout) {
                $scope.isEditable = $rootScope.isEditPortfolioMode;

                $scope.addMaterial = function() {
                    var addMaterialScope = $scope.$new(true);

                    addMaterialScope.uploadMode = true;
                    addMaterialScope.material = {};
                    addMaterialScope.isChapterMaterial = true;
                    storageService.setMaterial(null);

                    $mdDialog.show({
                        templateUrl: 'addMaterialDialog.html',
                        controller: 'addMaterialDialogController',
                        scope: addMaterialScope
                    }).then(closeDialog);
                };

                function closeDialog(material) {
                    if (material) {
                        if(!$scope.chapter.contentRows) $scope.chapter.contentRows = [];
                        $scope.chapter.contentRows.push({learningObjects: [material]});
                    }
                }

                $scope.addNewSubChapter = function() {
                    let subChapters = $scope.chapter.subchapters;

                    subChapters.push({
                        title: '',
                        materials: [],
                        openCloseChapter: true
                    });

                    let subChapterID = `chapter-${$scope.index}-${subChapters.length - 1}`;

                    $timeout(function () {
                        focusInput(subChapterID);
                    });
                };

                $scope.openMenu = function($mdOpenMenu, ev) {
                    $mdOpenMenu(ev);
                };

                $scope.openDetailedSearch = function () {
                    $rootScope.savedChapter = $scope.chapter;
                    $rootScope.$broadcast("detailedSearch:open");
                    $rootScope.isPlaceholderVisible = true;

                    if ($rootScope.isEditPortfolioPage) {
                        document.getElementById('header-search-input').focus();
                    }
                };
            }]
        }
    }]);
