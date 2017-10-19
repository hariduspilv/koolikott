'use strict'

{
class controller extends Controller {
    $onChanges({ data }) {
        if (data && data.currentValue && (!data.previousValue || data.currentValue.id != data.previousValue.id))
            this.init()
    }
    $onInit() {
        this.$scope.$on('dashboard:adminCountsUpdated', () => this.init())
        this.$scope.$on('errorMessage:reported', () => this.init())
        this.$rootScope.$watch('learningObjectChanged', (newValue, oldValue) => {
            if (newValue != oldValue)
                this.init()
        })

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

        this.$scope.toggleReports = this.toggleReports.bind(this)

        this.onWindowResize = () => requestAnimationFrame(
            this.toggleExpandableReports.bind(this)
        )

        // this.$timeout(() => this.setChangedData())
        // this.$scope.$on('errorMessage:updateChanged', this.setChangedData.bind(this))
    }
    $onDestroy() {
        if (this.listeningResize)
            window.removeEventListener('resize', this.onWindowResize)
    }
    init() {
        this.setState('', '', [], false)

        const isAdmin = this.authenticatedUserService.isAdmin()
        const isModerator = this.authenticatedUserService.isModerator()
        let message

        this.$scope.showDeleted =
            this.$rootScope.learningObjectDeleted
        this.$scope.showBroken =
            !this.$rootScope.learningObjectDeleted &&
            !this.$rootScope.learningObjectImproper &&
            this.$rootScope.learningObjectBroken
        this.$scope.showImproper =
            !this.$rootScope.learningObjectDeleted &&
            this.$rootScope.learningObjectImproper &&
            !this.$rootScope.learningObjectBroken
        this.$scope.showImproperAndBroken =
            !this.$rootScope.learningObjectDeleted &&
            this.$rootScope.learningObjectImproper &&
            this.$rootScope.learningObjectBroken
        this.$scope.showChanged =
            !this.$rootScope.learningObjectDeleted &&
            !this.$rootScope.learningObjectImproper &&
            !this.$rootScope.learningObjectBroken &&
            this.$rootScope.learningObjectChanged
        this.$scope.showUnreviewed =
            !this.$rootScope.learningObjectDeleted &&
            !this.$rootScope.learningObjectImproper &&
            !this.$rootScope.learningObjectBroken &&
            !this.$rootScope.learningObjectChanged &&
            this.$rootScope.learningObjectUnreviewed
        /*this.$scope.showUnreviewedAndImproper =
            !this.$rootScope.learningObjectDeleted &&
            this.$rootScope.learningObjectImproper &&
            !this.$rootScope.learningObjectBroken &&
            this.$rootScope.learningObjectUnreviewed
        this.$scope.showUnreviewedAndBroken =
            !this.$rootScope.learningObjectDeleted &&
            !this.$rootScope.learningObjectImproper &&
            this.$rootScope.learningObjectBroken &&
            this.$rootScope.learningObjectUnreviewed
        this.$scope.showUnreviewedAndImproperAndBroken =
            !this.$rootScope.learningObjectDeleted
            this.$rootScope.learningObjectImproper &&
            this.$rootScope.learningObjectBroken &&
            this.$rootScope.learningObjectUnreviewed &&*/

        if (this.$scope.showDeleted)
            this.setState('delete', 'ERROR_MSG_DELETED', [{
                icon: 'restore_page',
                label: 'BUTTON_RESTORE',
                onClick: () => this.$scope.$emit('restore:learningObject'),
                show: isAdmin,
            }])
        else
        if (this.$scope.showChanged)
            this.setState('priority_high', 'MESSAGE_CHANGED_LEARNING_OBJECT', [{
                icon: 'undo',
                label: 'UNDO_CHANGES',
                onClick: () => this.changedLearningObjectService.revertChanges(this.getCurrentLearningObjectId()),
                show: isAdmin,
            }, {
                icon: 'done',
                label: 'ACCEPT_CHANGES',
                onClick: () => this.changedLearningObjectService.acceptChanges(this.getCurrentLearningObjectId()),
                show: isAdmin,
            }])
        else
        if (this.$scope.showBroken)
            this.setState('report', 'ERROR_MSG_BROKEN', [{
                icon: 'delete',
                label: 'BUTTON_REMOVE',
                onClick: this.$scope.$parent.confirmMaterialDeletion,
                show: isAdmin || isModerator,
            }, {
                icon: 'done',
                label: 'REPORT_BROKEN_LINK_CORRECT',
                onClick: () => this.$scope.$emit('markCorrect:learningObject'),
                show: isAdmin || isModerator,
            }])
        else
        if (this.$scope.showImproper) {
            this.setState('warning', 'ERROR_MSG_IMPROPER', [{
                icon: 'delete',
                label: 'BUTTON_REMOVE',
                onClick: () => this.$scope.$emit('delete:learningObject'),
                show: isAdmin || isModerator,
            }, {
                icon: 'done',
                label: 'REPORT_NOT_IMPROPER',
                onClick: () => this.$scope.$emit('setNotImproper:learningObject'),
                show: isAdmin || isModerator,
            }], () =>
                this.getReasons()
            )
        } else
        if (this.$scope.showImproperAndBroken)
            this.setState('warning', 'ERROR_MSG_IMPROPER_AND_BROKEN', [{
                icon: 'delete',
                label: 'BUTTON_REMOVE',
                onClick: () => this.$scope.$emit('delete:learningObject'),
                show: isAdmin || isModerator,
            }, {
                icon: 'done',
                label: 'REPORT_NOT_IMPROPER',
                onClick: () => {
                    this.$scope.$emit('setNotImproper:learningObject')
                    this.$scope.$emit('markCorrect:learningObject')
                },
                show: isAdmin || isModerator,
            }], () => {
                this.forceCollapsible = true
                this.getReasons(false)
            })
        else
        if (this.$scope.showUnreviewed) {
            var messageKey = this.data && this.data.type == '.Portfolio'
                ? 'ERROR_MSG_UNREVIEWED_PORTFOLIO'
                : 'ERROR_MSG_UNREVIEWED'
            this.setState('lightbulb_outline', messageKey, [{
                icon: 'done',
                label: 'BUTTON_REVIEW',
                onClick: () => this.$scope.$emit('markReviewed:learningObject'),
                show: isAdmin || isModerator,
            }])
        }
    }
    setState(icon, messageKey, buttons, cb) {
        this.$scope.show = typeof cb === 'boolean' ? cb : true
        this.$scope.icon = icon
        this.$scope.messageKey = messageKey
        this.$scope.iconTooltipKey = messageKey
        this.$scope.message = ''
        this.$scope.buttons = buttons
        this.$scope.reports = null
        this.$scope.showExpandableReports = false

        if (typeof cb === 'function')
            cb()
    }
    getReasons(setMessage = true) {
        // cache this while reports are fetched
        const { messageKey } = this.$scope
        this.$scope.messageKey = ''

        if (this.data && this.data.id)
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
                                window.addEventListener('resize', this.onWindowResize)
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
    getCurrentLearningObjectId() {
        return this.$scope.$parent.material
            ? this.$scope.$parent.material.id
            : this.$scope.$parent.portfolio
                ? this.$scope.$parent.portfolio.id
                : this.$routeParams.id
    }
    toggleExpandableReports() {
        if (this.$scope.reports.length > 1 || this.forceCollapsible)
            return this.$scope.showExpandableReports = true

        const { offsetWidth, scrollWidth } = document.getElementById('error-message-heading')
        this.$scope.showExpandableReports = scrollWidth > offsetWidth
    }
    toggleReports() {
        if (!this.$scope.showReports) {
            const reasons = document.querySelector('.error-message-reports')
            reasons.style.height = Math.min(400, reasons.scrollHeight)+'px'
            this.$scope.showReports = true
        } else
            this.$scope.showReports = false
    }
    /*setChangedData() {
        if (this.$scope.showChanged)
            this.changedLearningObjectService
                .getChangedData(this.getCurrentLearningObjectId())
                .then(response => {
                    console.log('changed:', response.map(res => JSON.stringify(res, null, 2)))
                    // $scope.reasons = response
                })
    }*/
}
controller.$inject = [
    '$scope',
    '$rootScope',
    '$timeout',
    '$routeParams',
    '$translate',
    'serverCallService',
    'changedLearningObjectService',
    'taxonService',
    'authenticatedUserService'
]

angular.module('koolikottApp').component('dopErrorMessage', {
    bindings: {
        data: '<'
    },
    templateUrl: 'directives/errorMessage/errorMessage.html',
    controller
})
}
