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
                    $scope.authorized = authenticatedUserService.isAuthenticated();
                    
                    $scope.addComment = function() {
                        $scope.submitClick();
                    }
                }
            };
        }]);

    return app;
});