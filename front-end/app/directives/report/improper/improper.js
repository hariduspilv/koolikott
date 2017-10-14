'use strict'

{
class controller extends Controller {
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
                controller($scope, $mdDialog, data, reasons, loading) {
                    $scope.data = data
                    $scope.reasons = reasons
                    $scope.loading = loading
                    $scope.cancel = () => $mdDialog.cancel()
                    $scope.sendReport = () => {
                        data.reportingReasons.length
                            ? $mdDialog.hide()
                            : $scope.errors = { reasonRequired: true }
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
                        }
                    }, true)
                },
                templateUrl: 'directives/report/improper/improper.dialog.html',
                clickOutsideToClose:true,
                locals: {
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

        return console.log('data:', data)

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
