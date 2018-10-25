'use strict'

{
class controller extends Controller {
    constructor(...args) {
        super(...args)

        $('body').materialScrollTop({ offset: 300 })
        const storedPortfolio = this.storageService.getPortfolio()

        if (storedPortfolio &&
            storedPortfolio.type !== '.ReducedPortfolio' &&
            storedPortfolio.type !== '.AdminPortfolio'
        ) {
            this.setPortfolio(storedPortfolio)
            this.increaseViewCount()
        } else
            this.getPortfolio()

        this.$scope.newComment = {}
        this.$scope.isModerator = this.authenticatedUserService.isModerator()
        this.$scope.isAdmin = this.authenticatedUserService.isAdmin()
        this.$scope.isLoggedIn = () => this.authenticatedUserService.isAuthenticated();

        this.$scope.$watch(() => this.storageService.getPortfolio(), (newPortfolio, oldPortfolio) => {
            this.eventService.notify('portfolio:reloadTaxonObject')

            if (newPortfolio !== oldPortfolio)
                this.setPortfolio(newPortfolio)
        })
        this.$scope.$watch(() => this.$location.search().id, (newValue, oldValue) => {
            if (nv !== oldValue)
                this.$route.reload()
        }, true)

        this.$scope.$on('$routeChangeStart', () => {
            if (!this.$location.url().startsWith('/portfolio/edit?id='))
                this.setPortfolio(null)
        })
        this.$scope.$on('$destroy', () =>
            this.$timeout.cancel(this.increaseViewCountPromise)
        )
        this.$scope.$on('tags:updatePortfolio', (evt, value) => {
            if (!_.isEqual(value, this.$scope.portfolio))
                this.setPortfolio(value)
        })
    }
    getPortfolio() {
        const { id } = this.$route.current.params
        const fail = () => {
            this.toastService.show('ERROR_PORTFOLIO_NOT_FOUND')
            this.$location.url('/')
        }
        if (id)
            this.serverCallService
                .makeGet('rest/portfolio', { id })
                .then(({ status, data }) =>
                    200 <= status && status < 300 && data
                        ? this.setPortfolio(data, false) || this.increaseViewCount()
                        : fail(),
                    fail
                )
    }
    increaseViewCount() {
        /**
         * @todo This solution needs to be re-analysed. 1 sec delay looks like a fishy hack.
         *
         *  It is needed to have it in a timeout because of double call caused by using two different page structure.
         *  So we cancel it in case the page is destroyed so the new one that will be create makes the valid call.
         */
        this.increaseViewCountPromise = this.$timeout(() => {
            if (this.$scope.portfolio)
                this.serverCallService
                    .makePost('rest/learningObject/increaseViewCount', createPortfolio(this.$scope.portfolio.id))
        }, 1000)
    }
    setPortfolio(portfolio, isLocallyStored = true) {
        this.$scope.portfolio = portfolio
        this.storageService.setPortfolio(portfolio)

        this.$rootScope.learningObjectPrivate = portfolio && ['PRIVATE'].includes(portfolio.visibility)
        this.$rootScope.learningObjectImproper = portfolio && portfolio.improper > 0
        this.$rootScope.learningObjectDeleted = portfolio && portfolio.deleted === true
        this.$rootScope.learningObjectChanged = portfolio && portfolio.changed > 0
        this.$rootScope.learningObjectUnreviewed = portfolio && !!portfolio.unReviewed

        if (!isLocallyStored && portfolio && portfolio.chapters) {
            portfolio.chapters = (new Controller()).transformChapters(portfolio.chapters)

            // add id attributes to all subchapters derived from subchapter titles
            this.$timeout(() =>
                this.$timeout(() => {
                    const entries = (selector, el = document) => el.querySelectorAll(selector).entries()

                    for (let [idx, el] of entries('.portfolio-chapter'))
                        for (let [subIdx, subEl] of entries('.subchapter', el))
                            subEl.id = this.getSlug(`subchapter-${idx + 1}-${subIdx + 1}`)
                })
            )
        }
        this.$rootScope.$broadcast('portfolioChanged')
    }
}
controller.$inject = [
    '$scope',
    '$rootScope',
    '$route',
    '$location',
    '$timeout',
    'authenticatedUserService',
    'eventService',
    'serverCallService',
    'storageService',
    'toastService',
]
angular.module('koolikottApp').controller('portfolioController', controller)
}
