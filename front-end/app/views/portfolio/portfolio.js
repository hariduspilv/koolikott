'use strict'

{
class controller extends Controller {
    constructor(...args) {
        super(...args)
        this.$rootScope.isFullScreen = false

        this.$scope.showlogselect = false;
        this.$scope.showDeletedBanner = this.showDeletedBanner.bind(this);

        if (this.$location.url().contains('#')) {
            let splittedUrl = this.$location.url().split('#');
            this.$rootScope.slug = splittedUrl[splittedUrl.length - 1]
                    .replace('subchapter', 'alapeatukk')
                    .replace('chapter', 'peatukk')
        }

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
            if (!this.$location.url().startsWith('/kogumik/')) {
                this.setPortfolio(null)
            }
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
        this.$rootScope.$on('portfolioHistory:show', this.showPortfolioHistoryLog.bind(this));
        this.$rootScope.$on('portfolioHistory:closeLogBanner', this.closeLogBanner.bind(this));

        this.$scope.$watch('portfolio', (newValue, oldValue) => {
            if (newValue !== oldValue) {
                if (newValue && newValue.type === '.PortfolioLog' || oldValue && oldValue.type === '.PortfolioLog') {
                    this.eventService.notify('portfolio:reloadTaxonObject');
                    this.setPortfolio(this.$scope.portfolio)
                }
            }
        });

        this.showDeletedBanner();
    }

    showDeletedBanner(){
        return this.$rootScope.learningObjectDeleted && this.isOwner();
    }

    isOwner() {
        return !this.authenticatedUserService.isAuthenticated()
            ? false : this.portfolio && this.portfolio.creator
                ? this.portfolio.creator.id === this.authenticatedUserService.getUser().id : false
    }

    showPortfolioHistoryLog() {
        this.$scope.showlogselect = true;
    }

    closeLogBanner(){
        this.$scope.showlogselect = false;
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
                let url = `${this.$location.url().split('-')[0]}-${this.replaceSpacesAndCharacters(this.$scope.portfolio.title)}#${el.id}`

                if (!window.location.href.includes(title) && this.$location.path().startsWith('/kogumik')) {
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
        const id = this.$route.current.params.id.split('-')[0]
        const fail = () => {
            this.toastService.show('ERROR_PORTFOLIO_NOT_FOUND')
            window.location.replace('/404')
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
        if (portfolio) {
            this.$scope.portfolio = portfolio
            this.$rootScope.portfolio = portfolio
            this.$rootScope.tabTitle = portfolio.title;
            this.storageService.setPortfolio(portfolio)

            let locationUrl = this.$rootScope.slug ? `${this.getUrl(portfolio)}#${this.$rootScope.slug}` : this.getUrl(portfolio);
            this.$location.url(locationUrl);
            this.$scope.learningObject = portfolio;

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
                            for (let [subIdx, subEl] of entries('.subchapter', el)) {
                                subEl.id = this.getSlug(`alapeatukk-${idx + 1}-${subIdx + 1}`)
                            }
                    }, 1000)
                )
            }

            this.$scope.portfolioMetaData = this.createMetaData(portfolio);

            this.$rootScope.$broadcast('portfolioChanged')
        }
    }

    createMetaData(portfolio) {
        return [
            {
                '@context': 'http://schema.org/',
                '@type': 'CreativeWork',
                'author': {
                    '@type': 'Person',
                    'name': `${portfolio.creator.name} ${portfolio.creator.surname}`
                },
                'url': window.location.href,
                'publisher': {
                    '@type': 'Organization',
                    'name': 'e-koolikott.ee'
                },
                'audience': {
                    '@type': 'Audience',
                    'audienceType': audienceType(portfolio)
                },
                'dateCreated': this.formatDateToDayMonthYear(portfolio.added),
                'datePublished': this.formatDateToDayMonthYear(portfolio.publishedAt),
                'license': addLicense(portfolio.licenseType),
                'typicalAgeRange': portfolio.targetGroups.map(targetGroup => getTypicalAgeRange(targetGroup)),
                'interactionCount': portfolio.views,
                'headline': portfolio.title,
                'keywords': portfolio.tags,
                'text': portfolio.summary,
                'inLanguage': this.convertLanguage(this.translationService.getLanguage())
            },
            {
                '@context': 'https://schema.org',
                '@type': 'WebSite',
                'url': 'https://www.e-koolikott.ee/',
                'potentialAction': {
                    '@type': 'SearchAction',
                    'target': 'https://query.e-koolikott.ee/search?q={search_term_string}',
                    'query-input': 'required name=search_term_string'
                }
            },
            {
                '@context': 'https://schema.org',
                '@type': 'BreadcrumbList',
                'itemListElement': [{
                    '@type': 'ListItem',
                    'position': 1,
                    'name': this.$translate.instant(portfolio.taxonPositionDto[0].taxonLevelName),
                    'item': `https://e-koolikott.ee/search/result/?taxon=${portfolio.taxonPositionDto[0].taxonLevelId}`
                }, {
                    '@type': 'ListItem',
                    'position': 2,
                    'name': this.$translate.instant((`DOMAIN_${portfolio.taxonPositionDto[1].taxonLevelName}`).toUpperCase()),
                    'item': `https://e-koolikott.ee/search/result/?taxon=${portfolio.taxonPositionDto[1].taxonLevelId}`
                }]
            },
            {
                '@context': 'https://schema.org',
                '@type': 'Organization',
                'url': 'https://e-koolikott.ee',
                'logo': 'https://e-koolikott.ee/ekoolikott.png'
            }
        ];
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
    'translationService',
    'taxonGroupingService',
    '$translate'
]
angular.module('koolikottApp').controller('portfolioController', controller)
}
