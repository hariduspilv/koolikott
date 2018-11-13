'use strict'

{
    class controller extends Controller {
        constructor(...args) {
            super(...args)

            this.sessionTime();
            const sessionTimeInterval = this.$interval(this.sessionTime.bind(this), 1000);

            this.$scope.agree = () => {
                this.$mdDialog.hide({continueSession: true})
                this.$rootScope.sessionDialogIsOpen = false
            }

            this.$scope.cancel = () => {
                this.$mdDialog.hide({continueSession: false})
                this.$rootScope.sessionDialogIsOpen = false
            }

            this.$scope.$on('$destroy', () => {
                this.$interval.cancel(sessionTimeInterval)
            })

            this.$scope.$on('sessionDialog:stop', () => {
                this.$mdDialog.hide()
                this.$rootScope.sessionDialogIsOpen = false
            })
        }

        sessionTime() {
            this.userSessionService.getSessionTime()
                .then(({data: session}) => {
                    this.$scope.session = session;
                })
        }
    }

    controller.$inject = [
        '$scope',
        '$rootScope',
        '$mdDialog',
        'userSessionService',
        '$location',
        '$interval',
    ]

    angular.module('koolikottApp').controller('sessionDialogController', controller)
}
