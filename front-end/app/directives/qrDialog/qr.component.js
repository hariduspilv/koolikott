'use strict'

{
    class controller extends Controller {
        constructor(...args) {
            super(...args)

            this.showQrCodeDialog = () => {

                debugger
                console.log(this.location)
                console.log(this.title)
                console.log(this.slug)
                this.$mdDialog.show({
                    templateUrl: 'views/qrDialog/qrDialog.html',
                    controller: 'qrDialogController',
                    fullscreen: true,
                    locals: {
                        location,
                        slug: this.slug
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
            slug: '@'
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
