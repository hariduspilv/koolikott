'use strict'

angular.module('koolikottApp')
    .directive('dopTags',
        [
            'translationService', '$mdToast', '$translate', 'serverCallService', 'searchService', 'authenticatedUserService', '$location', '$rootScope', '$mdDialog', 'storageService', 'suggestService', '$http', 'dialogService',
            function (translationService, $mdToast, $translate, serverCallService, searchService, authenticatedUserService, $location, $rootScope, $mdDialog, storageService, suggestService, $http, dialogService) {
                return {
                    scope: {
                        learningObject: '='
                    },
                    templateUrl: 'directives/tags/tags.html',
                    controller: function ($scope, $mdToast, $translate, serverCallService, searchService, authenticatedUserService, $location) {
                        var allUpVoteForms;

                        function init() {
                            $scope.showMoreTags = false;

                            if ($scope.learningObject && $scope.learningObject.id) {
                                var reportParams = {
                                    learningObject: $scope.learningObject.id
                                };

                                serverCallService.makeGet("rest/tagUpVotes/report", reportParams, getTagUpVotesReportSuccess)
                            }

                            if ($scope.isAllowed()) {
                                getHasReportedImproper();
                            }
                        }

                        $scope.$watch('learningObject', function (newLearningObject, oldLearningObject) {
                            if (newLearningObject != oldLearningObject) {
                                init();
                            }
                        }, false);

                        function getTagUpVotesReportSuccess(upVoteForms) {
                            var sortedForms = sortTags(upVoteForms);
                            if (sortedForms.length > 10) {
                                $scope.upVoteForms = sortedForms.slice(0, 10);
                                $scope.showMoreTags = true;
                                allUpVoteForms = sortedForms;
                            } else {
                                $scope.upVoteForms = sortedForms;
                            }
                        }

                        $scope.upVote = function (upVoteForm) {
                            $scope.beingUpVotedForm = upVoteForm;
                            var tagUpVote = {
                                learningObject: $scope.learningObject,
                                tag: upVoteForm.tag,
                                user: authenticatedUserService.getUser()
                            };

                            serverCallService.makePut("rest/tagUpVotes", tagUpVote, upVoteSuccess, upVoteFail);
                        };

                        function upVoteSuccess(tagUpVote) {
                            $scope.beingUpVotedForm.tagUpVote = tagUpVote;
                            $scope.beingUpVotedForm.upVoteCount++;
                            $scope.upVoteForms = sortTags($scope.upVoteForms);
                        }

                        function upVoteFail() {
                            log("Failed to up vote.");
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

                        $scope.removeUpVote = function (upVoteForm) {
                            var removeUpVoteUrl = "rest/tagUpVotes/" + upVoteForm.tagUpVote.id;
                            serverCallService.makeDelete(removeUpVoteUrl, {}, removeUpVoteSuccess, removeUpVoteFail);

                            $scope.removedUpVoteForm = upVoteForm;
                        };

                        function removeUpVoteSuccess() {
                            if ($scope.removedUpVoteForm) {
                                $scope.removedUpVoteForm.tagUpVote = null;
                                $scope.removedUpVoteForm.upVoteCount--;
                                $scope.upVoteForms = sortTags($scope.upVoteForms);
                                $scope.removedUpVoteForm = null;
                            }
                        }

                        function removeUpVoteFail() {
                            log("Failed to remove upVote.");
                            $scope.removedUpVoteForm = null;
                        }

                        $scope.getTagSearchURL = function ($event, tag) {
                            $event.preventDefault();

                            searchService.clearFieldsNotInSimpleSearch();
                            searchService.setSearch('tag:"' + tag + '"');
                            $location.url(searchService.getURL());
                        };

                        $scope.removeTag = function (removedTag) {
                            if ($scope.$parent.portfolio && $scope.$parent.portfolio.tags) {
                                $scope.$parent.portfolio.tags.forEach(function(tag, index) {
                                    if (tag === removedTag) {
                                        $scope.$parent.portfolio.tags.splice(index, 1);
                                    }
                                });
                            }
                        };

                        $scope.addTag = function () {
                            if ($scope.learningObject && $scope.learningObject.id) {
                                // TODO
                                var url = "rest/learningObject/" + $scope.learningObject.id + "/tags";
                                serverCallService.makePut(url, JSON.stringify($scope.newTag.tagName), addTagSuccess, addTagFail);
                                $scope.newTag.tagName = null;
                            }
                        };

                        function addTagSuccess(learningObject) {
                            if (!learningObject) {
                                addTagFail();
                            } else {
                                learningObject.picture = $scope.learningObject.picture;
                                $scope.learningObject = learningObject;
                                if (!$scope.learningObject.source && learningObject.uploadedFile) {
                                    $scope.learningObject.source = learningObject.uploadedFile.url;
                                }
                                if (learningObject.type === ".Portfolio") {
                                    storageService.setPortfolio(learningObject);
                                } else if (learningObject.type === ".Material") {
                                    storageService.setMaterial(learningObject);
                                }

                                init();
                            }
                        }

                        function addTagFail() {
                            console.log("Adding tag failed")
                        }

                        $scope.reportTag = function (tag) {

                            var confirm = $mdDialog.confirm()
                                .title($translate.instant('REPORT_IMPROPER_TITLE'))
                                .content($translate.instant('REPORT_IMPROPER_CONTENT') + " " + $translate.instant('REASON_IMPROPER_TAG'))
                                .ok($translate.instant('BUTTON_NOTIFY'))
                                .cancel($translate.instant('BUTTON_CANCEL'));

                            $mdDialog.show(confirm).then(function () {
                                var entity = {
                                    learningObject: $scope.learningObject,
                                    reason: "Tag: " + tag
                                };

                                serverCallService.makePut("rest/impropers", entity, setImproperSuccessful, function () {
                                });
                            });
                        };

                        $scope.showMore = function () {
                            $scope.upVoteForms = allUpVoteForms;
                            $scope.showMoreTags = false;
                        };

                        $scope.showLess = function () {
                            $scope.upVoteForms = allUpVoteForms.slice(0, 10);
                            $scope.showMoreTags = true;
                        };


                        $scope.doSuggest = function (query) {
                            return suggestService.suggest(query, suggestService.getSuggestSystemTagURLbase());
                        };

                        $scope.tagSelected = function () {
                            if ($scope.newTag && $scope.newTag.tagName) {
                                $scope.addTag();
                            }
                        };
                        $scope.tagSelected = function () {
                            if ($scope.newTag && $scope.newTag.tagName) {
                                processSystemTag();
                            }
                        };

                        function processSystemTag() {
                            addSystemTag($scope.newTag.tagName)
                                .then(function (response) {
                                    addTagSuccess(response.data.learningObject);
                                    $scope.newTag.tagName = null;
                                    showSystemTagDialog(response.data.tagTypeName);
                                    $rootScope.$broadcast("errorMessage:updateChanged");
                                });
                        }

                        function showSystemTagDialog(tagType) {
                            if (!tagType) return;

                            $mdDialog.show(
                                $mdDialog.alert()
                                    .clickOutsideToClose(true)
                                    .title('Süsteemne märksõna valitud')
                                    .textContent('Valisid süsteemse märksõna. Vastav väärtus on lisatud materjali külge!')
                                    .ok('Ok')
                                    .closeTo('#' + tagType + '-close')
                            )
                        }

                        function addSystemTag(tagName) {
                            var url = "rest/learningObject/" + $scope.learningObject.id + "/system_tags";
                            return serverCallService.makeGet(url, {
                                'name': tagName,
                                'type': $scope.learningObject.type
                            });
                        }

                        function setImproperSuccessful() {
                            $rootScope.isReportedByUser = true;
                        }

                        function getHasReportedImproper() {
                            if ($scope.learningObject && $scope.learningObject.id) {
                                var improperParams = {
                                    learningObject: $scope.learningObject.id
                                };

                                serverCallService.makeGet("rest/impropers", improperParams, requestSuccessful, requestFailed);
                            }
                        }

                        function requestSuccessful(improper) {
                            if (!$scope.isAdmin()) {
                                $rootScope.isReportedByUser = improper.length > 0;
                            }
                        }

                        function requestFailed() {
                            console.log("Failed checking if already reported the resource")
                        }

                        init();
                    }
                }
            }
        ]);
