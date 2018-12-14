'use strict';
{
    class controller extends Controller {
        constructor(...args) {
            super(...args)
        }

        saveFaq(faq) {
            return this.serverCallService.makePost('rest/faq', faq)
        }

        getFaq() {
            return this.serverCallService.makeGet('rest/faq')
        }

        deleteFaq(faq) {
            return this.serverCallService.makePost('rest/faq/delete', faq)
        }

    }

    controller.$inject = [
        'serverCallService',
        'authenticatedUserService',
    ]
    factory('faqService', controller)
}
