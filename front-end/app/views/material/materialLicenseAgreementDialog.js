'use strict';

{
    class controller extends Controller {
        constructor(...args) {
            super(...args)

            this.$scope.agree = () => {
                this.$mdDialog.hide({accept: true})
            }

            this.$scope.cancel = () => {
                this.$mdDialog.hide()
            }
        }
    }

    controller.$inject = ['$scope', '$mdDialog']

    angular.module('koolikottApp').controller('materialLicenseAgreementController', controller)
}
