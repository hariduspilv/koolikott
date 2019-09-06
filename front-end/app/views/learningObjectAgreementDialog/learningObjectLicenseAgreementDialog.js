'use strict';

{
    class controller extends Controller {
        constructor(...args) {
            super(...args)

            const path = this.$location.path()

            this.$scope.agree = () => {
                this.$mdDialog.hide({accept: true})
            }

            this.$scope.cancel = () => {
                this.$mdDialog.hide()
            }

            this.$scope.isMaterial = () => {
                if(path.startsWith('/oppematerjal/')){
                    return true
                }
            }
        }
    }

    controller.$inject = ['$scope', '$mdDialog', '$location']

    angular.module('koolikottApp').controller('learningObjectLicenseAgreementController', controller)
}
