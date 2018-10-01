'use strict'

{
    class controller extends Controller {
        constructor(...args) {
            super(...args)

            this.showQrCodeDialog = () => {

                this.$mdDialog.show({
                    templateUrl: 'directives/qrDialog/qrDialog.html',
                    controller: 'qrDialogController',
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

    component('dopQr', {
        bindings: {
            location: '@',
            slug: '@',
            title: '@'
        },
        controller,
        template: `
            <div ng-click="$ctrl.showQrCodeDialog()">
                <md-tooltip><span data-translate="TOOLTIP_SHOW_QR_CODE"></span></md-tooltip>
                <md-button
                  class="md-icon-button qr-code">
                </md-button>
              </div>
        `
    })
}
