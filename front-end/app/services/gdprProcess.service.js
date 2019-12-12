'use strict';
{
    class controller extends Controller {
        constructor(...args) {
            super(...args)
        }

        saveTerm(term) {
            return this.serverCallService.makePost('rest/licenses', term)
        }

        getTerms() {
            return this.serverCallService.makeGet('rest/licenses')
        }

        deleteTerm(term) {
            return this.serverCallService.makePost('rest/licenses/delete', term)
        }
    }

    controller.$inject = [
        'serverCallService',
    ]
    factory('gdprProcessService', controller)
}
