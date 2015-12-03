define(['app'], function(app)
{
    app.directive('dopCommentsCard', ['translationService', 'serverCallService', 'authenticatedUserService',
        function(translationService, serverCallService, authenticatedUserService) {
            return {
                scope: {
                  comments: '=',
                  comment: '=',
                  submitClick: "&"
                },
                templateUrl: 'directives/commentsCard/commentsCard.html',
                controller: function ($scope) {
                    var COMMENTS_PER_PAGE = 5;
                    
                    $scope.authorized = authenticatedUserService.isAuthenticated();

                    $scope.visibleCommentsCount = COMMENTS_PER_PAGE;
                    
                    $scope.getLoadMoreCommentsLabel = function() {
                        var commentsLeft = getLeftCommentsCount();
                                      
                        if (commentsLeft <= COMMENTS_PER_PAGE)
                            return '(' + commentsLeft + ')'
                        
                        return '(' + COMMENTS_PER_PAGE + '/' + commentsLeft + ')';
                    }
                    
                    $scope.showMoreComments = function() {
                        var commentsLeft = getLeftCommentsCount();
                        
                        if (commentsLeft - COMMENTS_PER_PAGE >= 0)
                            $scope.visibleCommentsCount += COMMENTS_PER_PAGE
                        else
                            $scope.visibleCommentsCount = $scope.comments.length;
                    }
                    
                    function getLeftCommentsCount() {
                        return $scope.comments.length - $scope.visibleCommentsCount;
                    }
                    
                    $scope.addComment = function() {
                        $scope.submitClick();
                    }
                }
            };
        }]);

    return app;
});