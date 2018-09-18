'use strict'

{
class controller extends Controller {
    $onInit() {
        this.$scope.opened = null
        this.$scope.hasChildren = false
        this.$scope.taxon = this.taxon
        this.$scope.icon = this.icon

        if (this.taxon) {
            this.$scope.taxonName = this.getTaxonTranslation(this.taxon)

            const parentEdCtx = this.taxon.parentId
                ? this.taxonService.getEducationalContext(this.taxon)
                : {}

            // Taxon is EducationalContext
            if (this.taxon.domains) this.checkTaxonLevelAndAssignValues('.EducationalContext', this.taxon.domains)

            // Taxon is Domain
            else if (this.taxon.parentLevel === '.EducationalContext') {
                if (parentEdCtx.name === 'PRESCHOOLEDUCATION' || parentEdCtx.name === 'VOCATIONALEDUCATION')
                    this.checkTaxonLevelAndAssignValues('.Domain', this.taxon.topics)
                if (parentEdCtx.name === 'BASICEDUCATION' || parentEdCtx.name === 'SECONDARYEDUCATION')
                    this.checkTaxonLevelAndAssignValues('.Domain', this.taxon.subjects)
                if (parentEdCtx.name === 'VOCATIONALEDUCATION')
                    this.checkTaxonLevelAndAssignValues('.Domain', this.taxon.specializations)

            // Taxon is Subject or Specialization
            } else if (this.taxon.parentLevel === '.Domain') {
                if (parentEdCtx.name === 'BASICEDUCATION' || parentEdCtx.name === 'SECONDARYEDUCATION')
                    this.checkTaxonLevelAndAssignValues('.Subject', this.taxon.topics)

                if (parentEdCtx.name === 'VOCATIONALEDUCATION')
                    this.checkTaxonLevelAndAssignValues('.Specialization', this.taxon.modules)
            }

            // used under PRESCHOOLEDUCATION, BASICEDUCATION and SECONDARYEDUCATION
            this.checkTaxonLevelAndAssignValues('.Topic', this.taxon.subtopics)
            // only used under VOCATIONALEDUCATION
            this.checkTaxonLevelAndAssignValues('.Module', this.taxon.topics)

            this.refreshThisTaxonCount()
        }

        this.$scope.toggleChildren = this.toggleChildren.bind(this)
        this.$scope.$watch(() => localStorage.getItem(this.getTaxonCountKey()),
            (newCount, oldCount) => {
            if (newCount && newCount !== oldCount)
                    this.$scope.materialCount = localStorage.getItem(this.getTaxonCountKey())
            }
        )
        this.$scope.$watch(() => this.$location.url(), () =>
            this.$scope.isActive = this.$location.url() === '/search/result?q=&taxon=' + this.taxon.id
        )
    }
    toggleChildren(id) {
        if (this.$scope.materialCount === 0) return
        if (this.$scope.opened) return this.$scope.opened = false
        window.scrollTo(0,0)
        this.searchService.setTaxon([id])
        this.handleOrderAndGrouping()
        this.$location.url(this.searchService.getURL())
        this.$scope.opened = true
    }
    handleOrderAndGrouping() {
        if (!this.searchService.getQuery()) this.searchService.setIsGrouped(false)
        else this.searchService.setIsGrouped(true)
    }
    checkTaxonLevelAndAssignValues(level, children) {
        if (this.taxon.level === level && children.length > 0) {
            this.$scope.taxonChildren = children
            this.$scope.childrenCount = children.length
            this.$scope.hasChildren = true
        }
    }
    getTaxonTranslation() {
        const { level, name } = this.taxon

        return level && level !== '.EducationalContext'
            ? level.toUpperCase().substr(1) + '_' + name.toUpperCase()
            : name.toUpperCase()
    }
    refreshThisTaxonCount() {
        this.$scope.materialCount = localStorage.getItem(this.getTaxonCountKey())
        this.$timeout(this.getTaxonMaterialsCount.bind(this), 100)
    }
    getTaxonMaterialsCount() {
        this.serverCallService
            .makeGet('rest/search?q=&start=0&limit=0&taxon=' + this.taxon.id)
            .then(({ data }) =>
                localStorage.setItem(this.getTaxonCountKey(), data.totalResults)
            )
    }
    getTaxonCountKey() {
        let key = ''

        if (this.taxon.id)
            key = this.taxon.id.toString() + '_'

        return key + this.taxon.name.toUpperCase() + '_COUNT'
    }
}
controller.$inject = [
    '$scope',
    '$location',
    '$timeout',
    'searchService',
    'taxonService',
    'serverCallService',
]
component('dopSidenavTaxon', {
    bindings: {
        taxon: '<',
        icon: '<'
    },
    templateUrl: 'directives/sidenavTaxon/sidenavTaxon.html',
    controller
})
}
