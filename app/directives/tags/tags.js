define([
    'app',
    'services/translationService',
    'services/serverCallService',
    'services/searchService',
    'services/authenticatedUserService'
], function (app) {
    app.directive('dopTags', function (translationService, $mdToast, $translate, serverCallService, searchService, authenticatedUserService, $location) {
        return {
            scope: {
                material: '=',
                portfolio: '='
            },
            templateUrl: 'directives/tags/tags.html',
            controller: function ($scope, $mdToast, $translate, serverCallService, searchService, authenticatedUserService, $location) {

                function init() {
                    $scope.newTags = [];

                    if ($scope.material && $scope.material.id) {
                        var upVotesParams = {
                            'material': $scope.material.id
                        };
                    } else if ($scope.portfolio && $scope.portfolio.id) {
                        upVotesParams = {
                            'portfolio': $scope.portfolio.id
                        };
                    }

                    serverCallService.makeGet("rest/tagUpVotes", upVotesParams, getTagUpVotesSuccess, function () {
                    });
                }

                function getTagUpVotesSuccess(upVoteForms) {
                    $scope.tags = sortTags(upVoteForms);
                }

                $scope.upVote = function (tag) {
                    $scope.upVotedTag = tag;
                    $scope.upVotedTag.hasUpVoted = true;
                    var tagUpVote = {
                        material: $scope.material,
                        portfolio: $scope.portfolio,
                        tag: tag
                    };

                    serverCallService.makePut("rest/tagUpVotes", tagUpVote, upVoteSuccess, upVoteFail);
                };

                function upVoteSuccess() {
                    $scope.upVotedTag.upVoteCount = $scope.upVotedTag.upVoteCount + 1;
                    $scope.tags = sortTags($scope.tags);
                }

                function upVoteFail() {
                    $scope.upVotedTag.hasUpVoted = false;
                }

                $scope.isLoggedIn = function () {
                    return authenticatedUserService.isAuthenticated();
                };

                $scope.isAdmin = function () {
                    return authenticatedUserService.isAdmin();
                };

                $scope.isNullOrZeroLength = function (arg) {
                    return !arg || !arg.length;
                };

                $scope.removeUpVote = function (tag) {
                    $scope.upVoteRemovedTag = tag;
                    $scope.upVoteRemovedTag.hasUpVoted = false;
                    if ($scope.material && $scope.material.id) {
                        var removeUpVotesParams = {
                            'material': $scope.material.id,
                            'tag': tag.tag
                        };
                    } else if ($scope.portfolio && $scope.portfolio.id) {
                        removeUpVotesParams = {
                            'portfolio': $scope.portfolio.id,
                            'tag': tag.tag
                        };
                    }

                    serverCallService.makeDelete("rest/tagUpVotes", removeUpVotesParams, removeUpVoteSuccess, removeUpVoteFail);
                };

                function removeUpVoteSuccess() {
                    $scope.upVoteRemovedTag.upVoteCount = $scope.upVoteRemovedTag.upVoteCount - 1;
                    $scope.tags = sortTags($scope.tags);
                }

                function removeUpVoteFail() {
                    $scope.upVoteRemovedTag.hasUpVoted = true;
                }

                $scope.getTagSearchURL = function ($event, tag) {
                    $event.preventDefault();

                    searchService.setSearch('tag:"' + tag + '"');
                    $location.url(searchService.getURL());
                };

                init();
            }
        };
    });
});