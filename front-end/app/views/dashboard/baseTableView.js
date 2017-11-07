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
        'DELETED',
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

        this.$scope.itemsCount = 0
        this.$scope.filter = { options: { debounce: 500 } }
        this.$scope.query = {
            filter: '',
            order: 'bySubmittedAt',
            limit: 10,
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
    getData(restUri, sortBy) {
        this.serverCallService
            .makeGet('rest/admin/'+restUri)
            .then(({ data }) => {
                if (data) {
                    if (sortBy)
                        this.$scope.query.order = sortBy

                    if (restUri == 'changed')
                        data.forEach(o => {
                            o.__numChanges = o.reviewableChanges.filter(c => !c.reviewed).length
                            o.__changers = this.getChangers(o)
                        })
                    if (restUri == 'improper')
                        data.forEach(o => {
                            o.__numChanges = o.improperContents.filter(c => !c.reviewed).length
                            o.__changers = this.getReporters(o)
                        })

                    this.collection = data
                    this.$scope.itemsCount = data.length

                    this.sortService.orderItems(data, this.$scope.query.order)
                    this.$scope.data = data.slice(0, this.$scope.query.limit)
                }
            })
    }
    getCorrectLanguageTitle({ title, titles, language } = {}) {
        return title || titles && this.getUserDefinedLanguageString(
            titles,
            this.translationService.getLanguage(),
            language
        )
    }
    openLearningObject(learningObject) {
        this.$location.url(
            this.getLearningObjectUrl(learningObject)
        )
    }
    getLearningObjectUrl(learningObject) {
        if (learningObject)
            return this.isPortfolio(learningObject)
                ? '/portfolio?id=' + learningObject.id
                : '/material?id=' + learningObject.id
    }
    onSort(order) {
        this.sortService.orderItems(
            this.filteredCollection !== null
                ? this.filteredCollection
                : this.collection,
            order,
            this.unmergedData
        )
        this.$scope.data = this.paginate(this.$scope.query.page, this.$scope.query.limit)
    }
    getTaxonTranslation(taxon) {
        if (!taxon)
            return

        if (taxon.level = ".TaxonDTO")
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
        return this.getCreatedBy(improperContents);
    }
    getCreatedBy(items){
        const ids = [];
        return items
            .filter(c => !c.reviewed)
            .filter(c => {
                const { id } = c.createdBy
                return ids.includes(id)
                    ? false
                    : ids.push(id)
            })
    }
    getDuplicates(item) {
        return item.learningObject
            ? this.unmergedData.filter(r => r.learningObject.id == item.learningObject.id)
            : this.unmergedData.filter(r => r.id == item.id)
    }
    filterItems() {
        const isFilterMatch = (str, query) => str.toLowerCase().indexOf(query) > -1

        this.filteredCollection = this.collection.filter(data => {
            if (data) {
                const query = this.$scope.query.filter.toLowerCase()

                if (this.viewPath == 'moderators' || this.viewPath == 'restrictedUsers')
                    return (
                        isFilterMatch(data.name+' '+data.surname, query) ||
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
    paginate(page, limit) {
        const start = (page - 1) * limit
        const end = start + limit

        return this.filteredCollection !== null
            ? this.filteredCollection.slice(start, end)
            : this.collection.slice(start, end)
    }
    /**
     *  Merge reports/changes so that every learning object is represented by only 1 row in the table.
     *  //todo should remove merge
     */
    merge(items) {
        const merged = []
        const isSame = (a, b) =>
            (a.learningObject || a.material || a).id ===
            (b.learningObject || b.material || b).id

        for (let i = 0; i < items.length; i++) {
            let isAlreadyReported = false

            for (var j = 0; j < merged.length; j++)
                if (isSame(merged[j], items[i])) {
                    isAlreadyReported = true
                    const __reportLabelKey = this.getImproperReportLabelKey(items[i])

                    merged[j].__reportCount++
                    merged[j].__reportLabelKey = __reportLabelKey
                    items[i].__reportCount++
                    items[i].__reportLabelKey = __reportLabelKey

                    // include the newest
                    const mergedDate = merged[j].createdAt || merged[j].added
                    const itemDate = items[j].createdAt || items[j].added

                    if (new Date(itemDate) > new Date(mergedDate))
                        merged[j] = items[i]

                    break
                }

            if (!isAlreadyReported) {
                items[i].__reportCount = 1
                items[i].__reportLabelKey = this.getImproperReportLabelKey(items[i])
                merged.push(items[i])
            }

            items[i].__creators = this.getCreators(items[i])
        }

        return merged
    }
    getImproperReportLabelKey(item) {
        if (item.__reportCount && item.__reportCount === 1)
            return !Array.isArray(item.reportingReasons)
                ? ''
                : item.reportingReasons.length === 1
                    ? item.reportingReasons[0].reason
                    : item.reportingReasons.length > 1
                        ? 'MULTIPLE_REASONS'
                        : ''

        let reasonKey = ''
        const allReports = this.getDuplicates(item)

        for (let i = 0; i < allReports.length; i++) {
            if (!Array.isArray(allReports[i].reportingReasons))
                return ''

            if (allReports[i].reportingReasons.length > 1)
                return 'MULTIPLE_REASONS'

            if (allReports[i].reportingReasons.length === 1) {
                if (!reasonKey)
                    reasonKey = allReports[i].reportingReasons[0].reason
                else if (reasonKey != allReports[i].reportingReasons[0].reason)
                    return 'MULTIPLE_REASONS'
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
