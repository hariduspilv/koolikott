'use strict';
{
    class controller extends Controller {
        constructor(...args) {
            super(...args)
        }

        saveEmail(userEmail, user) {
            return this.serverCallService
                .makePost('rest/userEmail', {email: userEmail, user: user})
        }

        checkDuplicateEmail(userEmail, userState) {
            return this.serverCallService
                .makePost('rest/userEmail/check', {email: userEmail, userStatus: userState})
        }

        hasEmailOnLogin(userState) {
            return this.serverCallService.makePost('rest/userEmail/getEmailOnLogin', {userStatus: userState})
        }

        checkDuplicateEmailForProfile(userEmail) {
            return this.serverCallService
                .makePost('rest/userEmail/checkForProfile', {email: userEmail})
        }

        validatePin(user, pin, email, location) {
            if (location === '/profile') {
                return this.serverCallService
                    .makePost('rest/userEmail/validateFromPortfolio', {user: user, pin: pin, email: email})
            } else {
                return this.serverCallService
                    .makePost('rest/userEmail/validate', {user: user, pin: pin, email: email})
            }
        }

        getEmail() {
            return this.serverCallService.makeGet('rest/userEmail')
        }

        userHasEmail(userId) {
            return this.serverCallService
                .makeGet('rest/userEmail/getEmail/?userId=' + userId)
        }
    }

    controller.$inject = [
        'serverCallService',
    ]
    factory('userEmailService', controller)
}
