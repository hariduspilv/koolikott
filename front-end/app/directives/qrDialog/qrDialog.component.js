'use strict'

{
    class controller extends Controller {
        constructor(...args) {
            super(...args)

            debugger
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

            if (window.innerWidth < 1920)
                this.$scope.size = 400
            else
                this.$scope.size = 800
        }

    }
    controller.$inject = [
        '$rootScope',
        '$location',
        '$mdDialog',
        '$timeout',
        '$translate',
        'authenticatedUserService',
        'Socialshare',
        'FB_APP_ID',
        'GOOGLE_SHARE_CLIENT_ID'
    ]

    component('dopQr', {
        bindings: {
            title: '<',
            object: '<'
        },
        templateUrl: 'directives/share/share.html',
        controller
    })
}
