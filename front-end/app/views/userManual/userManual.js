'use strict'

{
    class controller extends Controller {
        constructor(...args) {
            super(...args)
            this.getUserManuals();
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
        'serverCallService',
        'authenticatedUserService',
        'userManualsAdminService'
    ]
    angular.module('koolikottApp').controller('userManualsController', controller)
}
