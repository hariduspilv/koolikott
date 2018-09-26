'use strict'

{
    class controller extends Controller {
        constructor(...args) {
            super(...args)

            debugger

            const location = this.locals.location.origin + this.locals.location.pathname + this.locals.location.search
            this.$scope.location = (this.locals.slug) ? location + '#' + this.locals.slug : location

            this.$scope.cancel = () => {
                this.$mdDialog.hide();
            };

            if (window.innerWidth < 1920)
                this.$scope.size = 400
            else
                this.$scope.size = 800
        }

    }

    controller.$inject = [
        '$scope',
        '$translate',
        'serverCallService',
        'sortService',
        'dialogService',
        '$window',
        '$mdDialog',
        '$location',
        'locals'
    ]
    angular.module('koolikottApp').controller('qrDialogController', controller)
}
