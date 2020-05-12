'use strict';
{

    class controller extends Controller {
        handleUserSession() {
            if (this.authenticatedUserService.isAuthenticated()) {
                this.getSessionAlert()
                    .then(({data: alertTime}) => {
                        this.getSessionTime()
                            .then(({data: session}) => {
                                if (session.continueSession && session.minRemaining > 0 && session.minRemaining <= parseInt(alertTime)) {
                                    if (!this.sessionDialogIsOpen) {
                                        this.sessionDialogIsOpen = true
                                        this.$mdDialog.show({
                                            templateUrl: '/views/sessionDialog/sessionDialog.html',
                                            controller: 'sessionDialogController',
                                            clickOutsideToClose: false,
                                            escapeToClose: false,
                                        }).then((data) => {
                                            this.updateSession(data);
                                            this.sessionDialogIsOpen = false;
                                        })
                                    }
                                } else if (session.minRemaining <= 0) {
                                    this.sessionDialogIsOpen = false;
                                    this.$rootScope.$broadcast("sessionService:terminateSession");
                                    this.$mdDialog.show({
                                        templateUrl: '/views/sessionDialog/sessionExpiredDialog.html',
                                        controller: 'sessionExpiredDialogController',
                                        clickOutsideToClose: true,
                                        escapeToClose: true,
                                    })
                                }
                            })
                    })
            }
        }

        updateSession(session) {
            return this.serverCallService.makePost('rest/user/updateSession', session);
        }

        getSessionTime() {
            return this.serverCallService.makeGet('rest/user/sessionTime');
        }

        getSessionAlert() {
            return this.serverCallService.makeGet('rest/user/sessionAlertTime');
        }

        startTimer() {
            this.updateTimer = this.$interval(this.handleUserSession.bind(this), 10e3) //10 sec
        }

        stopTimer() {
            this.$interval.cancel(this.updateTimer)
        }
    }

    controller.$inject = [
        '$http',
        '$location',
        '$interval',
        'serverCallService',
        'authenticatedUserService',
        '$mdDialog',
        '$rootScope'
    ]
    factory('userSessionService', controller)
}
