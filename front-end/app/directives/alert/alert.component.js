'use strict'

{
class controller extends Controller {
    $onInit() {
        this.$scope.$watch(
            () => this.alertService.getAlert(),
            ({ message }) => {
                if (message) {
                    this.toastService.show(message)
                    this.alertService.clearMessage()

                    $timeout(() => this.alert = null, 5000)
                }
            },
            true
        )
    }
}
controller.$inject = ['$scope', '$timeout', 'alertService', 'toastService']

angular.module('koolikottApp').component('dopAlert', {
    controller
})
}
