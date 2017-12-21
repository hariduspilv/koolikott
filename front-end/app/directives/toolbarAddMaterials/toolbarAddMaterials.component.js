'use strict'

{
class controller extends Controller {
    $onInit() {
        if (this.$rootScope.isEditPortfolioMode) {
            this.isPortfolioEdit = true
            this.portfolio = this.storageService.getPortfolio()
            this.chapter = window.embedInsertionChapterIdx + ''
        } else
            this.loadUserPortfolios()
    }
    loadUserPortfolios() {
        const fail = () => {
            this.toastService.show('LOADING_PORTFOLIOS_FAIL')
            this.removeSelection()
        }
        this.serverCallService
            .makeGet('rest/portfolio/getByCreator', {
                'username': this.authenticatedUserService.getUser().username
            })
            .then(
                data => !data ? fail() : this.usersPortfolios = data.data.items,
                fail
            )
    }
    getPortfolioSelectLabel() {
        return !this.portfolio
            ? this.$translate.instant('CHOOSE_PORTFOLIO')
            : this.portfolio == -1
                ? this.$translate.instant('ADD_TO_NEW_PORTFOLIO')
                : this.portfolio.title || ''
    }
    getChapterSelectLabel() {
        return !this.chapter
            ? this.$translate.instant('CHOOSE_PORTFOLIO_CHAPTER')
            : this.chapter == -1 || !this.portfolio.chapters[this.chapter]
                ? this.$translate.instant('ADD_TO_NEW_CHAPTER')
                : this.portfolio.chapters[this.chapter].title || this.$translate.instant('PORTFOLIO_ENTER_CHAPTER_TITLE')
    }
    addMaterialsToChapter(chapter, portfolio) {
        // Start spinner
        this.isSaving = true
        const selectedMaterials = this.$rootScope.selectedMaterials.slice(0)
        const invokeInsert = (chapterIdx) => {
            this.$rootScope.$broadcast('chapter:insertExistingMaterials', chapterIdx, selectedMaterials)
            this.toastService.show('PORTFOLIO_ADD_MATERIAL_SUCCESS')
        }
        this.removeSelection()

        if (portfolio == -1) {
            this.storageService.setPortfolio(this.createPortfolio())
            const unsubscribe = this.$rootScope.$on('$locationChangeSuccess', () => {
                this.$timeout(() => invokeInsert(0))
                unsubscribe()
            })
            this.$mdDialog
                .show({
                    templateUrl: 'views/addPortfolioDialog/addPortfolioDialog.html',
                    controller: 'addPortfolioDialogController'
                })
                .then(() => {
                    this.isSaving = false
                    this.$rootScope.$broadcast('detailedSearch:empty')
                })
        } else {
            this.isSaving = false
            this.$rootScope.$broadcast('detailedSearch:empty')
            this.$location.url('/portfolio/edit?id=' + portfolio.id)
            // it is imperative that 'chapter:insertMaterials' is broadcasted after navigating to portfolio edit screen
            this.$timeout(() =>
                invokeInsert(parseInt(this.chapter, 10))
            )
        }
    }
    portfolioSelectChange() {
        this.chapter = null
        this.portfolio == -1
            ? this.chapter = '-1'
            : this.loadPortfolioChapters()
    }
    loadPortfolioChapters() {
        this.loadingChapters = true
        this.loadingChaptersFailed = false
        this.portfolioService
            .getPortfolioById(this.portfolio.id)
            .then(data => {
                this.portfolio = data
                this.loadingChapters = false
            }, () => {
                this.loadingChapters = false
                this.loadingChaptersFailed = true
                this.toastService.show('LOADING_PORTFOLIOS_FAIL')
            })
    }
    removeSelection() {
        this.$rootScope.selectedMaterials = []
    }
}
controller.$inject = [
    '$rootScope',
    '$location',
    '$translate',
    '$timeout',
    '$mdDialog',
    'authenticatedUserService',
    'serverCallService',
    'toastService',
    'storageService',
    'materialService',
    'portfolioService'
]
component('dopToolbarAddMaterials', {
    templateUrl: 'directives/toolbarAddMaterials/toolbarAddMaterials.html',
    controller
})
}
