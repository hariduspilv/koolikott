'use strict';
{
    class controller extends Controller {
        constructor(...args) {
            super(...args)
        }

        saveTerm(term) {
            return this.serverCallService.makePost('rest/gdprProcessing', term)
        }

        getTerms() {
            return this.serverCallService.makeGet('rest/gdprProcessing')
        }

        deleteTerm(term) {
            return this.serverCallService.makePost('rest/gdprProcessing/delete', term)
        }
    }

    controller.$inject = [
        'serverCallService',
    ]
    factory('gdprProcessService', controller)
}
