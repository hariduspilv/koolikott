'use strict';
{
    class controller extends Controller {
        constructor(...args) {
            super(...args)
        }

        getUserManuals() {
            return this.serverCallService
                .makeGet('rest/admin/userManuals')
        }

        addUserManual(newUserManual) {
            return this.serverCallService
                .makePost('rest/admin/userManuals', newUserManual)
        }

        deleteUserManual(userManual) {
            return this.serverCallService
                .makePost('rest/admin/userManuals/delete', userManual)
        }

    }

    controller.$inject = [
        'serverCallService',
    ]
    factory('userManualsAdminService', controller)
}
