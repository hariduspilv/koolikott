'use strict'

{
class controller extends Controller {
    constructor(...args) {
        super(...args)

        const storedPortfolio = this.storageService.getPortfolio()
        storedPortfolio
            ? this.setPortfolio(storedPortfolio, true)
            : this.fetchPortfolio()

        /**
         * @todo Get rid of rootScope reference
         */
        if (this.$rootScope.newPortfolioCreated) {
            this.$rootScope.newPortfolioCreated = false
            this.$rootScope.$broadcast('tour:start:editPage:firstTime')
        }

        // what's this for?
        // if (this.$scope.portfolio && !this.$scope.portfolio.deleted)
        //     this.updatePortfolio()

        /**
         * @todo Re-enable 
         */
        // this.startAutosave()

        this.$scope.toggleSidenav = (menuId) => this.$mdSidenav(menuId).toggle()
        this.$scope.closeSidenav = (menuId) => this.$mdSidenav(menuId).close()

        this.$scope.$watch('portfolio', (currentValue, previousValue) => {
            if (currentValue !== previousValue)
                this.storageService.setPortfolio(currentValue)
        })
        this.$scope.$watch(
            () => this.storageService.getPortfolio(),
            (portfolio) => this.$scope.portfolio = portfolio
        )
        this.$scope.$on('$destroy', () =>
            this.$interval.cancel(this.autoSaveInterval)
        )
    }
    createChapter() {
        this.$scope.portfolio.chapters = this.$scope.portfolio.chapters.concat({
            title: '',
            blocks: []
        })
        this.$timeout(() => {
            // scroll to new chapter & focus title input
            const chapter = document.getElementById(`chapter-${this.$scope.portfolio.chapters.length - 1}`)
            this.scrollToElement(chapter, 200, 60)
            chapter.querySelector('input').focus()
        })
    }
    deleteChapter(idx) {
        this.dialogService.showDeleteConfirmationDialog(
            'PORTFOLIO_DELETE_CHAPTER_CONFIRM_TITLE',
            '',
            () => this.$scope.portfolio.chapters.splice(idx, 1)
        )
    }
    move(idx, up = false) {
        this.$scope.portfolio.chapters.splice(
            up  ? idx - 1
                : idx + 1,
            0,
            this.$scope.portfolio.chapters.splice(idx, 1)[0]
        )
    }
    fetchPortfolio() {
        const fail = () => {
            this.alertService.setErrorAlert('ERROR_PORTFOLIO_NOT_FOUND')
            this.$location.url('/')
        }
        this.serverCallService
            .makeGet('rest/portfolio?id=' + this.$route.current.params.id)
            .then(({ data: portfolio }) =>
                portfolio
                    ? this.setPortfolio(portfolio)
                    : fail(),
                fail
            )
    }
    setPortfolio(portfolio, isLocallyStored = false) {
        if (this.checkAuthorized(portfolio)) {
            if (!isLocallyStored)
                portfolio.chapters = this.transformChapters(portfolio.chapters)

            if (!Array.isArray(portfolio.chapters))
                portfolio.chapters = []

            if  (!portfolio.chapters.length)
                portfolio.chapters.push({ title: '', blocks: [] })

            this.storageService.setPortfolio(portfolio)
        }
    }
    checkAuthorized(portfolio) {
        var user = this.authenticatedUserService.getUser()

        if ((user && user.id == portfolio.creator.id) ||
            this.authenticatedUserService.isAdmin() ||
            this.authenticatedUserService.isModerator()
        )
            return true

        console.log("You don't have permission to edit this portfolio")
        this.$location.url('/')
        return false
    }
    updatePortfolio() {
        this.updateChaptersStateFromEditors()
        this.serverCallService
            .makePost('rest/portfolio/update', this.$scope.portfolio)
            .then(({ data: portfolio }) => {
                if (portfolio) {
                    if (!this.isAutoSaving)
                        this.setPortfolio(portfolio)

                    this.toastService.show(
                        this.isAutoSaving
                            ? 'PORTFOLIO_AUTOSAVED'
                            : 'PORTFOLIO_SAVED'
                    )
                }
            })
    }
    startAutosave() {
        this.autoSaveInterval = this.$interval(() => {
            this.isAutoSaving = true

            if (this.$scope.portfolio && !this.$scope.portfolio.deleted)
                this.updatePortfolio()
        }, 20000)
    }
}
controller.$inject = [
    '$scope',
    '$rootScope',
    '$route',
    '$location',
    '$interval',
    '$timeout',
    '$document',
    'alertService',
    'authenticatedUserService',
    'dialogService',
    'toastService',
    'serverCallService',
    'storageService',
]
angular.module('koolikottApp').controller('editPortfolioController', controller)
}
