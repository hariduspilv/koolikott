'use strict'

{
class controller extends Controller {
    $onDestroy(){
        this.clearAllSearchOptions()
        this.searchService.setIsExact('');
        this.searchService.setDetails('');
    }

    $onChanges({ title, subtitle, filter, params, exactTitle, similarTitle, description }) {
        if (title && title.currentValue !== title.previousValue) this.setTitle()
        if (exactTitle && exactTitle.currentValue !== exactTitle.previousValue) this.setPhraseTitleExact()
        if (similarTitle && similarTitle.currentValue !== similarTitle.previousValue) this.setPhraseTitleSimilar()
        if (subtitle && subtitle.currentValue !== subtitle.previousValue) this.$scope.subtitle = subtitle.currentValue
        if (filter && filter.currentValue !== filter.previousValue) this.$scope.filter = filter.currentValue
        if (description && description.currentValue !== description.previousValue) this.$scope.description = description.currentValue

        if (params && !params.isFirstChange() && this.params) {
            const c = params.currentValue;
            const p = params.previousValue;
            if (   !this.equals(c.q, p.q)
                || !this.arrayEquals(c.taxon, p.taxon)
                || !this.equals(c.paid, p.paid)
                || !this.equals(c.type, p.type)
                || !this.equals(c.language, p.language)
                || !this.arrayEquals(c.targetGroup, p.targetGroup)
                || !this.equals(c.resourceType, p.resourceType)
                || !this.equals(c.curriculumLiterature, p.curriculumLiterature)
                || !this.equals(c.specialEducation, p.specialEducation)
                || !this.equals(c.issuedFrom, p.issuedFrom)
                || !this.equals(c.crossCurricularTheme, p.crossCurricularTheme)
                || !this.equals(c.keyCompetence, p.keyCompetence)
                || !this.equals(c.favorites, p.favorites)
                || !this.equals(c.recommended, p.recommended)
                || !this.equals(c.isGrouped, p.isGrouped)) {
                this.clearAllSearchOptions()
                this.searchService.setIsExact('');
                this.searchService.setDetails('');
                this.$location.url(this.searchService.getURL())
                this.initialParams = Object.assign({}, this.params)
                this.search(true)
            } else if (!this.equals(c.sort, p.sort)
                    || !this.equals(c.sortDirection, p.sortDirection)) {
                this.initialParams = Object.assign({}, this.params)
                this.search(true)
            }
        }
    }
    $onInit() {
        if (!this.url) this.url = 'rest/search'

        this.initialParams = Object.assign({}, this.params)
        this.buildConstants();
        this.$scope.nextPage = () => this.$timeout(this.search.bind(this))
        this.search(true)
        this.$scope.isEditMode = false
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
            this.$scope.title = this.buildTitle(this.title, this.totalResults, this.titleTranslations)
        )
    }
    setPhraseTitleExact() {
        this.$translate.onReady().then(() =>
            this.$scope.exactTitle = this.buildTitle(this.exactTitle, this.distinctCount.exact, this.phaseTitlesExact)
        )
    }
    setPhraseTitleSimilar() {
        this.$translate.onReady().then(() =>
            this.$scope.similarTitle = this.buildTitle(this.similarTitle, this.distinctCount.similar, this.phaseTitlesSimilar)
        )
    }
    buildTitle(title, results, translations) {
        let t = (key) => this.$translate.instant(key);
        return title ? t(title) :
            this.$scope.searching ? t('SEARCH_RESULTS') :
                this.replaceTitleContent(results, t, this.searchService.getQuery(), translations)
    }
    setPhraseTitles() {
        this.setPhraseTitleExact()
        this.setPhraseTitleSimilar()
    }
    resetSort() {
        this.sortInner();
    }
    sort(field, direction) {
        this.sortInner(field, direction, field, direction);
    }
    sortInner(field = this.initialParams.sort, direction = this.initialParams.sortDirection, sField = 'default', sDirection = 'desc') {
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

        for (const param in this.params) {
            if (this.params.hasOwnProperty(param)) {
                if (param === 'all'){
                    this.setActive(param, '', "countMaterial", "isMaterialActive");
                } else if (param.startsWith('material')) {
                    this.setActive(param, 'material', "countMaterial", "isMaterialActive");
                } else if (param.startsWith('portfolio')) {
                    this.setActive(param, 'portfolio', "countPortfolio", "isPortfolioActive");
                } else if (param === 'isExact'){
                    this.searchService.search[param] = "true";
                } else if (param === 'details'){
                    this.searchService.search[param] = "true";
                }
            }
        }
    }

    setActive(param, loType, countType, activeType) {
        const lowerCase = !loType ? param : param.substr(loType.length).toLowerCase();
        this.searchService.search[param] = "true";
        const element = this.$scope[this.params.isExact ? "filterGroupsExact" : "filterGroups"][lowerCase];
        if (element[countType] > 0) {
            element[activeType] = true
        }
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
            if (key === 'material' || key === 'portfolio') {
                currentGroupType = key
            }
            if (key === 'exact' || key === 'similar') {
                currentSearchType = key
            }
            this.setCounts(key, content)
            if (content.hasOwnProperty('items')) {
                this.setGroupItemsCount(currentGroupType, currentSearchType, key, content)
                this.updateItemAttributes(flatItemList, currentSearchType, key, content)
            } else {
                flatItemList = flatItemList.concat(this.extractItemsFromGroups(content, currentGroupType, currentSearchType))
            }
        })
        return flatItemList
    }
    setCounts(key, content) {
        if (key === 'exact') {
            this.$scope.filterGroupsExact['all'].countMaterial = content.totalResults
            this.distinctCount.exact = content.distinctIdCount
        } else if (key === 'similar') {
            this.$scope.filterGroups['all'].countMaterial = content.totalResults
            this.distinctCount.similar = content.distinctIdCount
        }
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
            ? this.search() : this.expectedItemCount += this.maxResults
    }
    selectMaterialFilter(groupId, isExact) {
        let filterGroup = this.getFiltersByType(isExact)
        if (filterGroup[groupId].countMaterial === 0) {
            return
        }
        this.unselectPreviousFilters(filterGroup, groupId, isExact);
        this.flipState(filterGroup, groupId, isExact, "material", "isMaterialActive");
    }

    selectPortfolioFilter(groupId, isExact) {
        let filterGroup = this.getFiltersByType(isExact)
        if (filterGroup[groupId].countPortfolio === 0) {
            return
        }
        this.unselectPreviousFilters(filterGroup, groupId, isExact);
        this.disableAllOppositeGroups(isExact)

        this.flipState(filterGroup, groupId, isExact, "portfolio", "isPortfolioActive");
    }

    unselectPreviousFilters(filterGroup, groupId, isExact) {
        if (this.allIsOrWas(filterGroup, groupId)) {
            this.clearAllSearchOptions()
            this.clearAllFilters();
        } else {
            if (!this.equals(this.searchService.isExact(), isExact)) {
                this.clearAllSearchOptions()
            }
            this.disableAllOppositeGroups(isExact)
        }
    }

    clearAllFilters() {
        this.disableAllGroupsForFilter(this.$scope.filterGroups)
        this.disableAllGroupsForFilter(this.$scope.filterGroupsExact)
    }

    flipState(filterGroup, groupId, isExact, loType, isActiveProp) {
        filterGroup[groupId][isActiveProp] = !filterGroup[groupId][isActiveProp]
        this.countAllInSelected()
        if (groupId === 'all'){
            this.searchService.setAll(filterGroup[groupId][isActiveProp] ? "true" : '');
        } else {
            this.searchService.search[loType + this.toTitleCase(groupId)] = filterGroup[groupId][isActiveProp] ? "true" : '';
        }
        this.searchService.setIsExact(isExact);
        this.searchService.setDetails(this.selectedMaxCount > 0 ? "true" : "");
        this.$location.url(this.searchService.getURL())
    }

    clearAllSearchOptions() {
        Object.entries(this.$scope.filterGroups).forEach(([name, content]) => {
            this.searchService.search["material" + this.toTitleCase(name)] = '';
            this.searchService.search["portfolio" + this.toTitleCase(name)] = '';
        })
        this.searchService.setAll('');
    }

    getFiltersByType(isExact) {
        return isExact ? this.$scope.filterGroupsExact : this.$scope.filterGroups
    }
    countAllInSelected() {
        this.selectedMaxCount = 0;
        this.selectedMaxCount += this.countSelected(this.$scope.filterGroups)
        if (this.$scope.showFilterGroups === 'phraseGrouping') {
            this.selectedMaxCount += this.countSelected(this.$scope.filterGroupsExact)
        }
    }
    disableAllOppositeGroups(isExact) {
        if (this.$scope.showFilterGroups === 'phraseGrouping') {
            this.disableAllGroupsForFilter(isExact ? this.$scope.filterGroups : this.$scope.filterGroupsExact)
        }
    }
    isLoggedIn() {
        return this.authenticatedUserService.isAuthenticated()
    }

    isAdmin() {
        return this.authenticatedUserService.isAdmin()

    }
    buildConstants() {
        $('body').materialScrollTop({ offset: 300 })
        this.$scope.items = []
        this.selectedMaxCount = 0
        this.distinctCount = { similar: 0, exact: 0 }
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
            publisher: this.filterModel('GROUPS_PUBLISHERS'),
            all: this.filterModel('GROUPS_ALL'),
        };
        this.$scope.filterGroupsExact = angular.copy(this.$scope.filterGroups);
        this.titleTranslations = {
            none: 'NO_RESULTS_AT_THIS_TIME',
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

    editLandingPage() {
        this.$scope.isEditMode = true
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
