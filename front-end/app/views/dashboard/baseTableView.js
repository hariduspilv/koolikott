'use strict';

{
    const DASHBOARD_VIEW_STATE_MAP = {
        'uus-oppevara': [
            'UNREVIEWED', // title translation key
            'firstReview/unReviewed', // rest URI (after 'rest/admin/')
            '-byCreatedAt', // default sort by (use leading minus for DESC)
            true, // backendPagination?
            false // userpage
        ],
        'saadetud-teated': [
            'EMAIL_SENT_EMAILS',
            'sentEmails',
            '-byEmailSentAt',
            true,
            false
        ],
        'teatatud-oppevara': [
            'IMPROPER',
            'improper',
            '-byReportCount',
            false,
            false
        ],
        'muudetud-oppevara': [
            'CHANGED_LEARNING_OBJECTS',
            'changed',
            'byLastChangedAt',
            false,
            false
        ],
        'kustutatud-oppevara': [
            'DELETED_LEARNING_OBJECTS',
            'deleted',
            'byUpdatedAt',
            false,
            false
        ],
        'kasutajad': [
            'USERS',
            'user/all',
            'byRole',
            true,
            true
        ]
    };

    class controller extends Controller {
        constructor(...args) {
            super(...args);

            this.collection = null;
            this.filteredCollection = null;

            this.viewPath = this.$location.path().replace(/^\/toolaud\//, '');
            const [titleTranslationKey, url, sort, backendPagination, userPage] = DASHBOARD_VIEW_STATE_MAP[this.viewPath] || [];

            this.$scope.titleTranslationKey = titleTranslationKey;
            this.$translate(titleTranslationKey).then((translation) => this.$rootScope.tabTitle = translation);
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
                if (newValue !== oldValue && (newValue.length >= 3 || !newValue)) {
                    this.$scope.query.page = 1;
                    this.filterItems();
                }
            });

            this.$scope.$watch('filter.taxons', this.onFilterChange.bind(this), true);

            this.$scope.filter = {};
            this.$scope.filter.materialType = 'All';

            if (this.userPage) {
                this.$scope.query = {
                    filter: '',
                    order: this.sortedBy,
                    limit: 25,
                    page: 1
                }
                this.getUserRoles();
                this.getUserSelectedRoles();
            } else {
                this.$scope.query = {
                    filter: '',
                    order: this.sortedBy,
                    limit: 20,
                    page: 1
                };
            }

            this.$scope.onPaginate = this.onPaginate.bind(this);
            this.$scope.onSort = this.onSort.bind(this);

            this.getModeratorsAndAllUsers();
            this.loadEducationalContexts();

            url
                ? this.getData(url, sort)
                : console.error(new Error(`Could not find ${url} in DASHBOARD_VIEW_STATE_MAP. See baseTableView.js`));
        }

        getModeratorsAndAllUsers() {
            if (this.viewPath === 'uus-oppevara') {
                this.getModerators();
            }
            if (this.userPage) {
                this.getAllUsers();
            }
        }

        selectType(type) {
            this.$scope.filter.materialType = type;
            if (type !== 'All' && this.$scope.sortByType) {
                this.sortedBy = DASHBOARD_VIEW_STATE_MAP[this.viewPath][2];
            }
        }

        onFilterChange(filter) {
            const params = Object.assign({}, filter);
            if (params.taxons && !this.$scope.isPaginating) {
                this.$scope.filter.taxons = params.taxons;
            }
        }

        getFilterResults() {
            this.$scope.isFiltering = true;
            this.$scope.query.page = 1;
            this.getData(this.restUri, this.sortedBy);
            this.$scope.sortByType = this.$scope.filter.materialType === 'All';
        }

        onParamsChange({users, taxons}) {
            this.$scope.isSubmitButtonEnabled = users || taxons;
            this.$scope.isTaxonSelectVisible = !users
        }

        clearFields() {
            this.$scope.educationalContext = undefined;
            this.$scope.isSubmitButtonEnabled = false;
            this.$scope.filter = {};
            this.$scope.clearFields = true;
            this.$scope.query.filter = '';
            this.$route.reload()
        }

        clearFilter() {
            this.$scope.query.filter = '';

            this.$scope.itemsCount = this.collection.length;
            this.filteredCollection = null;

            this.$scope.data = this.paginate(this.$scope.query.page, this.$scope.query.limit);

            if (this.$scope.filter.form.$dirty)
                this.$scope.filter.form.$setPristine()
        }

        onSelectTaxons(taxons) {
            this.$scope.filter.taxons = taxons.length ? taxons : undefined;
            this.$scope.clearFields = false
        }

        onEducationalContextChange(educationalContext) {
            this.$scope.filter.taxons = undefined;
            this.onParamsChange({});
        }

        getTranslation(key) {
            return this.$filter('translate')(key)
        }

        editUser(id) {
            if (id) {
                const scope = this.$scope.$new(true);
                this.serverCallService.makeGet("rest/user/id", {id : id})
                    .then(({data: user}) => {
                        scope.user = user;
                        this.$mdDialog
                            .show({
                                templateUrl: '/views/editUserDialog/editUser.html',
                                controller: 'editUserController',
                                scope
                            })
                    })
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
            } else if (this.restUri === 'user/all') {
                query = this.getAllUsers(restUri);
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
                                o.__numChanges = o.reviewableChanges.filter(c => !c.reviewed).length;
                                o.__changers = this.getChangers(o)
                            });
                        if (restUri === 'improper')
                            data.forEach(o => {
                                o.__reports = o.improperContents.filter(c => !c.reviewed);
                                o.__reporters = this.getReporters(o.__reports);
                                o.__reportLabelKey = this.getImproperReportLabelKey(o)
                            });
                        this.collection = data;

                        if (this.restUri === 'firstReview/unReviewed') {
                            this.$scope.data = data.items;
                            this.$scope.itemsCount = data.totalResults;

                        } else if (this.restUri === 'sentEmails') {
                            this.$scope.data = data.content;
                            this.$scope.itemsCount = data.totalElements;
                        } else if (this.restUri === 'user/all') {
                            this.$scope.itemsCount = data.totalElements;
                            this.$scope.data = data.content;
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

        getAllUsers(restUri) {
            if (restUri) {
                let url = this.usersTableUrlBuilder(restUri);
                return this.serverCallService.makeGet(url);
            }
        }

        usersTableUrlBuilder(restUri) {
            let url = 'rest/' + restUri +
                '?page=' + this.$scope.query.page +
                '&itemSortedBy=' + this.$scope.query.order;
            if (this.$scope.query) url += '&query=' + this.$scope.query.filter.toLowerCase();
            if (this.$scope.filter.userRole && this.allOptionNotSelected(this.$scope.filter.userRole)) url += '&userRole=' + this.$scope.filter.userRole;
            if (this.$scope.filter.role && this.allOptionNotSelected(this.$scope.filter.role)) url += '&role=' + this.$scope.filter.role;
            if (this.$scope.filter.userEducationalContext && this.allOptionNotSelected(this.$scope.filter.userEducationalContext)) url += '&userEducationalContext=' + this.$scope.filter.userEducationalContext;
            if (this.$scope.filter.withEmail) url += '&withEmail=' + this.$scope.filter.withEmail;
            if (this.$scope.filter.withoutEmail) url += '&withoutEmail=' + this.$scope.filter.withoutEmail;
            if (localStorage.getItem('userPreferredLanguage')) url += '&language=' + localStorage.getItem('userPreferredLanguage');
            return url;
        }

        generateFile(fileType) {
            if (fileType) {
                this.$scope.fetchingDownload = true;
                let url = this.usersTableUrlBuilder('admin/userStatistics/export');
                url += '&fileType=' + fileType;
                this.serverCallService
                    .makePost(url)
                    .then(({ status, data: filename }) => {
                        this.$scope.fetchingDownload = false;

                        this.serverCallService
                            .makeGet(`/rest/admin/statistics/export/download/${filename}`)
                            .then(({status, data}) => {
                                const arrayBuffer = this.stringToArrayBuffer(atob(data));
                                const formattedDate = moment(new Date()).format('YYYY-MM-DDThh:mmTZD');
                                const filenameWithFormat = 'E-koolikoti_kasutajad_' + formattedDate + '.' + fileType;

                                if (200 <= status && status < 300) {
                                    let file = new Blob([arrayBuffer]);

                                    if (window.navigator && window.navigator.msSaveOrOpenBlob) {
                                        window.navigator.msSaveOrOpenBlob(file, filenameWithFormat);
                                        return;
                                    }
                                    let link = document.createElement('a');
                                    link.href = window.URL.createObjectURL(file);
                                    link.download = filenameWithFormat;
                                    document.body.appendChild(link);
                                    link.click();
                                    document.body.removeChild(link);
                                }
                            })
                    })
            }
        }

        // function from https://stackoverflow.com/questions/34993292/how-to-save-xlsx-data-to-file-as-a-blob
        stringToArrayBuffer(s) {
            let buf = new ArrayBuffer(s.length);
            let view = new Uint8Array(buf);
            for (let i=0; i!=s.length; ++i) view[i] = s.charCodeAt(i) & 0xFF;
            return buf;
        }

        addAllOption(options) {
            if (options) {
                let array = options;
                if (!array.includes('GROUPS_ALL')) {
                    array.unshift('GROUPS_ALL');
                }
                return array;
            }
        }

        addAllOptionToEduContext(options) {
            let eduList = ['GROUPS_ALL'];
            if (options) {
                options.forEach(e => eduList.push(e.name))
            }
            return eduList;
        }

        allOptionNotSelected(option) {
            return option !== 'GROUPS_ALL'
        }

        loadEducationalContexts() {
            this.metadataService.loadEducationalContexts((educationalContext) => {
                this.$scope.educationalContext = educationalContext;
            });
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

        getUserRoles() {
            this.serverCallService
                .makeGet('rest/user/roles')
                .then(res => {
                    this.$scope.userRoles = res.data
                })
        }

        getUserSelectedRoles() {
            this.serverCallService
                .makeGet('rest/user/selectedRoles')
                .then(res => {
                    this.$scope.userSelectedRoles = res.data
                })
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
                );
                this.$scope.data = this.paginate(this.$scope.query.page, this.$scope.query.limit)
            }
        }

        getTaxonTranslation(taxon) {
            if (!taxon)
                return;

            if (taxon.level === '.TaxonDTO')
                taxon = this.taxonService.getFullTaxon(taxon.id);

            if (!taxon)
                return;

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
                const {name, surname} = creator;
                return `${str}${str ? ', ' : ''}${name} ${surname}`
            }, '')
        }

        getChangers({reviewableChanges}) {
            return this.getCreatedBy(reviewableChanges);
        }

        getReporters(improperContents) {
            return this.getCreatedBy(improperContents)
        }

        getCreatedBy(items) {
            const ids = [];
            return items.reduce((creators, {reviewed, createdBy}) => {
                const {id} = createdBy || {id: 'UNKNOWN'};
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
                        const query = this.$scope.query.filter.toLowerCase();

                        if (this.userPage)
                            return (
                                isFilterMatch(data.name + ' ' + data.surname, query) ||
                                isFilterMatch(data.name, query) ||
                                isFilterMatch(data.surname, query) ||
                                isFilterMatch(data.username, query)
                            );

                        const text = this.getCorrectLanguageTitle(data);

                        if (text)
                            return isFilterMatch(text, query)
                    }
                });

                this.$scope.itemsCount = this.filteredCollection.length;
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
                return '';

            if (item.__reports.length === 1) {
                let {reportingReasons} = item.__reports[0];

                return !Array.isArray(reportingReasons)
                    ? ''
                    : reportingReasons.length === 1
                        ? reportingReasons[0].reason
                        : this.getAllReasons(item)
            }
            return this.getAllReasons(item)
        }

        getAllReasons(item) {
            let reasonArray = [];
            for (const report of item.__reports) {
                let {reportingReasons} = report;
                for (let singleReason of reportingReasons) {
                    if (!reasonArray.includes(singleReason.reason)) {
                        reasonArray.push(singleReason.reason)
                    }
                }
            }
            return this.capitalizeAndTranslateReason(reasonArray);
        }

        capitalizeAndTranslateReason(reasonKey) {
            let fullReason = this.getTranslation(reasonKey[0]);
            for (const singleReason of reasonKey.slice(1)) {
                let appendReason = ', ' + this.getTranslation(singleReason);
                fullReason += appendReason.toLowerCase()
            }
            return fullReason
        }

        showDialog() {
            this.$mdDialog.show({
                templateUrl: '/directives/sendEmail/sendEmail.html',
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
                    templateUrl: '/views/sentEmail/sentEmail.html',
                    controller: 'sentEmailController',
                    controllerAs: '$ctrl',
                    scope,
                    clickOutsideToClose: true
                })
            }
        }

        openDownloadMenu($mdMenu, evt) {
            $mdMenu.open(evt)
        }
    }

    controller.$inject = [
        '$scope',
        '$rootScope',
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
        '$window',
        'metadataService'
    ];
    angular.module('koolikottApp').controller('baseTableViewController', controller)
}
