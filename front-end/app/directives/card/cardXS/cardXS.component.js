'use strict'

{
class controller extends Controller {
    navigateTo() {
        const { id } = this.learningObject
        
        if (this.isMaterial(this.learningObject)) {
            this.storageService.setMaterial(this.learningObject)
            this.$location.path('/material').search({ id })
        }
        if (isPortfolio(this.learningObject.type)) {
            this.storageService.setPortfolio(this.learningObject)
            this.$location.path('/portfolio').search({ id })
        }
    }
    getCorrectLanguageTitle() {
        if (this.learningObject)
            return this.getUserDefinedLanguageString(
                this.learningObject.titles,
                this.translationService.getLanguage(),
                this.learningObject.language
            )
    }
}
controller.$inject = ['$location', 'translationService', 'storageService']

angular.module('koolikottApp').component('dopCardXs', {
    bindings: {
        learningObject: '='
    },
    templateUrl: 'directives/card/cardXS/cardXS.html',
    controller
})
}
