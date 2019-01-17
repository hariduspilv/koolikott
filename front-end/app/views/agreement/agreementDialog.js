'use strict'

{
    class controller extends Controller {
        constructor(...args) {
            super(...args)

            this.$scope.agreementDialogEmail = ''
            this.$scope.validEmail = VALID_EMAIL

            this.unsubscribeRouteChangeSuccess = this.$rootScope.$on('$routeChangeSuccess', () => this.$mdDialog.hide())
            this.$scope.$watch(
                () => this.authenticatedUserService.isAuthenticated(),
                (newValue, oldValue) => newValue === true && this.$mdDialog.hide(),
                false
            );

            this.$scope.agree = () => {
                this.$scope.gdprDialogContent.email.$setValidity('validationError', true)
                this.$rootScope.email = this.$scope.agreementDialogEmail
                this.userEmailService.checkDuplicateEmail(this.$scope.agreementDialogEmail)
                    .then(response => {
                        if (response.status = 200)
                            this.$mdDialog.hide(true)
                    }).catch(() => {
                    this.$scope.gdprDialogContent.email.$setValidity('validationError', false)
                })
            }

            this.$scope.cancel = () => {
                this.$mdDialog.hide()
            }
        }

        $onDestroy() {
            if (typeof this.unsubscribeRouteChangeSuccess === 'function')
                this.unsubscribeRouteChangeSuccess()
        }

        isSubmitDisabled() {
            const {email, pattern} = this.$scope.gdprDialogContent.email.$error
            return !this.$scope.agreementDialogEmail || email || pattern
        }
    }

    controller.$inject = [
        '$scope',
        '$rootScope',
        '$mdDialog',
        'authenticatedUserService',
        'userEmailService'
    ]

    angular.module('koolikottApp').controller('agreementDialogController', controller)
}
