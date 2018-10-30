'use strict'

{
    class controller extends Controller {
        constructor(...args) {
            super(...args)
            this.getUserManuals();
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
    }

    controller.$inject = [
        '$scope',
        'serverCallService',
    ]
    angular.module('koolikottApp').controller('userManualsController', controller)
}
