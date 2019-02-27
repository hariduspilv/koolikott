'use strict'

{
    class controller extends Controller {
        constructor(...args) {
            super(...args)

            this.showLoEmailCodeDialog = () => {
                this.$mdDialog.show({
                    templateUrl: 'directives/sendEmail/sendEmail.html',
                    controller: 'sendEmailDialogController',
                    controllerAs: '$ctrl',
                    fullscreen: true,
                    locals: {
                        location,
                        slug: this.slug,
                        title: this.title
                    },
                    clickOutsideToClose: true,
                })
            };
        }
    }

    controller.$inject = [
        '$rootScope',
        '$location',
        '$mdDialog',
    ]

    component('dopSendEmail', {
        bindings: {
            location: '@',
            slug: '@',
            title: '@'
        },
        controller,
        template: `
            <div ng-click="$ctrl.showLoEmailCodeDialog()">
                <md-tooltip><span data-translate="TOOLTIP_SHOW_QR_CODE"></span></md-tooltip>
                <md-button
                  class="md-icon-button">
                  <md-icon md-menu-align-target>email</md-icon>
                  <md-tooltip><span data-translate="FULL_SCREEN_ON"></span></md-tooltip>
                </md-button>
              </div>
        `
    })
}
