'use strict'

{
    class controller extends Controller {
        constructor(...args) {
            super(...args)

            this.showQrCodeDialog = () => {

                if(this.location.contains('kogumik')){
                    gTagCaptureEventWithLabel('show', 'teaching portfolio', 'QR code')
                } else {
                    gTagCaptureEventWithLabel('show', 'teaching material', 'QR code')
                }

                this.$mdDialog.show({
                    templateUrl: '/directives/qrDialog/qrDialog.html',
                    controller: 'qrDialogController',
                    fullscreen: true,
                    locals: {
                        url: this.location
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
            location: '='
        },
        controller,
        template: `
            <div ng-click="$ctrl.showQrCodeDialog()">
                <md-tooltip md-direction="top"><span data-translate="TOOLTIP_SHOW_QR_CODE"></span></md-tooltip>
                <md-button
                  class="md-icon-button qr-code">
                </md-button>
              </div>
        `
    })
}
