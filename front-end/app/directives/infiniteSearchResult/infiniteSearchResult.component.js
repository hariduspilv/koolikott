'use strict'

{
class controller extends Controller {
    $onChanges({ title, subtitle, filter, params, exactTitle, similarTitle }) {
        if (title && title.currentValue !== title.previousValue) this.setTitle()
        if (exactTitle && exactTitle.currentValue !== exactTitle.previousValue) this.setPhraseTitlesExact()
        if (similarTitle && similarTitle.currentValue !== similarTitle.previousValue) this.setPhraseTitlesSimilar()
        if (subtitle && subtitle.currentValue !== subtitle.previousValue) this.$scope.subtitle = subtitle.currentValue
        if (filter && filter.currentValue !== filter.previousValue) this.$scope.filter = filter.currentValue

        if (params && !params.isFirstChange() && this.params) {
            this.initialParams = Object.assign({}, this.params)
            this.search(true)
        }
    }
    $onInit() {
        if (!this.url) this.url = 'rest/search'

        this.initialParams = Object.assign({}, this.params)
        this.selectedCount = 0

        $('body').materialScrollTop()
        this.$scope.items = []
        this.$scope.sortOptions = []
        this.$scope.filterGroups = {}
        this.$scope.filterGroupsExact = {}
        this.$scope.searching = false
        this.$scope.sorting = false
        this.distinctCount = {
            similar: 0,
            exact: 0
        }
        this.createMultipleSortOptions(
            ['ADDED_DATE_DESC', 'added', 'desc'],
            ['ADDED_DATE_ASC', 'added', 'asc'],
            ['PORTFOLIOS_FIRST', 'type', 'desc'],
            ['MATERIALS_FIRST', 'type', 'asc'],
        )
        this.createMultipleFilterGroups(
            ['GROUPS_TITLES', 'title'],
            ['GROUPS_DESCRIPTIONS', 'description'],
            ['GROUPS_TAGS', 'tag'],
            ['GROUPS_AUTHORS', 'author'],
            ['GROUPS_PUBLISHERS', 'publisher'],
            ['GROUPS_ALL', 'all'],
        )
        this.$scope.nextPage = () => this.$timeout(this.search.bind(this))
        this.search(true)
        this.$rootScope.$on('logout:success', this.search.bind(this))
    }
    showExactGroupButtons() {
        return this.$scope.showFilterGroups === 'phraseGrouping' && this.$scope.filterGroupsExact['all'].countMaterial
    }
    showDefaultGroupButtons() {
        return this.$scope.showFilterGroups !== 'noGrouping' && this.$scope.filterGroups['all'].countMaterial
    }
    createMultipleSortOptions(...options) {
        options.forEach((option) =>
            this.$scope.sortOptions.push(controller.createSortOption(option[0], option[1], option[2]))
        )
    }
    static createSortOption(optionKey, fieldValue, sortDirection) {
        return {
            option: optionKey,
            field: fieldValue,
            direction: sortDirection,
        }
    }
    createMultipleFilterGroups(...groups) {
        groups.forEach((group) => {
            this.createSingleFilterGroup(group[0], group[1])
        })
    }
    createSingleFilterGroup(groupName, groupId) {
        this.$scope.filterGroups[groupId] = controller.createFilterGroupModel(groupName)
        this.$scope.filterGroupsExact[groupId] = controller.createFilterGroupModel(groupName)
    }
    static createFilterGroupModel(groupName) {
        return {
            name: groupName,
            countMaterial: 0,
            countPortfolio: 0,
            isMaterialActive: false,
            isPortfolioActive: false
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
        const translationsKeys = {
            noResults: 'NO_RESULTS_FOUND',
            singleResult: 'SEARCH_RESULT_1_WORD',
            multipleResults: 'SEARCH_RESULT_MULTIPLE_WORD',
        }
        this.$translate.onReady().then(() => this.$scope.title = this.buildTitle(
            t, this.title, this.totalResults, translationsKeys
        ))
    }
    buildTitle(t, title, totalResults, translationsKeys) {
        const query = this.searchService.getQuery()
        let newTitle = ''
        if (!totalResults) newTitle = t(translationsKeys.noResults).replace('${query}', this.searchService.getQuery())
        else if (totalResults === 1) {
            newTitle = t(translationsKeys.singleResult)
            newTitle = query ? newTitle.replace('${query}', query) : newTitle.replace('\"${query}\"', '')
        }
        else if (totalResults > 1) {
            newTitle = t(translationsKeys.multipleResults)
            newTitle = query ? newTitle.replace('${count}', totalResults).replace('${query}', query)
                : newTitle.replace('${count}', totalResults).replace('${query}', '').replace(/"/g, '')
        }
        return title ? t(title) : this.$scope.searching ? t('SEARCH_RESULTS') : newTitle
    }
    setPhraseTitlesExact() {
        const t = (key) => this.$translate.instant(key)
        const translationsKeys = {
            noResults: 'SEARCH_RESULT_NO_RESULT_EXACT_PHRASE',
            singleResult: 'SEARCH_RESULT_1_RESULT_EXACT_PHRASE',
            multipleResults: 'SEARCH_RESULT_MULTIPLE_RESULT_EXACT_PHRASE',
        }
        this.$translate.onReady().then(() =>
            this.$scope.exactTitle = this.buildTitle(
                t, this.exactTitle, this.distinctCount.exact, translationsKeys
            ))
    }
    setPhraseTitlesSimilar() {
        const t = (key) => this.$translate.instant(key)
        const translationsKeys = {
            noResults: 'SEARCH_RESULT_NO_RESULT_SIMILAR_PHRASE',
            singleResult: 'SEARCH_RESULT_1_RESULT_SIMILAR_PHRASE',
            multipleResults: 'SEARCH_RESULT_MULTIPLE_RESULT_SIMILAR_PHRASE',
        }
        this.$translate.onReady().then(() =>
            this.$scope.similarTitle = this.buildTitle(
                t, this.similarTitle, this.distinctCount.similar, translationsKeys
            ))
    }
    setPhraseTitles() {
        this.setPhraseTitlesExact()
        this.setPhraseTitlesSimilar()
    }
    resetSort() {
        this.params.sort = this.initialParams.sort
        this.params.sortDirection = this.initialParams.sortDirection
        this.$scope.sorting = true
        this.searchService.setSort('default')
        this.searchService.setSortDirection('desc')
        // this.search(true)
        this.$location.url(this.searchService.getURL())
    }
    sort(field, direction) {
        this.params.sort = field
        this.searchService.setSort(field)
        this.params.sortDirection = direction
        this.searchService.setSortDirection(direction)
        this.$scope.sorting = true
        // this.search(true)
        this.$location.url(this.searchService.getURL())
    }
    allResultsLoaded() {
        if (!this.params.isGrouped) {
            return (this.$scope.items || []).length >= this.totalResults || this.$scope.start >= this.totalResults
        } else {
            return (this.$scope.items || []).length >= this.selectedCount || this.$scope.start >= this.selectedCount;
        }
    }
    search(isNewSearch) {
        if (isNewSearch) this.setParams()
        if (this.$scope.searching || !isNewSearch && this.allResultsLoaded()) return

        this.$scope.searching = true
        this.$scope.start = this.searchCount * this.maxResults

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
        )
    }
    searchSuccess(data) {
        if (!data || !data.items) return this.searchFail()
        if (data.start === 0) this.$scope.items.splice(0, this.$scope.items.length)
        this.$scope.showFilterGroups = 'noGrouping'
        this.$scope.sorting = false

        ;[].push.apply(this.$scope.items, data.items)

        this.totalResults = data.totalResults
        this.searchCount++
        this.$scope.searching = false

        this.setTitle()
        this.searchMoreIfNecessary()
    }
    groupedSearchSuccess(data) {
        if (!data || !data.groups) return this.searchFail()
        if (data.start === 0) {
            this.$scope.items.splice(0, this.$scope.items.length)
            if (!this.$scope.sorting) {
                controller.disableAllGroupsForFilter(this.$scope.filterGroups)
                controller.disableAllGroupsForFilter(this.$scope.filterGroupsExact)
            }
        }
        this.$scope.sorting = false

        const groupsView = this.pickGroupView(data)
        this.totalResults = data.totalResults
        if (groupsView !== 'phraseGrouping') {
            this.$scope.filterGroups['all'].countMaterial = data.totalResults
            this.totalResults = data.distinctIdCount
        }

        let foundItems = this.extractItemsFromGroups(data.groups);
        [].push.apply(this.$scope.items, foundItems)
        if (this.params.isGrouped) {
            this.$scope.items = this.$scope.items.sort((a, b) => {
                return a.orderNr - b.orderNr;
            })
        }

        this.searchCount++
        this.$scope.searching = false

        this.$scope.showFilterGroups === 'phraseGrouping' ? this.setPhraseTitles() : this.setTitle()
        this.searchMoreIfNecessary()
    }
    pickGroupView(data) {
        const groupView = data.totalResults !== 0
            ? data.groups.hasOwnProperty('exact') ? 'phraseGrouping' : 'grouping'
            : 'noGrouping'
        this.$scope.showFilterGroups = groupView
        return groupView
    }
    extractItemsFromGroups(groups, currentGroupType, currentSearchType) {
        let flatItemList = []
        Object.entries(groups).forEach(([key, content]) => {
            if (key === 'material' || key === 'portfolio') currentGroupType = key
            currentSearchType = this.detectSearchType(key, currentSearchType, content)
            if (content.hasOwnProperty('items')) {
                this.setGroupItemsCount(currentGroupType, currentSearchType, key, content)
                this.updateItemAttributes(flatItemList, currentSearchType, key, content)
            }
            else flatItemList = flatItemList.concat(this.extractItemsFromGroups(content, currentGroupType, currentSearchType))
        })
        return flatItemList
    }
    detectSearchType(key, currentSearchType, content) {
        if (key === 'exact' || key === 'similar') {
            currentSearchType = key
            if (key === 'exact') {
                this.$scope.filterGroupsExact['all'].countMaterial = content.totalResults
                this.distinctCount.exact = content.distinctIdCount
            }
            else {
                this.$scope.filterGroups['all'].countMaterial = content.totalResults
                this.distinctCount.similar = content.distinctIdCount
            }
        }
        return currentSearchType
    }
    updateItemAttributes(allItems, searchType, groupName, content) {
        content.items.forEach((item) => {
            item['foundFrom'] = groupName
            if (searchType) item['searchType'] = searchType
            allItems.push(item)
        })
    }
    setGroupItemsCount(groupType, searchType, name, content) {
        if (groupType === 'material') {
            if (searchType === 'exact') this.$scope.filterGroupsExact[name].countMaterial = content.totalResults
            else this.$scope.filterGroups[name].countMaterial = content.totalResults
        }
        if (groupType === 'portfolio') {
            if (searchType === 'exact') this.$scope.filterGroupsExact[name].countPortfolio = content.totalResults
            else this.$scope.filterGroups[name].countPortfolio = content.totalResults
        }
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
    selectMaterialGroup(groupId, isExact) {
        let searchGroupType = isExact ? this.$scope.filterGroupsExact : this.$scope.filterGroups
        if (searchGroupType[groupId].countMaterial === 0) return
        const isAllActive = searchGroupType['all'].isMaterialActive
        this.disableAllOppositeGroups(isExact)
        if (groupId === 'all' && !isAllActive) controller.disableAllGroupsForFilter(searchGroupType)
        if (groupId !== 'all' && isAllActive) controller.disableAllGroupsForFilter(searchGroupType)
        searchGroupType[groupId].isMaterialActive = !searchGroupType[groupId].isMaterialActive
        this.countAllInSelected()
    }
    selectPortfolioGroup(groupId, isExact) {
        let searchGroupType = isExact ? this.$scope.filterGroupsExact : this.$scope.filterGroups
        if (searchGroupType[groupId].countPortfolio === 0) return
        const isAllActive = searchGroupType['all'].isMaterialActive
        this.disableAllOppositeGroups(isExact)
        if (groupId !== 'all' && isAllActive) controller.disableAllGroupsForFilter(searchGroupType)
        searchGroupType[groupId].isPortfolioActive = !searchGroupType[groupId].isPortfolioActive
        this.countAllInSelected()
    }
    countAllInSelected() {
        this.selectedCount = 0
        this.countSelected(this.$scope.filterGroups)
        if (this.$scope.showFilterGroups === 'phraseGrouping') this.countSelected(this.$scope.filterGroupsExact)
    }
    countSelected(filterGroup) {
        Object.entries(filterGroup).forEach(([name, content]) => {
            if (content.isMaterialActive) this.selectedCount = this.selectedCount + content.countMaterial
            if (content.isPortfolioActive) this.selectedCount = this.selectedCount + content.countPortfolio
        })
    }
    static disableAllGroupsForFilter(filter) {
        Object.entries(filter).forEach(([name, content]) => {
            content.isMaterialActive = false
            content.isPortfolioActive = false
        })
    }
    disableAllOppositeGroups(isExact) {
        if (this.$scope.showFilterGroups !== 'phraseGrouping') return
        if (isExact) controller.disableAllGroupsForFilter(this.$scope.filterGroups)
        else controller.disableAllGroupsForFilter(this.$scope.filterGroupsExact)
    }
    isLoggedIn() {
        return this.authenticatedUserService.isAuthenticated()
    }
}
controller.$inject = [
    '$scope',
    '$rootScope',
    '$element',
    '$timeout',
    '$translate',
    '$location',
    'serverCallService',
    'searchService',
    'sortService',
    'authenticatedUserService'
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
        exactTitle: '<',
        similarTitle: '<',
    },
    templateUrl: 'directives/infiniteSearchResult/infiniteSearchResult.html',
    controller
})
}
