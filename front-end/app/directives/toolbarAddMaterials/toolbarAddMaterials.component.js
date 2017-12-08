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
    getPortfolioSelectLabel() {
        return !this.portfolio
            ? this.$translate.instant('CHOOSE_PORTFOLIO')
            : this.portfolio === '-1'
                ? this.$translate.instant('ADD_TO_NEW_PORTFOLIO')
                : this.portfolio.title || ''
    }
    getChapterSelectLabel() {
        return !this.chapter
            ? this.$translate.instant('CHOOSE_PORTFOLIO_CHAPTER')
            : this.chapter === -1 || !this.portfolio.chapters[this.chapter]
                ? this.$translate.instant('ADD_TO_NEW_CHAPTER')
                : this.portfolio.chapters[this.chapter].title || this.$translate.instant('PORTFOLIO_ENTER_CHAPTER_TITLE')
    }
    upgradeMaterials(materials) {
        return Promise.all(
            materials.map(m =>
                this.materialService.getMaterialById(m.id).then(data => ({
                    learningObjects: [data]
                }))
            )
        )
    }
    addMaterialsToChapter(chapter, portfolio) {
        // Start spinner
        this.isSaving = true

        if (portfolio === '-1') {
            // @todo
            const tempPortfolio = this.createPortfolio()
            tempPortfolio.chapters = []

            if (indexes[0] === -1)
                chapterIndex = 0

            this.toastService.showOnRouteChange('PORTFOLIO_ADD_MATERIAL_SUCCESS')
            this.storageService.setPortfolio(tempPortfolio)
            this.$mdDialog.show({
                templateUrl: 'views/addPortfolioDialog/addPortfolioDialog.html',
                controller: 'addPortfolioDialogController'
            })
            this.removeSelection()
        } else {
            const selectedMaterials = this.$rootScope.selectedMaterials.slice(0)
            this.isSaving = false
            this.$rootScope.$broadcast('detailedSearch:empty')
            this.$rootScope.back()
            // it is imperative that 'chapter:insertMaterials' is broadcasted after navigating back
            this.$timeout(() => {
                this.$rootScope.$broadcast('chapter:insertExistingMaterials', parseInt(this.chapter, 10), selectedMaterials)
                this.toastService.show('PORTFOLIO_ADD_MATERIAL_SUCCESS')
            })
            this.removeSelection()
        }
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
    removeSelection() {
        this.$rootScope.selectedMaterials = []
    }
    portfolioSelectChange() {
        this.chapter = null
        this.portfolio === '-1'
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
}
controller.$inject = [
    '$rootScope',
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
