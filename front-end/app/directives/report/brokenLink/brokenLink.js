'use strict'

{
class controller extends Controller {
    $onInit() {
        const isAdmin = this.authenticatedUserService.isAdmin()

        this.$scope.$watch('material', (newValue) => {
            if (newValue && this.isMaterial(newValue)) {
                const url = isAdmin
                    ? 'rest/material/isBroken?id='
                    : 'rest/material/hasSetBroken?materialId='

                this.serverCallService
                    .makeGet(url + newValue.id)
                    .then(({ data }) => {
                        this.$scope.isBrokenReportedByUser = data
                        
                        if (isAdmin)
                            this.$scope.isBroken = data
                    })
            }
        }, false)

        this.$scope.showConfirmationDialog = () => {
            const confirm = this.$mdDialog
                .confirm()
                .title(this.$translate.instant('REPORT_BROKEN_LINK_TITLE'))
                .content(this.$translate.instant('REPORT_BROKEN_LINK_CONTENT'))
                .ok(this.$translate.instant('BUTTON_REPORT'))
                .cancel(this.$translate.instant('BUTTON_CANCEL'))

            this.$mdDialog.show(confirm).then(() =>
                this.serverCallService
                    .makePost('rest/material/setBroken', this.$scope.material)
                    .then(() => {
                        this.$scope.isBrokenReportedByUser = true
                        this.toastService.show('TOAST_NOTIFICATION_SENT_TO_ADMIN')
                    })
            )
        }
    }
}
controller.$inject = [
    '$scope',
    '$mdDialog',
    '$translate',
    'authenticatedUserService',
    'serverCallService',
    'toastService'
]
/**
 * Declaring this as a directive since we need to use it as an attribute on
 * <md-menu-item> (component usage is restricted to element tagname only).
 */
directive('dopReportBrokenLink', {
    templateUrl: 'directives/report/brokenLink/brokenLink.html',
    controller
})
}
