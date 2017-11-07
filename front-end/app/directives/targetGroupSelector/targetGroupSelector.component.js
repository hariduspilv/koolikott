'use strict'

{
class controller extends Controller {
    $onInit() {
        this.$scope.isReady = false
        this.$scope.selectedTargetGroup = this.targetGroupService.getLabelByTargetGroups(this.targetGroups)

        this.$scope.parentClick = (evt) => {
            if (!this.$scope.selectedTargetGroup)
                this.$scope.selectedTargetGroup = []

            const added = (evt.group && this.$scope.selectedTargetGroup.indexOf(evt.group.label) == -1)
                || this.$scope.selectedTargetGroup === []

            added
                ? this.addMissingGrades(evt.group.children)
                : this.removeGrades(evt.group.children)

            this.parseSelectedTargetGroup()
        }

        // Reduced text for select label
        this.$scope.getSelectedText = () =>
            this.targetGroups && this.targetGroups.length > 0
                ? _.join(this.targetGroupService.getSelectedText(this.targetGroups), ', ')
                : this.$translate.instant('DETAILED_SEARCH_TARGET_GROUP')

        this.$scope.update = () =>
            !this.$scope.selectedTargetGroup &&
            (this.$scope.selectedTargetGroup = this.targetGroupService.getLabelByTargetGroups(this.targetGroups))

        this.$scope.$on('detailedSearch:prefillTargetGroup', (evt, args) =>
            this.$scope.selectedTargetGroup = this.targetGroupService.getLabelByTargetGroups(args)
        )

        this.fill()
        this.addListeners()
        this.selectValue()
        this.$timeout(() => this.$scope.isReady = true)
    }
    $onChanges({ taxon, markRequired }) {
        if (taxon &&
            taxon.newValue !== taxon.oldValue &&
            !this.$rootScope.isEditPortfolioMode
        ) {
            const newEdCtx = this.taxonService.getEducationalContext(taxon.newValue)
            const oldEdCtx = this.taxonService.getEducationalContext(taxon.oldValue)

            if (!oldEdCtx || (newEdCtx && newEdCtx.name !== oldEdCtx.name) || !newEdCtx) {
                this.fill()
                this.resetIfInvalid()
            }
        }

        if (markRequired && markRequired.newValue && this.isRequired && this.$scope.selectedTargetGroup.length === 0)
            this.$scope.targetGroupForm.targetGroupSelect.$touched = true
    }
    addListeners() {
        this.$scope.$watch('selectedTargetGroup', (newGroup, oldGroup) => {
            if (newGroup !== oldGroup) {
                const { item } = this.getDifference(newGroup, oldGroup)

                if (!this.targetGroupService.isParent(item)) {
                    this.updateParents()
                    this.parseSelectedTargetGroup()
                }
            }
            if (!newGroup)
                this.$scope.selectedTargetGroup = []
        }, false)

        this.$scope.$on('detailedSearch:taxonChange', (evt, taxonChange) => {
            const newEdCtx = this.taxonService.getEducationalContext(taxonChange.newValue)
            const oldEdCtx = this.taxonService.getEducationalContext(taxonChange.oldValue)

            if (!_.isEqual(newEdCtx, oldEdCtx))
                this.fill()
                this.resetIfInvalid()
        })

        this.$scope.$on('targetGroupSelector:clear', () => {
            this.fill()
            this.resetIfInvalid()
        })
    }
    getDifference(newArray, oldArray) {
        const result = {}
        const doItem = (array, removed, item) => {
            if (array != null) {
                if (array.indexOf(item) === -1) {
                    result.removed = removed
                    result.item = item
                }
            } else {
                result.removed = removed
                result.item = item
            }
        }

        newArray && newArray.forEach(item => doItem(oldArray, false, item))
        oldArray && oldArray.forEach(item => doItem(newArray, true, item))

        return result
    }
    fill() {
        this.$scope.groups = this.targetGroupService.getAll()
    }
    parseSelectedTargetGroup() {
        this.targetGroups = this.targetGroupService.getByLabel(this.$scope.selectedTargetGroup)
    }
    selectValue() {
        this.$scope.selectedTargetGroup = this.targetGroupService.getLabelByTargetGroups(this.targetGroups)
    }
    resetIfInvalid() {
        const groupNames = []

        if (this.$scope.groups)
            this.$scope.groups.forEach(group =>
                group && groupNames.push(group)
            )

        if (groupNames.indexOf(this.$scope.selectedTargetGroup) < 0 || !this.$scope.groups) {
            this.$scope.selectedTargetGroup = null
            this.targetGroups = []

            if (this.$scope.groups && this.$scope.groups.length === 1)
                this.$scope.selectedTargetGroup = this.$scope.groups
        }
    }
    addMissingGrades(targetGroups) {
        if (!this.$scope.selectedTargetGroup)
            this.$scope.selectedTargetGroup = []

        ;[].push.apply(
            this.$scope.selectedTargetGroup,
            this.getMissingGrades(this.$scope.selectedTargetGroup, targetGroups)
        )
    }
    getMissingGrades(selectedTargetGroup, targetGroups) {
        return targetGroups.reduce(
            (missing, group) =>
                selectedTargetGroup.indexOf(group) < 0
                    ? missing.concat(group)
                    : missing,
            []
        )
    }
    removeGrades(items) {
        for (let i = 0; i < items.length; i++)
            this.$scope.selectedTargetGroup.splice(
                this.$scope.selectedTargetGroup.indexOf(items[i]),
                1
            )
    }
    updateParents() {
        for (let i = 0; i < this.$scope.groups.length; i++) {
            const hasChildren = this.targetGroupService.hasAllChildren(
                this.$scope.groups[i],
                this.$scope.selectedTargetGroup
            )

            if (hasChildren && this.$scope.selectedTargetGroup.indexOf(this.$scope.groups[i].label) == -1)
                this.$scope.selectedTargetGroup.push(this.$scope.groups[i].label)
            
            if (!hasChildren && this.$scope.selectedTargetGroup) {
                const idx = this.$scope.selectedTargetGroup.indexOf(this.$scope.groups[i].label)

                idx != -1 &&
                this.$scope.selectedTargetGroup.splice(idx, 1)
            }
        }
    }
}
controller.$inject = [
    '$scope',
    '$rootScope',
    '$timeout',
    '$translate',
    'targetGroupService',
    'taxonService'
]
component('dopTargetGroupSelector', {
    bindings: {
        targetGroups: '=',
        taxon: '<',
        isRequired: '=',
        markRequired: '<'
    },
    templateUrl: 'directives/targetGroupSelector/targetGroupSelector.html',
    controller
})
}
