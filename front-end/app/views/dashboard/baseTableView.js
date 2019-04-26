'use strict';

{
    const DASHBOARD_VIEW_STATE_MAP = {
        unReviewed: [
            'UNREVIEWED', // title translation key
            'firstReview/unReviewed', // rest URI (after 'rest/admin/')
            '-byCreatedAt', // default sort by (use leading minus for DESC)
            true, // backendPagination?
            false // userpage
        ],
        sentEmails: [
            'EMAIL_SENT_EMAILS',
            'sentEmails',
            '-byEmailSentAt',
            true,
            false
        ],
        improper: [
            'IMPROPER',
            'improper',
            '-byReportCount',
            false,
            false
        ],
        changes: [
            'CHANGED_LEARNING_OBJECTS',
            'changed',
            'byLastChangedAt',
            false,
            false
        ],
        deleted: [
            'DELETED_LEARNING_OBJECTS',
            'deleted',
            'byUpdatedAt',
            false,
            false
        ],
        moderators: [
            'MODERATORS_TAB',
            'moderator',
            'byUsername',
            false,
            true
        ],
        restrictedUsers: [
            'RESTRICTED_USERS_TAB',
            'restrictedUser',
            'byUsername',
            false,
            true
        ]
    };

    class controller extends Controller {
        constructor(...args) {
            super(...args);

            this.collection = null;
            this.filteredCollection = null;

            this.viewPath = this.$location.path().replace(/^\/dashboard\//, '');
            const [titleTranslationKey, url, sort, backendPagination, userPage] = DASHBOARD_VIEW_STATE_MAP[this.viewPath] || [];

            this.$scope.titleTranslationKey = titleTranslationKey;
            this.sortedBy = sort;
            this.restUri = url;
            this.isBackendPagination = backendPagination;
            this.userPage = userPage;

            this.$scope.isTaxonSelectVisible = true;
            this.$scope.isExpertsSelectVisible = true;
            this.$scope.sortByType = true;

            this.$scope.types = ['All', 'Material', 'Portfolio'];

            this.$scope.$watch('filter.educationalContext', this.onEducationalContextChange.bind(this), true);
            this.$scope.$watch('query.filter', (newValue, oldValue) => {
                if (newValue !== oldValue && (newValue.length >= 3 || !newValue))
                    this.filterItems()
            });

            this.$scope.$watch('filter.taxons', this.onFilterChange.bind(this), true)

            this.$scope.filter = {};
            this.$scope.filter.materialType = 'All';

            this.$scope.query = {
                filter: '',
                order: this.sortedBy,
                limit: 20,
                page: 1
            };

            this.$scope.onPaginate = this.onPaginate.bind(this);
            this.$scope.onSort = this.onSort.bind(this);

            this.getModeratorsAndAllUsers();

            url
                ? this.getData(url, sort)
                : console.error(new Error(`Could not find ${url} in DASHBOARD_VIEW_STATE_MAP. See baseTableView.js`));
        }

        getModeratorsAndAllUsers() {
            if (this.viewPath === 'unReviewed') {
                this.getModerators();
            }
            if (this.userPage) {
                this.getAllUsers();
            }
        }

        selectType(type) {
            this.$scope.filter.materialType = type
            if (type !== 'All' && this.$scope.sortByType) {
                this.sortedBy = DASHBOARD_VIEW_STATE_MAP[this.viewPath][2];
            }
        }

        onFilterChange(filter) {
            const params = Object.assign({}, filter)
            if (params.taxons && !this.$scope.isPaginating) {
                this.$scope.filter.taxons = params.taxons;
            }
        }

        getFilterResults() {
            this.$scope.query.filter = ''
            this.$scope.isFiltering = true
            this.$scope.query.page = 1
            this.getData(this.restUri, this.sortedBy);
            this.$scope.sortByType = this.$scope.filter.materialType === 'All';
        }

        onParamsChange({users, taxons}) {
            this.$scope.isSubmitButtonEnabled = users || taxons;
            this.$scope.isTaxonSelectVisible = !users
        }

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

        onSelectTaxons(taxons) {
            this.$scope.filter.taxons = taxons.length ? taxons : undefined;
            this.$scope.clearFields = false
        }

        onEducationalContextChange(educationalContext) {
            this.$scope.filter.taxons = undefined
            this.onParamsChange({});
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
            this.$scope.isLoading = true;

            if (this.restUri === 'firstReview/unReviewed') {
                query = this.getUnreviewed(restUri, sortBy);
            } else if (this.restUri === 'sentEmails') {
                query = this.getSentEmails(restUri, sortBy);
            } else {
                query = this.serverCallService.makeGet('rest/admin/' + restUri)
            }

            query
                .then(({data}) => {
                    if (data) {
                        this.$scope.isLoading = false;

                        if (sortBy)
                            this.$scope.query.order = sortBy;

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
                        this.collection = data;

                        if (this.isBackendPagination) {
                            this.$scope.data = data.content;
                            this.$scope.itemsCount = data.totalElements;
                        } else {
                            this.$scope.itemsCount = data.length;
                            this.$scope.data = data.slice(0, this.$scope.query.limit)
                        }
                    }
                })
        }

        getUnreviewed(restUri, sortBy) {
            let url = 'rest/admin/' + restUri + '/' +
                '?page=' + this.$scope.query.page +
                '&itemSortedBy=' + sortBy +
                '&query=' + this.$scope.query.filter.toLowerCase() +
                this.selectTaxons() +
                this.selectUsers() +
                '&lang=' + this.getLanguage() +
                '&materialtype=' + this.$scope.filter.materialType;

            return this.serverCallService.makeGet(url);
        }

        getSentEmails(restUri, sortBy) {
            let url = 'rest/userEmail/' + restUri + '/' +
                '?page=' + this.$scope.query.page +
                '&itemSortedBy=' + sortBy +
                '&query=' + this.$scope.query.filter.toLowerCase() +
                '&lang=' + this.getLanguage();

            return this.serverCallService.makeGet(url);
        }

        isModerator() {
            return this.authenticatedUserService.isModerator();
        }

        selectUsers() {
            if (this.$scope.filter && this.$scope.filter.user) {
                return '&user=' + this.$scope.filter.user.id;
            }
            return ''
        }

        selectTaxons() {
            if (this.$scope.filter && this.$scope.filter.taxons) {
                return this.$scope.filter.taxons.map(t => '&taxon=' + t.id).join('');
            } else if (this.$scope.filter && this.$scope.filter.educationalContext) {
                return '&taxon=' + this.$scope.filter.educationalContext.id
            } else
                return '';
        }

        getModerators() {
            this.serverCallService
                .makeGet('rest/admin/moderator')
                .then(res => this.$scope.moderators = res.data)
        }

        getAllUsers() {
            this.serverCallService
                .makeGet('rest/user/all')
                .then(r => this.$scope.users = r.data);
        }

        openLearningObject(learningObject) {
            this.$window.open(this.getLearningObjectUrl(learningObject), '_blank')
        }

        showMaxMessageText(message) {
            return message.length > 130 ? message.substring(0, 126) + '...' : message;
        }

        onSort(order) {
            this.sortedBy = order;
            this.$scope.query.order = order;
            this.$scope.query.page = 1;
            if (order === 'byType' && this.$scope.filter.materialType !== 'All') {
                this.sortedBy = DASHBOARD_VIEW_STATE_MAP[this.viewPath][2];
            }

            if (this.isBackendPagination) {
                return this.getData(this.restUri, this.sortedBy)
            } else {
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

            if (taxon.level === '.TaxonDTO')
                taxon = this.taxonService.getFullTaxon(taxon.id)

            if (!taxon)
                return

            return taxon.level !== '.EducationalContext'
                ? taxon.level.toUpperCase().substr(1) + '_' + taxon.name.toUpperCase()
                : taxon.name.toUpperCase()
        }

        onPaginate(page, limit) {
            if (this.isBackendPagination)
                this.paginate(page, limit);
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
                const {name, surname} = creator
                return `${str}${str ? ', ' : ''}${name} ${surname}`
            }, '')
        }

        getChangers({reviewableChanges}) {
            return this.getCreatedBy(reviewableChanges);
        }

        getReporters({improperContents}) {
            return this.getCreatedBy(improperContents)
        }

        getCreatedBy(items) {
            const ids = []
            return items.reduce((creators, {reviewed, createdBy}) => {
                const {id} = createdBy || {id: 'UNKNOWN'}
                return ids.includes(id)
                    ? creators
                    : ids.push(id) && creators.concat(createdBy || 'UNKNOWN')
            }, [])
        }

        filterItems() {
            this.$scope.isFiltering = true;
            if (this.isBackendPagination) {
                return this.getData(this.restUri, this.sortedBy)
            } else {
                const isFilterMatch = (str, query) => str.toLowerCase().indexOf(query) > -1;

                this.filteredCollection = this.collection.filter(data => {
                    if (data) {
                        const query = this.$scope.query.filter.toLowerCase()

                        if (this.userPage)
                            return (
                                isFilterMatch(data.name + ' ' + data.surname, query) ||
                                isFilterMatch(data.name, query) ||
                                isFilterMatch(data.surname, query) ||
                                isFilterMatch(data.username, query)
                            );

                        const text = this.getCorrectLanguageTitle(data.learningObject);

                        if (text)
                            return isFilterMatch(text, query)
                    }
                });

                this.$scope.itemsCount = this.filteredCollection.length
                this.$scope.data = this.paginate(this.$scope.query.page, this.$scope.query.limit)
            }
        }

        paginate(page, limit) {
            this.$scope.isPaginating = true;
            const start = (page - 1) * limit;
            const end = start + limit;

            if (this.isBackendPagination) {
                this.$scope.query.page = page;
                return this.getData(this.restUri, this.sortedBy)
            } else {
                return this.filteredCollection !== null
                    ? this.filteredCollection.slice(start, end)
                    : this.collection.slice(start, end)
            }
        }

        getImproperReportLabelKey(item) {
            if (!Array.isArray(item.__reports))
                return ''

            if (item.__reports.length === 1) {
                let {reportingReasons} = item.__reports[0]

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
                let {reportingReasons} = item.__reports[i]

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
                locals: {
                    learningObject: this.learningObject
                }
            })
        }

        openContent(learningObject) {
            if (learningObject) {
                const scope = this.$scope.$new(true);
                scope.learningObject = learningObject;
                this.$mdDialog.show({
                    templateUrl: 'views/sentEmail/sentEmail.html',
                    controller: 'sentEmailController',
                    controllerAs: '$ctrl',
                    scope,
                    clickOutsideToClose: true
                })
            }
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
