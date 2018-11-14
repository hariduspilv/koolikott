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

        emptyTitle() {
            $translate('SESSION_IS_EXPIRING_MESSAGE').then((value) => {
                this.$scope.message = value.replace('${minRemaining}', `x`);
            })
        }

        sessionTime() {
            this.userSessionService.getSessionTime()
                .then(({data: session}) => {
                    if (session.minRemaining) {
                        this.$translate('SESSION_IS_EXPIRING_MESSAGE').then((value) => {
                            this.$scope.message = value.replace('${minRemaining}', `${session.minRemaining}`);
                        })
                    } else {
                        this.emptyTitle();
                    }
                }, () => {
                    this.emptyTitle();
                })
        }
    }

    controller.$inject = [
        '$scope',
        '$mdDialog',
        '$location',
        '$interval',
        '$translate',
        '$rootScope',
        'userSessionService',
    ]

    angular.module('koolikottApp').controller('sessionDialogController', controller)
}
