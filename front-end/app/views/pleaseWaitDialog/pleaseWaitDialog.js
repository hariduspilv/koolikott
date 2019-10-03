'use strict';

{
    class controller extends Controller {
        constructor(...args) {
            super(...args)

            this.$scope.$watch(() => {
                if (this.$rootScope.canCloseWaitingDialog) {
                    this.$rootScope.canCloseWaitingDialog = false
                    this.$mdDialog.hide()
                }
            });
        }

        close() {
            this.$mdDialog.hide()
        }
    }

    controller.$inject = ['$scope', '$rootScope', '$mdDialog']

    angular.module('koolikottApp').controller('pleaseWaitDialogController', controller)
}
