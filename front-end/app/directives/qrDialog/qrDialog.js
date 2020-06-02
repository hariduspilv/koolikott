'use strict'

{
    class controller extends Controller {
        constructor(...args) {
            super(...args)

            this.checkResolution()

            this.$scope.location = this.locals.url

            this.$scope.cancel = () => {
                this.$mdDialog.hide();
            };

            $(window).resize(() => {
                this.checkResolution()
            })
        }

        checkResolution() {
            var width = window.innerWidth
            this.$scope.size = width >= 1920 ? 700 :
                (width < 1920 && width > 1280) ? 650 :
                    (width < 1280 && width > 960) ? 600 :
                        (width < 960 && width > 768) ? 500 :
                            (width < 768) ? 250 : 200
        }
    }

    controller.$inject = [
        '$scope',
        'dialogService',
        '$window',
        '$mdDialog',
        '$location',
        'locals',
        '$interval',
    ]
    angular.module('koolikottApp').controller('qrDialogController', controller)
}
