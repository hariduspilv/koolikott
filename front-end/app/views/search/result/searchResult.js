'use strict'

{
class controller extends Controller {
    constructor(...args) {
        super(...args)

        this.setParams()
        this.$scope.$watch(
            () => this.$location.search(),
            this.onLocationChange.bind(this),
            true
        )
    }
    onLocationChange(newValue, oldValue) {
        if (newValue !== oldValue)
            this.setParams(newValue.q)
    }
    setParams(q) {
        q  ?  this.searchService.setSearch(q)
        : q = this.searchService.getQuery()

        this.$scope.params = { q, start: 0 }
        const params = {
            taxon: this.searchService.getTaxon(),
            paid: this.searchService.isPaid(),
            type: this.searchService.getType(),
            language: this.searchService.getLanguage(),
            targetGroup: this.searchService.getTargetGroups(),
            resourceType: this.searchService.getResourceType(),
            curriculumLiterature: this.searchService.isCurriculumLiterature(),
            specialEducation: this.searchService.isSpecialEducation(),
            issuedFrom: this.searchService.getIssuedFrom(),
            crossCurricularTheme: this.searchService.getCrossCurricularTheme(),
            keyCompetence: this.searchService.getKeyCompetence(),
            sort: this.searchService.getSort(),
            sortDirection: this.searchService.getSortDirection(),
        }
        Object.keys(params).forEach((param) => {
            const value = params[param]

            if ((param == 'taxon' && (!value || !value[0])) ||
                (param == 'paid' && value !== false) ||
                (param == 'type' && (!value || !this.searchService.isValidType(value))) ||
                (param == 'specialEducation' && value !== true)
            )
                return

            if (typeof value !== 'undefined' && value !== '')
                this.$scope.params[param] = value
        })
    }
}
controller.$inject = [
    '$scope',
    '$location',
    'searchService'
]
angular.module('koolikottApp').controller('searchResultController', controller)
}
