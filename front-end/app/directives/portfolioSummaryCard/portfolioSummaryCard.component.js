'use strict'

{
    const VISIBILITY_PUBLIC = 'PUBLIC'
    const VISIBILITY_PRIVATE = 'PRIVATE'
    const VISIBILITY_NOT_LISTED = 'NOT_LISTED'
    const licenceTypeMap = {
        'CCBY': ['by'],
        'CCBYSA': ['by', 'sa'],
        'CCBYND': ['by', 'nd'],
        'CCBYNC': ['by', 'nc'],
        'CCBYNCSA': ['by', 'nc', 'sa'],
        'CCBYNCND': ['by', 'nc', 'nd'],
        'CCBYSA30': ['by', 'sa']
    };

class controller extends Controller {
    $onInit() {

        this.$scope.$on("$mdMenuClose", () => {
            this.$scope.showTooltip1 = false;
            this.$scope.showTooltip2 = false;
        });
        this.$scope.showEditModeButton = true
        this.deletePortfolio = this.deletePortfolio.bind(this)
        this.restorePortfolio = this.restorePortfolio.bind(this)

        this.eventService.notify('portfolio:reloadTaxonObject')

        // Main purpose of this watch is to handle situations
        // where portfolio is undefined at the moment of init()
        this.$scope.$watch('portfolio', (newValue, oldValue) => {
            if (this.portfolio) {
                if (this.portfolio.deletedOrNotPublic)
                    this.$scope.deletedOrNotPublic = this.portfolio.deletedOrNotPublic

                if (this.portfolio.copiedLOStatus)
                    this.$scope.copiedLOStatus = this.portfolio.copiedLOStatus

                if (this.portfolio.copiedFromDirectName)
                    this.$scope.copiedFromDirectName = this.portfolio.copiedFromDirectName

                if (this.portfolio.type === '.Portfolio' &&
                    (this.$rootScope.acceptableLicenses === undefined || this.$rootScope.portfolioHasMaterialWithUnacceptableLicense === undefined)) {
                    this.serverCallService
                    .makeGet('rest/portfolio/portfolioHasAnyUnAcceptableLicense', {id: this.portfolio.id})
                    .then((response) => {
                        this.$rootScope.acceptableLicenses = !response.data
                    })
                    this.serverCallService.makeGet('rest/portfolio/portfolioHasAnyMaterialWithUnacceptableLicense', {id: this.$scope.portfolio.id})
                        .then((response) => {
                            this.$rootScope.portfolioHasMaterialWithUnacceptableLicense = response.data
                        })
                }
            }
            if (newValue !== oldValue) {
                this.$rootScope.$broadcast('portfolioHistory:loadHistory');
                this.eventService.notify('portfolio:reloadTaxonObject')
            }
        })
        this.$scope.$watch('portfolio.taxon.id', (newValue, oldValue) => {
            if (newValue !== oldValue)
                $scope.portfolioSubject = this.taxonService.getSubject(this.portfolio.taxon)
        }, true)
        this.$scope.$watch(() => this.storageService.getPortfolio(), (currentValue, previousValue) => {
            if (currentValue !== previousValue) {
                this.$scope.portfolio = currentValue
            }
        }, true)

        this.$rootScope.$on('portfolioHistory:hide', this.showButtons.bind(this));
        this.$rootScope.$on('portfolio:autoSave', this.getHistoryType.bind(this));
        this.$rootScope.$on('portfolioHistory:hideDeleteButton', this.hideButtons.bind(this));

        this.$scope.portfolio = this.portfolio
        this.$scope.showlogselect = this.showlogselect
        this.$scope.pageUrl = this.$location.absUrl()

        this.$scope.isAutoSaving = false;
        this.$scope.showLogButton = true;
        this.$scope.showDeleteButton = true;
        this.$scope.showSendEmailButton = true;
        this.$scope.showRecommendButton = true;
        this.$scope.showReportImproperButton = true;

        this.$scope.canEdit = this.canEdit.bind(this)
        this.$scope.isAdmin = this.isAdmin.bind(this)
        this.$scope.isOwner= this.isOwner.bind(this)
        this.$scope.isAdminOrModerator = this.isAdminOrModerator.bind(this)
        this.$scope.isLoggedIn = this.isLoggedIn.bind(this)
        this.$scope.isPublic = this.isPublic.bind(this)
        this.$scope.isRestricted = this.isRestricted.bind(this)
        this.$scope.editPortfolio = this.editPortfolio.bind(this)
        this.$scope.getPortfolioEducationalContexts = this.getPortfolioEducationalContexts.bind(this)
        this.$scope.showEditMetadataDialog = this.showEditMetadataDialog.bind(this)
        this.$scope.confirmPortfolioDeletion = this.confirmPortfolioDeletion.bind(this)
        this.$scope.getTargetGroups = this.getTargetGroups.bind(this)
        this.$scope.isAdminButtonsShowing = this.isAdminButtonsShowing.bind(this)
        this.$scope.setRecommendation = this.setRecommendation.bind(this)
        this.$scope.dotsAreShowing = this.dotsAreShowing.bind(this)
        this.$scope.restorePortfolio = this.restorePortfolio
        this.$scope.toggleFullScreen = this.toggleFullScreen.bind(this)
        this.$scope.getLicenseIconList = () => {
            if (this.portfolio && this.portfolio.licenseType) {
                return licenceTypeMap[this.portfolio.licenseType.name];
            }
        };
        this.$scope.getPortfolioVisibility = () => (this.storageService.getPortfolio() || {}).visibility

        this.$scope.makePublic = () => {
            this.checkUnacceptableLicensesAndUpdate()
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

    showButtons(){
        this.$scope.showEditModeButton = true
        this.$scope.showLogButton = true;
        this.$scope.showDeleteButton = true;
        this.$scope.showSendEmailButton = true;
        this.$scope.showRecommendButton = true;
        this.$scope.showReportImproperButton = true;
    }

    showPortfolioHistoryDialog() {
        this.$scope.showEditModeButton = false;
        this.$scope.showLogButton = false;
        this.$rootScope.$broadcast('portfolioHistory:show');

        gTagCaptureEventWithLabel('show', 'teaching portfolio', 'History')
    }

    hideButtons() {
        this.$scope.showDeleteButton = false;
        this.$scope.showSendEmailButton = false;
        this.$scope.showRecommendButton = false;
        this.$scope.showReportImproperButton = false;
    }

    canEdit() {
        return this.isOwner() ||
            this.authenticatedUserService.isAdmin() ||
            this.authenticatedUserService.isModerator()

    }
    isOwner() {
        return !this.authenticatedUserService.isAuthenticated()
            ? false : this.portfolio && this.portfolio.creator
                ? this.portfolio.creator.id === this.authenticatedUserService.getUser().id : false
    }
    isAdmin() {
        return this.authenticatedUserService.isAdmin()
    }
    isModerator() {
        return this.authenticatedUserService.isModerator()
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
        this.$location.url('/kogumik/muuda/' + this.$route.current.params.id)

        gTagCaptureEvent('modify', 'teaching portfolio')

    }
    updatePortfolio() {
        this.updateChaptersStateFromEditors()
        this.serverCallService
            .makePost(`rest/portfolio/update`,
                this.storageService.getPortfolio())
            .then(({data: portfolio}) => {
                if (portfolio) {
                    this.storageService.setPortfolio(portfolio);
                    this.toastService.show('PORTFOLIO_SAVED');
                    this.$route.reload();
                }
            })
            .catch(() => this.toastService.show('PORTFOLIO_SAVE_FAILED',15000))
    }

    getHistoryType(){
        this.$scope.isAutoSaving = true;
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
    showEditMetadataDialog() {
        this.storageService.setPortfolio(this.portfolio)
        this.$mdDialog.show({
            templateUrl: 'views/addPortfolioDialog/addPortfolioDialog.html',
            controller: 'addPortfolioDialogController',
            locals: {
                mode: 'EDIT'
            },
        })
    }

    checkUnacceptableLicensesAndUpdate(){
        this.serverCallService.makeGet('rest/portfolio/portfolioHasAnyUnAcceptableLicense',
            {
                id: this.$scope.portfolio.id,
            })
            .then(({ data }) => {
                if(data){
                    this.$mdDialog.show({
                        templateUrl: 'views/learningObjectAgreementDialog/learningObjectLicenseAgreementDialog.html',
                        controller: 'learningObjectLicenseAgreementController',
                    }).then((res) => {
                        if (res.accept) {
                            this.$rootScope.portfolioLicenseTypeChanged = true
                            this.showEditMetadataDialog()
                        }
                    })
                } else {
                    this.$scope.portfolio.visibility = VISIBILITY_PUBLIC
                    this.updatePortfolio()
                }
            })
    }

    deletePortfolio() {
        this.serverCallService
            .makePost('rest/portfolio/delete', this.portfolio)
            .then(() => {
                this.toastService.show('PORTFOLIO_DELETED')
                this.portfolio.deleted = true
                this.$rootScope.learningObjectDeleted = true
                this.$location.url('/kogumik/' + this.$route.current.params.id)
                this.$rootScope.$broadcast('dashboard:adminCountsUpdated')
                this.$rootScope.$broadcast('portfolioHistory:closeLogBanner')
                this.$scope.showEditModeButton = false;
                this.$scope.showLogButton = false;
            })
    }

    confirmPortfolioDeletion() {
        this.dialogService.showDeleteConfirmationDialog(
            'PORTFOLIO_CONFIRM_DELETE_DIALOG_TITLE',
            'PORTFOLIO_CONFIRM_DELETE_DIALOG_CONTENT',
            this.deletePortfolio
        )
        gTagCaptureEvent('delete', 'teaching portfolio')
    }
    restorePortfolio() {
        this.serverCallService
            .makePost('rest/admin/deleted/restore', this.portfolio)
            .then(() => {
                this.toastService.show('PORTFOLIO_RESTORED')
                this.portfolio.deleted = false
                this.$rootScope.learningObjectDeleted = false
                this.$rootScope.$broadcast('dashboard:adminCountsUpdated')
                this.$rootScope.$broadcast('portfolioHistory:show')
                this.$scope.showEditModeButton = true;
                this.$scope.showLogButton = true;
            })
    }
    copyPortfolio() {
        const portfolio = this.storageService.getPortfolio();
        if (!portfolio) console.log('copying failed')
        this.storageService.setEmptyPortfolio(portfolio)
        this.$mdDialog.show({
            templateUrl: 'views/addPortfolioDialog/addPortfolioDialog.html',
            controller: 'addPortfolioDialogController',
            fullscreen: false,
            locals: {
                mode: 'COPY'
            }
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
        return !this.portfolio.deleted;
    };
    setRecommendation(recommendation) {
        if (this.portfolio)
            this.portfolio.recommendation = recommendation
    }

    isPublic() {
        if (this.portfolio)
            return this.portfolio.visibility === 'PUBLIC';
    }

    originalIsPrivate() {
        if (this.portfolio)
            return this.portfolio.copiedLOStatus === 'PRIVATE';
    }

    originalIsDeleted() {
        if (this.portfolio)
            return this.portfolio.copiedLOStatus === 'DELETED';
    }

    toggleFullScreen() {
        this.$rootScope.isFullScreen = !this.$rootScope.isFullScreen;
        toggleFullScreen();
        if (this.$rootScope.isFullScreen) {
            this.toastService.show('YOU_CAN_LEAVE_PAGE_WITH_ESC', 15000, 'user-missing-id');

            gTagCaptureEvent('full-screen', 'teaching portfolio')
        }
        else {
            this.toastService.hide()
        }
    }

    onDivAction1() {
        this.$scope.showTooltip1 = this.$scope.portfolioHasMaterialWithUnacceptableLicense;
    }

    onDivAction2() {
        this.$scope.showTooltip2 = this.$scope.portfolioHasMaterialWithUnacceptableLicense;
    }

    onDivLeave1() {
        this.$scope.showTooltip1 = false;
    }

    onDivLeave2() {
        this.$scope.showTooltip2 = false;
    }
}
controller.$inject = [
    '$rootScope',
    '$scope',
    '$location',
    '$mdDialog',
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
    'portfolioService',
    'translationService',
    '$timeout'
]
component('dopPortfolioSummaryCard', {
    bindings: {
        portfolio: '=',
    },
    templateUrl: 'directives/portfolioSummaryCard/portfolioSummaryCard.html',
    controller
})
}
