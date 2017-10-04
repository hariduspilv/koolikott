'use strict'

{
class controller extends Controller {
    $onInit() {
        // this.initialParams = { ...this.params }
        this.searchCount = 0
        this.maxResults = this.params
            ? this.params.maxResults || this.params.limit
            : null
        this.expectedItemCount = this.maxResults

        if (!this.url)
            this.url = "rest/search"

        if (!this.params)
            this.params = {}

        this.$scope.title = this.title
        this.$scope.subtitle = this.subtitle
        this.$scope.filter = this.filter
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
        this.$scope.allResultsLoaded = () => this.allResultsLoaded()
        this.$scope.sort = (field, direction) => {
            field
                ? this.params.sort = field
                : this.params.sort = this.initialParams.sort

            direction
                ? this.params.sortDirection = direction
                : this.params.sortDirection = this.initialParams.sortDirection

            this.searchCount = 0
            this.expectedItemCount = this.maxResults

            this.search(true)
        }

        this.search()
    }
    allResultsLoaded() {
        return this.$scope.items.length >= this.totalResults
    }
    search(newSearch) {
        if (this.$scope.searching || this.allResultsLoaded() && !newSearch)
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

        data.start === 0
            ? this.$scope.items = data.items
            : this.$scope.items.push.apply(this.$scope.items, data.items)

        this.totalResults = data.totalResults
        this.searchCount++
        this.$scope.searching = false

        this.searchMoreIfNecessary()
    }
    searchFail() {
        this.$scope.searching = false
    }
    searchMoreIfNecessary() {
        this.$scope.items.length < this.expectedItemCount && !this.allResultsLoaded()
            ? this.search()
            : this.expectedItemCount += this.maxResults
    }
}
controller.$inject = ['$scope', '$timeout', 'serverCallService']

angular.module('koolikottApp').component('dopInfiniteSearchResult', {
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
