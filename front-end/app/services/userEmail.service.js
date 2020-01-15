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

        checkDuplicateEmail(userEmail, userToken) {
            return this.serverCallService
                .makePost('rest/userEmail/check', {userEmail: userEmail, token:  userToken})
        }

        hasEmailOnLogin(userToken) {
            return this.serverCallService.makePost('rest/userEmail/getEmailOnLogin', {userEmail: null, token: userToken})
        }

        checkDuplicateEmailForProfile(userEmail) {
            return this.serverCallService.makePost('rest/userEmail/checkForProfile', {email: userEmail})
        }

        validatePin(user, pin, email, location) {
            if (location === '/profiil') {
                return this.serverCallService
                    .makePost('rest/userEmail/validateFromProfile', {user: user, pin: pin, email: email})
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
