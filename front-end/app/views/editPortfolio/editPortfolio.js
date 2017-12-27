'use strict'

{
class controller extends Controller {
    constructor(...args) {
        super(...args)

        const storedPortfolio = this.storageService.getPortfolio()
        storedPortfolio
            ? this.setPortfolio(storedPortfolio, true)
            : this.fetchPortfolio()

        this.startAutosave()

        this.$scope.toggleSidenav = (menuId) => this.$mdSidenav(menuId).toggle()
        this.$scope.closeSidenav = (menuId) => this.$mdSidenav(menuId).close()

        this.$scope.$watch('portfolio', (currentValue, previousValue) => {
            if (currentValue !== previousValue)
                this.storageService.setPortfolio(currentValue)
        }, true)
        this.$scope.$watch(
            () => this.storageService.getPortfolio(),
            (portfolio) => this.$scope.portfolio = portfolio
        )
        this.$scope.$on('$destroy', () =>
            this.$interval.cancel(this.autoSaveInterval)
        )

        // Create a new chapter if user wishes to add their chosen materials to a new chapter.
        this.unsubscribeInsertMaterials = this.$rootScope.$on(
            'chapter:insertExistingMaterials',
            this.onInsertExistingMaterials.bind(this)
        )
        this.$scope.$on('$destroy', () => {
            this.unsubscribeInsertMaterials()
            window.isEditPortfolioControllerConstructed = false
        })

        window.isEditPortfolioControllerConstructed = true
    }
    createChapter(cb) {
        this.$scope.portfolio.chapters.push({
            title: '',
            blocks: []
        })
        this.$timeout(() => {
            // scroll to new chapter & focus title input
            const chapter = document.getElementById(`chapter-${this.$scope.portfolio.chapters.length}`)
            this.scrollToElement(chapter, 200, 60)

            cb  ? cb()
                : chapter.querySelector('.chapter-title-input').focus()
        })
    }
    deleteChapter(idx) {
        this.dialogService.showDeleteConfirmationDialog(
            'ARE_YOU_SURE_DELETE',
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
                if (portfolio)
                    this.isAutoSaving
                        ? this.$rootScope.$broadcast('portfolio:autoSave')
                        : this.setPortfolio(portfolio)
            })
    }
    startAutosave() {
        this.autoSaveInterval = this.$interval(() => {
            this.isAutoSaving = true

            if (this.$scope.portfolio && !this.$scope.portfolio.deleted)
                this.updatePortfolio()
        }, 20e3) // 20 secs
    }
    onInsertExistingMaterials(evt, chapterIdx, selectedMaterials) {
        if (chapterIdx === -1)
            this.createChapter(() =>
                this.$rootScope.$broadcast(
                    'chapter:insertExistingMaterials',
                    this.$scope.portfolio.chapters.length - 1,
                    selectedMaterials
                )
            )
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
    '$translate',
    'alertService',
    'authenticatedUserService',
    'dialogService',
    'serverCallService',
    'storageService',
]
angular.module('koolikottApp').controller('editPortfolioController', controller)
}
