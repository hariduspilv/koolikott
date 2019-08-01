'use strict'

{
    class controller extends Controller {
        constructor(...args) {
            super(...args)
            this.$rootScope.tabTitle = '404';
        }
    }

    controller.$inject = [
        '$rootScope',
    ]
    angular.module('koolikottApp').controller('404Controller', controller)
}
