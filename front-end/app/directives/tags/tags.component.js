'use strict';

angular.module('koolikottApp')
    .component('dopTags', {
        bindings: {
            learningObject: '=',
            isEditPortfolioMode: '<?'
        },
        templateUrl: 'directives/tags/tags.html',
        controller: TagsController
    });

TagsController.$inject = ['$translate', 'authenticatedUserService', '$rootScope',
    '$mdDialog', 'storageService', 'suggestService', '$scope', 'tagsService', 'toastService'];

function TagsController($translate, authenticatedUserService, $rootScope,
                        $mdDialog, storageService, suggestService, $scope, tagsService, toastService) {

    let vm = this;

    let allUpVoteForms;
    vm.newTag = {};

    vm.$onInit = () => {
        init();
    };

    vm.isLoggedOutAndHasNoTags = () => {
        return !authenticatedUserService.isAuthenticated()
            && vm.learningObject
            && vm.learningObject.tags
            && vm.learningObject.tags.length === 0;
    };

    vm.upVote = (upVoteForm) => {
        vm.beingUpVotedForm = upVoteForm;
        let tagUpVote = {
            learningObject: vm.learningObject,
            tag: upVoteForm.tag,
            user: authenticatedUserService.getUser()
        };

        tagsService.addUpVode(tagUpVote, upVoteSuccess, upVoteFail);
    };

    vm.isAllowed = () => {
        return authenticatedUserService.isAuthenticated() && !authenticatedUserService.isRestricted();
    };

    vm.isAdmin = () => authenticatedUserService.isAdmin();

    vm.isNullOrZeroLength = (arg) => !arg || !arg.length;

    vm.removeUpVote = (upVoteForm) => {
        tagsService.removeUpVote(upVoteForm, removeUpVoteSuccess, removeUpVoteFail);
        vm.removedUpVoteForm = upVoteForm;
    };

    vm.getTagSearchURL = ($event, tag) => {
        $event.preventDefault();
        tagsService.searchByTag(tag);
    };

    vm.removeTag = (removedTag) => {
        if (vm.learningObject && vm.learningObject.tags) {
            vm.learningObject.tags.forEach(function (tag, index) {
                if (tag === removedTag) {
                    vm.learningObject.tags.splice(index, 1);
                }
            });
        }
    };

    vm.addTag = () => {
        if (vm.learningObject && vm.learningObject.id) {
            tagsService.addTag(vm.newTag, vm.learningObject, addTagSuccess, addTagFail);
            vm.newTag.tagName = null;
        }
    };

    vm.reportTag = (tag) => {
        tagsService.reportTag(tag, vm.learningObject, () => {
            toastService.show('TOAST_NOTIFICATION_SENT_TO_ADMIN')
        })
    };

    vm.showMore = () => {
        vm.upVoteForms = allUpVoteForms;
        vm.showMoreTags = false;
    };

    vm.showLess = () => {
        vm.upVoteForms = allUpVoteForms.slice(0, 10);
        vm.showMoreTags = true;
    };

    vm.doSuggest = (query) => suggestService.suggest(query, suggestService.getSuggestSystemTagURLbase());

    vm.tagSelected = () => {
        if (vm.newTag && vm.newTag.tagName) {
            processSystemTag();
        }
    };

    vm.limitTextLength = () => {
        if (vm.newTag && vm.newTag.tagName && vm.newTag.tagName.length > 60) {
            vm.newTag.tagName = vm.newTag.tagName.slice(0, -1);
        }
    };

    function init() {
        vm.showMoreTags = false;

        if (vm.learningObject && vm.learningObject.id) {
            let reportParams = {
                learningObject: vm.learningObject.id
            };

            tagsService.getTagUpVotes(reportParams, getTagUpVotesReportSuccess);
        }
    }

    function getTagUpVotesReportSuccess(upVoteForms) {
        let sortedForms = sortTags(upVoteForms);
        if (sortedForms.length > 10) {
            vm.upVoteForms = sortedForms.slice(0, 10);
            vm.showMoreTags = true;
            allUpVoteForms = sortedForms;
        } else {
            vm.upVoteForms = sortedForms;
        }
    }

    function upVoteSuccess(tagUpVote) {
        vm.beingUpVotedForm.tagUpVote = tagUpVote;
        vm.beingUpVotedForm.upVoteCount++;
        vm.upVoteForms = sortTags(vm.upVoteForms);
    }

    function upVoteFail() {
        log("Failed to up vote.");
    }

    function removeUpVoteSuccess() {
        if (vm.removedUpVoteForm) {
            vm.removedUpVoteForm.tagUpVote = null;
            vm.removedUpVoteForm.upVoteCount--;
            vm.upVoteForms = sortTags(vm.upVoteForms);
            vm.removedUpVoteForm = null;
        }
    }

    function removeUpVoteFail() {
        log("Failed to remove upVote.");
        vm.removedUpVoteForm = null;
    }


    function addTagSuccess(learningObject) {
        if (!learningObject) {
            addTagFail();
        } else {
            learningObject.picture = vm.learningObject.picture;
            vm.learningObject = learningObject;
            if (!vm.learningObject.source && learningObject.uploadedFile) {
                vm.learningObject.source = learningObject.uploadedFile.url;
            }
            if (isPortfolio(learningObject.type)) {
                storageService.setPortfolio(learningObject);
            } else if (isMaterial(learningObject.type)) {
                storageService.setMaterial(learningObject);
            }

            init();
        }
    }

    function addTagFail() {
        console.log("Adding tag failed")
    }

    function processSystemTag() {
        let params = {
            'name': vm.newTag.tagName,
            'type': vm.learningObject.type
        };

        tagsService.addSystemTag(vm.learningObject.id, params)
            .then(function (response) {
                addTagSuccess(response.learningObject);
                showSystemTagDialog(response.tagTypeName);
                updateLearningObject(response.learningObject);
                vm.newTag.tagName = null;
                $rootScope.$broadcast("errorMessage:updateChanged");
            }).catch(function () {
            vm.newTag.tagName = null;
        });
    }

    function updateLearningObject(learningObject) {
        if (isMaterial(learningObject.type)) {
            $scope.$emit("tags:updateMaterial", learningObject);
        } else if (isPortfolio(learningObject.type)) {
            $scope.$emit("tags:updatePortfolio", learningObject)
        }
    }

    function showSystemTagDialog(tagType) {
        if (!tagType) return;

        $mdDialog.show(
            $mdDialog.alert()
                .clickOutsideToClose(true)
                .title($translate.instant('SYSTEM_TAG_DIALOG_TITLE'))
                .textContent($translate.instant('SYSTEM_TAG_DIALOG_CONTENT'))
                .ok('Ok')
                .closeTo('#' + tagType + '-close')
        )
    }

    $rootScope.$on('materialEditModalClosed', function () {
        let reportParams = {
            learningObject: vm.learningObject.id
        };

        tagsService.getTagUpVotes(reportParams, getTagUpVotesReportSuccess);
    });
}
