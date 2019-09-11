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

            this.$scope.isMaterial = () => {
                if (this.$location.path().startsWith('/oppematerjal/')){
                    return true
                }
            }
        }
    }

    controller.$inject = ['$scope', '$mdDialog', '$location']

    angular.module('koolikottApp').controller('learningObjectLicenseAgreementController', controller)
}
