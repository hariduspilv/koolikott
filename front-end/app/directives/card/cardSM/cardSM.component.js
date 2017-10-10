'use strict'

{
class controller extends Controller {
    $onInit() {
        this.selected = false
        this.isEditPortfolioPage = this.$rootScope.isEditPortfolioPage
        this.isEditPortfolioMode = this.$rootScope.isEditPortfolioMode
        this.domains = []
        this.subjects = []

        this.domainSubjectList = this.taxonGroupingService.getDomainSubjectList(this.learningObject.taxons)
        this.targetGroups = this.targetGroupService.getConcentratedLabelByTargetGroups(this.learningObject.targetGroups)
    }
    navigateTo() {
        const { id } = this.learningObject

        if (this.isMaterial(this.learningObject)) {
            this.storageService.setMaterial(this.learningObject)
            this.$location.path('/material').search({ id })
        }
        if (this.isPortfolio(this.learningObject)) {
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
    '$location',
    '$rootScope',
    'translationService',
    'authenticatedUserService',
    'targetGroupService',
    'storageService',
    'taxonGroupingService'
]

angular.module('koolikottApp').component('dopCardSm', {
    bindings: {
        learningObject: '=',
        chapter: '=?'
    },
    templateUrl: 'directives/card/cardSM/cardSM.html',
    controller
})
}
