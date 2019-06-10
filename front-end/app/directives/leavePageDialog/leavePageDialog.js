'use strict'

{
    class controller extends Controller {
        constructor(...args) {
            super(...args)

            this.$scope.agree = () => {
                this.$mdDialog.hide();
                history.back()
            }

            this.$scope.cancel = () => {
                this.$mdDialog.hide();
            }
        }
    }

    controller.$inject = [
        '$scope',
        '$mdDialog',
        '$location',
        '$translate',
        '$rootScope',
    ]

    angular.module('koolikottApp').controller('leavePageDialogController', controller)
}
