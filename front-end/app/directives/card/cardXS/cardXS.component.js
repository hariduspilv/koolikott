'use strict'

{
class controller extends Controller {
    navigateTo() {
        const { id } = this.learningObject

        if (this.isMaterial(this.learningObject)) {
            if (this.learningObject.type === '.Material'){
                this.storageService.setMaterial(this.learningObject)
            }
            this.$location.path('/material').search({ id })
        }
        if (isPortfolio(this.learningObject.type)) {
            if (this.learningObject.type === '.ReducedPortfolio'){
                this.storageService.setPortfolio(this.learningObject)
            }
            this.$location.path('/portfolio').search({ id })
        }
    }
}
controller.$inject = [
    '$location',
    'translationService',
    'storageService'
]
component('dopCardXs', {
    bindings: {
        learningObject: '='
    },
    templateUrl: 'directives/card/cardXS/cardXS.html',
    controller
})
}
