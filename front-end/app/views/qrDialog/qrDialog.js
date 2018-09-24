'use strict'

{
    class controller extends Controller {
        constructor(...args) {
            super(...args)

            debugger;
            const location = this.$location.absUrl()
            if (this.locals.slug) {
                if (!location.substring(0, location.indexOf('#')))
                    this.$scope.location = location  + '#' + this.locals.slug
                else
                    this.$scope.location = location.substring(0, location.indexOf('#')) + '#' + this.locals.slug
            } else {
                if (location.substring(0, location.indexOf('#')))
                    this.$scope.location = location.substring(0, location.indexOf('#'))
                else
                    this.$scope.location = location
            }

            this.$scope.cancel = () => {
                this.$mdDialog.hide();
            };
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
