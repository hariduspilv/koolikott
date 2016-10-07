define([
    'app',
    'services/translationService',
    'services/dialogService',
    'directives/chapter/addChapterMaterial/addChapterMaterial',
    'directives/embeddedMaterial/embeddedMaterial'
], function (app) {
    app.directive('dopChapter', ['translationService', '$rootScope', 'dialogService', function (translationService, $rootScope, dialogService) {
        return {
            scope: {
                chapter: '=',
                index: '@',
                onDelete: '&'
            },
            templateUrl: 'directives/chapter/chapter.html',
            controller: function ($scope, $rootScope) {
                $scope.isEditable = $rootScope.isEditPortfolioMode;
                $scope.isCollapsed = false;
                $scope.subisCollapsed = [];
                angular.forEach($scope.chapter.subchapters, function (value, key) {
                    $scope.subisCollapsed[value.$$hashKey] = false;
                }, log);

                if ($scope.chapter.openCloseChapter == true) {
                    $scope.isCollapsed = false;
                }

                //Open/Close Chapter
                $scope.ocChapter = function () {
                    $scope.isCollapsed = !$scope.isCollapsed;
                };

                //Open/Close SubChapter
                $scope.ocSubChapter = function (subChapter) {
                    $scope.subisCollapsed[subChapter.$$hashKey] = !$scope.subisCollapsed[subChapter.$$hashKey];
                };

                $scope.toggle = function (e) {
                    var item = $("div.chapter-arrow");
                    $(e.currentTarget).find(item).toggleClass('toggled');
                };

                $scope.editToggle = function (e) {
                    $(e.currentTarget).toggleClass('toggled');
                };

                $scope.onDeleteSubChapter = function (subChapter) {

                    var deleteSubChapter = function () {
                        $scope.chapter.subchapters.splice($scope.chapter.subchapters.indexOf(subChapter), 1);
                    };

                    dialogService.showDeleteConfirmationDialog(
                        'PORTFOLIO_DELETE_SUB_CHAPTER_CONFIRM_TITLE',
                        'PORTFOLIO_DELETE_SUB_CHAPTER_CONFIRM_MESSAGE',
                        deleteSubChapter);

                };

                $scope.deleteChapter = function () {
                    $scope.onDelete()($scope.chapter);
                };

                $scope.getParsedMaterial = function (material) {
                    delete material['taxons']
                    return material
                }

            }
        };
    }]);
});
