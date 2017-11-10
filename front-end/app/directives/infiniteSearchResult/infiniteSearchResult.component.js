'use strict'

{
class controller extends Controller {
    $onChanges({ title, subtitle, filter, params }) {
        if (title && title.currentValue != title.previousValue)
            this.setTitle()

        if (subtitle && subtitle.currentValue !== subtitle.previousValue)
            this.$scope.subtitle = subtitle.currentValue

        if (filter && filter.currentValue !== filter.previousValue)
            this.$scope.filter = filter.currentValue

        if (params && !params.isFirstChange() && this.params) {
            this.initialParams = Object.assign({}, this.params)
            this.search(true)
        }
    }
    $onInit() {
        if (!this.url)
            this.url = 'rest/search'

        this.initialParams = Object.assign({}, this.params)
        this.setParams()

        this.$scope.items = []
        this.$scope.searching = false
        this.$scope.sortOptions = [{
            option: 'MOST_LIKED',
            field: 'like_score',
            direction: 'desc'
        }, {
            option: 'ADDED_DATE_DESC',
            field: 'added',
            direction: 'desc'
        }, {
            option: 'VIEW_COUNT_DESC',
            field: 'views',
            direction: 'desc'
        }]

        this.$scope.nextPage = () => this.$timeout(this.search.bind(this))
        this.search()
    }
    setParams() {
        if (!this.params)
            this.params = {}

        this.searchCount = 0
        this.maxResults = this.params.maxResults || this.params.limit || 20
        this.expectedItemCount = this.maxResults
    }
    setTitle() {
        const t = (key) => this.$translate.instant(key)

        this.$translate.onReady().then(() =>
            this.$scope.title = this.title
                ? t(this.title)
                : this.$scope.searching
                    ? t('SEARCH_RESULTS')
                    : !this.totalResults
                        ? t('SEARCH_RESULT_NO_RESULT')
                        : this.totalResults == 1
                            ? `${t('SEARCH_RESULT_1_RESULT_PART_1')} <strong>${this.totalResults}</strong> ${t('SEARCH_RESULT_1_RESULT_PART_2')}`
                            : this.totalResults > 1
                                ? `${t('SEARCH_RESULT_PART_1')} <strong>${this.totalResults}</strong> ${t('SEARCH_RESULT_PART_2')}`
                                : ''
        )
    }
    resetSort() {
        this.params.sort = this.initialParams.sort
        this.params.sortDirection = this.initialParams.sortDirection

        this.search(true)
    }
    sort(field, direction) {
        this.params.sort = field
        this.params.sortDirection = direction

        this.search(true)
    }
    allResultsLoaded() {
        return (this.$scope.items || []).length >= this.totalResults
    }
    search(newSearch) {
        if (newSearch)
            this.setParams()

        if (this.$scope.searching || !newSearch && this.allResultsLoaded())
            return

        this.$scope.searching = true
        this.$scope.start = this.searchCount * this.maxResults

        this.params.limit = this.maxResults
        this.params.maxResults = this.maxResults
        this.params.start = this.$scope.start

        this.serverCallService.makeGet(
            this.url,
            this.params,
            this.searchSuccess.bind(this),
            this.searchFail.bind(this),
            {},
            false,
            !!this.cache
        );
    }
    searchSuccess(data) {
        if (!data || !data.items)
            return this.searchFail()

        if (data.start === 0)
            this.$scope.items.splice(0, this.$scope.items.length)

        ;[].push.apply(this.$scope.items, data.items)

        this.totalResults = data.totalResults
        this.searchCount++
        this.$scope.searching = false

        this.setTitle()
        this.searchMoreIfNecessary()
    }
    searchFail() {
        this.$scope.searching = false
        this.setTitle()
    }
    searchMoreIfNecessary() {
        this.$scope.items.length < this.expectedItemCount && !this.allResultsLoaded()
            ? this.search()
            : this.expectedItemCount += this.maxResults
    }
}
controller.$inject = [
    '$scope',
    '$element',
    '$timeout',
    '$translate',
    'serverCallService'
]
component('dopInfiniteSearchResult', {
    bindings: {
        params: '<',
        url: '<?',
        title: '<',
        subtitle: '<',
        filter: '<',
        cache: '<?'
    },
    templateUrl: 'directives/infiniteSearchResult/infiniteSearchResult.html',
    controller
})
}
