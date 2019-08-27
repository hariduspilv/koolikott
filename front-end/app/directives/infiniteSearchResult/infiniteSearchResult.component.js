'use strict'

{
    class controller extends Controller {
        constructor(...args) {
            super(...args)

            this.landingPageLanguages = ['ET', 'EN', 'RU']
            this.initEmptyLandingPageEdit()
            if (this.isLandingPage()) {
                this.getNoticeAndTranslationString()
            }

            this.$scope.currentLanguage = this.translationService.getLanguage();
            this.$scope.filteredTitle = {}
            this.$scope.highligthTitle = {}
            this.$scope.searchKeyWord = ""
            this.getFrontPageTitleTranslations()
            this.getAllFrontPageTitleTranslations()
            this.headlineAndMaintananceLanguage()

            this.$scope.editMode = false;
            this.$rootScope.$on('logout:success', () => this.$scope.isEditMode = false);
        }

        headlineAndMaintananceLanguage() {
            if (this.$scope.currentLanguage) {
                this.$scope.headlineLanguage = this.convertLanguage(this.$scope.currentLanguage);
                this.$scope.maintenanceLanguage = this.convertLanguage(this.$scope.currentLanguage);
            }
        }

        $onDestroy() {
            this.clearAllSearchOptions()
            this.searchService.setIsExact('');
            this.searchService.setDetails('');
        }


        $onChanges({title, subtitle, filter, params, exactTitle, similarTitle, description, notice, home, searchpage}) {
            if (title && title.currentValue !== title.previousValue) this.setTitle()
            if (exactTitle && exactTitle.currentValue !== exactTitle.previousValue) this.setPhraseTitleExact()
            if (similarTitle && similarTitle.currentValue !== similarTitle.previousValue) this.setPhraseTitleSimilar()
            if (subtitle && subtitle.currentValue !== subtitle.previousValue) this.$scope.subtitle = subtitle.currentValue
            if (filter && filter.currentValue !== filter.previousValue) this.$scope.filter = filter.currentValue
            if (description && description.currentValue !== description.previousValue) this.$scope.description = description.currentValue
            if (notice && notice.currentValue !== notice.previousValue) this.$scope.notice = notice.currentValue
            if (home && home.currentValue !== home.previousValue) this.$scope.home = home.currentValue
            if (searchpage && searchpage.currentValue !== searchpage.previousValue) this.$scope.searchpage = searchpage.currentValue

            if (params && !params.isFirstChange() && this.params) {
                const c = params.currentValue;
                const p = params.previousValue;
                if (!this.equals(c.q, p.q)
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
            this.$scope.showNoticeEdit = false
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

        setTabTitle(){
            if (this.$scope.searchKeyWord) {
                if (this.$scope.headlineLanguage === 'ET') {
                    this.$rootScope.tabTitle = `Otsing: ${this.$scope.searchKeyWord}`;
                } else if (this.$scope.headlineLanguage === 'EN') {
                    this.$rootScope.tabTitle = `Search: ${this.$scope.searchKeyWord}`;
                } else if (this.$scope.headlineLanguage === 'RU') {
                    this.$rootScope.tabTitle = `Поиск: ${this.$scope.searchKeyWord}`;
                }
            } else if (this.$scope.educationLevel === '') {
                this.$rootScope.tabTitle = this.$scope.title.replace(/<strong>/gi, '').replace(/<\/strong>/gi, '')
            }
        }

        setTitle() {
            this.$translate.onReady().then(() => {
                this.$scope.title = (this.buildTitle(this.title, this.totalResults, this.titleTranslations))
                this.setTabTitle()
            })
        }

        setPhraseTitleExact() {
            this.$translate.onReady().then(() => {
                    this.$scope.exactTitle = this.buildTitle(this.exactTitle, this.distinctCount.exact, this.phaseTitlesExact)
                    this.setTabTitle()
            })
        }

        setPhraseTitleSimilar() {
            this.$translate.onReady().then(() => {
                    this.$scope.similarTitle = this.buildTitle(this.similarTitle, this.distinctCount.similar, this.phaseTitlesSimilar)
                    this.setTabTitle()
            })
        }

        buildTitle(title, results, translations) {
            this.$scope.searchKeyWord = this.searchService.getQuery()
            let t = (key) => this.$translate.instant(key);
            return title ? t(title) :
                this.$scope.searching ? '' :
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
            if (isNewSearch) {
                this.setParams()
                this.$scope.educationLevel = ''
            }
            if (this.$scope.searching || !isNewSearch && this.allResultsLoaded()) return

            this.$scope.searching = true
            this.$scope.start = this.searchCount * this.maxResults

            this.params.limit = this.maxResults
            this.params.maxResults = this.maxResults
            this.params.start = this.$scope.start

            let taxonId
            if (this.params.taxon) {
                taxonId = this.params.taxon[0]
            }

            this.getSearchTaxonHeader(taxonId);

            this.serverCallService.makeGet(this.url, this.params)
                .then(({data}) => {
                    this.params.isGrouped
                        ? this.groupedSearchSuccess(data)
                        : this.searchSuccess(data)
                }, () => {
                    this.searchFail()
                })
        }

        getSearchTaxonHeader(taxonId) {
            const fullTaxon = this.taxonService.getFullTaxon(taxonId)
            if (fullTaxon) {
                this.$scope.educationLevel = fullTaxon.taxonLevel === 'EDUCATIONAL_CONTEXT' ?
                    this.$translate.instant(fullTaxon.name)
                    : this.$translate.instant((fullTaxon.taxonLevel + '_' + fullTaxon.name).toUpperCase());
                if(this.$scope.searchKeyWord === ''){
                    this.$rootScope.tabTitle = this.$scope.educationLevel
                }
            }
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
                    if (param === 'all') {
                        this.setActive(param, '', "countMaterial", "isMaterialActive");
                    } else if (param.startsWith('material')) {
                        this.setActive(param, 'material', "countMaterial", "isMaterialActive");
                    } else if (param.startsWith('portfolio')) {
                        this.setActive(param, 'portfolio', "countPortfolio", "isPortfolioActive");
                    } else if (param === 'isExact') {
                        this.searchService.search[param] = "true";
                    } else if (param === 'details') {
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
            if (groupId === 'all') {
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

        isAdmin() {
            return this.authenticatedUserService.isAdmin()

        }

        buildConstants() {
            this.$scope.items = []
            this.selectedMaxCount = 0
            this.distinctCount = {similar: 0, exact: 0}
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

        cancelEdit() {
            this.$scope.isEditMode = false
            this.$scope.visible = false
            this.setNoticesAndDescriptions()
            this.$scope.maintenanceLanguage = this.convertLanguage(this.$scope.currentLanguage);
        }

        toggleNoticeAndDescriptionLanguageInputs(lang) {
            this.$scope.maintenanceLanguage = lang
        }

        toggleNoticeAndDescriptionLanguageInputs2(lang) {
            this.$scope.highligthTitle[this.$scope.headlineLanguage] = this.$scope.filteredTitle.text;
            this.$scope.headlineLanguage = lang;
            this.$scope.filteredTitle.text = this.$scope.highligthTitle[lang];
        }

        isLangFilled(lang) {
            let isFilled = false;

            Object.keys(this.$scope.noticesAndDescriptions).forEach(key => {
                if (lang === key && !!this.$scope.noticesAndDescriptions[key].description) {
                    isFilled = true;
                }
            });

            return isFilled;
        }

        getNoticeAndTranslationString() {
            this.serverCallService
                .makeGet('rest/translation/landingPage')
                .then(({data}) => {
                    this.$scope.description = data.descriptions.find(obj => {
                        return obj.language === this.translationService.getLanguage()
                    }).text
                    this.$scope.notice = data.notices.find(obj => {
                        return obj.language === this.translationService.getLanguage()
                    }).text
                })
        }

        setNoticesAndDescriptions() {
            this.serverCallService
                .makeGet('rest/translation/landingPage/admin')
                .then(({data}) => {
                    this.$scope.landingPageTexts = data
                    const languageCodeMap = {
                        ET: 'est',
                        EN: 'eng',
                        RU: 'rus'
                    }
                    const findText = (prop, lang) =>
                        ((this.$scope.landingPageTexts[prop] || []).find(c => c.language === languageCodeMap[lang]) || {}).text

                    this.$scope.noticesAndDescriptions = this.landingPageLanguages.reduce(
                        (noticesAndDescriptions, lang) =>
                            Object.assign(noticesAndDescriptions, {
                                [lang]: {
                                    notice: findText('notices', lang) || '',
                                    /**
                                     * @todo this.stripHtml is a temporary solution until
                                     * existing content is migrated in the DB
                                     */
                                    description: findText('descriptions', lang) || '',
                                }
                            }),
                        {}
                    )
                })
        }

        initEmptyLandingPageEdit() {
            this.setNoticesAndDescriptions()
        }

        getNoticesAndDescriptions() {
            const notices = [];
            const descriptions = [];
            const languageCodeMap = {
                ET: 'est',
                EN: 'eng',
                RU: 'rus'
            }

            Object.keys(this.$scope.noticesAndDescriptions).forEach(lang => {
                const item = this.$scope.noticesAndDescriptions[lang];

                if (item.notice === '') {
                    notices.push({
                        language: languageCodeMap[lang],
                        text: ""
                    });
                }
                if (item.notice) {
                    notices.push({
                        language: languageCodeMap[lang],
                        text: item.notice
                    });
                }
                if (item.description) {
                    descriptions.push({
                        language: languageCodeMap[lang],
                        text: item.description
                    });
                }
            });

            return {notices, descriptions};
        }

        isSubmitDisabled() {
            return !(this.getNoticesAndDescriptions().descriptions.length === 3)
        }

        save() {
            this.$scope.isSaving = true

            const {notices, descriptions} = this.getNoticesAndDescriptions()

            this.serverCallService
                .makePost('rest/translation/update', {notices: notices, descriptions: descriptions})
                .then(response => {
                    if (response.status === 200) {
                        this.toastService.show('LANDING_PAGE_UPDATED')
                        this.$scope.isSaving = false
                        this.$scope.isEditMode = false
                        this.$scope.visible = false
                        this.getNoticeAndTranslationString()
                        this.$scope.maintenanceLanguage = this.convertLanguage(this.$scope.currentLanguage);
                    }
                })
                .catch(() => {
                    this.$scope.isSaving = false
                    this.toastService.show('LANDING_PAGE_UPDATE_FAILED')
                })
        }

        isLandingPage() {
            return this.$scope.$ctrl.home
        }

        isSearchPage() {
            return this.$scope.$ctrl.searchpage
        }

        isUserViewPage() {
            return this.$rootScope.userView
        }

        maintenanceVisible() {
            return this.$scope.visible = !this.$scope.visible
        }

        editFilteredGroup() {
            this.$scope.editMode = true;
        }

        getFrontPageTitleTranslations() {
            let languageKey = this.$scope.editMode ? this.$scope.headlineLanguage : this.$scope.currentLanguage;

            this.serverCallService.makeGet('rest/translation/getTranslationForTranslationKey', {
                translationKey: 'FRONT_PAGE_FILTERED_TITLE',
                languageKey: languageKey,
            }).then((response) => {
                if (response)
                    this.$scope.filteredTitle.text = response.data
            }).catch(e => {
                console.log(e)
            })
        }

        getAllFrontPageTitleTranslations() {
            this.serverCallService.makeGet('rest/translation/getAllTranslations',
                {
                    translationKey: 'FRONT_PAGE_FILTERED_TITLE',
                })
                .then((response) => {
                    if (response) {
                        this.$scope.highligthTitle.ET = response.data[0];
                        this.$scope.highligthTitle.RU = response.data[1];
                        this.$scope.highligthTitle.EN = response.data[2];
                    }
                })
                .catch(e => {
                    console.log(e)
                });
        }

        cancelEditMode() {
            this.$scope.highligthTitle = {}
            this.$scope.editMode = false
            this.getAllFrontPageTitleTranslations();
            this.getFrontPageTitleTranslations();
            this.$scope.headlineLanguage = this.convertLanguage(this.$scope.currentLanguage);
        }

        saveFilteredTitles() {
            this.$scope.isSaving = true;
            this.$scope.filteredTitle.translationKey = 'FRONT_PAGE_FILTERED_TITLE';
            this.$scope.highligthTitle[this.$scope.headlineLanguage] = this.$scope.filteredTitle.text;

            const LANGS = Object.keys(this.$scope.highligthTitle);
            const VALUES = Object.values(this.$scope.highligthTitle);

            this.serverCallService
                .makePost('rest/translation/updateTranslations', {
                    translations: VALUES,
                    translationKey: this.$scope.filteredTitle.translationKey,
                    languageKeys: LANGS
                }).then(response => {
                if (response.status === 200) {
                    this.toastService.show('FRONT_PAGE_HEADLINE_TITLE_UPDATED')
                    this.$scope.isSaving = false
                    this.$scope.editMode = false
                    this.$scope.afterSave = true;
                    this.getAllFrontPageTitleTranslations();
                    this.getFrontPageTitleTranslations();
                    this.$scope.headlineLanguage = this.convertLanguage(this.$scope.currentLanguage);
                    this.$scope.afterSave = false;
                }
            })
                .catch(() => this.toastService.show('USER_PROFILE_UPDATE_FAILED', 2000));
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
        'toastService',
        'translationService',
        'taxonService'
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
            home: '<',
            searchpage: '<'
        },
        templateUrl: 'directives/infiniteSearchResult/infiniteSearchResult.html',
        controller
    })
}
