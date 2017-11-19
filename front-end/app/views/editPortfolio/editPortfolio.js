'use strict'

{
class controller extends Controller {
    constructor(...args) {
        super(...args)

        const storedPortfolio = this.storageService.getPortfolio()
        storedPortfolio
            ? this.setPortfolio(storedPortfolio)
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
        this.$scope.$watch(() => this.storageService.getPortfolio(), (newPortfolio) => {
            this.$scope.portfolio = newPortfolio

            /**
             * @todo Mocking it
             */
            if (this.$scope.portfolio)
                this.$scope.portfolio._chapters = [{
                    title: 'Olen esimene peatükk',
                    blocks: []
                }, {
                    title: 'Olen teine peatükk',
                    blocks: [{
                        narrow: false,
                        htmlContent: '<p>short content</p>'
                    }]
                }, {
                    title: 'Olen kolmas peatükk',
                    blocks: [{
                        narrow: false,
                        htmlContent: `
                            <p>Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book.</p>
                            <div class="embed-card embed-card--material embed-card--float-left" data-id="66000"></div>
                            <p>Contrary to popular belief, Lorem Ipsum is not simply random text. It has roots in a piece of classical Latin literature from 45 BC, making it over 2000 years old. Richard McClintock, a Latin professor at Hampden-Sydney College in Virginia, looked up one of the more obscure Latin words, consectetur, from a Lorem Ipsum passage, and going through the cites of the word in classical literature, discovered the undoubtable source.</p>
                            <h3 class="subchapter" id="Olen-alampeatükk">Olen alampeatükk</h3>
                            <p>Lorem Ipsum comes from sections 1.10.32 and 1.10.33 of "de Finibus Bonorum et Malorum" (The Extremes of Good and Evil) by Cicero, written in 45 BC. This book is a treatise on the theory of ethics, very popular during the Renaissance. The first line of Lorem Ipsum, "Lorem ipsum dolor sit amet..", comes from a line in section 1.10.32.</p>`
                        }, {
                        narrow: true,
                        htmlContent: `
                            <div class="embed-card embed-card--image" data-src="..." data-caption="..."></div>
                            <p>The standard chunk of Lorem Ipsum used since the 1500s is reproduced below for those interested. Sections 1.10.32 and 1.10.33 from "de Finibus Bonorum et Malorum" by Cicero are also reproduced in their exact original form, accompanied by English versions from the 1914 translation by H. Rackham.</p>`
                        }, {
                        narrow: true,
                        htmlContent: `
                            <p>It is a long established fact that a reader will be distracted by the readable content of a page when looking at its layout. The point of using Lorem Ipsum is that it has a more-or-less normal distribution of letters, as opposed to using 'Content here, content here', making it look like readable English.</p>
                            <div class="embed-card embed-card--youtube" data-id="i8kNDga1tmM"></div>`
                        }, {
                        narrow: false,
                        htmlContent: `
                            <p>Many desktop publishing packages and web page editors now use Lorem Ipsum as their default model text, and a search for 'lorem ipsum' will uncover many web sites still in their infancy. Various versions have evolved over the years, sometimes by accident, sometimes on purpose (injected humour and the like).</p>
                            <div class="embed-card embed-card--soundcloud" data-id="dkmntl/dasha-rush-at-dekmantel-festival-sao-paulo-2017"></div>`
                    }]
                }]
            console.log('this.$scope.portfolio:', this.$scope.portfolio)
        })
        this.$scope.$on('$destroy', () =>
            this.$interval.cancel(this.autoSaveInterval)
        )
    }
    createChapter() {
        this.$scope.portfolio._chapters = this.$scope.portfolio._chapters.concat({
            title: '',
            blocks: []
        })
        this.$timeout(() => {
            // scroll to new chapter & focus title input
            const chapter = document.getElementById(`chapter-${this.$scope.portfolio._chapters.length - 1}`)
            this.scrollToElement(chapter, 200, 60)
            chapter.querySelector('input').focus()
        })
    }
    deleteChapter(idx) {
        this.dialogService.showDeleteConfirmationDialog(
            'PORTFOLIO_DELETE_CHAPTER_CONFIRM_TITLE',
            '',
            () => this.$scope.portfolio._chapters.splice(idx, 1)
        )
    }
    move(idx, up = false) {
        this.$scope.portfolio._chapters.splice(
            up  ? idx - 1
                : idx + 1,
            0,
            this.$scope.portfolio._chapters.splice(idx, 1)[0]
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
    setPortfolio(portfolio) {
        if (this.checkAuthorized(portfolio)) {
            // normalize material sources (uploadedFile -> source)
            if (portfolio.chapters)
                portfolio.chapters.forEach(({ contentRows }) =>
                    contentRows &&
                    contentRows.forEach(row =>
                        row.learningObjects &&
                        row.learningObjects.forEach(learningObject =>
                            learningObject.source = this.getMaterialSource(learningObject)
                        )
                    )
                )
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
