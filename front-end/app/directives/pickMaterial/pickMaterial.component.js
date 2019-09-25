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
    isPublic(){
        return this.learningObject.visibility === 'PUBLIC'
    }
    isPrivate(){
        return this.learningObject.visibility === 'PRIVATE'
    }
    hasUnAcceptableLicenses() {
        return this.learningObject.licenseType ? this.learningObject.licenseType.name === 'allRightsReserved' ||
            this.learningObject.licenseType.name === 'CCBYND' ||
            this.learningObject.licenseType.id === 'CCBYNCND' : !this.learningObject.licenseType;
    }
}
controller.$inject = [
    '$rootScope',
    'authenticatedUserService'
]
component('dopPickMaterial', {
    bindings: {
        learningObject: '<',
        disable: '<'
    },
    templateUrl: 'directives/pickMaterial/pickMaterial.html',
    controller
})
}
