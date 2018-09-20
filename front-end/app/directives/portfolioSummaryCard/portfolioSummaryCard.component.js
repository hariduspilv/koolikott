'use strict'

{
class controller extends Controller {
    $onInit() {

        const VISIBILITY_PUBLIC = 'PUBLIC'
        const VISIBILITY_PRIVATE = 'PRIVATE'
        const VISIBILITY_NOT_LISTED = 'NOT_LISTED'
        const licenceTypeMap = {
            'CCBY': ['by'],
            'CCBYSA': ['by', 'sa'],
            'CCBYND': ['by', 'nd'],
            'CCBYNC': ['by', 'nc'],
            'CCBYNCSA': ['by', 'nc', 'sa'],
            'CCBYNCND': ['by', 'nc', 'nd']
        };

        this.deletePortfolio = this.deletePortfolio.bind(this)
        this.getTaxonObject = this.getTaxonObject.bind(this)
        this.restorePortfolio = this.restorePortfolio.bind(this)

        this.eventService.subscribe(this.$scope, 'taxonService:mapInitialized', this.getTaxonObject)
        this.eventService.subscribe(this.$scope, 'portfolio:reloadTaxonObject', this.getTaxonObject)
        this.eventService.notify('portfolio:reloadTaxonObject')

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
        this.$scope.$watch(() => this.storageService.getPortfolio(), (currentValue, previousValue) => {
            if (currentValue !== previousValue)
                this.$scope.portfolio = currentValue
        }, true)

        this.$scope.portfolio = this.portfolio
        this.$scope.commentsOpen = false
        this.$scope.pageUrl = this.$location.absUrl()
        this.$scope.isVocationalOnly = false

        this.$scope.canEdit = this.canEdit.bind(this)
        this.$scope.isAdmin = this.isAdmin.bind(this)
        this.$scope.isAdminOrModerator = this.isAdminOrModerator.bind(this)
        this.$scope.isLoggedIn = this.isLoggedIn.bind(this)
        this.$scope.isRestricted = this.isRestricted.bind(this)
        this.$scope.editPortfolio = this.editPortfolio.bind(this)
        this.$scope.toggleCommentSection = this.toggleCommentSection.bind(this)
        this.$scope.getPortfolioEducationalContexts = this.getPortfolioEducationalContexts.bind(this)
        this.$scope.showEditMetadataDialog = this.showEditMetadataDialog.bind(this)
        this.$scope.confirmPortfolioDeletion = this.confirmPortfolioDeletion.bind(this)
        this.$scope.getTargetGroups = this.getTargetGroups.bind(this)
        this.$scope.isAdminButtonsShowing = this.isAdminButtonsShowing.bind(this)
        this.$scope.setRecommendation = this.setRecommendation.bind(this)
        this.$scope.dotsAreShowing = this.dotsAreShowing.bind(this)
        this.$scope.restorePortfolio = this.restorePortfolio

        this.$scope.getLicenseIconList = () => {
            if (this.portfolio && this.portfolio.licenseType) {
                return licenceTypeMap[this.portfolio.licenseType.name];
            }
        };
        this.$scope.getPortfolioVisibility = () => (this.storageService.getPortfolio() || {}).visibility

        this.$scope.makePublic = () => {
            this.storageService.getPortfolio().visibility = VISIBILITY_PUBLIC
            this.updatePortfolio()
            this.toastService.show('PORTFOLIO_HAS_BEEN_MADE_PUBLIC')
        }

        this.$scope.makeNotListed = () => {
            this.storageService.getPortfolio().visibility = VISIBILITY_NOT_LISTED
            this.updatePortfolio()
        }

        this.$scope.makePrivate = () => {
            this.storageService.getPortfolio().visibility = VISIBILITY_PRIVATE
            this.updatePortfolio()
        }

        if (this.$rootScope.openMetadataDialog) {
            this.showEditMetadataDialog()
            this.$rootScope.openMetadataDialog = null
        }
    }
    $doCheck() {
        if (this.$scope.portfolio !== this.portfolio)
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
    updatePortfolio() {
        this.updateChaptersStateFromEditors()
        this.serverCallService
            .makePost('rest/portfolio/update', this.storageService.getPortfolio())
            .then(({ data: portfolio }) => {
                if (portfolio)
                    this.storageService.setPortfolio(portfolio)
            })
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

        if (this.$scope.taxonObject.educationalContexts) {
            const txObj = this.$scope.taxonObject.educationalContexts
            if (txObj.includes('VOCATIONALEDUCATION') && !txObj.includes('PRESCHOOLEDUCATION') && !txObj.includes('BASICEDUCATION') && !txObj.includes('SECONDARYEDUCATION'))
                this.$scope.isVocationalOnly = true
            else
                this.$scope.isVocationalOnly = false
        }
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
                this.$location.url('/portfolio?id=' + this.$route.current.params.id)
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
            .makePost('rest/admin/deleted/restore', this.portfolio)
            .then(() => {
                this.toastService.show('PORTFOLIO_RESTORED')
                this.portfolio.deleted = false
                this.$rootScope.learningObjectDeleted = false
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
        return this.portfolio
            ? this.targetGroupService.getConcentratedLabelByTargetGroups(this.portfolio.targetGroups || [])
            : undefined
    }
    isAdminButtonsShowing() {
        return this.authenticatedUserService.isAdmin() && (
            this.$rootScope.learningObjectDeleted === true || this.$rootScope.learningObjectImproper === true
        )
    }
    dotsAreShowing () {
        return this.$rootScope.learningObjectDeleted === false || this.authenticatedUserService.isAdmin();
    };
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
component('dopPortfolioSummaryCard', {
    bindings: {
        portfolio: '=',
        comment: '=',
        onAddComment: '&'
    },
    templateUrl: 'directives/portfolioSummaryCard/portfolioSummaryCard.html',
    controller
})
}
