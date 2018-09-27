'use strict'

{
class controller extends Controller {
    $onChanges({ title, subtitle, filter, params, exactTitle, similarTitle, description }) {
        if (title && title.currentValue !== title.previousValue) this.setTitle()
        if (exactTitle && exactTitle.currentValue !== exactTitle.previousValue) this.setPhraseTitlesExact()
        if (similarTitle && similarTitle.currentValue !== similarTitle.previousValue) this.setPhraseTitlesSimilar()
        if (subtitle && subtitle.currentValue !== subtitle.previousValue) this.$scope.subtitle = subtitle.currentValue
        if (filter && filter.currentValue !== filter.previousValue) this.$scope.filter = filter.currentValue
        if (description && description.currentValue !== description.previousValue) this.$scope.description = description.currentValue

        if (params && !params.isFirstChange() && this.params) {
            this.initialParams = Object.assign({}, this.params)
            this.search(true)
        }
    }
    $onInit() {
        if (!this.url) this.url = 'rest/search'

        this.initialParams = Object.assign({}, this.params)
        this.selectedMaxCount = 0

        $('body').materialScrollTop({ offset: 300 })
        this.$scope.items = []
        this.buildConstants();
        this.$scope.filterGroupsExact = angular.copy(this.$scope.filterGroups);
        this.distinctCount = { similar: 0, exact: 0 }
        this.$scope.nextPage = () => this.$timeout(this.search.bind(this))
        this.$rootScope.$on('logout:success', this.search.bind(this))
        this.search(true)
    }
    showExactGroupButtons() {
        return this.$scope.showFilterGroups === 'phraseGrouping' && this.$scope.filterGroupsExact['all'].countMaterial
    }
    showDefaultGroupButtons() {
        return this.$scope.showFilterGroups !== 'noGrouping' && this.$scope.filterGroups['all'].countMaterial
    }
    setParams() {
        if (!this.params) this.params = {}
        this.searchCount = 0
        this.maxResults = this.params.maxResults || this.params.limit || 20
        this.expectedItemCount = this.maxResults
    }
    setTitle() {
        this.$translate.onReady().then(() =>
            this.$scope.title = this.buildTitle(this.t(), this.title, this.totalResults, this.titleTranslations)
        )
    }
    setPhraseTitlesExact() {
        this.$translate.onReady().then(() =>
            this.$scope.exactTitle = this.buildTitle(this.t(), this.exactTitle, this.distinctCount.exact, this.phaseTitlesExact)
        )
    }
    setPhraseTitlesSimilar() {
        this.$translate.onReady().then(() =>
            this.$scope.similarTitle = this.buildTitle(this.t(), this.similarTitle, this.distinctCount.similar, this.phaseTitlesSimilar)
        )
    }
    t() {
        return (key) => this.$translate.instant(key);
    }
    buildTitle(t, title, totalResults, translations) {
        return title ? t(title) :
            this.$scope.searching ? t('SEARCH_RESULTS') :
                this.replaceTitleContent(totalResults, t, translations, this.searchService.getQuery())
    }
    setPhraseTitles() {
        this.setPhraseTitlesExact()
        this.setPhraseTitlesSimilar()
    }
    resetSort() {
        this.sortInner(this.initialParams.sort, this.initialParams.sortDirection, 'default', 'desc');
    }
    sort(field, direction) {
        this.sortInner(field, direction, field, direction);
    }
    sortInner(field, direction, sField, sDirection) {
        this.params.sort = field
        this.params.sortDirection = direction
        this.searchService.setSort(sField)
        this.searchService.setSortDirection(sDirection)
        this.$scope.sorting = true
        this.$location.url(this.searchService.getURL())
    }
    allResultsLoaded() {
        if (!this.params.isGrouped) {
            return (this.$scope.items || []).length >= this.totalResults || this.$scope.start >= this.totalResults
        } else {
            return (this.$filter('itemGroupFilter', this.$scope.items, this.$scope.filterGroups,
                this.$scope.filterGroupsExact, this.$scope.showFilterGroups) || []).length >= this.selectedMaxCount
                || this.$scope.start >= this.selectedMaxCount;
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

        this.serverCallService.makeGet(this.url, this.params).then(({data}) => {
            this.params.isGrouped ? this.groupedSearchSuccess(data) : this.searchSuccess(data)
        }, () => {this.searchFail()})
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
                this.disableAllGroupsForFilter(this.$scope.filterGroups)
                this.disableAllGroupsForFilter(this.$scope.filterGroupsExact)
            }
        }
        this.$scope.sorting = false
        this.totalResults = data.totalResults

        if (this.pickGroupView(data) !== 'phraseGrouping') {
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
            ? data.groups.hasOwnProperty('exact')
                ? 'phraseGrouping' : 'grouping'
                    : 'noGrouping'
        this.$scope.showFilterGroups = groupView
        return groupView
    }
    extractItemsFromGroups(groups, currentGroupType, currentSearchType) {
        let flatItemList = []
        Object.entries(groups).forEach(([key, content]) => {
            if (key === 'material' || key === 'portfolio') currentGroupType = key
            currentSearchType = this.detectSearchTypeAndSetCounts(key, currentSearchType, content)
            if (content.hasOwnProperty('items')) {
                this.setGroupItemsCount(currentGroupType, currentSearchType, key, content)
                this.updateItemAttributes(flatItemList, currentSearchType, key, content)
            }
            else flatItemList = flatItemList.concat(this.extractItemsFromGroups(content, currentGroupType, currentSearchType))
        })
        return flatItemList
    }
    detectSearchTypeAndSetCounts(key, currentSearchType, content) {
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
            if (searchType) {
                item['searchType'] = searchType
            }
            allItems.push(item)
        })
    }
    setGroupItemsCount(groupType, searchType, name, content) {
        if (groupType === 'material') {
            if (searchType === 'exact') {
                this.$scope.filterGroupsExact[name].countMaterial = content.totalResults
            } else {
                this.$scope.filterGroups[name].countMaterial = content.totalResults
            }
        } else if (groupType === 'portfolio') {
            if (searchType === 'exact') {
                this.$scope.filterGroupsExact[name].countPortfolio = content.totalResults
            } else {
                this.$scope.filterGroups[name].countPortfolio = content.totalResults
            }
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
    selectMaterialFilter(groupId, isExact) {
        let filterGroup = this.getFiltersByType(isExact)
        if (filterGroup[groupId].countMaterial === 0) return
        const isAllActive = filterGroup['all'].isMaterialActive
        const disableAllGroups = (groupId !== 'all' && isAllActive) || (groupId === 'all' && !isAllActive)
        this.disableAllOppositeGroups(isExact)
        if (disableAllGroups) this.disableAllGroupsForFilter(filterGroup)
        filterGroup[groupId].isMaterialActive = !filterGroup[groupId].isMaterialActive
        this.countAllInSelected()
        // this.searchService.setMaterialTitle(true)
        // this.$location.url(this.searchService.getURL())
    }
    selectPortfolioFilter(groupId, isExact) {
        let filterGroup = this.getFiltersByType(isExact)
        if (filterGroup[groupId].countPortfolio === 0) return
        const isAllActive = filterGroup['all'].isMaterialActive
        this.disableAllOppositeGroups(isExact)
        if (groupId !== 'all' && isAllActive) this.disableAllGroupsForFilter(filterGroup)
        filterGroup[groupId].isPortfolioActive = !filterGroup[groupId].isPortfolioActive
        this.countAllInSelected()
    }
    getFiltersByType(isExact) {
        return isExact ? this.$scope.filterGroupsExact : this.$scope.filterGroups
    }
    countAllInSelected() {
        this.selectedMaxCount += this.countSelected(this.$scope.filterGroups)
        if (this.$scope.showFilterGroups === 'phraseGrouping') {
            this.selectedMaxCount += this.countSelected(this.$scope.filterGroupsExact)
        }
    }
    disableAllOppositeGroups(isExact) {
        if (this.$scope.showFilterGroups !== 'phraseGrouping') return
        if (isExact) {
            this.disableAllGroupsForFilter(this.$scope.filterGroups)
        } else {
            this.disableAllGroupsForFilter(this.$scope.filterGroupsExact)
        }
    }
    isLoggedIn() {
        return this.authenticatedUserService.isAuthenticated()
    }
    buildConstants() {
        this.$scope.sortOptions = [
            {option: 'ADDED_DATE_DESC', field: 'added', direction: 'desc'},
            {option: 'ADDED_DATE_ASC', field: 'added', direction: 'asc'},
            {option: 'PORTFOLIOS_FIRST', field: 'type', direction: 'desc'},
            {option: 'MATERIALS_FIRST', field: 'type', direction: 'asc'},
        ];
        this.$scope.filterGroups = {
            title: this.filterModel('GROUPS_TITLES'),
            description: this.filterModel('GROUPS_DESCRIPTIONS'),
            tag: this.filterModel('GROUPS_TAGS'),
            author: this.filterModel('GROUPS_AUTHORS'),
            publisher: this.filterModel('GROUPS_AUTHORS'),
            all: this.filterModel('GROUPS_ALL'),
        };
        this.titleTranslations = {
            none: 'NO_RESULTS_FOUND',
            single: 'SEARCH_RESULT_1_WORD',
            multiple: 'SEARCH_RESULT_MULTIPLE_WORD',
        }
        this.phaseTitlesExact = {
            none: 'SEARCH_RESULT_NO_RESULT_EXACT_PHRASE',
            single: 'SEARCH_RESULT_1_RESULT_EXACT_PHRASE',
            multiple: 'SEARCH_RESULT_MULTIPLE_RESULT_EXACT_PHRASE',
        }
        this.phaseTitlesSimilar = {
            none: 'SEARCH_RESULT_NO_RESULT_SIMILAR_PHRASE',
            single: 'SEARCH_RESULT_1_RESULT_SIMILAR_PHRASE',
            multiple: 'SEARCH_RESULT_MULTIPLE_RESULT_SIMILAR_PHRASE',
        }
    }
}
controller.$inject = [
    '$scope',
    '$rootScope',
    '$element',
    '$timeout',
    '$translate',
    '$location',
    '$filter',
    'serverCallService',
    'searchService',
    'authenticatedUserService',
]
component('dopInfiniteSearchResult', {
    bindings: {
        params: '<',
        url: '<?',
        title: '<',
        subtitle: '<',
        description: '<',
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
