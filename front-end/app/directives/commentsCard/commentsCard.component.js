'use strict'

{
const COMMENTS_PER_PAGE = 5

class controller extends Controller {
    $onInit() {
        this.visibleCommentsCount = COMMENTS_PER_PAGE

        this.isAuthenticated = () =>
            this.authenticatedUserService.isAuthenticated()
        
        this.isAuthorized = () =>
            this.authenticatedUserService.isAuthenticated() &&
            !this.authenticatedUserService.isRestricted()

        this.getLoadMoreCommentsLabel = () => {
            let commentsLeft = this.getLeftCommentsCount()

            return commentsLeft <= COMMENTS_PER_PAGE
                ? `(${commentsLeft})`
                : `(${COMMENTS_PER_PAGE}/${commentsLeft})`
        }
        this.showMoreComments = () => {
            let commentsLeft = this.getLeftCommentsCount()

            commentsLeft - COMMENTS_PER_PAGE >= 0
                ? this.visibleCommentsCount += COMMENTS_PER_PAGE
                : this.visibleCommentsCount = this.comments.length
        }
        this.showMoreCommentsButton = () => this.getLeftCommentsCount() > 0
        this.addComment = this.submitClick

        // Commentbox hotfix
        setTimeout(() =>
            angular
                .element(document.getElementById('comment-list'))
                .find('textarea')
                .css('height', '112px'),
            1000
        )
    }
    getLeftCommentsCount() {
        return Array.isArray(this.comments)
            ? this.comments.length - this.visibleCommentsCount
            : 0
    }
}
controller.$inject = ['authenticatedUserService']

angular.module('koolikottApp').component('dopCommentsCard', {
    bindings: {
        comments: '<',
        comment: '<',
        isOpen: '<',
        submitClick: '&'
    },
    templateUrl: 'directives/commentsCard/commentsCard.html',
    controller
})
}
