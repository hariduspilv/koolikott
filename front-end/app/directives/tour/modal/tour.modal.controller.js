'use strict'

{
class controller extends Controller {
    constructor(...args) {
        super(...args)
        angular.element('body').addClass('tour-modal-is-showing')

        this.cancel = () => {
            this.$rootScope.$broadcast('tour:start:cancelled')
            angular.element('body').removeClass('tour-modal-is-showing')
            this.$mdDialog.hide()
        }
        this.showTour = () => {
            this.$rootScope.$broadcast('tour:start')
            angular.element('body').removeClass('tour-modal-is-showing')
            this.$mdDialog.hide()
        }
    }
}
controller.$inject = ['$rootScope', '$mdDialog']
angular.module('koolikottApp').controller('tourModalController', controller)
}
