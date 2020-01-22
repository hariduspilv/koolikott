'use strict';
{
    class controller extends Controller {
        constructor(...args) {
            super(...args)
        }

        saveTerm(term) {
            return this.serverCallService.makePost('rest/terms', term)
        }

        getTerms() {
            return this.serverCallService.makeGet('rest/terms?type=USAGE')
        }

        getGdprTerms() {
            return this.serverCallService.makeGet('rest/terms?type=GDPR')
        }
    }

    controller.$inject = [
        'serverCallService',
        'authenticatedUserService',
    ]
    factory('termsService', controller)
}
