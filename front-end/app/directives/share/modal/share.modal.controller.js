'use strict'

{
class controller extends Controller {
    constructor(...args) {
        super(...args)

        if(this.isMaterial(this.locals.learningObject)) {
            this.title = this.$translate.instant('THIS_MATERIAL_IS_PRIVATE')
            this.context = this.$translate.instant('SHARE_PRIVATE_MATERIAL')
            this.ariaLabel = this.$translate.instant('THIS_MATERIAL_IS_PRIVATE')
        } else if (this.isPortfolio(this.locals.learningObject)) {
            this.title = this.$translate.instant('THIS_IS_PRIVATE')
            this.context = this.$translate.instant('SHARE_PRIVATE_PORTFOLIO')
            this.ariaLabel = this.$translate.instant('THIS_IS_PRIVATE')
        }
    }

    back() {
        this.$mdDialog.cancel()
    }
}
controller.$inject = [
    '$mdDialog',
    '$translate',
    'locals'
]
angular.module('koolikottApp').controller('shareModalController', controller)
}
