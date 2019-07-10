'use strict'

{
const VIEW_STATE_MAP = {
    showDeleted: [
        'delete_forever', // icon
        'ERROR_MSG_DELETED', // message translation key
        // array of buttons
        [{
            icon: () => 'restore_page', // button icon
            label: 'BUTTON_RESTORE', // button tooltip translation key
            onClick: ($ctrl) => $ctrl.setNotDeleted(),
            show: ($ctrl) => $ctrl.isAdmin // to show or not to show this button
        }]
    ],
    showChanged: [
        'autorenew',
        'MESSAGE_CHANGED_LEARNING_OBJECT',
        [{
            icon: () => 'undo',
            label: 'UNDO_CHANGES',
            onClick: ($ctrl) => $ctrl.setAllChanges('revert'),
            show: ($ctrl) => $ctrl.isAdmin || $ctrl.isModerator
        }, {
            icon: ($ctrl) => {
                const numNewTaxons = $ctrl.newTaxons ? $ctrl.newTaxons.length : 0
                return numNewTaxons > 1 || numNewTaxons && $ctrl.oldLink
                    ? 'done_all'
                    : 'done'
            },
            label: 'ACCEPT_CHANGES',
            onClick: ($ctrl) => $ctrl.setAllChanges('accept'),
            show: ($ctrl) => $ctrl.isAdmin || $ctrl.isModerator
        }],
        ($ctrl) => $ctrl.getChanges()
    ],
    showImproper: [
        'warning',
        'ERROR_MSG_IMPROPER',
        [{
            icon: () => 'delete',
            label: 'BUTTON_REMOVE',
            onClick: ($ctrl) => $ctrl.setDeleted(),
            show: ($ctrl) => $ctrl.isAdmin || $ctrl.isModerator,
        }, {
            icon: () => 'done',
            label: 'REPORT_NOT_IMPROPER',
            onClick: ($ctrl) => $ctrl.setNotImproper(),
            show: ($ctrl) => $ctrl.isAdmin || $ctrl.isModerator
        }],
        ($ctrl) => $ctrl.getReasons()
    ],
    showUnreviewed: [
        'lightbulb_outline',
        ($ctrl) =>
            $ctrl.data && $ctrl.data.type == '.Portfolio'
                ? 'ERROR_MSG_UNREVIEWED_PORTFOLIO'
                : 'ERROR_MSG_UNREVIEWED',
        [{
            icon: () => 'done',
            label: 'BUTTON_REVIEW',
            onClick: ($ctrl) => $ctrl.setReviewed(),
            show: ($ctrl) => $ctrl.isAdmin || $ctrl.isModerator
        }]
    ]
}
class controller extends Controller {
    $onChanges({ data }) {
        if (data && data.currentValue && (!data.previousValue || data.currentValue.id != data.previousValue.id))
            this.init()
    }
    $onInit() {
        this.isAdmin = this.authenticatedUserService.isAdmin()
        this.isModerator = this.authenticatedUserService.isModerator()
        this.oldLinkColor = this.$mdColors.getThemeColor('grey-500')

        this.$scope.getChangeType = (item) =>
            item.taxon
                ? 'DETAIL_VIEW_DOMAIN'
                : item.resourceType
                    ? 'MATERIAL_VIEW_RESOURCE_TYPE'
                    : item.targetGroup
                        ? 'DETAIL_VIEW_TARGET_GROUP'
                        : ''

        this.$scope.getChangeName = (item) =>
            item.taxon
                ? this.taxonService.getTaxonTranslationKey(item.taxon)
                : item.resourceType
                    ? item.resourceType.name
                    : item.targetGroup
                        ? 'TARGET_GROUP_' + item.targetGroup
                        : ''

        this.$scope.toggleExpandable = this.toggleExpandable.bind(this)

        this.onWindowResizeReports = () => requestAnimationFrame(
            this.toggleExpandableReports.bind(this)
        )
        this.onWindowResizeChanges = () => requestAnimationFrame(
            this.toggleExpandableChanges.bind(this)
        )

        this.$rootScope.$on('dashboard:adminCountsUpdated', () => this.init())
        this.$rootScope.$on('errorMessage:reported', () => this.init())
        this.$rootScope.$on('portfolioChanged', () => this.init());
        this.$rootScope.$watch('learningObjectChanged', (newValue, oldValue) => {
            if (newValue != oldValue)
                this.init()
        })
        this.onLearningObjectChange = this.onLearningObjectChange.bind(this)
        this.$scope.$watch(() => this.storageService.getMaterial(), this.onLearningObjectChange, true)
        this.$scope.$watch(() => this.storageService.getPortfolio(), this.onLearningObjectChange, true)
    }
    $onDestroy() {
        if (this.listeningResize) {
            window.removeEventListener('resize', this.onWindowResizeReports)
            window.removeEventListener('resize', this.onWindowResizeChanges)
        }
        this.$rootScope.learningObjectDeleted = undefined
        this.$rootScope.learningObjectImproper = undefined
        this.$rootScope.learningObjectUnreviewed = undefined
        this.$rootScope.learningObjectChanged = undefined
        this.$rootScope.learningObjectChanges = undefined
    }
    onLearningObjectChange(newLearningObject, oldLearningObject) {
        if (newLearningObject && (!oldLearningObject || newLearningObject.changed != oldLearningObject.changed))
            this.init()
    }
    init() {
        this.setState('', '', [], false); // reset

        if (!this.$rootScope.learningObjectPrivate) {
            this.bannerType =
                this.$rootScope.learningObjectImproper ? 'showImproper' :
                this.$rootScope.learningObjectUnreviewed ? 'showUnreviewed' :
                this.$rootScope.learningObjectChanged && 'showChanged'

            // make sure if deleted then show deleted stuff
            if (this.$rootScope.learningObjectDeleted)
                this.bannerType = 'showDeleted';

            if (this.bannerType)
                this.setState(...VIEW_STATE_MAP[this.bannerType])
        }
    }
    setState(icon, messageKey, buttons, cb) {
        this.$scope.show = typeof cb === 'boolean' ? cb : true
        this.$scope.icon = icon
        if (this.bannerType != 'showChanged')
            this.$scope.messageKey = typeof messageKey === 'function' ? messageKey(this) : messageKey
            this.$scope.htmlMessage = false
        this.$scope.iconTooltipKey = this.$scope.messageKey
        this.$scope.message = ''
        this.$scope.buttons = buttons
        this.$scope.reports = null
        this.$scope.showExpandableReports = false
        this.$scope.showExpandableChanges = false

        if (typeof cb === 'function')
            cb(this)
    }
    showButton(conditions) {
        return conditions.reduce(
            (show, condition) =>
                show || typeof this[condition] === 'undefined'
                    ? show
                    : this[condition],
            false
        )
    }
    getReasons(setMessage = true) {
        const { id } = this.data || {}

        if (id) {
            // cache this while reports are fetched
            const { messageKey } = this.$scope
            this.$scope.messageKey = ''

            if (this.isAdmin || this.isModerator) {
                this.serverCallService
                    .makeGet('rest/admin/improper/' + this.data.id)
                    .then(({data: reports}) => {
                            if (Array.isArray(reports) && reports.length) {
                                const done = (reasons = '') => {
                                    !setMessage
                                        ? this.$scope.messageKey = messageKey
                                        : this.$scope.message = reports[0].reportingText
                                        ? reasons.join(', ') + ': ' + reports[0].reportingText
                                        : reasons.join(', ')

                                    if (!this.listeningResize) {
                                        this.listeningResize = true
                                        window.addEventListener('resize', this.onWindowResizeReports)
                                    }
                                    this.onWindowResizeReports()
                                }

                                !reports[0].reportingReasons
                                    ? done()
                                    : Promise
                                        .all(reports[0].reportingReasons.map(r => this.$translate(r.reason)))
                                        .then(done)

                                this.$scope.reports = reports
                            }
                        }, () =>
                            this.$scope.messageKey = messageKey
                    )
            }
        }
    }
    getChanges() {
        const { id } = this.data || {}

        if (id)
            this.serverCallService
                .makeGet('rest/admin/changed/'+id)
                .then(({ data: changes }) => {
                    this.newTaxons = []
                    this.oldLink = ''
                    if (Array.isArray(changes) && changes.length) {
                        changes.forEach(change =>
                            change.taxon
                                ? this.newTaxons.push(change.taxon) // taxon was added
                                : this.oldLink = change.materialSource // link was changed
                        )
                        this.changes = changes
                        this.$rootScope.learningObjectChanges = this.changes

                        if (this.$scope.expanded)
                            this.setExpandableHeight()

                        this.metadataService.loadEducationalContexts(() => {
                            this.$scope.messageKey = ''
                            this.$scope.htmlMessage = this.getChangedMessage()

                            if (!this.listeningResize) {
                                this.listeningResize = true
                                window.addEventListener('resize', this.onWindowResizeChanges)
                            }
                            this.onWindowResizeChanges()
                        })
                    }
                })
    }
    getChangedMessage() {
        const translate = (key) => this.$translate.instant(key)
        const taxons = () =>
            this.newTaxons
                .map(t => `<span class="changed-new-taxon">${this.getTaxonLabel(t)}</span>`)
                .join(', ')

        const message = this.oldLink && !this.newTaxons.length
            ? `${translate('CHANGED_LINK')} <a href="${this.oldLink}" target="_blank" style="color: ${this.oldLinkColor}">${this.oldLink}</a>`
            : this.newTaxons.length === 1
                ? `${translate(this.oldLink ? 'CHANGED_LINK_AND_ADDED_ONE_TAXON' : 'ADDED_ONE_TAXON')} ${taxons()}`
                : `${this.sprintf(
                        translate(this.oldLink ? 'CHANGED_LINK_AND_ADDED_MULTIPLE_TAXONS' : 'ADDED_MULTIPLE_TAXONS'),
                        this.newTaxons.length
                    )} ${taxons()}`

        return message
    }
    getTaxonLabel(taxon) {
        return this.$translate.instant(
            this.taxonService.getTaxonTranslationKey(taxon)
        )
    }
    toggleExpandableReports() {
        if (this.$scope.reports.length > 1 || this.forceCollapsible)
            return this.$scope.showExpandableReports = true

        const { offsetWidth, scrollWidth } = document.getElementById('error-message-heading')
        this.$scope.showExpandableReports = offsetWidth && scrollWidth && scrollWidth > offsetWidth
    }
    toggleExpandableChanges() {
        if (this.newTaxons.length > 1 || this.oldLink && this.newTaxons.length === 1)
            return this.$scope.showExpandableChanges = true

        const { offsetWidth, scrollWidth } = document.getElementById('error-message-heading')
        this.$scope.showExpandableChanges = offsetWidth && scrollWidth && scrollWidth > offsetWidth
    }
    toggleExpandable() {
        if (!this.$scope.expanded) {
            this.setExpandableHeight()
            this.$scope.expanded = true
        } else
            this.$scope.expanded = false
    }
    setExpandableHeight() {
        const expandable = document.querySelector('.error-message-expandable')
        if (expandable) {
            expandable.style.height = ''
            expandable.style.height = Math.min(400, expandable.scrollHeight)+'px'
        }
    }
    setNotImproper() {
        const { id, type } = this.data

        if (id && (this.isAdmin || this.isModerator))
            this.serverCallService
                .makePost('rest/admin/improper/setProper', {id, type})
                .then(({ status, data }) => {
                    this.$rootScope.learningObjectImproper = false
                    this.$rootScope.learningObjectUnreviewed = false
                    this.$rootScope.learningObjectChanged = false
                    this.$rootScope.$broadcast('dashboard:adminCountsUpdated')
                })
    }
    setDeleted() {
        const { id, type } = this.data
        // TODO ips creator can delete their portfolio
        if (id && (this.isAdmin || this.isModerator)) {
            const isPortfolio = this.isPortfolio(this.data);

            (isPortfolio
                ? this.serverCallService.makePost('rest/portfolio/delete', { id, type })
                : this.serverCallService.makePost('rest/material/delete', { id, type })
            ).then(({ status, data }) => {
                this.data.deleted = true
                this.toastService.showOnRouteChange(isPortfolio ? 'PORTFOLIO_DELETED' : 'MATERIAL_DELETED')
                this.$rootScope.learningObjectDeleted = true
                this.$rootScope.$broadcast('dashboard:adminCountsUpdated')
            })
        }
    }
    setNotDeleted() {
        const { id, type } = this.data

        if (id && this.isAdmin) {
            const isPortfolio = this.isPortfolio(this.data)
            const url = 'rest/admin/deleted/restore';

            this.serverCallService.makePost(url, { id, type }).then(({ status, data }) => {
                this.data.deleted = false
                this.data.improper = false
                this.data.unReviewed = false
                this.data.broken = false
                this.data.changed = false
                this.toastService.show(isPortfolio ? 'PORTFOLIO_RESTORED' : 'MATERIAL_RESTORED')
                this.$rootScope.learningObjectDeleted = false
                this.$rootScope.learningObjectImproper = false
                this.$rootScope.learningObjectUnreviewed = false
                this.$rootScope.learningObjectChanged = false
                this.$rootScope.$broadcast('dashboard:adminCountsUpdated')
                this.$rootScope.$broadcast('portfolioHistory:hide');
            })
        }
    }
    setReviewed() {
        const { id, type } = this.data

        if (id && (this.isAdmin || this.isModerator))
            this.serverCallService
                .makePost('rest/admin/firstReview/setReviewed', { id, type })
                .then(({ status, data }) => {
                    this.$rootScope.learningObjectUnreviewed = false
                    this.$rootScope.$broadcast('dashboard:adminCountsUpdated')
                })
    }
    setAllChanges(action, cb) {
        const { id } = this.data || {}

        if (id) {
            // making optimistic changes
            const revertedOrAccepted = this.changes.splice(0, this.changes.length)
            this.$rootScope.learningObjectChanged = false

            const undo = () => {
                // the changes were too optimistic
                ;[].splice.apply(this.changes, [0, 0].concat(revertedOrAccepted))
                this.$rootScope.learningObjectChanged = true
            }

            this.serverCallService
                .makePost(`rest/admin/changed/${id}/${action}All`)
                .then(({ status, data }) => {
                    200 <= status && status < 300
                        ? this.setData(data)
                        : undo()
                }, undo)
        }
    }
    setOneChange(action, change) {
        const { id } = this.data || {}

        if (id && change.id) {
            // making optimistic changes
            const revertedOrAcceptedIdx = this.changes.findIndex(c => c.id === change.id)
            const [revertedOrAcceptedChange] = this.changes.splice(revertedOrAcceptedIdx, 1)
            this.$rootScope.learningObjectChanged = !!this.changes.length

            const undo = () => {
                // the changes were too optimistic
                this.changes.splice(revertedOrAcceptedIdx, 0, revertedOrAcceptedChange)
                this.$rootScope.learningObjectChanged = true
            }

            this.serverCallService
                .makePost(`rest/admin/changed/${id}/${action}One/${change.id}`)
                .then(({ status, data }) => {
                    200 <= status && status < 300
                        ? this.setExpandableHeight() || this.setData(data)
                        : undo()
                }, undo)
        }
    }
    setData(data) {
        this.data = data || this.data
        this.isMaterial(data) ? this.storageService.setMaterial(data) :
        this.isPortfolio(data) && this.storageService.setPortfolio(data)
        this.$rootScope.$broadcast('dashboard:adminCountsUpdated')
        this.$rootScope.$broadcast('tags:resetTags');
    }
}
controller.$inject = [
    '$scope',
    '$rootScope',
    '$timeout',
    '$routeParams',
    '$translate',
    '$mdColors',
    'serverCallService',
    'storageService',
    'taxonService',
    'authenticatedUserService',
    'metadataService',
    'toastService'
]
component('dopErrorMessage', {
    bindings: {
        data: '<'
    },
    templateUrl: 'directives/errorMessage/errorMessage.html',
    controller
})
}
