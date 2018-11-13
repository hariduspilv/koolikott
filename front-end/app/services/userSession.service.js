'use strict';
{

    class controller extends Controller {
        constructor(...args) {
            super(...args)
            this.updateTimer = undefined;
        }

        handleUserSession() {
            if (this.authenticatedUserService.isAuthenticated()) {
                this.getSessionTime()
                    .then(({data: session}) => {
                        if (session.continueSession && session.minRemaining > 0 && session.minRemaining <= 10) {
                            if (!this.$rootScope.sessionDialogIsOpen) {
                                this.$rootScope.sessionDialogIsOpen = true
                                this.$mdDialog.show({
                                    templateUrl: 'views/sessionDialog/sessionDialog.html',
                                    controller: 'sessionDialogController',
                                    clickOutsideToClose: false,
                                    escapeToClose: false,
                                }).then((data) => {
                                    if (data){
                                        this.updateSession(data);
                                    }
                                })
                            }
                        } else if (session.minRemaining === 0){
                            this.terminateSession()
                                .then(()=>{
                                    this.authenticatedUserService.removeAuthenticatedUser()
                                    this.$mdDialog.show({
                                        templateUrl: 'views/sessionDialog/sessionExpiredDialog.html',
                                        controller: 'sessionExpiredDialogController',
                                        clickOutsideToClose: true,
                                        escapeToClose: true,
                                    })
                                    this.$rootScope.$broadcast("sessionDialog:stop");
                                    this.stopTimer();
                                })
                        }
                    })
            }
        }

        terminateSession() {
            return this.serverCallService.makePost('rest/user/terminateSession');
        }

        updateSession(session) {
            return this.serverCallService.makePost('rest/user/updateSession', session);
        }

        getSessionTime() {
            return this.serverCallService.makeGet('rest/user/sessionTime');
        }

        startTimer() {
            this.updateTimer = this.$interval(this.handleUserSession.bind(this), 3000) //1 min
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
