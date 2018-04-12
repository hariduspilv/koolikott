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
                this.materialService.getMaterialById(this.learningObject.id).then(learningObject=>{
                    this.modifyRootScope(learningObject);
                    this.storageService.setMaterial(learningObject);
                    this.$location.path('/material').search({ id })
                })
            }
        } else if (isPortfolio(this.learningObject.type)) {
            if (this.learningObject.type === '.Portfolio') {
                this.storageService.setPortfolio(this.learningObject)
                this.$location.path('/portfolio').search({id})
            } else {
                this.portfolioService.getPortfolioById(this.learningObject.id).then(learningObject=>{
                    this.modifyRootScope(learningObject);
                    this.storageService.setPortfolio(learningObject);
                    this.$location.path('/portfolio').search({id})
                })
            }
        }
    }

    modifyRootScope(learningObject) {
        this.$rootScope.learningObjectPrivate = learningObject && ['PRIVATE'].includes(learningObject.visibility)
        this.$rootScope.learningObjectImproper = learningObject && learningObject.improper > 0
        this.$rootScope.learningObjectDeleted = learningObject && learningObject.deleted === true
        this.$rootScope.learningObjectChanged = learningObject && learningObject.changed > 0
        this.$rootScope.learningObjectUnreviewed = learningObject && !!learningObject.unReviewed
    }
}
    controller.$inject = [
    '$location',
    '$rootScope',
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
