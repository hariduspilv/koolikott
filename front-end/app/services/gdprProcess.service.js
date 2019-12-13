'use strict';
{
    class controller extends Controller {
        constructor(...args) {
            super(...args)
        }

        saveTerm(term) {
            term.type = 'GDPR'
            return this.serverCallService.makePost('rest/terms', term)
        }

        getTerms() {
            return this.serverCallService.makeGet('rest/terms/gdpr')
        }

        deleteTerm(term) {
            return this.serverCallService.makePost('rest/terms/delete', term)
        }
    }

    controller.$inject = [
        'serverCallService',
    ]
    factory('gdprProcessService', controller)
}
