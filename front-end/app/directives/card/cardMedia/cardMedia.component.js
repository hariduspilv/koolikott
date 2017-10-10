'use strict'

{
class controller extends Controller {
    $onInit() {
        if (this.isMaterial(this.learningObject)) {
            this.learningObjectType = 'material'
            this.materialType = this.iconService.getMaterialIcon(
                this.learningObject.resourceTypes
            )
        } else if (this.isPortfolio(this.learningObject))
            this.learningObjectType = 'portfolio'
    }
}
controller.$inject = ['$rootScope', 'iconService']

angular.module('koolikottApp').component('dopCardMedia', {
    bindings: {
        learningObject: '=',
        isAuthenticated: '<',
        disablePick: '<'
    },
    templateUrl: 'directives/card/cardMedia/cardMedia.html',
    controller
})
}
