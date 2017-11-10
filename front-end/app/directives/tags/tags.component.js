'use strict'

{
const SHOW_TAG_REPORT_MODAL_HASH = 'dialog-report-tag'

class controller extends Controller {
    $onInit() {
        this.newTag = {}
        this.$rootScope.$on('materialEditModalClosed', this.getTagUpVotes.bind(this))
        this.init()

        this.unsubscribeTagsAdded = this.$rootScope.$watch('learningObjectChanges', () => {
            if (Array.isArray(this.$scope.tags) && this.$scope.tags.length)
                this.setNewTags()
        }, true)

        // auto-launch the report dialog upon login or page load if hash is found in location URL
        this.$timeout(() =>
            window.location.hash.includes(SHOW_TAG_REPORT_MODAL_HASH)
                ? this.onLoginSuccess()
                : this.unsubscribeLoginSuccess = this.$rootScope.$on('login:success', this.onLoginSuccess.bind(this))
        )
    }
    $onDestroy() {
        if (typeof this.unsubscribeLoginSuccess === 'function')
            this.unsubscribeLoginSuccess()
        if (typeof this.unsubscribeTagsAdded === 'function')
            this.unsubscribeTagsAdded()
    }
    init() {
        this.showMoreTags = false

        if (this.learningObject && this.learningObject.id)
            this.getTagUpVotes()
    }
    onLoginSuccess() {
        if (
            window.location.hash.includes(SHOW_TAG_REPORT_MODAL_HASH) &&
            this.authenticatedUserService.isAuthenticated()
        ) {
            this.removeHash()
            !this.loginDialog
                ? this.reportTag()
                : this.loginDialog.then(() => {
                    this.reportTag()
                    delete this.loginDialog
                })
        }
    }
    getTagUpVotes() {
        this.serverCallService
            .makeGet('rest/tagUpVotes/report', {
                learningObject: this.learningObject.id
            })
            .then(({ data: tags }) => {
                let sorted = this.sortTagsByUpVoteCount(tags)

                if (sorted.length > 10) {
                    this.allTags = sorted
                    this.$scope.tags = this.allTags.slice(0, 10)
                    this.showMoreTags = true
                } else
                    this.$scope.tags = sorted

                this.setNewTags()
            })
    }
    sortTagsByUpVoteCount(tags) {
        return Array.isArray(tags)
            ? tags.sort((a, b) => b.upVoteCount - a.upVoteCount)
            : tags
    }
    isLoggedOutAndHasNoTags() {
        return !this.authenticatedUserService.isAuthenticated()
            && this.learningObject
            && this.learningObject.tags
            && this.learningObject.tags.length === 0
    }
    upVote(tag) {
        this.upVotedTag = tag
        this.tagsService
            .addUpVote({
                learningObject: this.learningObject,
                tag: tag.tag,
                user: this.authenticatedUserService.getUser()
            })
            .then(tagUpVote => {
                this.upVotedTag.tagUpVote = tagUpVote
                this.upVotedTag.upVoteCount++
                this.$scope.tags = this.sortTagsByUpVoteCount(this.$scope.tags)
            })
    }
    isAllowed() {
        return this.authenticatedUserService.isAuthenticated()
            && !this.authenticatedUserService.isRestricted()
    }
    removeUpVote(upVoteForm) {
        this.removedTag = upVoteForm
        this.tagsService
            .removeUpVote(upVoteForm)
            .then(() => {
                if (this.removedTag) {
                    this.removedTag.tagUpVote = null
                    this.removedTag.upVoteCount--
                    this.$scope.tags = this.sortTagsByUpVoteCount(this.$scope.tags)
                    this.removedTag = null
                }
            }, () =>
                this.removedTag = null
            )
    }
    getTagSearchURL($event, tag) {
        $event.preventDefault()
        this.tagsService.searchByTag(tag)
    }
    removeTag(removedTag) {
        if (this.learningObject && this.learningObject.tags)
            this.learningObject.tags.forEach((tag, idx) => {
                if (tag === removedTag)
                    this.learningObject.tags.splice(idx, 1)
            })
    }
    addTag() {
        if (this.learningObject && this.learningObject.id) {
            this.serverCallService
                .makePut(`rest/learningObject/${this.learningObject.id}/tags`, JSON.stringify(this.newTag.tagName))
                .then(({ data }) =>
                    this.addTagSuccess(data)
                )
            this.newTag.tagName = null
        }
    }
    addTagSuccess(learningObject) {
        if (this.learningObject) {
            learningObject.picture = this.learningObject.picture
            this.learningObject = learningObject

            if (!this.learningObject.source && learningObject.uploadedFile)
                this.learningObject.source = learningObject.uploadedFile.url

            this.isPortfolio(learningObject) ? this.storageService.setPortfolio(learningObject) :
            this.isMaterial(learningObject) && this.storageService.setMaterial(learningObject)

            this.init()
        }
    }
    reportTag(evt) {
        !this.authenticatedUserService.isAuthenticated()
            ? this.showLoginDialog(evt)
            : this.tagsService
                .reportTag(this.learningObject, evt)
                .then(() => {
                    this.$rootScope.learningObjectImproper = true
                    this.$rootScope.$broadcast('errorMessage:reported')
                    this.toastService.show('TOAST_NOTIFICATION_SENT_TO_ADMIN')
                })
    }
    showLoginDialog(targetEvent) {
        this.addHash()
        this.loginDialog = this.$mdDialog.show({
            templateUrl: 'views/loginDialog/loginDialog.html',
            controller: 'loginDialogController',
            bindToController: true,
            locals: {
                title: this.$translate.instant('LOGIN_MUST_LOG_IN_TO_REPORT_IMPROPER')
            },
            clickOutsideToClose: true,
            escapeToClose: true,
            targetEvent
        })
        .catch(this.removeHash)
    }
    addHash() {
        window.history.replaceState(null, null,
            ('' + window.location).split('#')[0] + '#' + SHOW_TAG_REPORT_MODAL_HASH
        )
    }
    removeHash() {
        window.history.replaceState(null, null,
            ('' + window.location).split('#')[0]
        )
    }
    showMore() {
        this.$scope.tags = this.allTags
        this.showMoreTags = false
    }
    showLess() {
        this.$scope.tags = this.allTags.slice(0, 10)
        this.showMoreTags = true
    }
    doSuggest(query) {
        return this.suggestService.suggest(query, this.suggestService.getSuggestSystemTagURLbase())
    }
    tagSelected() {
        if (this.newTag && this.newTag.tagName) {
            this.serverCallService
                .makePut(`rest/learningObject/${this.learningObject.id}/system_tags`, JSON.stringify(this.newTag.tagName))
                .then(({ data }) => {
                    this.addTagSuccess(data.learningObject)
                    this.showSystemTagDialog(data.tagTypeName)
                    this.$scope.$emit(
                        this.isMaterial(learningObject)
                            ? 'tags:updateMaterial'
                            : 'tags:updatePortfolio',
                        learningObject
                    )
                })
            this.newTag.tagName = null
        }
    }
    limitTextLength() {
        if (this.newTag && this.newTag.tagName && this.newTag.tagName.length > 60)
            this.newTag.tagName = this.newTag.tagName.slice(0, -1)
    }
    showSystemTagDialog(tagType) {
        if (tagType)
            this.$mdDialog.show(
                this.$mdDialog
                    .alert()
                    .clickOutsideToClose(true)
                    .title(this.$translate.instant('SYSTEM_TAG_DIALOG_TITLE'))
                    .textContent(this.$translate.instant('SYSTEM_TAG_DIALOG_CONTENT'))
                    .ok('Ok')
                    .closeTo(`#${tagType}-close`)
            )
    }
    setNewTags() {
        const setNew = (tags) => Array.isArray(tags) && tags.forEach(t => {
            t.isNew = !this.$rootScope.learningObjectChanges ? false : !!this.$rootScope.learningObjectChanges
                .find(c => {
                    if (c.taxon && c.taxon.translationKey) {
                        return this.$translate.instant(c.taxon.translationKey).toLowerCase() === t.tag;
                    }
                    if (c.resourceType && c.resourceType.name) {
                        return this.$translate.instant(c.resourceType.name).toLowerCase() === t.tag
                    }
                    if (c.targetGroup && c.targetGroup.name) {
                        return this.$translate.instant(c.targetGroup.name).toLowerCase() === t.tag
                    }
                    return false;
                });
        });
        setNew(this.$scope.tags)
        setNew(this.allTags)
    }
}
controller.$inject = [
    '$scope',
    '$translate',
    '$rootScope',
    '$mdDialog',
    '$timeout',
    'authenticatedUserService',
    'storageService',
    'suggestService',
    'tagsService',
    'toastService',
    'serverCallService'
]
component('dopTags', {
    bindings: {
        learningObject: '='
    },
    templateUrl: 'directives/tags/tags.html',
    controller
})
/**
 * @see: https://github.com/angular/material/issues/8692
 */
directive('dopTagCustomChip', {
    link($scope, $elem) {
        $scope.$watch('$chip.isNew', (isNew) =>
            $elem.parent().parent().toggleClass('new', isNew)
        )
    }
})
}
