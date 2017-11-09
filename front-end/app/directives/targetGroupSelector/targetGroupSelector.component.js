'use strict'

{
class controller extends Controller {
    $onInit() {
        this.$scope.$on('detailedSearch:prefillTargetGroup', (evt, args) =>
            this.$scope.selectedTargetGroup = this.targetGroupService.getLabelByTargetGroups(args)
        )
        this.fill()
        this.addListeners()
        this.selectValue()
        this.setSelectedText()
    }
    $onChanges({ taxon, markRequired, targetGroups }) {
        if (taxon &&
            taxon.currentValue !== taxon.previousValue &&
            !this.$rootScope.isEditPortfolioMode
        ) {
            const newEdCtx = this.taxonService.getEducationalContext(taxon.currentValue)
            const oldEdCtx = this.taxonService.getEducationalContext(taxon.previousValue)

            if (!oldEdCtx || (newEdCtx && newEdCtx.name !== oldEdCtx.name) || !newEdCtx) {
                this.fill()
                this.resetIfInvalid()
            }
        }

        if (markRequired && markRequired.currentValue && this.isRequired && this.$scope.selectedTargetGroup.length === 0)
            this.$scope.targetGroupForm.targetGroupSelect.$touched = true

        if (targetGroups && targetGroups.currentValue !== targetGroups.previousValue)
            this.setSelectedText()
    }
    setSelectedText() {
        const selected = this.$scope.selectedTargetGroup.length
            ? this.$scope.selectedTargetGroup
            : this.targetGroups && this.targetGroups.length
                && this.targetGroups

        selected
            ? this.$scope.selectedText = this.targetGroupService.getSelectedText(selected).join(', ')
            : this.$translate('DETAILED_SEARCH_TARGET_GROUP').then(selectedText =>
                this.$scope.selectedText = selectedText
            )
    }
    parentClick(evt) {
        const added = !this.$scope.selectedTargetGroup.length || (
            evt.group &&
            this.$scope.selectedTargetGroup.indexOf(evt.group.label) < 0
        )
        added
            ? this.addGroups(evt.group.children)
            : this.removeGroups(evt.group.children)

        this.parseSelectedTargetGroup()
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

            this.setSelectedText()
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
        if (!this.$scope.selectedTargetGroup)
            this.$scope.selectedTargetGroup = this.targetGroupService.getLabelByTargetGroups(this.targetGroups)
    }
    resetIfInvalid() {
        const groupNames = []

        if (this.$scope.groups)
            this.$scope.groups.forEach(group =>
                group && groupNames.push(group)
            )

        if (groupNames.indexOf(this.$scope.selectedTargetGroup) < 0 || !this.$scope.groups) {
            this.$scope.selectedTargetGroup = []
            this.targetGroups = []

            if (this.$scope.groups && this.$scope.groups.length === 1)
                this.$scope.selectedTargetGroup = this.$scope.groups
        }
    }
    addGroups(groupsToAdd) {
        [].push.apply(
            this.$scope.selectedTargetGroup, 
            groupsToAdd.reduce(
                (missing, group) =>
                    this.$scope.selectedTargetGroup.includes(group)
                        ? missing
                        : missing.concat(group),
                []
            )
        )
    }
    removeGroups(groupsToRemove) {
        groupsToRemove.forEach(groupToRemove => {
            const idx = this.$scope.selectedTargetGroup.indexOf(groupToRemove)
            if (idx > -1)
                this.$scope.selectedTargetGroup.splice(idx, 1)
        })
    }
    updateParents() {
        for (let i = 0; i < this.$scope.groups.length; i++) {
            const group = this.$scope.groups[i]
            this.targetGroupService.hasAllChildren(group, this.$scope.selectedTargetGroup)
                ? this.addGroups([ group.label ])
                : this.removeGroups([ group.label ])
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
