'use strict';

{
    class controller extends Controller {
        constructor(...args) {
            super(...args)

            this.$scope.agree = () => {
                this.$mdDialog.hide({agreed: true})
            }

            this.$scope.cancel = () => {
                this.$mdDialog.hide({disagreed: true})
            }

            this.$scope.logout = () => {
                this.$mdDialog.hide({logout: true})
            }
        }
    }

    controller.$inject = ['$scope', '$mdDialog']

    angular.module('koolikottApp').controller('migrationAgreementController', controller)
}
