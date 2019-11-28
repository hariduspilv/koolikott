'use strict';
{
    class controller extends Controller {
        constructor(...args) {
            super(...args)
        }

        saveLicence(licence) {
            return this.serverCallService.makePost('rest/licenses', licence)
        }

        getLicenses() {
            return this.serverCallService.makeGet('rest/licenses')
        }

        deleteLicence(licence) {
            return this.serverCallService.makePost('rest/licenses/delete', licence)
        }
    }

    controller.$inject = [
        'serverCallService',
        'authenticatedUserService',
    ]
    factory('licensesService', controller)
}
