'use strict';

{
    class controller extends Controller {
        constructor(...args) {
            super(...args)

        }

        close() {
            this.$mdDialog.hide()
        }
    }

    controller.$inject = ['$scope', '$mdDialog']

    angular.module('koolikottApp').controller('pleaseWaitDialogController', controller)
}
