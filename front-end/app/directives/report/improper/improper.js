'use strict'

{
const SHOW_GENERAL_REPORT_MODAL_HASH = 'dialog-report-general'

class controller extends Controller {
    $onInit() {
        this.$scope.data = {
            reportingReasons: [],
            reportingText: '',
            learningObject: this.$scope.learningObject
        }
        this.$scope.$watch('learningObject', (newValue) => {
            if (newValue)
                this.$scope.data = Object.assign({}, this.$scope.data, {
                    learningObject: newValue
                })
        })
        this.$scope.reasons = this.getReasons()
        this.$scope.showDialog = (evt) => {
            this.authenticatedUserService.isAuthenticated()
                ? this.showReportDialog(evt)
                : this.showLoginDialog(evt)
        }

        // auto-launch the report dialog if hash is found in location URL
        if (
            window.location.hash.includes(SHOW_GENERAL_REPORT_MODAL_HASH) &&
            this.authenticatedUserService.isAuthenticated()
        )
            this.$timeout(() =>
                this.showReportDialog()
            )
        
        // remove hash from location URL upon navigating away
        const unSubscribe = this.$rootScope.$on('$routeChangeSuccess', () => {
            unSubscribe()
            this.removeHash()
        })
    }
    showReportDialog(targetEvent) {
        this.$mdDialog
            .show({
                controller: ['$scope', '$mdDialog', 'data', 'reasons', 'title',
                    function ($scope, $mdDialog, data, reasons, title) {
                    $scope.title = title
                    $scope.data = data
                    $scope.reasons = reasons.reasons
                    $scope.loading = reasons.loading
                    $scope.cancel = () => {
                        reasons.reasons.forEach(reason => reason.checked = false)
                        data.reportingReasons = []
                        data.reportingText = ''
                        $mdDialog.cancel()
                    }
                    $scope.sendReport = () => {
                        if (data.reportingReasons.length)
                            return $mdDialog.hide()
                        
                        $scope.errors = { reasonRequired: true }
                        $scope.submitEnabled = false
                    }
                    $scope.$watch('reasons', (newValue) => {
                        if (Array.isArray(newValue)) {
                            let anyChecked = false

                            $scope.data.reportingReasons = newValue.reduce((reportingReasons, r) =>
                                r.checked
                                    ? (anyChecked = true) && reportingReasons.concat({ reason: r.key })
                                    : reportingReasons,
                                []
                            )

                            if (anyChecked)
                                $scope.errors = null
                            
                            $scope.submitEnabled = anyChecked
                        } else
                            $scope.submitEnabled = false
                    }, true)

                    $scope.characters = { used: 0, remaining: 255 }
                    $scope.$watch('data.reportingText', (newValue) => {
                        const used = newValue ? newValue.length : 0
                        $scope.characters = { used, remaining: 255 - used }
                    })
                }],
                templateUrl: 'directives/report/improper/improper.dialog.html',
                clickOutsideToClose: true,
                escapeToClose: true,
                targetEvent,
                locals: {
                    title: this.$translate.instant('REPORT_IMPROPER_TITLE'),
                    data: this.$scope.data,
                    reasons: this.$scope.reasons
                }
            })
            .then(this.sendReport.bind(this))
    }
    showLoginDialog(targetEvent) {
        this.addHash()
        this.$mdDialog.show({
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
    }
    addHash() {
        window.history.replaceState(null, null,
            ('' + window.location).split('#')[0] + '#' + SHOW_GENERAL_REPORT_MODAL_HASH
        )
    }
    removeHash() {
        window.history.replaceState(null, null,
            ('' + window.location).split('#')[0]
        )
    }
    getReasons() {
        return this.serverCallService
            .makeGet('rest/learningMaterialMetadata/learningObjectReportingReasons')
            .then(({ data: reasons }) => ({
                loading: false,
                reasons: (reasons || []).map(key => ({
                    key,
                    checked: false
                }))
            }))
    }
    sendReport() {
        const data = Object.assign({}, this.$scope.data)

        console.log('data:', data)

        this.serverCallService
            .makePut('rest/impropers', data)
            .then(({ status }) => {
                if (status == 200) {
                    this.$scope.reasons.then(reasons =>
                        reasons.forEach(reason => reason.checked = false)
                    )
                    this.$scope.data.reportingReasons = []
                    this.$scope.data.reportingText = ''
                    this.$rootScope.learningObjectImproper = true
                    this.$rootScope.$broadcast('errorMessage:reported')
                    this.toastService.show('TOAST_NOTIFICATION_SENT_TO_ADMIN')
                }
            })
    }
}
controller.$inject = [
    '$scope',
    '$rootScope',
    '$mdDialog',
    '$translate',
    '$timeout',
    'toastService',
    'serverCallService',
    'authenticatedUserService'
]

/**
 * Declaring this as a directive since we need to use it as an attribute on
 * <md-menu-item> (component usage is restricted to element tagname only).
 */
angular.module('koolikottApp').directive('dopReportImproper', () => ({
    scope: {
        learningObject: '<'
    },
    templateUrl: 'directives/report/improper/improper.html',
    controller
}))
}
