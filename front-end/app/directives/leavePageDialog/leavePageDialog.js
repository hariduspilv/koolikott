'use strict'

{
    class controller extends Controller {
        constructor(...args) {
            super(...args)

            this.$scope.agree = () => {
                this.$mdDialog.hide();
            }

            this.$scope.cancel = () => {
                this.$mdDialog.cancel();
            }
        }
    }

    controller.$inject = [
        '$scope',
        '$mdDialog',
    ]

    angular.module('koolikottApp').controller('leavePageDialogController', controller)
}
