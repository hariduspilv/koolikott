'use strict'

{
const DASHBOARD_VIEW_STATE_MAP = {
    unReviewed: [
        'UNREVIEWED', // title translation key
        'firstReview/unReviewed', // rest URI (after 'rest/admin/')
        '-byCreatedAt' // default sort by (use leading minus for DESC)
    ],
    improper: [
        'IMPROPER',
        'improper',
        '-byReportCount',
    ],
    changes: [
        'CHANGED_LEARNING_OBJECTS',
        'changed',
        'byLastChangedAt'
    ],
    deleted: [
        'DELETED_LEARNING_OBJECTS',
        'deleted',
        'byUpdatedAt'
    ],
    moderators: [
        'MODERATORS_TAB',
        'moderator',
        'byUsername'
    ],
    restrictedUsers: [
        'RESTRICTED_USERS_TAB',
        'restrictedUser',
        'byUsername'
    ],
}
class controller extends Controller {
    constructor(...args) {
        super(...args)

        this.collection = null
        this.filteredCollection = null

        this.viewPath = this.$location.path().replace(/^\/dashboard\//, '')
        const [ titleTranslationKey, ...rest ] = DASHBOARD_VIEW_STATE_MAP[this.viewPath] || []

        this.getModerators();
        this.getMaximumUnreviewed();
        this.sortedBy = 'byCreatedAt';
        this.$scope.isFiltering = false
        this.$scope.isUserSelected = false
        this.$scope.isTaxonSelectVisible = true
        this.$scope.isSubmitButtonEnabled = false
        this.$scope.selectedUser = null
        this.$scope.selectedTaxons = ''

        this.$scope.filter = { options: { debounce: 500 } };

        this.$scope.query = {
            filter: "",
            order: this.sortedBy,
            limit: 20,
            page: 1
        }

        this.$scope.filterTaxons = { options: { debounce: 500 } };
        this.$scope.queryTaxons = {
            filter: "",
            order: this.sortedBy,
            limit: 20,
            page: 1
        }

        this.$scope.onPaginate = this.onPaginate.bind(this)
        this.$scope.onSort = this.onSort.bind(this)
        this.$scope.titleTranslationKey = titleTranslationKey

        rest.length
            ? this.getData(...rest)
            : console.error(new Error(`Could not find ${this.viewPath} in DASHBOARD_VIEW_STATE_MAP. See baseTableView.js`))

        // Get all users for the autocomplete
        if (this.viewPath == 'moderators' || this.viewPath == 'restrictedUsers')
            this.serverCallService
                .makeGet('rest/user/all')
                .then(r => this.$scope.users = r.data)

        this.$scope.$watch('query.filter', (newValue, oldValue) => {
            if (newValue !== oldValue)
                this.filterItems()
        })

        this.$scope.$watch('queryTaxons.filter',(newVal, oldVal) => {
            if (newVal !== oldVal)
                this.filterByTaxons()
        })

        this.$scope.$watch('educationalContext', this.onEducationalContextChange.bind(this), true)
        this.$scope.$watch('filter', this.onFilterChange.bind(this), true)

    }

    onFilterChange(filter) {
        const params = Object.assign({}, filter)

        if (params.taxons) {
            this.$scope.filter.taxons = params.taxons;
        }

        if (params.user) {
            this.$scope.filter.user = params.user;
        }
    }

    getFilterResults(){
        this.$scope.isFiltering = true
        if(this.$scope.isUserSelected)
            this.getSelectedUserTaxonsCount();

        this.getData('firstReview/unReviewed', this.sortedBy)

    }

    onParamsChange({ users, taxons }) {
        this.$scope.isSubmitButtonEnabled =  taxons
        this.$scope.isTaxonSelectVisible = !users
    }

    clearFields() {
        this.$scope.filter = {}
        this.$scope.filterTaxons = {}
        this.$scope.educationalContext = undefined
        this.$scope.data = {}
        this.$scope.isUserSelected = false
        this.$scope.isSubmitButtonEnabled = false
        this.$scope.filter.taxons = {}
        this.$scope.queryTaxons.filter = ''
        this.$scope.filter.taxons = {}
        this.$scope.params.taxons = {}

    }
    getPostParams() {
        const params = Object.assign({}, this.$scope.params)

        if (params.taxons) {
            params.taxons = params.taxons.map(({ id, level }) => ({ id, level }))
        }

        this.$scope.paramsForDownload = params;
        return params
    }

    onSelectTaxons(taxons) {
        this.$scope.filter.taxons = taxons

    }

    onEducationalContextChange(educationalContext) {
        this.$scope.isExpertsSelectVisible = !educationalContext
        // this.$scope.sortBy = educationalContext ? 'byDomainOrSubject' : 'byEducationalContext'
        this.onParamsChange({});
    }

    isDisabled(){
         return !((this.$scope.filter && this.$scope.filter.taxons) || (this.$scope.isUserSelected));
    }

    getMaximumUnreviewed(){
        this.serverCallService
            .makeGet('rest/admin/firstReview/unReviewed/count')
            .then(result => {
                this.$scope.totalCountOfUnreviewed = result.data;
            })
    }

    getSelectedUserTaxonsCount(){

        this.$scope.isUserSelected = true

        let strings = this.$scope.filter.taxons.map(t => '&taxon=' + t.id);

        this.serverCallService
            .makeGet('rest/admin/firstReview/unReviewed/count' +
                '?isUserTaxon=' + this.$scope.isUserSelected +
                strings)

            .then(result => {
                this.$scope.totalCountOfUnreviewed = result.data;
            })

    }
    getTranslation(key) {
        return this.$filter('translate')(key)
    }
    editUser(user) {
        if (user) {
            const scope = this.$scope.$new(true)
            scope.user = user

            this.$mdDialog
                .show({
                    templateUrl: 'views/editUserDialog/editUser.html',
                    controller: 'editUserController',
                    scope
                })
                .then(() => this.$route.reload())
        }
    }
    filterUsers(query) {
        return query
            ? this.$scope.users.filter(u => u.username.indexOf(query.toLowerCase()) === 0)
            : this.$scope.users
    }
    getUsernamePlaceholder() {
        return this.$filter('translate')('USERNAME')
    }

    getLanguage() {
        return this.translationService.getLanguageCode() === 'et' ? 1 : this.translationService.getLanguageCode() === 'ru' ? 2 : 3
    }

    getData(restUri, sortBy) {

        let query;

        if (restUri === 'firstReview/unReviewed') {
            query = this.serverCallService
                .makeGet(
                    'rest/admin/' + restUri + '/' +
                    '?page=' + this.$scope.query.page +
                    '&itemSortedBy=' + sortBy +
                    '&query=' + this.$scope.query.filter +
                    this.selectTaxons() +
                    this.selectUsers() +
                    '&isUserTaxon=' + this.$scope.isUserSelected +
                    '&lang=' + this.getLanguage());
        }
        else
            query = this.serverCallService
                .makeGet('rest/admin/' + restUri )

                query
                .then(({ data }) => {
                if (data) {
                    if (sortBy)
                        this.$scope.query.order = sortBy

                    if (restUri === 'changed')
                        data.forEach(o => {
                            o.__numChanges = o.reviewableChanges.filter(c => !c.reviewed).length
                            o.__changers = this.getChangers(o)
                        })
                    if (restUri === 'improper')
                        data.forEach(o => {
                            o.__reports = o.improperContents.filter(c => !c.reviewed)
                            o.__reporters = this.getReporters(o)
                            o.__reportLabelKey = this.getImproperReportLabelKey(o)
                        })

                    console.log(data);
                    this.collection = data.items

                    if (this.viewPath === 'unReviewed') {
                        this.$scope.data = data.items;
                        this.$scope.itemsCount = data.totalResults;
                    } else {
                        this.$scope.itemsCount = data.length;
                        this.$scope.data = data.slice(0, this.$scope.query.limit)
                    }
                    // this.sortService.orderItems(data, this.$scope.query.order)
                }
        })
    }

    selectUsers() {
        if (this.$scope.filter && this.$scope.filter.user) {
            return "&user=" + this.$scope.filter.user.id;
        }
        return ""
    }

    selectTaxons() {
        if (this.$scope.filter && this.$scope.filter.taxons) {
            return this.$scope.filter.taxons.map(t => '&taxon=' + t.id);
        }
        return ""
    }

    getModerators() {
        this.serverCallService
            .makeGet('rest/admin/moderator')
            .then(res => this.$scope.moderators = res.data)
    }

    openLearningObject(learningObject) {
        this.$location.url(
            this.getLearningObjectUrl(learningObject)
        )
    }
    getLearningObjectUrl(learningObject) {

        if (learningObject)

            return this.isPortfolio(learningObject)
                ? '/portfolio?name=' + learningObject.titleForUrl + '&id=' + learningObject.id
                : '/material?name=' + this.getCorrectLanguageTitleForMaterialUrl(learningObject) + '&id=' + learningObject.id
    }
    formatTitleForUrl(learningObject) {
        return this.replaceSpacesAndCharacters(this.getUserDefinedLanguageString(learningObject.titles, this.currentLanguage, language));
    }

    onSort(order) {
        // this.isSorting = true
        this.sortedBy = order;
        this.$scope.query.page = 1;

        if (this.viewPath === 'unReviewed'){

            if (order === 'bySubject' || order === '-bySubject'){
                this.$scope.data = this.getData('firstReview/unReviewed',order);
            } else {
                this.$scope.data = this.sortService.orderItems(this.getData('firstReview/unReviewed',order))
            }
        }
        else{
            this.sortService.orderItems(
                this.filteredCollection !== null
                    ? this.filteredCollection
                    : this.collection,
                order,
                this.unmergedData
            )
            this.$scope.data = this.paginate(this.$scope.query.page, this.$scope.query.limit)
        }
    }

    getTaxonTranslation(taxon) {
        if (!taxon)
            return

        if (taxon.level == ".TaxonDTO")
            taxon = this.taxonService.getFullTaxon(taxon.id)

        if (!taxon)
            return

        return taxon.level !== '.EducationalContext'
            ? taxon.level.toUpperCase().substr(1) + "_" + taxon.name.toUpperCase()
            : taxon.name.toUpperCase()
    }

    onPaginate(page, limit) {

        this.$scope.data = this.paginate(page, limit)

    }
    clearFilter() {
        this.$scope.query.filter = ''

        this.$scope.itemsCount = this.collection.length
        this.filteredCollection = null

        this.$scope.data = this.paginate(this.$scope.query.page, this.$scope.query.limit)

        if (this.$scope.filter.form.$dirty)
            this.$scope.filter.form.$setPristine()
    }
    getNumCreatorsLabel(item, translationKey) {
        return this.sprintf(
            this.$translate.instant(translationKey),
            item.__creators.length
        )
    }
    getCommaSeparatedCreators(item) {
        return item.__creators.reduce((str, creator) => {
            const { name, surname } = creator
            return `${str}${str ? ', ' : ''}${name} ${surname}`
        }, '')
    }
    getCreators(item) {
        const ids = []
        return this
            .getDuplicates(item)
            .filter(c => {
                const { id } = c.createdBy || c.creator || { id: 'UNKNOWN' }
                return ids.includes(id)
                    ? false
                    : ids.push(id)
            })
            .map(c => c.createdBy || c.creator)
    }
    getChangers({ reviewableChanges }) {
        return this.getCreatedBy(reviewableChanges);
    }
    getReporters({ improperContents }) {
        return this.getCreatedBy(improperContents)
    }
    getCreatedBy(items) {
        const ids = []
        return items.reduce((creators, { reviewed, createdBy }) => {
            const { id } = createdBy || { id: 'UNKNOWN' }
            return ids.includes(id)
                ? creators
                : ids.push(id) && creators.concat(createdBy || 'UNKNOWN')
        }, [])
    }
    getDuplicates(item) {
        return item.learningObject
            ? this.unmergedData.filter(r => r.learningObject.id == item.learningObject.id)
            : this.unmergedData.filter(r => r.id == item.id)
    }

    filterByTaxons(){

        if (this.viewPath === 'unReviewed' ) {
            this.$scope.query.filter = this.$scope.queryTaxons.filter;
            return this.getData('firstReview/unReviewed', this.sortedBy)
        }
    }

    filterItems() {

        this.isFiltering = true
        this.isSorting = false

        if (this.viewPath === 'unReviewed' ) {
            return this.getData('firstReview/unReviewed', this.sortedBy)
        }

        else {

            const isFilterMatch = (str, query) => str.toLowerCase().indexOf(query) > -1;

            this.filteredCollection = this.collection.filter(data => {
                if (data) {
                    const query = this.$scope.query.filter.toLowerCase()


                    if (this.viewPath == 'moderators' || this.viewPath == 'restrictedUsers')
                        return (
                            isFilterMatch(data.name + ' ' + data.surname, query) ||
                            isFilterMatch(data.name, query) ||
                            isFilterMatch(data.surname, query) ||
                            isFilterMatch(data.username, query)
                        )

                    const text = data.learningObject
                        ? (this.isMaterial(data.learningObject)
                            ? this.getCorrectLanguageTitle(data.learningObject)
                            : data.learningObject.title)
                        : data.material
                            ? this.getCorrectLanguageTitle(data.material)
                            : this.isMaterial(data)
                                ? this.getCorrectLanguageTitle(data)
                                : data.title

                    if (text)
                        return isFilterMatch(text, query)
                }
            })

            this.$scope.itemsCount = this.filteredCollection.length
            this.$scope.data = this.paginate(this.$scope.query.page, this.$scope.query.limit)
        }
    }

    paginate(page, limit) {
        const start = (page - 1) * limit
        const end = start + limit

        if (this.viewPath === 'unReviewed' && !this.$scope.isFiltering){
            // this.collection =  this.getData('firstReview/unReviewed', this.sortedBy)
            return this.getData('firstReview/unReviewed', this.sortedBy);

        }
        else {
            return this.filteredCollection !== null
                ? this.filteredCollection.slice(start, end)
                : this.collection.slice(start, end)
        }
    }

    getImproperReportLabelKey(item) {
        if (!Array.isArray(item.__reports))
            return ''

        if (item.__reports.length === 1) {
            let { reportingReasons } = item.__reports[0]

            return !Array.isArray(reportingReasons)
                ? ''
                : reportingReasons.length === 1
                    ? reportingReasons[0].reason
                    : reportingReasons.length > 1
                        ? 'MULTIPLE_REASONS'
                        : ''
        }

        let reasonKey = ''

        for (let i = 0; i < item.__reports.length; i++) {
            let { reportingReasons } = item.__reports[i]

            if (Array.isArray(reportingReasons)) {
                if (reportingReasons.length > 1)
                    return 'MULTIPLE_REASONS'

                if (reportingReasons.length === 1) {
                    if (!reasonKey)
                        reasonKey = reportingReasons[0].reason
                    else if (reasonKey != reportingReasons[0].reason)
                        return 'MULTIPLE_REASONS'
                }
            }
        }

        return reasonKey
    }
}
controller.$inject = [
    '$scope',
    '$location',
    '$filter',
    '$mdDialog',
    '$route',
    '$translate',
    '$timeout',
    'serverCallService',
    'sortService',
    'taxonService',
    'iconService',
    'translationService'
]
angular.module('koolikottApp').controller('baseTableViewController', controller)
}
