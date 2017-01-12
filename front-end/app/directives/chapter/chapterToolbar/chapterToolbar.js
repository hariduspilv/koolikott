'use strict'

angular.module('koolikottApp')
.directive('dopChapterToolbar', function() {
    return {
        scope: {
            chapter: '=',
            isSub: '=',
            index: '='
        },
        templateUrl: 'directives/chapter/chapterToolbar/chapterToolbar.html',
        controller: ['$scope', '$timeout', 'translationService', '$mdDialog', '$rootScope', 'storageService', 'serverCallService', '$filter', '$window', function($scope, $timeout, translationService, $mdDialog, $rootScope, storageService, serverCallService, $filter, $window) {
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
                    title: $filter('translate')('PORTFOLIO_DEFAULT_NEW_SUBCHAPTER_TITLE'),
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
                if ($window.innerWidth >= BREAK_SM) {
                    $rootScope.$broadcast("detailedSearch:open");
                } else {
                    $rootScope.$broadcast("mobileSearch:open");
                }
                $rootScope.isPlaceholderVisible = true;

                if ($rootScope.isEditPortfolioPage) {
                    document.getElementById('header-search-input').focus();
                }
            };
        }]
    }
});
