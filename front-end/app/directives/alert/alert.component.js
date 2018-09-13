'use strict'

{
class controller extends Controller {
    $onInit() {
        this.$scope.$watch(
            () => this.alertService.getAlert(),
            ({ message, timeout = 3000 }) => {
                if (message) {
                    this.toastService.show(message, timeout)
                    this.alertService.clearMessage()

                    this.$timeout(() => this.alert = null, timeout)

                }
            },
            true
        )
    }
}
controller.$inject = ['$scope', '$timeout', 'alertService', 'toastService']
component('dopAlert', { controller })
}
