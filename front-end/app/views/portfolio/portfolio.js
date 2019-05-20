'use strict'

{
class controller extends Controller {
    constructor(...args) {
        super(...args)
        this.$rootScope.isFullScreen = false

        this.$scope.showlogselect = false;

        window.addEventListener('scroll', (e) => {
            this.handleScroll(e);
        });

        document.addEventListener('keyup', (e) => {
            if (e.code === "Escape" && this.$rootScope.isFullScreen) {
                this.$rootScope.isFullScreen = !this.$rootScope.isFullScreen
                toggleFullScreen();
            }
        });

        window.addEventListener('popstate',() => {
            if (this.$rootScope.isFullScreen) {
                this.$rootScope.isFullScreen = !this.$rootScope.isFullScreen;
                toggleFullScreen();
            }
        });

        $('body').materialScrollTop({ offset: 300 })
        const storedPortfolio = this.storageService.getPortfolio()

        if (storedPortfolio && storedPortfolio.type === '.Portfolio') {
            this.setPortfolio(storedPortfolio)
            this.increaseViewCount()
        } else
            this.getPortfolio()

        this.$scope.newComment = {}
        this.$scope.isModerator = this.authenticatedUserService.isModerator()
        this.$scope.isAdmin = this.authenticatedUserService.isAdmin()

        this.$scope.$watch(() => this.storageService.getPortfolio(), (newPortfolio, oldPortfolio) => {
            this.eventService.notify('portfolio:reloadTaxonObject');

            if (newPortfolio !== oldPortfolio) {
                this.setPortfolio(newPortfolio)
            }
        });
        this.$scope.$watch(() => this.$location.search().id, (newValue, oldValue) => {
            if (newValue !== oldValue)
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
            if (!_.isEqual(value, this.$scope.portfolio)) {
                this.eventService.notify('portfolio:reloadTaxonObject')
                this.setPortfolio(this.$scope.portfolio)
                this.toastService.show('PORTFOLIO_SAVED')
            }
        });
        this.$scope.$on('portfolioHistory:show',this.showPortfolioHistoryLog.bind(this));

        this.$scope.$watch('portfolio', (newValue, oldValue) => {
            if (newValue !== oldValue && (newValue.type === '.PortfolioLog' || oldValue.type === '.PortfolioLog')) {
                this.eventService.notify('portfolio:reloadTaxonObject');
                this.setPortfolio(this.$scope.portfolio)
            }
        });
    }

    showPortfolioHistoryLog() {
        this.$scope.showlogselect = true;
        // let menu = document.getElementById('historymenu');
        // this.$timeout(() => {
        //     menu.dispatchEvent(this.returnEvent());
        // }, 400);
    }

    returnEvent() {
        let event;
        if (typeof (MouseEvent) === 'function') {
            event = new MouseEvent('click');
        } else {
            event = document.createEvent('MouseEvent');
            event.initEvent('click', true, true);
        }
        return event;
    }

    handleScroll(e) {
        let allElements = Array.from(document.querySelectorAll('.portfolio-chapter'))
        allElements.forEach((el) => {

            if (this.isElementInViewport(el)) {
                e.preventDefault()
                e.stopPropagation()

                let item = $(".sidenav__list .sidenav__item--chapter-title a");
                let total = item.length

                for (let i = 0; i < total; i++) {
                    let link = item.eq(i).attr("ng-href")
                    if (link) {
                        let id = link.split("#")[1]
                        item.eq(i).css("background-color", id === el.id ? "rgba(158, 158, 158, 0.2)" : "transparent")
                    }
                }
                let title = $(el).find('h2').text()
                title = this.replaceSpacesAndCharacters(title)
                let url = this.$location.url().split("&chapterName=")[0] + "&chapterName=" + title + '#' + el.id;

                if (!window.location.href.includes(title) && this.$location.path() === '/portfolio') {
                    this.$location.url(url)
                    history.pushState({}, '', url)
                }
            }
        })
    }

    showUnreviewedMessage(id) {
        this.serverCallService.makeGet('rest/learningObject/showUnreviewed?id=' + id)
            .then(response => {
                this.$scope.showUnreviewedPortfolio = response.data;
            })
    }

    getPortfolio() {
        const { id } = this.$route.current.params
        const fail = () => {
            this.toastService.show('ERROR_PORTFOLIO_NOT_FOUND')
            this.$location.url('/')
        }
        if (id) {
            this.serverCallService
                .makeGet('rest/portfolio', {id})
                .then(({status, data}) =>
                        200 <= status && status < 300 && data
                            ? this.setPortfolio(data, false) || this.increaseViewCount()
                            : fail(),
                    fail
                )
        }

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

    isElementInViewport (el) {
        if (typeof jQuery === "function" && el instanceof jQuery) {
            el = el[0];
        }

        let rect = el.getBoundingClientRect();

        return (
            rect.top >= -100 && rect.top <= 440 &&
            rect.left >= 0
        );
    }

    setPortfolio(portfolio, isLocallyStored = true) {
        this.$scope.portfolio = portfolio
        this.storageService.setPortfolio(portfolio)

        this.$scope.learningObject = portfolio

        this.$rootScope.learningObjectPrivate = portfolio && ['PRIVATE'].includes(portfolio.visibility)
        this.$rootScope.learningObjectImproper = portfolio && portfolio.improper > 0
        this.$rootScope.learningObjectDeleted = portfolio && portfolio.deleted === true
        this.$rootScope.learningObjectChanged = portfolio && portfolio.changed > 0
        this.$rootScope.learningObjectUnreviewed = portfolio && !!portfolio.unReviewed
        if (!isLocallyStored){
            this.showUnreviewedMessage(portfolio.id);
        }

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
