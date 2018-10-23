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
                })
            }

            this.$scope.cancel = () => {
                this.$mdDialog.hide()
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
    '$translate',
    '$mdDialog',
    'authenticationService',
    'translationService',
    'authenticatedUserService',
    'userLocatorService',
    '$location'
]

angular.module('koolikottApp').controller('locationDialogController', controller)
}
