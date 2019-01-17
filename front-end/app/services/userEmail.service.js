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

        checkDuplicateEmail(userEmail) {
            return this.serverCallService
                .makePost('rest/userEmail/check', {email: userEmail})
        }

        validatePin(user, pin) {
            return this.serverCallService
                .makePost('rest/userEmail/validate', {user: user, pin: pin})
        }

    }

    controller.$inject = [
        'serverCallService',
    ]
    factory('userEmailService', controller)
}
