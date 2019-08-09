'use strict'

{
class controller extends Controller {
    $onInit() {
        this.init()
        this.$scope.disablePick = this.disablePick
    }
    $doCheck() {
        if (this.learningObject !== this.$scope.learningObject) this.init()
        if (this.disablePick !== this.$scope.disablePick) this.$scope.disablePick = this.disablePick
    }
    init() {
        if (this.isMaterial(this.learningObject)) {
            this.$scope.learningObjectType = 'material'
            this.$scope.materialType = this.iconService.getMaterialIcon(this.learningObject.resourceTypes)
            this.$scope.coverClass = this.learningObject.picture
                ? 'card-cover imaged'
                : `card-cover material ${this.$scope.materialType}`
        } else if (this.isPortfolio(this.learningObject)) {
            this.$scope.learningObjectType = 'portfolio'
            this.$scope.coverClass = this.learningObject.picture
                ? 'card-cover imaged'
                : `card-cover portfolio`
        }
        this.$scope.learningObject = this.learningObject
        this.$scope.learningObjectTitle = this.replaceSpaces(this.getCorrectLanguageTitle(this.learningObject))
    }
}
controller.$inject = ['$scope', 'iconService', 'authenticatedUserService', 'translationService']
component('dopCardMedia', {
    bindings: {
        learningObject: '=',
        disablePick: '<',
        showPrivacy: '<'
    },
    templateUrl: 'directives/card/cardMedia/cardMedia.html',
    controller
})
}
