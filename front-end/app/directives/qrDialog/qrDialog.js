'use strict'

{
    class controller extends Controller {
        constructor(...args) {
            super(...args)

            this.checkResolution()

            const location = this.locals.location.origin + this.locals.location.pathname + this.locals.location.search
            this.$scope.location = (this.locals.slug) ? location + '#' + this.locals.slug : location

            this.$scope.cancel = () => {
                this.$mdDialog.hide();
            };

            this.$scope.$on('$destroy', () =>
                this.$interval.cancel(this.resolutionCheckInterval)
            )
        }

        checkResolution() {
            this.resolutionCheckInterval = this.$interval(() => {
                let width = window.innerWidth
                this.$scope.size = width >= 1920 ? 700 :
                    (width < 1920 && width > 1280) ? 650 :
                        (width < 1280 && width > 960) ? 600 :
                            (width < 960 && width > 768) ? 500 :
                                (width < 768) ? 250 : 200

                this.$scope.qrStyle = width >= 1920 ? {'padding': '9px 75px 75px'} :
                    (width < 1920 && width > 1280) ? {'padding': '6px 70px 70px'} :
                        (width < 1280 && width > 960) ? {'padding': '0px 64px 64px'} :
                            (width < 960 && width > 768) ? {'padding': '0px 54px 54px'} :
                                (width < 768) ? {'padding': '0px 27px 27px'} : {'padding': '0px 27px 27px'}

            }, 20)
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
