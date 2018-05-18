'use strict'

{
    class controller extends Controller {
        constructor(...args) {
            super(...args)

            this.unsubscribeRouteChangeSuccess = this.$rootScope.$on('$routeChangeSuccess', () => this.$mdDialog.hide())
            this.$scope.$watch(
                () => this.authenticatedUserService.isAuthenticated(),
                (newValue, oldValue) => newValue === true && this.$mdDialog.hide(),
                false
            );

            this.$scope.agree = () => {
                this.$mdDialog.hide(true)
            }

            this.$scope.cancel = () => {
                this.$mdDialog.hide()
            }
        }
        $onDestroy() {
            if (typeof this.unsubscribeRouteChangeSuccess === 'function')
                this.unsubscribeRouteChangeSuccess()
        }
    }
    controller.$inject = [
        '$scope',
        '$rootScope',
        '$translate',
        '$mdDialog',
        'authenticationService',
        'translationService',
        'authenticatedUserService'
    ]

    angular.module('koolikottApp').controller('agreementDialogController', controller)
}
