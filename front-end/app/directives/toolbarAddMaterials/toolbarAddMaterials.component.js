'use strict'

{
class controller extends Controller {
    $onInit() {
        if (this.$rootScope.isEditPortfolioMode) {
            this.isPortfolioEdit = true
            this.portfolio = this.storageService.getPortfolio()

            if (this.$rootScope.savedIndexes)
                this.chapter = this.$rootScope.savedIndexes
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
        if (!this.chapter)
            return this.$translate.instant('CHOOSE_PORTFOLIO_CHAPTER')

        const indexes = this.chapter.split('_').map(item => parseInt(item, 10))

        return !indexes.length
            ? ''
            : indexes[0] === -1
                ? this.$translate.instant('ADD_TO_NEW_CHAPTER')
                : indexes.length > 1
                    ? this.portfolio.chapters[indexes[0]].subchapters[indexes[1]].title || this.$translate.instant('PORTFOLIO_SUBCHAPTER_TITLE_MISSING')
                    : this.portfolio.chapters[indexes[0]].title || this.$translate.instant('PORTFOLIO_CHAPTER_TITLE_MISSING')
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

        const tempPortfolio = portfolio !== '-1' ? portfolio : this.createPortfolio()
        const indexes = chapter.split('_').map(item => parseInt(item, 10))

        if (!Array.isArray(tempPortfolio.chapters))
            tempPortfolio.chapters = []

        // Indexes in arrays
        const chapterIndex = indexes[0] !== -1
            ? indexes[0]
            : tempPortfolio.chapters.length
        const subchapterIndex = indexes[1]

        this.upgradeMaterials(this.$rootScope.selectedMaterials).then(data => {
            if (subchapterIndex == null) {
                if (indexes[0] === -1)
                    tempPortfolio.chapters[chapterIndex] = { title: '' }

                if (!tempPortfolio.chapters[chapterIndex].contentRows)
                    tempPortfolio.chapters[chapterIndex].contentRows = []

                ;[].push.apply(tempPortfolio.chapters[chapterIndex].contentRows, data)
            } else {
                if(!tempPortfolio.chapters[chapterIndex].subchapters[subchapterIndex].contentRows)
                    tempPortfolio.chapters[chapterIndex].subchapters[subchapterIndex].contentRows = []

                ;[].push.apply(tempPortfolio.chapters[chapterIndex].subchapters[subchapterIndex].contentRows, data)
            }

            if (portfolio === '-1') {
                this.toastService.showOnRouteChange('PORTFOLIO_ADD_MATERIAL_SUCCESS')
                this.storageService.setPortfolio(tempPortfolio)
                this.$mdDialog.show({
                    templateUrl: 'views/addPortfolioDialog/addPortfolioDialog.html',
                    controller: 'addPortfolioDialogController'
                })
                this.removeSelection()
            } else {
                // When adding to an existing portfolio
                const fail = () => {
                    this.isSaving = false
                    this.toastService.show('PORTFOLIO_ADD_MATERIAL_FAIL')
                }
                this.$rootScope.$broadcast('detailedSearch:empty')
                this.serverCallService
                    .makePost('rest/portfolio/update', tempPortfolio)
                    .then(({ data: portfolio }) => {
                        if (!portfolio)
                            fail()
                        else {
                            this.removeSelection()
                            this.toastService.show('PORTFOLIO_ADD_MATERIAL_SUCCESS')

                            if (this.$rootScope.isEditPortfolioMode) {
                                this.$rootScope.back()
                                this.$rootScope.savedIndexes = null
                            }
                        }
                    }, fail)
            }
        })
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
                data => !data ? fail() : this.usersPortfolios = data.items,
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
