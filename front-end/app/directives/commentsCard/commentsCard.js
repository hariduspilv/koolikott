'use strict'

angular.module('koolikottApp')
.directive('dopCommentsCard',
[
    'translationService', 'serverCallService', 'authenticatedUserService',
    function(translationService, serverCallService, authenticatedUserService) {
        return {
            scope: {
                comments: '=',
                comment: '=',
                submitClick: "&"
            },
            templateUrl: 'directives/commentsCard/commentsCard.html',
            link: function($scope, iElem, iAttr) {
                if (angular.isUndefined($scope.comments))
                $scope.comments = [];
            },
            controller: function($scope) {
                var COMMENTS_PER_PAGE = 5;

                $scope.isAuthorized = function() {
                    return authenticatedUserService.isAuthenticated() &&
                    !authenticatedUserService.isRestricted();
                };

                $scope.isAuthenticated = function() {
                    return authenticatedUserService.isAuthenticated()
                };

                $scope.visibleCommentsCount = COMMENTS_PER_PAGE;

                $scope.getLoadMoreCommentsLabel = function() {
                    var commentsLeft = getLeftCommentsCount();

                    if (commentsLeft <= COMMENTS_PER_PAGE)
                    return '(' + commentsLeft + ')'

                    return '(' + COMMENTS_PER_PAGE + '/' + commentsLeft + ')';
                };

                $scope.showMoreComments = function() {
                    var commentsLeft = getLeftCommentsCount();

                    if (commentsLeft - COMMENTS_PER_PAGE >= 0)
                    $scope.visibleCommentsCount += COMMENTS_PER_PAGE
                    else
                    $scope.visibleCommentsCount = $scope.comments.length;
                };

                $scope.showMoreCommentsButton = function() {
                    return getLeftCommentsCount() > 0;
                };

                function getLeftCommentsCount() {
                    if ($scope.comments) {
                        return $scope.comments.length - $scope.visibleCommentsCount;
                    }
                }

                $scope.addComment = function() {
                    $scope.submitClick();
                }

                //Commentbox hotfix
                setTimeout(commentHotfix, 1000);
                function commentHotfix() {
                    angular.element(document.getElementById('comment-list')).find('textarea').css('height', '112px');
                }

            }
        };
    }]);
