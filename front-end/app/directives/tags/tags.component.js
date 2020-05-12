'use strict'

{
const SHOW_TAG_REPORT_MODAL_HASH = 'dialog-report-tag'

class controller extends Controller {
    $onInit() {
        this.newTag = {}
        this.showMoreTags = false
        this.deleteQueryIsRunning = false
        this.$rootScope.$on('materialEditModalClosed', this.getTagUpVotes.bind(this))
        this.getTagUpVotes()

        this.$scope.isAutoSaving = 'MANUAL';
        this.$scope.isPortfolioLog = false;

        this.$rootScope.$on('portfolio:autoSave', this.getHistoryLogType.bind(this))

        this.$rootScope.$on('tags:resetTags', this.getTagUpVotes.bind(this))
        this.$rootScope.$on('tags:focusInput', () => {
            let numAttempts = 0
            this.focusInterval = setInterval(() => {
                numAttempts++
                const input = this.$element[0].querySelector('input')
                if (input) {
                    input.focus()
                    clearInterval(this.focusInterval)
                }
                if (numAttempts >= 20)
                    clearInterval(this.focusInterval)
            }, 500)
        })

        // auto-launch the report dialog upon login or page load if hash is found in location URL
        this.$timeout(() =>
            window.location.hash.includes(SHOW_TAG_REPORT_MODAL_HASH)
                ? this.onLoginSuccess()
                : this.unsubscribeLoginSuccess = this.$rootScope.$on('login:success', this.onLoginSuccess.bind(this))
        )

        this._previousTags = this.learningObject.tags || []
    }
    $onDestroy() {
        if (typeof this.unsubscribeLoginSuccess === 'function')
            this.unsubscribeLoginSuccess()
        if (typeof this.unsubscribeTagsAdded === 'function')
            this.unsubscribeTagsAdded()
    }
    $doCheck() {
        if (!_.isEqual(this.learningObject.tags, this._previousTags)) {
            this.getTagUpVotes()
            this._previousTags = this.learningObject.tags
        }
    }

    getHistoryLogType(){
        this.$scope.isAutoSaving = 'AUTO';

    }

    isSmallScreen() {
        return window.innerWidth < 700
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
        const {id, type} = this.learningObject || {}

        let portfolioLog = (type === '.PortfolioLog');
        if (!id) {
            this.$scope.upvotes = undefined
            this.allUpvotes = undefined
            return
        }

        this.$scope.isPortfolioLog = portfolioLog;

        this.serverCallService
            .makeGet('rest/tagUpVotes/report', {learningObject: id, portfolioLog: portfolioLog})
            .then(({ data: tags }) => {
                let sorted = this.sortTagsByUpVoteCount(tags)

                if (sorted.length > 10) {
                    this.allUpvotes = sorted
                    this.$scope.upvotes = this.allUpvotes.slice(0, 10)
                    this.showMoreTags = true
                } else {
                    this.allUpvotes = sorted
                    this.$scope.upvotes = sorted
                    this.showMoreTags = false
                }
            })
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
                this.$scope.upvotes = this.sortTagsByUpVoteCount(this.$scope.upvotes)
            })
    }
    isAllowedToAdd() {
        return this.authenticatedUserService.isModeratorOrAdminOrCreator(this.learningObject) && !this.$scope.isPortfolioLog
    }
    isAllowedToRemove() {
        return this.authenticatedUserService.isModeratorOrAdminOrCreator(this.learningObject) && !this.$scope.isPortfolioLog;
    }
    isDeleteQueryRunning(){
        return this.deleteQueryIsRunning
    }
    removeUpVote(upVoteForm) {
        this.removedTag = upVoteForm
        this.tagsService
            .removeUpVote(upVoteForm.tagUpVote)
            .then(() => {
                if (this.removedTag) {
                    this.removedTag.tagUpVote = null
                    this.removedTag.upVoteCount--
                    this.$scope.upvotes = this.sortTagsByUpVoteCount(this.$scope.upvotes)
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
        if (this.learningObject && this.learningObject.tags) {
            this.learningObject.tags.forEach((tag, idx) => {
                if (tag === removedTag)
                    this.learningObject.tags.splice(idx, 1)
            });
            this.deleteQueryIsRunning = true;
            if (this.isMaterial(this.learningObject)) {
                this.serverCallService
                    .makePost('rest/material/update', this.learningObject)
                    .then(({ data: material }) => {
                        this.addTagSuccess(material)
                        this.deleteQueryIsRunning = false
                    })
            } else if (this.isPortfolio(this.learningObject)) {
                this.updateChaptersStateFromEditors()
                this.learningObject.saveType = this.$scope.isAutoSaving;
                this.serverCallService
                    .makePost(`rest/portfolio/update`,this.learningObject)
                    .then(({ data: portfolio }) => {
                        this.addTagSuccess(portfolio)
                        this.deleteQueryIsRunning = false
                    })
                    .catch(() => this.toastService.show('PORTFOLIO_SAVE_FAILED',15000));
            }
        }
    }

    addTag() {
        if (this.newTag && this.newTag.tagName && this.learningObject && this.learningObject.id) {
            this.learningObject.saveType = this.$scope.isAutoSaving;
            this.serverCallService
                .makePut(`rest/learningObject/${this.learningObject.id}/tags`, JSON.stringify(this.newTag.tagName))
                .then(({data}) => this.addTagSuccess(data))
                .catch(() => this.toastService.show('PORTFOLIO_SAVE_FAILED',15000));
            this.newTag.tagName = null
        }
    }

    addTagSuccess(learningObject) {
        if (this.learningObject) {
            const {tags, taxons, targetGroups, resourceTypes, changed} = learningObject
            Object.assign(this.learningObject, {tags, taxons, targetGroups, resourceTypes, changed})

            if (!this.learningObject.source && learningObject.uploadedFile)
                this.learningObject.source = learningObject.uploadedFile.url

            if (this.isPortfolio(this.learningObject)) {
                this.storageService.setPortfolio(this.learningObject);
                this.$rootScope.$broadcast('tags:updatePortfolio');
            } else if (this.isMaterial(this.learningObject)) {
                this.storageService.setMaterial(this.learningObject)
            }
            this.getTagUpVotes()
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
            templateUrl: '/views/loginDialog/loginDialog.html',
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
        this.$scope.upvotes = this.allUpvotes
        this.showMoreTags = false
    }
    showLess() {
        this.$scope.upvotes = this.allUpvotes.slice(0, 10)
        this.showMoreTags = true
    }

    limitTextLength() {
        if (this.newTag && this.newTag.tagName && this.newTag.tagName.length > 60)
            this.newTag.tagName = this.newTag.tagName.slice(0, -1)
    }
}
controller.$inject = [
    '$scope',
    '$translate',
    '$rootScope',
    '$mdDialog',
    '$timeout',
    '$element',
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
    templateUrl: '/directives/tags/tags.html',
    controller
})
/**
 * @see: https://github.com/angular/material/issues/8692
 */
}
