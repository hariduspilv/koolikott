define(['app'], function(app)
{
    app.directive('dopChapter', ['translationService', '$rootScope', 'dialogService',
     function(translationService, $rootScope, dialogService) {
        return {
            scope: {
                chapter: '=',
                index: '@',
                onDelete: '&'
            },
            templateUrl: 'directives/chapter/chapter.html',
            controller: function($scope) {
                $scope.onDeleteSubChapter = function(subChapter) {
                    
                    var deleteSubChapter = function() {
                        $scope.chapter.subchapters.splice($scope.chapter.subchapters.indexOf(subChapter), 1);
                    };
                    
                    var confirm = dialogService.showDeleteConfirmationDialog(
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

    return app;
});
