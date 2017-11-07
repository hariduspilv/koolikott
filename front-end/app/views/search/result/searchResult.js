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
        if (newValue !== oldValue) {
            if (newValue.q === '')
                this.searchService.setType('all')
            this.setParams(newValue.q)
        }
    }
    setParams(q) {
        q  ?  this.searchService.setSearch(q)
        : q = this.searchService.getQuery()

        this.$scope.params = { q, start: 0 }
        
        Object.keys({
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
        })
        .forEach((param, idx, params) => {
            const value = params[param]

            switch(param) {
                case 'taxon':
                    if (!value || !value[0])
                        return
                case 'paid':
                    if (value !== false)
                        return
                case 'type':
                    if (!value || !this.searchService.isValidType(value))
                        return
                case 'specialEducation':
                    if (value !== true)
                        return
                default:
                    if (typeof value !== 'undefined')
                        this.$scope[param] = value
            }
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
