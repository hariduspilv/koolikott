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

        validatePin(user, pin, email) {
            return this.serverCallService
                .makePost('rest/userEmail/validate', {user: user, pin: pin, email: email})
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
