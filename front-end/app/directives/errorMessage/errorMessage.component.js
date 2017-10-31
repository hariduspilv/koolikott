'use strict'

{
const VIEW_STATE_MAP = {
    showDeleted: [
        'delete', // icon
        'ERROR_MSG_DELETED', // message translation key
        // array of buttons
        [{
            icon: () => 'restore_page', // button icon
            label: 'BUTTON_RESTORE', // button tooltip translation key
            onClick: ($ctrl) => $ctrl.$scope.$emit('restore:learningObject'),
            show: ($ctrl) => $ctrl.isAdmin // to show or not to show this button
        }]
    ],
    showChanged: [
        'priority_high',
        'MESSAGE_CHANGED_LEARNING_OBJECT',
        [{
            icon: () => 'undo',
            label: 'UNDO_CHANGES',
            onClick: ($ctrl) => $ctrl.revertAllChanges(),
            show: ($ctrl) => $ctrl.isAdmin
        }, {
            icon: ($ctrl) => {
                const numNewTaxons = $ctrl.newTaxons ? $ctrl.newTaxons.length : 0
                return numNewTaxons > 1 || numNewTaxons && $ctrl.oldLink
                    ? 'done_all'
                    : 'done'
            },
            label: 'ACCEPT_CHANGES',
            onClick: ($ctrl) => $ctrl.acceptAllChanges(),
            show: ($ctrl) => $ctrl.isAdmin
        }],
        ($ctrl) => $ctrl.getChanges()
    ],
    showBroken: [
        'report',
        'ERROR_MSG_BROKEN',
        [{
            icon: () => 'delete',
            label: 'BUTTON_REMOVE',
            onClick: ($ctrl) => $ctrl.$scope.$parent.confirmMaterialDeletion(),
            show: ($ctrl) => $ctrl.isAdmin || $ctrl.isModerator
        }, {
            icon: () => 'done',
            label: 'REPORT_BROKEN_LINK_CORRECT',
            onClick: ($ctrl) => $ctrl.$scope.$emit('markCorrect:learningObject'),
            show: ($ctrl) => $ctrl.isAdmin || $ctrl.isModerator
        }]
    ],
    showImproper: [
        'warning',
        'ERROR_MSG_IMPROPER',
        [{
            icon: () => 'delete',
            label: 'BUTTON_REMOVE',
            onClick: ($ctrl) => $ctrl.$scope.$emit('delete:learningObject'),
            show: ($ctrl) => $ctrl.isAdmin || $ctrl.isModerator,
        }, {
            icon: () => 'done',
            label: 'REPORT_NOT_IMPROPER',
            onClick: ($ctrl) => $ctrl.$scope.$emit('setNotImproper:learningObject'),
            show: ($ctrl) => $ctrl.isAdmin || $ctrl.isModerator
        }],
        ($ctrl) => $ctrl.getReasons()
    ],
    showImproperAndBroken: [
        'warning',
        'ERROR_MSG_IMPROPER_AND_BROKEN',
        [{
            icon: () => 'delete',
            label: 'BUTTON_REMOVE',
            onClick: ($ctrl) => $ctrl.$scope.$emit('delete:learningObject'),
            show: ($ctrl) => $ctrl.isAdmin || $ctrl.isModerator
        }, {
            icon: () => 'done',
            label: 'REPORT_NOT_IMPROPER',
            onClick($ctrl) {
                $ctrl.$scope.$emit('setNotImproper:learningObject')
                $ctrl.$scope.$emit('markCorrect:learningObject')
            },
            show: ($ctrl) => $ctrl.isAdmin || $ctrl.isModerator
        }],
        ($ctrl) => {
            $ctrl.forceCollapsible = true
            $ctrl.getReasons(false)
        }
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
            onClick: ($ctrl) => $ctrl.$scope.$emit('markReviewed:learningObject'),
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

        this.$scope.$on('dashboard:adminCountsUpdated', () => this.init())
        this.$scope.$on('errorMessage:reported', () => this.init())
        this.$scope.$on('errorMessage:updateChanged', () => this.getChanges())
        this.$rootScope.$watch('learningObjectChanged', (newValue, oldValue) => {
            if (newValue != oldValue)
                this.init()
        })
    }
    $onDestroy() {
        if (this.listeningResize) {
            window.removeEventListener('resize', this.onWindowResizeReports)
            window.removeEventListener('resize', this.onWindowResizeChanges)
        }
    }
    init() {
        this.setState('', '', [], false) // reset

        this.bannerType = 
            this.$rootScope.learningObjectDeleted
                ? 'showDeleted' :
            !this.$rootScope.learningObjectDeleted &&
            !this.$rootScope.learningObjectImproper &&
            this.$rootScope.learningObjectBroken
                ? 'showBroken' :
            !this.$rootScope.learningObjectDeleted &&
            this.$rootScope.learningObjectImproper &&
            !this.$rootScope.learningObjectBroken
                ? 'showImproper' :
            !this.$rootScope.learningObjectDeleted &&
            this.$rootScope.learningObjectImproper &&
            this.$rootScope.learningObjectBroken
                ? 'showImproperAndBroken' :
            !this.$rootScope.learningObjectDeleted &&
            !this.$rootScope.learningObjectImproper &&
            !this.$rootScope.learningObjectBroken &&
            this.$rootScope.learningObjectChanged
                ? 'showChanged' :
            !this.$rootScope.learningObjectDeleted &&
            !this.$rootScope.learningObjectImproper &&
            !this.$rootScope.learningObjectBroken &&
            !this.$rootScope.learningObjectChanged &&
            this.$rootScope.learningObjectUnreviewed
                && 'showUnreviewed'

        if (this.bannerType)
            this.setState(...VIEW_STATE_MAP[this.bannerType])
    }
    setState(icon, messageKey, buttons, cb) {
        this.$scope.show = typeof cb === 'boolean' ? cb : true
        this.$scope.icon = icon
        if (this.bannerType != 'showChanged')
            this.$scope.messageKey = typeof messageKey === 'function' ? messageKey(this) : messageKey
        this.$scope.iconTooltipKey = messageKey
        this.$scope.message = ''
        this.$scope.buttons = buttons
        this.$scope.reports = null
        this.$scope.showExpandableReports = false

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

            this.serverCallService
                .makeGet('rest/impropers/'+this.data.id)
                .then(({ data: reports }) => {
                    if (Array.isArray(reports) && reports.length) {
                        const done = (reasons = '') => {
                            !setMessage
                                ? this.$scope.messageKey = messageKey
                                : this.$scope.message = reports[0].reportingText
                                    ? reasons.join(', ')+': '+reports[0].reportingText
                                    : reasons.join(', ')

                            if (!this.listeningResize) {
                                this.listeningResize = true
                                window.addEventListener('resize', this.onWindowResizeReports)
                            }
                            this.$timeout(() =>
                                setTimeout(() => this.toggleExpandableReports())
                            )
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
    getChanges() {
        const { id } = this.data || {}

        if (!id)
            return

        this.newTaxons = []
        this.oldLink = ''

        this.serverCallService
            .makeGet('rest/admin/changed/'+id)
            .then(({ data: changes }) => {
                if (Array.isArray(changes) && changes.length) {
                    changes.forEach(change =>
                        change.taxon
                            ? this.newTaxons.push(change.taxon) // taxon was added
                            : this.oldLink = change.materialSource // link was changed
                    )
                    this.changes = changes
                    this.$rootScope.learningObjectChanges = changes
                    this.metadataService.loadEducationalContexts(() => {
                        this.$scope.messageKey = ''
                        this.$scope.htmlMessage = this.getChangedMessage()

                        if (!this.listeningResize) {
                            this.listeningResize = true
                            window.addEventListener('resize', this.onWindowResizeChanges)
                        }
                        this.$timeout(() =>
                            setTimeout(() => this.toggleExpandableChanges())
                        )
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
                        translate(this.oldLink ? 'CHANGED_LINK_AND_ADDED_MULTIPLE_TAXONS' : 'ADDED_MULTIPLE_TAXON'),
                        this.newTaxons.length
                    )} ${taxons()}`

        return message
    }
    getTaxonLabel(taxon) {
        return this.$translate.instant(
            this.taxonService.getTaxonTranslationKey(taxon)
        )
    }
    acceptAllChanges() {
        const { id } = this.data || {}

        if (id)
            this.serverCallService
                .makePost(`rest/admin/changed/${id}/acceptAll`)
                .then(() => this.$rootScope.learningObjectChanged = false)
    }
    revertAllChanges() {
        const { id } = this.data || {}

        if (id)
            this.serverCallService
                .makePost(`rest/admin/changed/${id}/revertAll`)
                .then(({ data }) => {
                    this.$rootScope.learningObjectChanged = false
                    this.onReverted(data)
                })
    }
    administerChange(action, change, cbName) {
        const { id } = this.data || {}

        if (id && change.id) {
            // making optimistic changes
            const acceptedIdx = this.changes.findIndex(c => c.id === change.id)
            const [acceptedChange] = this.changes.splice(acceptedIdx, 1)
            this.$rootScope.learningObjectChanged = !!this.changes.length

            const undo = () => {
                // the changes were too optimistic
                this.changes.splice(acceptedIdx, 0, acceptedChange)
                this.$rootScope.learningObjectChanged = true
            }

            this.serverCallService
                .makePost(`rest/admin/changed/${id}/${action}One/${change.id}`)
                .then(({ status, data }) => {
                    200 <= status && status < 300
                        ? this[cbName](data)
                        : undo()
                }, undo)
        }
    }
    onReverted(data) {
        this.data = data || this.data
        this.setExpandableHeight()
        this.isMaterial(data) ? this.storageService.setMaterial(data) :
        this.isPortfolio(data) && this.storageService.setPortfolio(data)
        this.init()
    }
    toggleExpandableReports() {
        if (this.$scope.reports.length > 1 || this.forceCollapsible)
            return this.$scope.showExpandableReports = true

        const { offsetWidth, scrollWidth } = document.getElementById('error-message-heading')
        this.$scope.showExpandableReports = scrollWidth > offsetWidth
    }
    toggleExpandableChanges() {
        if (this.newTaxons.length > 1 || this.oldLink && this.newTaxons.length === 1)
            return this.$scope.showExpandableChanges = true

        const { offsetWidth, scrollWidth } = document.getElementById('error-message-heading') || {}
        this.$scope.showExpandableChanges = scrollWidth && offsetWidth && scrollWidth > offsetWidth
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
    'metadataService'
]
component('dopErrorMessage', {
    bindings: {
        data: '<'
    },
    templateUrl: 'directives/errorMessage/errorMessage.html',
    controller
})
}
