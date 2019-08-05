'use strict'

{
    class controller extends Controller {
        constructor(...args) {
            super(...args)
            $('body').materialScrollTop({ offset: 300 })
        }

        showScrollTopHigher() {
            return this.authenticatedUserService.isAuthenticated() &&
               !this.$location.path().contains('/kogumik/muuda')
        }
    }

    controller.$inject = [
        '$location',
        'authenticatedUserService',
    ]

    component('dopScrollTop', {
        controller,
        template: `
            <button data-ng-class="{ 'higher': $ctrl.showScrollTopHigher() }" class="material-scrolltop" type="button">
                <md-tooltip md-direction="left"><span data-translate="BACK_TO_THE_TOP">Back to the top</span></md-tooltip>
            </button>
        `
    })
}
