'use strict'

{
class controller extends Controller {
    navigateTo() {
        const { id } = this.learningObject

        if (isMaterial(this.learningObject.type)) {
            if (this.learningObject.type === '.Material'){
                this.storageService.setMaterial(this.learningObject)
                this.$location.path('/material').search({ id })
            } else {
                this.materialService.getMaterialById(this.learningObject.id).then(result=>{
                    this.storageService.setMaterial(result);
                    this.$location.path('/material').search({ id })
                })
            }
        }
        if (isPortfolio(this.learningObject.type)) {
            if (this.learningObject.type === '.Portfolio') {
                this.storageService.setPortfolio(this.learningObject)
                this.$location.path('/portfolio').search({id})
            } else {
                this.porfolioService.getPortfolioById(this.learningObject.id).then(result=>{
                    this.storageService.setPortfolio(result);
                    this.$location.path('/portfolio').search({id})
                })
            }
        }
    }
}
controller.$inject = [
    '$location',
    'translationService',
    'storageService',
    'materialService',
    'portfolioService'
]
component('dopCardXs', {
    bindings: {
        learningObject: '='
    },
    templateUrl: 'directives/card/cardXS/cardXS.html',
    controller
})
}
