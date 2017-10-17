'use strict'

{
const COMMENTS_PER_PAGE = 5

class controller extends Controller {
    $onChanges({ learningObject } = {}) {
        if (learningObject && learningObject.currentValue)
            this.$scope.comments = learningObject.currentValue.comments
    }
    $onInit() {
        this.visibleCommentsCount = COMMENTS_PER_PAGE
        this.$scope.newComment = { text: '' }

        // Commentbox hotfix
        setTimeout(() =>
            angular
                .element(document.getElementById('comment-list'))
                .find('textarea')
                .css('height', '112px'),
            1000
        )
    }
    isAuthorized() {
        return this.authenticatedUserService.isAuthenticated()
            && !this.authenticatedUserService.isRestricted()
    }
    getLoadMoreCommentsLabel() {
        let commentsLeft = this.getLeftCommentsCount()

        return commentsLeft <= COMMENTS_PER_PAGE
            ? `(${commentsLeft})`
            : `(${COMMENTS_PER_PAGE}/${commentsLeft})`
    }
    showMoreComments() {
        let commentsLeft = this.getLeftCommentsCount()

        commentsLeft - COMMENTS_PER_PAGE >= 0
            ? this.visibleCommentsCount += COMMENTS_PER_PAGE
            : this.visibleCommentsCount = this.learningObject.comments.length
    }
    showMoreCommentsButton() {
        return this.getLeftCommentsCount() > 0
    }
    addComment() {
        if (this.$scope.newComment.text && typeof this.submitClick === 'function')
            this.submitClick({
                newComment: this.$scope.newComment, 
                learningObject: this.learningObject
            })
    }
    reportComment(comment) {
        const { serverCallService, learningObject } = this

        return this.$mdDialog
            .show({
                    controller: ['$scope', '$mdDialog', 'title', function ($scope, $mdDialog, title){
                    $scope.title = title
                    $scope.data = {
                        learningObject,
                        reportingText: ''
                    }
                    $scope.cancel = () => {
                        $scope.data.reportingText = ''
                        $mdDialog.cancel()
                    }
                    $scope.sendReport = () => $mdDialog.hide($scope)
                    $scope.loading = true
                    $scope.submitEnabled = true

                    serverCallService
                        .makeGet('rest/learningMaterialMetadata/commentReportingReasons')
                        .then(({ data: reasons }) => {
                            if (Array.isArray(reasons)) {
                                $scope.hideReasons = reasons.length === 1
                                $scope.reasons = reasons.map(key => ({
                                    key,
                                    checked: reasons.length === 1
                                }))
                            }
                            $scope.loading = false
                        })

                    $scope.characters = { used: 0, remaining: 255 }
                    $scope.$watch('data.reportingText', (newValue) => {
                        const used = newValue ? newValue.length : 0
                        $scope.characters = { used, remaining: 255 - used }
                    })
                }],
                templateUrl: 'directives/report/improper/improper.dialog.html',
                clickOutsideToClose: true,
                locals: {
                    title: this.$translate.instant('COMMENT_TOOLTIP_REPORT_AS_IMPROPER')
                }
            })
            .then(({ data, reasons }) => {
                Object.assign(data, {
                    reportingReasons: reasons.reduce((reportingReasons, r) =>
                        r.checked
                            ? reportingReasons.concat({ reason: r.key })
                            : reportingReasons,
                        []
                    )
                })
                return this.serverCallService
                    .makePut('rest/impropers', data)
                    .then(({ status, data }) => {
                        if (status === 200) {
                            this.$rootScope.learningObjectImproper = true
                            this.$rootScope.$broadcast('errorMessage:reported')
                            this.toastService.show('TOAST_NOTIFICATION_SENT_TO_ADMIN')
                        }
                    })
            })
    }
    getLeftCommentsCount() {
        return Array.isArray(this.learningObject.comments)
            ? this.learningObject.comments.length - this.visibleCommentsCount
            : 0
    }
}
controller.$inject = [
    '$scope',
    '$rootScope',
    '$mdDialog',
    '$translate',
    'authenticatedUserService',
    'serverCallService',
    'toastService'
]

angular.module('koolikottApp').component('dopCommentsCard', {
    bindings: {
        learningObject: '<',
        isOpen: '<',
        submitClick: '&'
    },
    templateUrl: 'directives/commentsCard/commentsCard.html',
    controller
})
}
