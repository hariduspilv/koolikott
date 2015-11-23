define(['app'], function(app)
{
    app.directive('dopTableOfContents', [
     function() {
        return {
            scope: {
                portfolio: '=',
                readonly: '=readonly'
            },
            templateUrl: 'directives/tableOfContents/tableOfContents.html',
            controller: function ($scope) {
                $scope.isReadOnly = angular.isDefined($scope.isReadOnly) ? $scope.isReadOnly : false;

                $scope.gotoChapter = function(chapterId, subchapterId) {
                    var combinedId = 'chapter-' + chapterId + '-' + subchapterId;
                    var $chapter = angular.element(document.getElementById(combinedId));
                    var $context = angular.element(document.getElementById('main-content'));

                    $context.scrollToElement($chapter, 30, 200);
                }

                $scope.addNewSubChapter = function(index) {
                    var subChapters = $scope.portfolio.chapters[index].subchapters;

                    subChapters.push({
                        title: 'Alampeatükk ' + (subChapters.length + 1),
                        materials: []
                    });
                };

                $scope.addNewChapter = function(index) {
                    $scope.portfolio.chapters.push({
                        title: 'Uus peatükk',
                        subchapters: []
                    });
                };
            }
        };
    }]);

    return app;
});
