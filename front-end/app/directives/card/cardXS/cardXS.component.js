'use strict'

{
class controller extends Controller {
    navigateTo() {

        if (this.isMaterial(this.learningObject)) {
            if (type === '.Material') this.storageService.setMaterial(this.learningObject)
            else {
                this.materialService.getMaterialById(this.learningObject.id).then(learningObject => {
                    this.modifyRootScope(learningObject)
                    this.storageService.setMaterial(learningObject)
                })
            }
        } else if (this.isPortfolio(this.learningObject)) {
            if (type === '.Portfolio') this.storageService.setPortfolio(this.learningObject)
            else {
                this.portfolioService.getPortfolioById(this.learningObject.id).then(learningObject => {
                    this.modifyRootScope(learningObject)
                    this.storageService.setPortfolio(learningObject)
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
        learningObject: '<'
    },
    templateUrl: '/directives/card/cardXS/cardXS.html',
    controller
})
}
