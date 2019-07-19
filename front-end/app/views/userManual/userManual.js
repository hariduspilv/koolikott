'use strict'

{
    class controller extends Controller {
        constructor(...args) {
            super(...args)
            this.getUserManuals();
            this.$translate('USER_MANUALS_HEADING').then((translation) => this.$rootScope.tabTitle = translation);
        }

        getUserManuals() {
            this.userManualsAdminService.getUserManuals()
                .then(({data}) => {
                    if (data) {
                        this.$scope.videos = data
                    }
                });
        }
    }

    controller.$inject = [
        '$scope',
        '$rootScope',
        '$translate',
        'serverCallService',
        'authenticatedUserService',
        'userManualsAdminService'
    ]
    angular.module('koolikottApp').controller('userManualsController', controller)
}
