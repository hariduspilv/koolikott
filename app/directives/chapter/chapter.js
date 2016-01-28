define([
    'app',
    'services/translationService',
    'services/dialogService',
    'directives/chapter/addChapterMaterial/addChapterMaterial'
], function(app) {
    app.directive('dopChapter', ['translationService', '$rootScope', 'dialogService', function(translationService, $rootScope, dialogService) {
        return {
            scope: {
                chapter: '=',
                index: '@',
                onDelete: '&'
            },
            templateUrl: 'directives/chapter/chapter.html',
            controller: function($scope, $rootScope) {
                $scope.isEditable = $rootScope.isEditPortfolioMode;

                $scope.onDeleteSubChapter = function(subChapter) {

                    var deleteSubChapter = function() {
                        $scope.chapter.subchapters.splice($scope.chapter.subchapters.indexOf(subChapter), 1);
                    };

                    dialogService.showDeleteConfirmationDialog(
                        'PORTFOLIO_DELETE_SUB_CHAPTER_CONFIRM_TITLE',
                        'PORTFOLIO_DELETE_SUB_CHAPTER_CONFIRM_MESSAGE',
                        deleteSubChapter);

                };

                $scope.deleteChapter = function() {
                    $scope.onDelete()($scope.chapter);
                };
            }
        };
    }]);
});