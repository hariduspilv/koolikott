define([
    'angularAMD',
    'services/translationService',
    'services/serverCallService',
    'services/searchService',
    'services/authenticatedUserService'
], function (angularAMD) {
    angularAMD.directive('dopTags',['translationService', '$mdToast', '$translate', 'serverCallService', 'searchService', 'authenticatedUserService', '$location', '$rootScope', '$mdDialog',
        function (translationService, $mdToast, $translate, serverCallService, searchService, authenticatedUserService, $location, $rootScope, $mdDialog) {
        return {
            scope: {
                material: '=',
                portfolio: '='
            },
            templateUrl: 'directives/tags/tags.html',
            controller: function ($scope, $mdToast, $translate, serverCallService, searchService, authenticatedUserService, $location) {
                var allTags;

                function init() {
                    $scope.showMoreTags = false;
                    if ($scope.material && $scope.material.id) {
                        var upVotesParams = {
                            'material': $scope.material.id
                        };
                    } else if ($scope.portfolio && $scope.portfolio.id) {
                        upVotesParams = {
                            'portfolio': $scope.portfolio.id
                        };
                    }
                    if($scope.isAllowed()) {
                        getHasReported();
                    }

                    serverCallService.makeGet("rest/tagUpVotes", upVotesParams, getTagUpVotesSuccess, function () {
                    });
                }

                function getTagUpVotesSuccess(upVoteForms) {
                    var tags = sortTags(upVoteForms);
                    if(tags.length > 9) {
                        $scope.tags = tags.slice(0, 10);
                        $scope.showMoreTags = true;
                        allTags = tags;
                    } else {
                        $scope.tags = tags;
                    }
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

                $scope.isAllowed = function () {
                    return authenticatedUserService.isAuthenticated() && !authenticatedUserService.isRestricted();
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

                    searchService.clearFieldsNotInSimpleSearch();
                    searchService.setSearch('tag:"' + tag + '"');
                    $location.url(searchService.getURL());
                };

                $scope.addTag = function () {
                    var url;
                    if ($scope.material && $scope.material.id) {
                        url = "rest/material/" + $scope.material.id + "/tag";
                    } else if ($scope.portfolio && $scope.portfolio.id) {
                        url = "rest/portfolio/" + $scope.portfolio.id + "/tag";
                    }
                    serverCallService.makePut(url, $scope.newTag, addTagSuccess, addTagFail);
                    $scope.newTag = null;
                };

                function addTagSuccess(data) {
                    if(data && data.type === ".Portfolio") {
                        $scope.portfolio = data;
                        $rootScope.savedPortfolio = data;
                    } else if(data && data.type === ".Material") {
                        $scope.material = data;
                    }

                    init();
                }

                function addTagFail() {
                    console.log("Adding tag failed")
                }

                $scope.reportTag = function (tag) {

                    var confirm = $mdDialog.confirm()
                        .title($translate.instant('REPORT_IMPROPER_TITLE'))
                        .content($translate.instant('REPORT_IMPROPER_CONTENT') + " " + $translate.instant('REASON') + ": " + tag)
                        .ok($translate.instant('BUTTON_NOTIFY'))
                        .cancel($translate.instant('BUTTON_CANCEL'));

                    $mdDialog.show(confirm).then(function () {
                    	var learningObject = $scope.material ? $scope.material : $scope.portfolio;
                        var entity = {
                            learningObject: learningObject,
                            reason: "Tag: " + tag
                        };

                        serverCallService.makePut("rest/impropers", entity, setImproperSuccessful, function () {});
                    });
                };

                $scope.showMore = function () {
                    $scope.tags = allTags;
                    $scope.showMoreTags = false;
                };

                $scope.showLess = function () {
                    $scope.tags = allTags.slice(0, 10);
                    $scope.showMoreTags = true;
                };

                function setImproperSuccessful() {
                    $scope.isReportedByUser = true;
                }
                
                function getHasReported() {
                	var learningObject = $scope.material ? $scope.material : $scope.portfolio;
                    if (learningObject && learningObject.id) {
                        var url = "rest/impropers?learningObject=" + learningObject.id;
                        serverCallService.makeGet(url, {}, requestSuccessful, requestFailed);
                    }
                }

                function requestSuccessful(improper) {
                    if (!$scope.isAdmin()) {
                        $scope.isReportedByUser = improper.length > 0;
                    }
                }

                function requestFailed() {
                    console.log("Failed checking if already reported the resource")
                }

                init();
            }
        };
    }]);
});