'use strict'

{
class controller extends Controller {
    $onInit() {
        if (this.$rootScope.isEditPortfolioMode) {
            this.isPortfolioEdit = true
            this.portfolio = this.storageService.getPortfolio()
            this.chapter = window.embedInsertionChapterIdx || -1;
        } else
            this.loadUserPortfolios()

        this.$rootScope.$watch('selectedMaterials.length', (newLen) => {
            if (newLen > 0) {
                const addMaterialsToolbarElement = document.getElementById('addMaterialsToolbar')
                const cookieParentElement = document.getElementById('cookieParent')
                const cookieEditModeElement = document.getElementById('cookieEditMode')

                if (cookieParentElement !== null && addMaterialsToolbarElement !== null) {
                    if (cookieEditModeElement !== null &&
                        addMaterialsToolbarElement.style.top !== '98px')
                        addMaterialsToolbarElement.style.top = 98 + 'px'
                    else if (cookieEditModeElement === null &&
                        addMaterialsToolbarElement.style.top !== '58px')
                        addMaterialsToolbarElement.style.top = 58 + 'px'
                }
            }
        }, false)
    }
    isMobileView(){
        return this.isNVP();
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
            : this.portfolio.title

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
        this.removeSelection()

        const insertAfterLocationChange = (chapterIdx) => {
            let numAttempts = 0
            const insertMaterials = () => {
                if (numAttempts <= 20) {
                    if (!window.isEditPortfolioControllerConstructed) {
                        numAttempts++
                        return setTimeout(insertMaterials, 500)
                    }

                    this.$timeout(() => {
                        this.$rootScope.$broadcast('chapter:insertExistingMaterials', chapterIdx, selectedMaterials)
                        this.toastService.show('PORTFOLIO_ADD_MATERIAL_SUCCESS')
                    })
                } else
                    console.error('Waited a whole 10 seconds for editPortfolio view controller to construct. Aborting the insertion of selected materials')
            }
            const unsubscribe = this.$rootScope.$on('$locationChangeSuccess', () => {
                insertMaterials()
                unsubscribe()
            })
        }

        if (portfolio == -1) {
            this.storageService.setPortfolio(this.createPortfolio())
            insertAfterLocationChange(0)
            this.$mdDialog
                .show({
                    templateUrl: 'views/addPortfolioDialog/addPortfolioDialog.html',
                    controller: 'addPortfolioDialogController',
                    locals: {
                        mode: 'EDIT'
                    }
                })
                .then(() => {
                    this.isSaving = false
                    this.$rootScope.$broadcast('detailedSearch:empty')
                })
        } else {
            insertAfterLocationChange(parseInt(this.chapter, 10))
            this.isSaving = false
            this.$rootScope.$broadcast('detailedSearch:empty')
            this.$location.url('/kogumik/muuda/' + portfolio.id)
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
