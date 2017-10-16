'use strict'

{
class controller extends Controller {
    $onChanges({ learningObject }) {
        if (learningObject && learningObject.newValue)
            this.$scope.data = Object.assign({}, this.$scope.data, {
                learningObject: learningObject.newValue
            })
    }
    $onInit() {
        this.$scope.data = {
            reportingReasons: [],
            reportingText: '',
            learningObject: this.$scope.learningObject
        }
        this.$scope.loading = true
        this.$scope.reasons = []
        this.$scope.showConfirmationDialog = this.showConfirmationDialog.bind(this)
        this.getReasons()
    }
    showConfirmationDialog() {
        this.$mdDialog
            .show({
                controller: ['$scope', '$mdDialog', 'data', 'reasons', 'loading', 'title',
                    function ($scope, $mdDialog, data, reasons, loading, title) {
                    $scope.title = title
                    $scope.data = data
                    $scope.reasons = reasons
                    $scope.loading = loading
                    $scope.cancel = () => $mdDialog.cancel()
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
                clickOutsideToClose:true,
                locals: {
                    title: this.$translate.instant('REPORT_IMPROPER_TITLE'),
                    data: this.$scope.data,
                    reasons: this.$scope.reasons,
                    loading: this.$scope.loading
                }
            })
            .then(this.sendReport.bind(this))
    }
    getReasons() {
        this.serverCallService
            .makeGet('rest/learningMaterialMetadata/learningObjectReportingReasons')
            .then(({ data: reasons }) => {
                if (Array.isArray(reasons))
                    this.$scope.reasons = reasons.map(key => ({
                        key,
                        checked: false
                    }))

                this.$scope.loading = false
            })
    }
    sendReport() {
        const data = Object.assign({}, this.$scope.data)

        this.serverCallService
            .makePut('rest/impropers', data)
            .then(({ status }) => {
                if (status == 200) {
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
    'serverCallService'
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
