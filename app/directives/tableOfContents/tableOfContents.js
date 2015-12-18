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
            controller: function ($scope, $rootScope) {
                $scope.isReadOnly = angular.isDefined($scope.isReadOnly) ? $scope.isReadOnly : false;
                $scope.isEditPortfolioMode = $rootScope.isEditPortfolioMode;

                $scope.gotoChapter = function(e, chapterId, subchapterId) {
                    e.preventDefault();

                    var combinedId = 'chapter-' + chapterId;
                    if(subchapterId != null) {
                    	combinedId += '-' + subchapterId;
                    }
                    var $chapter = angular.element(document.getElementById(combinedId));
                    var $context = angular.element(document.getElementById('scrollable-content'));

                    $context.scrollToElement($chapter, 30, 200);
                };

                $scope.addNewSubChapter = function(index) {
                    var subChapters = $scope.portfolio.chapters[index].subchapters;

                    subChapters.push({
                        title: $filter('translate')('PORTFOLIO_DEFAULT_NEW_SUBCHAPTER_TITLE'),
                        materials: []
                    });
                };

                $scope.addNewChapter = function(index) {
                    if(!$scope.portfolio.chapters) {
                        $scope.portfolio.chapters = [];
                    }

                    $scope.portfolio.chapters.push({
                        title: $filter('translate')('PORTFOLIO_DEFAULT_NEW_CHAPTER_TITLE'),
                        subchapters: [],
                        materials: []
                    });
                };
            }
        };
    }]);

    return app;
});
