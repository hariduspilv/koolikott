'use strict'

{
    class controller extends Controller {
        constructor(...args) {
            super(...args)
            this.getUserManuals();
            $('body').materialScrollTop({ offset: 300 })
        }

        getUserManuals() {
            this.serverCallService
                .makeGet('rest/admin/userManuals')
                .then(({data}) => {
                    if (data) {
                        this.$scope.videos = data
                    }
                });
        }
        isLoggedIn() {
            return this.authenticatedUserService.isAuthenticated()
        }
    }

    controller.$inject = [
        '$scope',
        'serverCallService',
        'authenticatedUserService',
    ]
    angular.module('koolikottApp').controller('userManualsController', controller)
}
