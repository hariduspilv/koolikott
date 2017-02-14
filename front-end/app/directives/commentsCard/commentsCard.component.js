'use strict'

angular.module('koolikottApp')
.component('dopCommentsCard', {
    bindings: {
        comments: '<',
        comment: '<',
        isOpen: '<',
        submitClick: "&"
    },
    templateUrl: 'directives/commentsCard/commentsCard.html',
    controller: dopCommentsCardController
});

dopCommentsCardController.$inject = ['translationService', 'serverCallService', 'authenticatedUserService'];

function dopCommentsCardController (translationService, serverCallService, authenticatedUserService) {
    let vm = this;

    const COMMENTS_PER_PAGE = 5;
    vm.visibleCommentsCount = COMMENTS_PER_PAGE;

    vm.isAuthorized = () => authenticatedUserService.isAuthenticated() && !authenticatedUserService.isRestricted();
    vm.isAuthenticated = () => authenticatedUserService.isAuthenticated();

    vm.getLoadMoreCommentsLabel = () => {
        let commentsLeft = getLeftCommentsCount();

        if (commentsLeft <= COMMENTS_PER_PAGE) {
            return '(' + commentsLeft + ')';
        }

        return '(' + COMMENTS_PER_PAGE + '/' + commentsLeft + ')';
    };

    vm.showMoreComments = () => {
        let commentsLeft = getLeftCommentsCount();

        if (commentsLeft - COMMENTS_PER_PAGE >= 0) {
            vm.visibleCommentsCount += COMMENTS_PER_PAGE;
        } else {
            vm.visibleCommentsCount = vm.comments.length;
        }
    };

    vm.showMoreCommentsButton = () => getLeftCommentsCount() > 0;

    function getLeftCommentsCount() {
        if (vm.comments) {
            return vm.comments.length - vm.visibleCommentsCount;
        }
    }

    vm.addComment = () => {
        vm.submitClick();
    }

    //Commentbox hotfix
    setTimeout(commentHotfix, 1000);
    function commentHotfix() {
        angular.element(document.getElementById('comment-list')).find('textarea').css('height', '112px');
    }
}
