define([
    'angularAMD',
    'services/translationService',
    'services/serverCallService',
    'services/searchService',
    'services/authenticatedUserService',
    'services/storageService'
], function (angularAMD) {
    angularAMD.directive('dopTags',['translationService', '$mdToast', '$translate', 'serverCallService', 'searchService', 'authenticatedUserService', '$location', '$rootScope', '$mdDialog', 'storageService',
        function (translationService, $mdToast, $translate, serverCallService, searchService, authenticatedUserService, $location, $rootScope, $mdDialog, storageService) {
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

                    if($scope.isAllowed()) {
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
                    if(sortedForms.length > 10) {
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
                	$scope.removedUpVoteForm.tagUpVote = null;
                    $scope.removedUpVoteForm.upVoteCount--;
                    $scope.upVoteForms = sortTags($scope.upVoteForms);
                    $scope.removedUpVoteForm = null;
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

                $scope.addTag = function () {
                    if ($scope.learningObject && $scope.learningObject.id) {
                        var url = "rest/learningObject/" + $scope.learningObject.id + "/tags";
                        serverCallService.makePut(url, $scope.newTag, addTagSuccess, addTagFail);
                        $scope.newTag = null;
                    }
                };

                function addTagSuccess(learningObject) {
                	if (!learningObject) {
                		addTagFail();
                	} else {
                        learningObject.picture = $scope.learningObject.picture;
                		$scope.learningObject = learningObject;
                		if(learningObject.type === ".Portfolio") {
                			$rootScope.savedPortfolio = learningObject;
                		} else if (learningObject.type === ".Material") {
                			storageService.setMaterial($scope.learningObject);
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

                        serverCallService.makePut("rest/impropers", entity, setImproperSuccessful, function () {});
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
        };
    }]);
});
