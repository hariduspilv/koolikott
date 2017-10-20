'use strict'

{
class controller extends Controller {
    $onInit() {
        this.deletePortfolio = this.deletePortfolio.bind(this)
        this.getTaxonObject = this.getTaxonObject.bind(this)
        this.restorePortfolio = this.restorePortfolio.bind(this)
        this.setNotImproper = this.setNotImproper.bind(this)

        this.eventService.subscribe(this.$scope, 'taxonService:mapInitialized', this.getTaxonObject)
        this.eventService.subscribe(this.$scope, 'portfolio:reloadTaxonObject', this.getTaxonObject)
        this.eventService.notify('portfolio:reloadTaxonObject')

        this.$scope.$on('restore:portfolio', this.restorePortfolio)
        this.$scope.$on('delete:portfolio', this.deletePortfolio)
        this.$scope.$on('setNotImproper:portfolio', this.setNotImproper)
        this.$scope.$on('markReviewed:portfolio', this.markReviewed.bind(this))
        // Main purpose of this watch is to handle situations
        // where portfolio is undefined at the moment of init()
        this.$scope.$watch('portfolio', (newValue, oldValue) => {
            if (newValue !== oldValue)
                this.eventService.notify('portfolio:reloadTaxonObject')
        })
        this.$scope.$watch('portfolio.taxon.id', (newValue, oldValue) => {
            if (newValue !== oldValue)
                $scope.portfolioSubject = this.taxonService.getSubject(this.portfolio.taxon)
        }, true)
        
        this.$scope.portfolio = this.portfolio
        this.$scope.commentsOpen = false
        this.$scope.taxonObject = {}
        this.$scope.pageUrl = this.$location.absUrl()
        this.$scope.isViewPortforlioPage = this.$rootScope.isViewPortforlioPage
        this.$scope.isEditPortfolioMode = this.$rootScope.isEditPortfolioMode

        this.$scope.canEdit = this.canEdit.bind(this)
        this.$scope.isAdmin = this.isAdmin.bind(this)
        this.$scope.isAdminOrModerator = this.isAdminOrModerator.bind(this)
        this.$scope.isLoggedIn = this.isLoggedIn.bind(this)
        this.$scope.isRestricted = this.isRestricted.bind(this)
        this.$scope.editPortfolio = this.editPortfolio.bind(this)
        this.$scope.addComment = this.addComment.bind(this)
        this.$scope.toggleCommentSection = this.toggleCommentSection.bind(this)
        this.$scope.getPortfolioEducationalContexts = this.getPortfolioEducationalContexts.bind(this)
        this.$scope.showEditMetadataDialog = this.showEditMetadataDialog.bind(this)
        this.$scope.confirmPortfolioDeletion = this.confirmPortfolioDeletion.bind(this)
        this.$scope.getTargetGroups = this.getTargetGroups.bind(this)
        this.$scope.isAdminButtonsShowing = this.isAdminButtonsShowing.bind(this)
        this.$scope.setRecommendation = this.setRecommendation.bind(this)
        this.$scope.setNotImproper = this.setNotImproper
        this.$scope.restorePortfolio = this.restorePortfolio

        if (this.$rootScope.openMetadataDialog) {
            this.showEditMetadataDialog()
            this.$rootScope.openMetadataDialog = null
        }
    }
    $doCheck() {
        if (!this.$scope.portfolio && this.portfolio)
            this.$scope.portfolio = this.portfolio
    }
    canEdit() {
        return !this.authenticatedUserService.isRestricted() && (
            this.isOwner() ||
            this.authenticatedUserService.isAdmin() ||
            this.authenticatedUserService.isModerator()
        )
    }
    isOwner() {
        return !this.authenticatedUserService.isAuthenticated()
            ? false
            : this.portfolio && this.portfolio.creator
                ? this.portfolio.creator.id === this.authenticatedUserService.getUser().id
                : false
    }
    isAdmin() {
        return this.authenticatedUserService.isAdmin()
    }
    isAdminOrModerator() {
        return (
            this.authenticatedUserService.isAdmin() ||
            this.authenticatedUserService.isModerator()
        )
    }
    isLoggedIn() {
        return this.authenticatedUserService.isAuthenticated()
    }
    isRestricted() {
        return this.authenticatedUserService.isRestricted()
    }
    editPortfolio() {
        this.$location.url('/portfolio/edit?id=' + this.$route.current.params.id)
    }
    addComment(newComment, portfolio) {
        this.submitClick({ newComment, portfolio })
    }
    toggleCommentSection() {
        this.$scope.commentsOpen = !this.$scope.commentsOpen
    }
    getPortfolioEducationalContexts() {
        if (!this.portfolio || !this.portfolio.taxons)
            return

        const educationalContexts = []
        
        this.portfolio.taxons.forEach(taxon => {
            const edCtx = this.taxonService.getEducationalContext(taxon)
            
            if (edCtx && !educationalContexts.includes(edCtx))
                educationalContexts.push(edCtx)
        })

        return educationalContexts
    }
    getTaxonObject() {
        if (this.portfolio && this.portfolio.taxons)
            this.$scope.taxonObject = this.taxonGroupingService.getTaxonObject(this.portfolio.taxons)
    }
    showEditMetadataDialog() {
        this.storageService.setPortfolio(this.portfolio)
        this.$mdDialog.show({
            templateUrl: 'views/addPortfolioDialog/addPortfolioDialog.html',
            controller: 'addPortfolioDialogController'
        })
    }
    deletePortfolio() {
        this.serverCallService
            .makePost('rest/portfolio/delete', this.portfolio)
            .then(() => {
                this.toastService.show('PORTFOLIO_DELETED')
                this.portfolio.deleted = true
                this.$rootScope.learningObjectDeleted = true
                this.$rootScope.$broadcast('dashboard:adminCountsUpdated')
            })
    }
    confirmPortfolioDeletion() {
        this.dialogService.showDeleteConfirmationDialog(
            'PORTFOLIO_CONFIRM_DELETE_DIALOG_TITLE',
            'PORTFOLIO_CONFIRM_DELETE_DIALOG_CONTENT',
            this.deletePortfolio
        )
    }
    restorePortfolio() {
        this.serverCallService
            .makePost('rest/admin/deleted/portfolio/restore', this.portfolio)
            .then(() => {
                this.toastService.show('PORTFOLIO_RESTORED')
                this.portfolio.deleted = false
                this.$rootScope.learningObjectDeleted = false
                this.$rootScope.$broadcast('dashboard:adminCountsUpdated')
            })
    }
    setNotImproper() {
        if ((this.authenticatedUserService.isAdmin() || this.authenticatedUserService.isModerator()) && this.portfolio)
            this.serverCallService
                .makeDelete('rest/impropers?learningObject=' + this.portfolio.id)
                .then(() => {
                    this.$rootScope.learningObjectImproper = false
                    this.$rootScope.learningObjectUnreviewed = false
                    this.$rootScope.$broadcast('dashboard:adminCountsUpdated')
                })
    }
    markReviewed() {
        if (this.portfolio && (
                this.authenticatedUserService.isAdmin() ||
                this.authenticatedUserService.isModerator()
            )
        )
            this.serverCallService
                .makePost('rest/admin/firstReview/setReviewed', this.portfolio)
                .then(() => {
                    this.$rootScope.learningObjectUnreviewed = false
                    this.$rootScope.$broadcast('dashboard:adminCountsUpdated')
                })
    }
    getTargetGroups() {
        this.portfolio
            ? this.targetGroupService.getConcentratedLabelByTargetGroups(this.portfolio.targetGroups)
            : undefined
    }
    isAdminButtonsShowing() {
        return this.authenticatedUserService.isAdmin() && (
            (
                this.$rootScope.learningObjectDeleted == false &&
                this.$rootScope.learningObjectBroken == true &&
                this.$rootScope.learningObjectImproper == false
            ) || (
                this.$rootScope.learningObjectDeleted == false &&
                this.$rootScope.learningObjectBroken == false &&
                this.$rootScope.learningObjectImproper == true
            ) || (
                this.$rootScope.learningObjectDeleted == false &&
                this.$rootScope.learningObjectBroken == true &&
                this.$rootScope.learningObjectImproper == true
            ) ||
                this.$rootScope.learningObjectDeleted == true
        )
    }
    setRecommendation(recommendation) {
        if (this.portfolio)
            this.portfolio.recommendation = recommendation
    }
}
controller.$inject = [
    '$scope',
    '$location',
    '$mdDialog',
    '$rootScope',
    'authenticatedUserService',
    '$route',
    'dialogService',
    'serverCallService',
    'toastService',
    'storageService',
    'targetGroupService',
    'taxonService',
    'taxonGroupingService',
    'eventService',
    'portfolioService'
]

angular.module('koolikottApp').component('dopPortfolioSummaryCard', {
    bindings: {
        portfolio: '=',
        comment: '=',
        submitClick: '&'
    },
    templateUrl: 'directives/portfolioSummaryCard/portfolioSummaryCard.html',
    controller
})
}
