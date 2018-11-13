'use strict'

{
    class controller extends Controller {
        constructor(...args) {
            super(...args)

            this.$scope.agree = () => {
                this.$mdDialog.hide({continueSession: true})
                this.$rootScope.sessionDialogIsOpen = false
            }
        }
    }

    controller.$inject = [
        '$scope',
        '$rootScope',
        '$mdDialog',
        'userSessionService',
        '$location',
        '$interval',
    ]

    angular.module('koolikottApp').controller('sessionExpiredDialogController', controller)
}
