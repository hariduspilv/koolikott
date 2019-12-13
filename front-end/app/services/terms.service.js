'use strict';
{
    class controller extends Controller {
        constructor(...args) {
            super(...args)
        }

        saveTerm(term) {
            term.type = 'TERM'
            return this.serverCallService.makePost('rest/terms', term)
        }

        getTerm() {
            return this.serverCallService.makeGet('rest/terms/terms')
        }

        deleteTerm(term) {
            return this.serverCallService.makePost('rest/terms/delete', term)
        }
    }

    controller.$inject = [
        'serverCallService',
        'authenticatedUserService',
    ]
    factory('termsService', controller)
}
