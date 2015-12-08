define(['app'], function(app)
{
    app.directive('dopTableOfContents', ['$filter',
     function($filter) {
        return {
            scope: {
                portfolio: '=',
                readonly: '=readonly'
            },
            templateUrl: 'directives/tableOfContents/tableOfContents.html',
            controller: function ($scope) {
                $scope.isReadOnly = angular.isDefined($scope.isReadOnly) ? $scope.isReadOnly : false;

                $scope.gotoChapter = function(chapterId, subchapterId) {
                    var combinedId = 'chapter-' + chapterId;
                    if(subchapterId != null) {
                    	combinedId += '-' + subchapterId;
                    }
                    var $chapter = angular.element(document.getElementById(combinedId));
                    var $context = angular.element(document.getElementById('main-content'));

                    $context.scrollToElement($chapter, 30, 200);
                }

                $scope.addNewSubChapter = function(index) {
                    var subChapters = $scope.portfolio.chapters[index].subchapters;

                    subChapters.push({
                        title: $filter('translate')('PORTFOLIO_DEFAULT_NEW_SUBCHAPTER_TITLE'),
                        materials: []
                    });
                };

                $scope.addNewChapter = function(index) {
                    $scope.portfolio.chapters.push({
                        title: $filter('translate')('PORTFOLIO_DEFAULT_NEW_CHAPTER_TITLE'),
                        subchapters: []
                    });
                };
            }
        };
    }]);

    return app;
});
