'use strict'

{
class controller extends Controller {
        constructor(...args) {
            super(...args)

            this.$scope.agree = () => {
                this.userLocatorService.getUserLocation().then((response) => {
                    if (response.data)
                        this.$location.url(response.data)

                    this.$mdDialog.hide(true)
                    this.$rootScope.locationDialogIsOpen = false
                })
            }

            this.$scope.cancel = () => {
                this.$mdDialog.hide()
                this.$rootScope.locationDialogIsOpen = false
                this.userLocatorService.startTimer()
            }
        }
    $onDestroy() {
        if (typeof this.unsubscribeRouteChangeSuccess === 'function')
            this.unsubscribeRouteChangeSuccess()
    }
}

controller.$inject = [
    '$scope',
    '$rootScope',
    '$mdDialog',
    'userLocatorService',
    '$location'
]

angular.module('koolikottApp').controller('locationDialogController', controller)
}
