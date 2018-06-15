'use strict'

{
class controller extends Controller {
    $onChanges({ title, subtitle, filter, params }) {
        if (title && title.currentValue !== title.previousValue) this.setTitle()

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
        if (!this.url) this.url = 'rest/search'

        this.initialParams = Object.assign({}, this.params)

        this.$scope.items = []
        this.$scope.shownItems = []
        this.$scope.groups = []
        this.$scope.filterGroups = {}
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
        this.createGroup('GROUPS_TITLES', 0, 0)
        this.createGroup('GROUPS_TAGS', 0, 0)
        this.createGroup('GROUPS_DESCRIPTIONS', 0, 0)
        this.createGroup('GROUPS_AUTHORS', 0, 0)
        this.createGroup('GROUPS_PUBLISHERS', 0, 0)
        this.createGroup('GROUPS_ALL', 0, 0)

        this.$scope.selectedFilters = {foundFrom: []}

        this.$scope.nextPage = () => this.$timeout(this.search.bind(this))
        this.search(true);
        this.$rootScope.$on('logout:success', this.search.bind(this));
    }
     mapGroups(groupName) {
        switch (groupName) {
            case 'author': return 'GROUPS_AUTHORS'
            case 'description': return 'GROUPS_DESCRIPTIONS'
            case 'publisher': return 'GROUPS_PUBLISHERS'
            case 'tag': return 'GROUPS_TAGS'
            case 'title': return 'GROUPS_TITLES'
        }
    }
    setParams() {
        if (!this.params) this.params = {}
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
                        : this.totalResults === 1
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
        return (this.$scope.items || []).length >= this.totalResults || this.$scope.start >= this.totalResults
    }
    search(isNewSearch) {
        if (isNewSearch) this.setParams()

        if (this.$scope.searching || !isNewSearch && this.allResultsLoaded()) return

        this.$scope.searching = true
        this.$scope.start = this.searchCount * this.maxResults

        this.$scope.isGrouped = this.params.isGrouped

        this.params.limit = this.maxResults
        this.params.maxResults = this.maxResults
        this.params.start = this.$scope.start

        this.serverCallService.makeGet(
            this.url,
            this.params,
            this.params.isGrouped ? this.groupedSearchSuccess.bind(this) : this.searchSuccess.bind(this),
            this.searchFail.bind(this),
            {},
            false,
            !!this.cache
        );
    }
    searchSuccess(data) {
        if (!data || !data.items) return this.searchFail()
        if (data.start === 0) this.$scope.items.splice(0, this.$scope.items.length)

        ;[].push.apply(this.$scope.items, data.items)

        this.totalResults = data.totalResults
        this.searchCount++
        this.$scope.searching = false

        this.setTitle()
        this.searchMoreIfNecessary()
    }
    groupedSearchSuccess(data) {
        if (!data || !data.groups || data.totalResults === 0) return this.searchFail()
        if (data.start === 0) {
            this.$scope.items.splice(0, this.$scope.items.length)
            this.disableAllGroups()
        }

        let foundItems = this.extractItemsFromGroups(data.groups)
        ;[].push.apply(this.$scope.items, foundItems)

        this.totalResults = data.totalResults
        this.searchCount++
        this.$scope.searching = false
        this.$scope.filterGroups['GROUPS_ALL'].countMaterial = this.totalResults

        this.setTitle()
        this.searchMoreIfNecessary()
    }
    extractItemsFromGroups(groups, groupType) {
        let allItems = []

        Object.entries(groups).forEach(([name, content]) => {
            if (name === 'material' || name === 'portfolio') groupType = name
            if (content.hasOwnProperty('items')) {
                const mappedName = this.mapGroups(name)
                if (groupType === 'material')
                    this.$scope.filterGroups[mappedName].countMaterial = content.totalResults
                if (groupType === 'portfolio')
                    this.$scope.filterGroups[mappedName].countPortfolio = content.totalResults

                content.items.forEach((item) => {
                    item['foundFrom'] = mappedName
                    allItems.push(item)
                })
            }
            else allItems = allItems.concat(this.extractItemsFromGroups(content, groupType))
        })
        return allItems
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
    createGroup(groupName, materialCount, portfolioCount) {
        this.$scope.filterGroups[groupName] = {
            countMaterial: materialCount,
            countPortfolio: portfolioCount,
            isMaterialActive: false,
            isPortfolioActive: false
        }
    }
    selectMaterialGroup(groupName) {
        const areAllActive = this.$scope.filterGroups['GROUPS_ALL'].isMaterialActive
        if (groupName === 'GROUPS_ALL' && !areAllActive) this.disableAllGroups()
        if (groupName !== 'GROUPS_ALL' && areAllActive) this.disableAllGroups()
        this.$scope.filterGroups[groupName].isMaterialActive = !this.$scope.filterGroups[groupName].isMaterialActive
    }
    selectPortfolioGroup(groupName) {
        const areAllActive = this.$scope.filterGroups['GROUPS_ALL'].isMaterialActive
        if (groupName !== 'GROUPS_ALL' && areAllActive) this.disableAllGroups()
        this.$scope.filterGroups[groupName].isPortfolioActive = !this.$scope.filterGroups[groupName].isPortfolioActive
    }
    disableAllGroups() {
        Object.entries(this.$scope.filterGroups).forEach(([name, content]) => {
            content.isMaterialActive = false
            content.isPortfolioActive = false
        })
    }
}
controller.$inject = [
    '$scope',
    '$rootScope',
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
        cache: '<?',
        isPreferred: '<',
    },
    templateUrl: 'directives/infiniteSearchResult/infiniteSearchResult.html',
    controller
})
}
