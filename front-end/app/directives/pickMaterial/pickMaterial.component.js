'use strict'

{
class controller extends Controller {
    isVisible() {
        return (
            this.learningObject &&
            this.isMaterial(this.learningObject) &&
            this.authenticatedUserService.isAuthenticated() &&
            !this.disable
        )
    }
    pickMaterial($event) {
        $event.preventDefault()
        $event.stopPropagation()

        if (this.$rootScope.selectedMaterials) {
            const idx = this.$rootScope.selectedMaterials.indexOf(this.learningObject)

            if (idx < 0) {
                this.$rootScope.selectedMaterials.push(this.learningObject)
                this.learningObject.selected = true
            } else {
                this.$rootScope.selectedMaterials.splice(idx, 1)
                this.learningObject.selected = false
            }
        } else {
            this.$rootScope.selectedMaterials = []
            this.$rootScope.selectedMaterials.push(this.learningObject)
            this.learningObject.selected = true
        }

        this.$rootScope.$broadcast('detailedSearch:close')
    }
}
controller.$inject = ['$rootScope', 'authenticatedUserService']

angular.module('koolikottApp').component('dopPickMaterial', {
    bindings: {
        learningObject: '<',
        disable: '<'
    },
    templateUrl: 'directives/pickMaterial/pickMaterial.html',
    controller
})
}
