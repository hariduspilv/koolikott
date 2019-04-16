'use strict'

{
const DASHBOARD_VIEW_STATE_MAP = {
    unReviewed: [
        'UNREVIEWED', // title translation key
        'firstReview/unReviewed', // rest URI (after 'rest/admin/')
        '-byCreatedAt' // default sort by (use leading minus for DESC)
    ],
    sentEmails: [
        'EMAIL_SENT_EMAILS',
        'sentEmails',
        '-byEmailSentAt'
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

        if (this.viewPath ==='sentEmails')
            this.sortedBy = '-byEmailSentAt';
        else
            this.sortedBy = '-byCreatedAt';

        this.$scope.isFiltering = false
        this.$scope.isTaxonSelectVisible = true
        this.$scope.isExpertsSelectVisible = true
        this.$scope.isSubmitButtonEnabled = false
        this.$scope.sortByType = true


        this.$scope.types = ['All','Material','Portfolio']

        this.$scope.$watch('filter.educationalContext', this.onEducationalContextChange.bind(this), true)
        this.$scope.$watch('query.filter', (newValue, oldValue) => {
            if (newValue !== oldValue && (newValue.length >=3 || !newValue))
                this.filterItems()
        })

        this.$scope.$watch('filter.taxons', this.onFilterChange.bind(this), true)

        this.$scope.filter = { };
        this.$scope.filter.materialType = 'All'
        this.$scope.filter.materialTypeTempForSort = 'All'
        this.$scope.filter.materialModeratorTempForSort = ''
        this.$scope.filter.materialEduTempForSort = ''
        this.$scope.filter.materialDomainTempForSort = ''

        this.$scope.query = {
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
        if (this.viewPath == 'moderators' || this.viewPath == 'restrictedUsers') {
            this.serverCallService
                .makeGet('rest/user/all')
                .then(r => this.$scope.users = r.data)
        }

        if (this.viewPath ==='sentEmails')
            this.sortedBy = '-byEmailSentAt';
    }

    selectType(type) {
        this.$scope.filter.materialType = type
        if (type !== 'All' && this.$scope.filter.materialTypeTempForSort === 'All') {
            this.sortedBy = '-byCreatedAt';
            this.$scope.sortByType = true
        }
    }

    onFilterChange(filter) {
        const params = Object.assign({}, filter)

        if (params.taxons && !this.$scope.isPaginating) {
            this.$scope.filter.taxons = params.taxons;
        }
    }

    getFilterResults(){
        this.$scope.query.filter = ''
        this.$scope.isFiltering = true
        this.$scope.query.page = 1
        this.$scope.filter.materialTypeTempForSort = this.$scope.filter.materialType;
        this.$scope.filter.materialModeratorTempForSort= this.$scope.filter.user;
        this.$scope.filter.materialEduTempForSort = this.$scope.filter.educationalContext
        this.$scope.filter.materialDomainTempForSort = this.$scope.filter.taxons

        this.getData('firstReview/unReviewed', this.sortedBy)
        if (this.$scope.filter.materialType !== 'All')
            this.$scope.sortByType = false
        else
            this.$scope.sortByType = true
    }

    onParamsChange({ users, taxons }) {
        this.$scope.isSubmitButtonEnabled =  users || taxons;
        this.$scope.isTaxonSelectVisible = !users}

    clearFields() {
        this.$scope.educationalContext = undefined
        this.$scope.isSubmitButtonEnabled = false
        this.$scope.filter = {}
        this.$scope.clearFields = true
        this.$scope.query.filter = ''
        this.$route.reload()
    }

    clearFilter() {
        this.$scope.query.filter = ''

        this.$scope.itemsCount = this.collection.length
        this.filteredCollection = null

        this.$scope.data = this.paginate(this.$scope.query.page, this.$scope.query.limit)

        if (this.$scope.filter.form.$dirty)
            this.$scope.filter.form.$setPristine()
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
        if (taxons.length === 0)
            this.$scope.filter.taxons = undefined
        else
            this.$scope.filter.taxons = taxons
        this.$scope.clearFields = false

    }

    onEducationalContextChange(educationalContext) {
        this.$scope.filter.taxons = undefined
        this.onParamsChange({});
    }

    isDisabled() {
        return this.isModerator() ? !((this.$scope.filter && this.$scope.filter.taxons) || this.$scope.filter.materialType)  : !((this.$scope.filter && this.$scope.filter.taxons) ||
            this.$scope.filter.user || this.$scope.filter.materialType);
    }

    getMaximumUnreviewed(){
        this.serverCallService
            .makeGet('rest/admin/firstReview/unReviewed/count')
            .then(result => {
                this.$scope.totalCountOfUnreviewed = result.data;
            })
    }

    getTranslation(key) {
        return this.$filter('translate')(key)
    }

    openContent(learningObject) {
        if (learningObject) {
            const scope = this.$scope.$new(true)
            scope.learningObject = learningObject
            this.$mdDialog.show({
                templateUrl: 'views/sentEmail/sentEmail.html',
                controller: 'sentEmailController',
                scope,
                clickOutsideToClose: true
            })
                .then(() => this.$route.reload())
        }
    }

    editUser(user) {
        if (user) {
            const scope = this.$scope.$new(true)
            scope.user = user

            this.$mdDialog
                .show({
                    templateUrl: 'views/editUserDialog/editUser.html',
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
        this.$scope.isLoading = true
        if (!this.$scope.filter.materialType)
            this.$scope.filter.materialType

        if (restUri === 'firstReview/unReviewed') {
            let url = 'rest/admin/' + restUri + '/' +
                '?page=' + this.$scope.query.page +
                '&itemSortedBy=' + sortBy +
                '&query=' + this.$scope.query.filter.toLowerCase() +
                this.selectTaxons() +
                this.selectUsers() +
                '&lang=' + this.getLanguage() +
                '&materialtype=' + this.$scope.filter.materialType;

                query = this.serverCallService
                .makeGet(url);
        }

        else if(restUri === 'sentEmails') {
            let url = 'rest/userEmail/' + restUri + '/' +
                '?page=' + this.$scope.query.page +
                '&itemSortedBy=' + sortBy +
                '&query=' + this.$scope.query.filter.toLowerCase() +
                '&lang=' + this.getLanguage();

            query = this.serverCallService
                .makeGet(url);
        }
        else {
            query = this.serverCallService
                .makeGet('rest/admin/' + restUri)
        }
                query
                .then(({ data }) => {
                if (data) {
                    this.$scope.isLoading = false

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
                    this.collection = data

                    if (this.viewPath === 'unReviewed' || this.viewPath ==='sentEmails') {
                        this.$scope.data = data.items;
                        this.$scope.itemsCount = data.totalResults;
                    } else {
                        this.$scope.itemsCount = data.length;
                        this.$scope.data = data.slice(0, this.$scope.query.limit)
                    }
                }
        })
    }


    isModerator() {
        return this.authenticatedUserService.isModerator();
    }

    selectUsers() {
        if (this.$scope.filter && this.$scope.filter.user) {
            return "&user=" + this.$scope.filter.user.id;
        }
        return ""
    }

    selectTaxons() {
        if (this.$scope.filter && this.$scope.filter.taxons) {
            return this.$scope.filter.taxons.map(t => '&taxon=' + t.id).join("");
        } else if (!this.$scope.filter.taxons && this.$scope.filter.educationalContext) {
            return '&taxon=' + this.$scope.filter.educationalContext.id
        } else
            return ""
    }

    getModerators() {
        this.serverCallService
            .makeGet('rest/admin/moderator')
            .then(res => this.$scope.moderators = res.data)
    }

    openLearningObject(learningObject) {
        this.$window.open(
            this.getLearningObjectUrl(learningObject), '_blank'
        )
    }

    showMaxMessageText(message) {
        return message.length > 130 ? message.substring(0, 129) : message;
    }

    showEllipsis(message) {
        return message.length > 120 ? 'ellipsis' : null;
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
        this.sortedBy = order;
        this.$scope.query.order = order;
        this.$scope.query.page = 1;
        this.$scope.filter.materialType = this.$scope.filter.materialTypeTempForSort;
        this.$scope.filter.user = this.$scope.filter.materialModeratorTempForSort;
        this.$scope.filter.educationalContext = this.$scope.filter.materialEduTempForSort
        this.$scope.filter.taxons =  this.$scope.filter.materialDomainTempForSort

        if (this.viewPath === 'unReviewed' || this.viewPath ==='sentEmails') {
            this.getData('firstReview/unReviewed', order);
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
        if (this.viewPath === 'unReviewed')
            this.paginate(page, limit)
        else
            this.$scope.data = this.paginate(page, limit)

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

    filterItems() {

        this.$scope.isFiltering = true

        if (this.viewPath === 'unReviewed') {
            return this.getData('firstReview/unReviewed', this.sortedBy)
        }

        else if (this.viewPath === 'sentEmails') {
            return this.getData('sentEmails', this.sortedBy)
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
        this.$scope.isPaginating = true
        const start = (page - 1) * limit
        const end = start + limit

        if (this.viewPath === 'unReviewed'){
            this.$scope.query.page = page;
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

    showDialog() {

        this.$mdDialog.show({
            templateUrl: 'directives/sendEmail/sendEmail.html',
            controller: 'sendEmailDialogController',
            controllerAs: '$ctrl',
            clickOutsideToClose: false,
            locals: {
                learningObject: this.learningObject
            }
        })
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
    'translationService',
    'authenticatedUserService',
    '$window'
]
angular.module('koolikottApp').controller('baseTableViewController', controller)
}
