'use strict'

{
class controller extends Controller {
    $onInit() {
        this.selected = false
        this.domains = []
        this.subjects = []

        this.domainSubjectList = this.taxonGroupingService.getDomainSubjectList(this.learningObject.taxons)
        this.targetGroups = this.targetGroupService.getConcentratedLabelByTargetGroups(this.learningObject.targetGroups || [])

        this.$scope.learningObject = this.learningObject
    }
    $doCheck() {
        if (this.learningObject !== this.$scope.learningObject)
            this.$scope.learningObject = this.learningObject
    }
    navigateTo() {
        const { id } = this.learningObject

        if (this.isMaterial(this.learningObject)) {
            if (this.learningObject.type === '.Material'){
                this.storageService.setMaterial(this.learningObject)
            }
            this.$location.path('/material').search({ id })
        } else if (this.isPortfolio(this.learningObject)) {
            if (this.learningObject.type === '.Portfolio'){
                this.storageService.setPortfolio(this.learningObject)
            }
            this.$location.path('/portfolio').search({ id })
        }
    }
    formatName(name) {
        if (name)
            return this.formatNameToInitials(name.trim())
    }
    formatSurname(surname) {
        if (surname)
            return this.formatSurnameToInitialsButLast(surname.trim())
    }
    isAuthenticated() {
        const authenticated =
            this.authenticatedUserService.getUser() &&
            !this.authenticatedUserService.isRestricted() &&
            !this.$rootScope.isEditPortfolioPage

        if (!authenticated && this.isMaterial(this.learningObject.type))
            this.learningObject.selected = false

        return authenticated
    }
    hoverEnter() {
        this.cardHover = true
    }
    hoverLeave() {
        this.cardHover = false
    }
}
controller.$inject = [
    '$scope',
    '$location',
    '$rootScope',
    'translationService',
    'authenticatedUserService',
    'targetGroupService',
    'storageService',
    'taxonGroupingService'
]
component('dopCardSm', {
    bindings: {
        learningObject: '=',
        chapter: '=?'
    },
    templateUrl: 'directives/card/cardSM/cardSM.html',
    controller
})
}
