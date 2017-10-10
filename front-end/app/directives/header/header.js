'use strict'

{
const VISIBILITY_PUBLIC = 'PUBLIC'
const VISIBILITY_PRIVATE = 'PRIVATE'
const VISIBILITY_NOT_LISTED = 'NOT_LISTED'
const SEARCH_DELAY = 1000

class controller extends Controller {
    $onInit() {
        this.dontSearch = false

        this.$scope.detailedSearch = {}
        this.$scope.detailedSearch.isVisible = false
        this.$scope.mobileSearch = {}
        this.$scope.mobileSearch.isVisible = false
        this.$scope.showLanguageSelection = false
        this.$scope.selectedLanguage = this.translationService.getLanguage()
        this.$scope.searchFields = {}
        this.$scope.searchFields.searchQuery = this.searchService.getQuery()
        this.$scope.detailedSearch = {}
        this.$scope.suggest = {}
        this.$scope.suggest.suggestions = null
        this.$scope.suggest.selectedItem = null
        this.$scope.canShowTour = false
        this.$scope.isMobileView = false
        this.$scope.isEditPortfolioMode = this.$rootScope.isEditPortfolioMode

        this.$scope.detailedSearch.accessor = {
            clearSimpleSearch: () => this.$scope.searchFields.searchQuery = ''
        }

        this.$scope.setLanguage = this.setLanguage.bind(this)

        this.$scope.logout = () => {
            this.authenticationService.logout()
            this.$rootScope.$broadcast('tour:close')
            this.$location.url('/')
        }

        this.$scope.showLogin = (targetEvent) =>
            this.$mdDialog.show({
                templateUrl: 'views/loginDialog/loginDialog.html',
                controller: 'loginDialogController',
                targetEvent
            })

        this.search = this.search.bind(this)
        this.$scope.search = this.search

        this.$scope.openDetailedSearch = () => {
            this.$scope.detailedSearch.isVisible = true
            this.$scope.detailedSearch.queryIn = this.$scope.searchFields.searchQuery
            this.$scope.$broadcast("detailedSearch:open")
        }

        this.closeDetailedSearch = this.closeDetailedSearch.bind(this)
        this.$scope.closeDetailedSearch = this.closeDetailedSearch

        this.openMobileSearch = this.openMobileSearch.bind(this)
        this.$scope.openMobileSearch = this.openMobileSearch

        this.$scope.closeMobileSearch = (emptySearch) => {
            this.$scope.mobileSearch.isVisible = false

            if (emptySearch)
                this.$scope.detailedSearch.accessor.clearSimpleSearch()
        }

        this.$scope.$on('detailedSearch:open', () => this.$scope.detailedSearch.isVisible = true)
        this.$scope.$on('detailedSearch:close', () => this.$scope.detailedSearch.isVisible = false)
        this.$scope.$on('detailedSearch:empty', this.closeDetailedSearch)
        this.$scope.$on('mobileSearch:open', this.openMobileSearch)

        this.$scope.suggest.doSuggest = (query) => {
            if (query == null)
                return []

            this.$scope.suggest.suggestions = suggestService.suggest(query, suggestService.getSuggestURLbase())
            
            if (this.$scope.doInlineSuggestion && this.$scope.suggest.suggestions)
                this.$scope.suggest.suggestions.then(data => {
                    const firstSuggestion = data[0]

                    if (!firstSuggestion)
                        return this.clearInlineSuggestion()

                    const searchTextLength = this.$scope.searchFields.searchQuery.length

                    this.$scope.hiddenInline = firstSuggestion.substring(0, searchTextLength)
                    this.$scope.inlineSuggestion = firstSuggestion.substring(searchTextLength)
                })

            return this.$scope.suggest.suggestions
        }

        this.$scope.clearInlineSuggestion = this.clearInlineSuggestion.bind(this)

        this.$scope.keyPressed = (evt) => {
            switch(evt.keyCode) {
                case 8: // backspace
                    if (this.$scope.inlineSuggestion)
                        evt.preventDefault()

                    this.$scope.doInlineSuggestion = false
                    break
                case 13: // enter
                    if (!this.$location.url().startsWith('/' + this.searchService.getSearchURLbase()))
                        this.processSearchQuery(this.$scope.searchFields.searchQuery)

                    angular.element(document.querySelector('#header-search-input')).controller('mdAutocomplete').hidden = true
                    document.getElementById('header-search-input').blur()
                    this.$scope.doInlineSuggestion = false
                    break
                default:
                    this.$scope.doInlineSuggestion = true
            }
            this.clearInlineSuggestion()
        }

        this.$scope.clickOutside = () => {
            if (this.$rootScope.dontCloseSearch)
                this.$rootScope.dontCloseSearch = false
        }

        this.$scope.$watch('detailedSearch.mainField', (newValue, oldValue) => {
            if (newValue != oldValue)
                this.$scope.searchFields.searchQuery = newValue || ''
        }, true)

        this.$scope.$watch('searchFields.searchQuery', this.processSearchQuery.bind(this), true)

        this.$scope.$watch(() => this.authenticatedUserService.getUser(), user => {
            this.$scope.user = user
        }, true)

        this.$scope.$watch(() => this.searchService.getQuery(), (query) => {
            // Search query is not updated from search service while detailed search is open
            if (!query || !this.$scope.detailedSearch.isVisible)
                this.$scope.searchFields.searchQuery = query
        }, true)

        this.$scope.$watch(() => this.translationService.getLanguage(), (language) => {
            this.setLanguage(language)
        }, true)

        this.$scope.getShareUrl = this.$location.protocol()+'://'+this.$location.host()+'/portfolio?id='+this.$location.search().id

        this.$scope.makePublic = () => {
            this.storageService.getPortfolio().visibility = VISIBILITY_PUBLIC
            this.updatePortfolio()
            this.toastService.show('PORTFOLIO_HAS_BEEN_MADE_PUBLIC')
        }

        this.$scope.makeNotListed = () => {
            this.storageService.getPortfolio().visibility = VISIBILITY_NOT_LISTED
            this.updatePortfolio()
        }

        this.$scope.makePrivate = () => {
            this.storageService.getPortfolio().visibility = VISIBILITY_PRIVATE
            this.updatePortfolio()
        }

        this.$scope.clearTaxonSelector = () => {
            this.$rootScope.$broadcast('taxonSelector:clear', null)
        }

        this.$scope.saveAndExitPortfolio = () => {
            if (this.storageService.getPortfolio().visibility === VISIBILITY_PUBLIC)
                return this.saveAndExit()

            this.dialogService.showConfirmationDialog(
                "{{'PORTFOLIO_MAKE_PUBLIC' | translate}}",
                "{{'PORTFOLIO_WARNING' | translate}}",
                "{{'PORTFOLIO_YES' | translate}}",
                "{{'PORTFOLIO_NO' | translate}}",
                () => {
                    this.storageService.getPortfolio().visibility = VISIBILITY_PUBLIC
                    this.saveAndExit()
                },
                this.saveAndExit.bind(this)
            )
        }

        this.$scope.$watch(() => this.$location.path(), (params) => {
            if (params.indexOf('/portfolio') > -1 || params.indexOf('/material') > -1)
                this.$scope.detailedSearch.isVisible = false
        })

        this.$scope.$watch(() => [this.$location.url(), this.$rootScope.isEditPortfolioMode], () => {
            this.$scope.isEditModeAndNotEditView = (
                this.$rootScope.isEditPortfolioMode &&
                this.$location.url().indexOf('/portfolio/edit') !== 0
            )
        }, true)

        this.$rootScope.$watch('isEditPortfolioMode', (newValue) => {
            this.$scope.isEditPortfolioMode = newValue
        })

        this.$scope.getTranslation = (string) => this.$translate.instant(string)

        this.$scope.isHeaderRed = () => {
            if ((this.authenticatedUserService.isAdmin() || this.authenticatedUserService.isModerator()) && (
                    this.$scope.isViewAdminPanelPage || (
                        this.$scope.isViewMaterialOrPortfolioPage && (
                            this.$scope.learningObjectImproper ||
                            this.$scope.learningObjectBroken ||
                            this.$scope.learningObjectChanged ||
                            this.$scope.learningObjectUnreviewed ||
                            this.$scope.learningObjectDeleted
                        )
                    )
                )
            ) {
                this.$rootScope.$broadcast('header:red')
                return true
            } else {
                this.$rootScope.$broadcast('header:default')
                return false
            }
        }

        this.$scope.getPortfolioVisibility = () => (this.storageService.getPortfolio() || {}).visibility

        this.$scope.openTour = (isEditPage = false) =>
            this.$rootScope.$broadcast(isEditPage ? 'tour:start:editPage' : 'tour:start')
    }
    setLanguage(language) {
        const shouldReload = this.$scope.selectedLanguage !== language

        this.translationService.setLanguage(language)
        this.$scope.selectedLanguage = language

        if (shouldReload)
            window.location.reload()
    }
    search() {
        this.searchService.setSearch(this.$scope.searchFields.searchQuery)
        this.searchService.clearFieldsNotInSimpleSearch()
        this.searchService.setType(this.$rootScope.isEditPortfolioMode ? 'material' : 'all')
        this.$location.url(this.searchService.getURL())
    }
    closeDetailedSearch() {
        this.$timeout(() => {
            if (!this.$rootScope.isEditPortfolioMode) {
                this.$scope.clearTaxonSelector()
                this.$scope.detailedSearch.accessor.clear()
            }
        }, 500)
        this.dontSearch = true
        this.$scope.detailedSearch.isVisible = false
        this.$scope.detailedSearch.queryIn = ''
    }
    openMobileSearch() {
        this.$scope.mobileSearch.isVisible = true
        this.$timeout(() =>
            document.getElementById('header-simple-search-input').focus()
        )
    }
    clearInlineSuggestion() {
        this.$scope.hiddenInline = ''
        this.$scope.inlineSuggestion = ''
    }
    processSearchQuery(newValue, oldValue) {
        if (newValue != oldValue && !newValue)
            this.clearInlineSuggestion()

        this.$scope.searchFields.searchQuery = newValue || ''

        if (newValue !== oldValue && !this.$scope.detailedSearch.isVisible && (!this.dontSearch || newValue))
            this.$timeout(this.search, SEARCH_DELAY)
        else
        if (this.$scope.detailedSearch.isVisible)
            this.$scope.detailedSearch.queryIn = this.$scope.searchFields.searchQuery

        if (this.dontSearch)
            this.dontSearch = false
    }
    updatePortfolio() {
        this.serverCallService.makePost('rest/portfolio/update', this.storageService.getPortfolio())
    }
    saveAndExit() {
        this.serverCallService
            .makePost('rest/portfolio/update', this.storageService.getPortfolio())
            .then(({ data: portfolio }) => {
                if (portfolio) {
                    this.toastService.show('PORTFOLIO_SAVED')
                    this.storageService.setPortfolio(null)
                    this.$location.url('/portfolio?id=' + portfolio.id)
                    this.dontSearch = true // otherwise reload will trigger search if search has values
                    this.$route.reload()
                }
            })
    }
}
controller.$inject = [
    '$scope',
    '$location',
    '$rootScope',
    '$timeout',
    '$mdDialog',
    '$route',
    '$translate',
    'translationService',
    'searchService',
    'authenticationService',
    'authenticatedUserService',
    'suggestService',
    'serverCallService',
    'toastService',
    'storageService',
    'dialogService'
]

angular.module('koolikottApp').directive('dopHeader', () => ({
    scope: true,
    templateUrl: 'directives/header/header.html',
    link($scope, $element, $attr, $ctrl) {
        let scrollTimer,
            detailedSearch = document.getElementById('detailedSearch'),
            $detailedSearch = angular.element(detailedSearch),
            $header = document.getElementById('md-toolbar-header'),
            isSuggestVisible = false

        const checkWindowWidth = () => {
            if ($ctrl.isMobile())
                $scope.isMobileView = true

            if (window.innerWidth >= BREAK_SM)
                $scope.canShowTour = true
        }

        checkWindowWidth()
        window.addEventListener('resize', checkWindowWidth)

        window.addEventListener('scroll', () => {
            clearTimeout(scrollTimer)
            scrollTimer = setTimeout(() => {
                if (
                    !document.querySelectorAll('.md-menu-backdrop, .md-select-backdrop').length &&
                    detailedSearch.getAttribute('aria-hidden') &&
                    !isSuggestVisible
                )
                    window.pageYOffset >= $header.offsetHeight && window.innerWidth >= BREAK_SM
                        ? $detailedSearch.addClass('md-toolbar-filter--fixed')
                        : $detailedSearch.removeClass('md-toolbar-filter--fixed')
            }, 200)
        })

        setTimeout(() => {
            const headerInput = document.getElementById('header-search-input')

            headerInput.addEventListener('focus', () => isSuggestVisible = headerInput.value != '')
            headerInput.addEventListener('blur', () => isSuggestVisible = false)
        })
    },
    controller
}))
}
