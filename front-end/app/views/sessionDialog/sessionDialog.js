'use strict'

{
    class controller extends Controller {
        constructor(...args) {
            super(...args)

            this.sessionTime();
            const sessionTimeInterval = this.$interval(this.sessionTime.bind(this), 10e3); //10sec

            this.$scope.agree = () => {
                this.$mdDialog.hide({continueSession: true})
            }

            this.$scope.cancel = () => {
                this.$mdDialog.hide({continueSession: false})
            }

            this.$scope.$on('$destroy', () => {
                this.$interval.cancel(sessionTimeInterval)
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
