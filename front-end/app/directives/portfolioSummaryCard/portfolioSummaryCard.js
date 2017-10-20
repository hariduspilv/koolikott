'use strict'

{
class controller extends Controller {
    $onInit() {
        this.$scope.portfolio = this.portfolio
        this.$scope.commentsOpen = false
        this.$scope.taxonObject = {}
        this.$scope.pageUrl = this.$location.absUrl()
        this.$scope.isViewPortforlioPage = this.$rootScope.isViewPortforlioPage
        this.$scope.isEditPortfolioMode = this.$rootScope.isEditPortfolioMode

        this.getTaxonObject = this.getTaxonObject.bind(this)
        this.eventService.subscribe(this.$scope, 'taxonService:mapInitialized', this.getTaxonObject)
        this.eventService.subscribe(this.$scope, 'portfolio:reloadTaxonObject', this.getTaxonObject)
        this.eventService.notify('portfolio:reloadTaxonObject')

        const isOwner = () =>
            !this.authenticatedUserService.isAuthenticated()
                ? false
                : this.$scope.portfolio && this.$scope.portfolio.creator
                    ? this.$scope.portfolio.creator.id === this.authenticatedUserService.getUser().id
                    : false

        this.$scope.canEdit = () => (isOwner() || this.authenticatedUserService.isAdmin() || this.authenticatedUserService.isModerator()) && !this.authenticatedUserService.isRestricted()
        this.$scope.isAdmin = () => this.authenticatedUserService.isAdmin()
        this.$scope.isAdminOrModerator = () => this.authenticatedUserService.isAdmin() || this.authenticatedUserService.isModerator()
        this.$scope.isLoggedIn = () => this.authenticatedUserService.isAuthenticated()
        this.$scope.isRestricted = () => this.authenticatedUserService.isRestricted()

        this.$scope.editPortfolio = () => this.$location.url("/portfolio/edit?id=" + this.$route.current.params.id)

        this.$scope.getPortfolioEducationalContexts = () => {
            if (!this.$scope.portfolio || !this.$scope.portfolio.taxons)
                return

            const educationalContexts = []
            
            this.$scope.portfolio.taxons.forEach(taxon => {
                const edCtx = this.taxonService.getEducationalContext(taxon)
                
                if (edCtx && !educationalContexts.includes(edCtx))
                    educationalContexts.push(edCtx)
            })

            return educationalContexts
        }

        this.$scope.showEditMetadataDialog = () => {
            this.storageService.setPortfolio(this.$scope.portfolio)
            this.$mdDialog.show({
                templateUrl: 'views/addPortfolioDialog/addPortfolioDialog.html',
                controller: 'addPortfolioDialogController'
            })
        }

        this.$scope.addComment = (newComment, portfolio) =>
            this.submitClick({ newComment, portfolio })

        this.$scope.toggleCommentSection = () => this.$scope.commentsOpen = !this.$scope.commentsOpen

        this.deletePortfolio = this.deletePortfolio.bind(this)
        this.$scope.confirmPortfolioDeletion = () =>
            this.dialogService.showDeleteConfirmationDialog(
                'PORTFOLIO_CONFIRM_DELETE_DIALOG_TITLE',
                'PORTFOLIO_CONFIRM_DELETE_DIALOG_CONTENT',
                this.deletePortfolio
            )

        this.restorePortfolio = this.restorePortfolio.bind(this)
        this.$scope.restorePortfolio = this.restorePortfolio

        this.setNotImproper = this.setNotImproper.bind(this)
        this.$scope.setNotImproper = this.setNotImproper

        this.$scope.$on('restore:portfolio', this.restorePortfolio)
        this.$scope.$on('delete:portfolio', this.deletePortfolio)
        this.$scope.$on('setNotImproper:portfolio', this.setNotImproper)
        this.$scope.$on('markReviewed:portfolio', this.markReviewed.bind(this))

        if (this.$rootScope.openMetadataDialog) {
            this.$scope.showEditMetadataDialog()
            this.$rootScope.openMetadataDialog = null
        }

        this.$scope.getTargetGroups = () =>
            this.$scope.portfolio
                ? this.targetGroupService.getConcentratedLabelByTargetGroups(this.$scope.portfolio.targetGroups)
                : undefined

        this.$scope.isAdminButtonsShowing = () =>
            this.authenticatedUserService.isAdmin() && (
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

        this.$scope.setRecommendation = (recommendation) => {
            if (this.$scope.portfolio)
                this.$scope.portfolio.recommendation = recommendation
        }
    }
    $onChanges({ portfolio }) {
        // Main purpose of this watch is to handle situations
        // where portfolio is undefined at the moment of init()
        if (portfolio && portfolio.currentValue !== portfolio.previousValue)
            this.eventService.notify('portfolio:reloadTaxonObject')

        if (portfolio && portfolio.taxon && portfolio.currentValue.taxon.id !== portfolio.previousValue.taxon.id)
            this.$scope.portfolioSubject = this.taxonService.getSubject(portfolio.taxon)
    }
    getTaxonObject() {
        if (this.$scope.portfolio && this.$scope.portfolio.taxons)
            this.$scope.taxonObject = this.taxonGroupingService.getTaxonObject(this.$scope.portfolio.taxons)
    }
    deletePortfolio() {
        this.serverCallService
            .makePost('rest/portfolio/delete', this.$scope.portfolio)
            .then(() => {
                this.toastService.show('PORTFOLIO_DELETED')
                this.$scope.portfolio.deleted = true
                this.$rootScope.learningObjectDeleted = true
                this.$rootScope.$broadcast('dashboard:adminCountsUpdated')
            })
    }
    restorePortfolio() {
        this.serverCallService
            .makePost('rest/admin/deleted/portfolio/restore', this.$scope.portfolio)
            .then(() => {
                this.toastService.show('PORTFOLIO_RESTORED')
                this.$scope.portfolio.deleted = false
                this.$rootScope.learningObjectDeleted = false
                this.$rootScope.$broadcast('dashboard:adminCountsUpdated')
            })
    }
    setNotImproper() {
        if ((this.authenticatedUserService.isAdmin() || this.authenticatedUserService.isModerator()) && this.$scope.portfolio)
            this.serverCallService
                .makeDelete('rest/impropers?learningObject=' + this.$scope.portfolio.id)
                .then(() => {
                    this.$rootScope.learningObjectImproper = false
                    this.$rootScope.learningObjectUnreviewed = false
                    this.$rootScope.$broadcast('dashboard:adminCountsUpdated')
                })
    }
    markReviewed() {
        if (this.$scope.portfolio && (
                this.authenticatedUserService.isAdmin() ||
                this.authenticatedUserService.isModerator()
            )
        )
            this.serverCallService
                .makePost('rest/admin/firstReview/setReviewed', this.$scope.portfolio)
                .then(() => {
                    this.$rootScope.learningObjectUnreviewed = false
                    this.$rootScope.$broadcast('dashboard:adminCountsUpdated')
                })
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
