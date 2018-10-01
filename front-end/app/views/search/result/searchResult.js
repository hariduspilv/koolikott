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
        if (newValue !== oldValue) this.setParams(newValue.q)
    }
    setParams(q) {
        q  ?  this.searchService.setQuery(q) : q = this.searchService.getQuery()

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
            favorites: this.searchService.isFavorites(),
            recommended: this.searchService.isRecommended(),
            sort: this.searchService.getSort(),
            sortDirection: this.searchService.getSortDirection(),
            isGrouped: this.searchService.isGrouped(),
            materialTitle: this.searchService.getMaterialTitle(),
            materialDescription: this.searchService.getMaterialDescription(),
            materialTag: this.searchService.getMaterialTag(),
            materialAuthor: this.searchService.getMaterialAuthor(),
            materialPublisher: this.searchService.getMaterialPublisher(),
            materialAll: this.searchService.getMaterialAll(),
            portfolioTitle: this.searchService.getPortfolioTitle(),
            portfolioDescription: this.searchService.getPortfolioDescription(),
            portfolioTag: this.searchService.getPortfolioTag(),
            portfolioAuthor: this.searchService.getPortfolioAuthor(),
            portfolioPublisher: this.searchService.getPortfolioPublisher(),
            portfolioAll: this.searchService.getPortfolioAll(),
            isExact: this.searchService.isExact(),
        }
        Object.keys(params).forEach((param) => {
            const value = params[param]
            if (this.validParams(param, value)) return
            this.$scope.params[param] = value
        })
    }

    validParams(param, value) {
        return (param === 'taxon' && (!value || !value[0])) ||
            (param === 'paid' && value !== false) ||
            (param === 'type' && (!value || !this.searchService.isValidType(value))) ||
            (param === 'specialEducation' && value !== true) ||
            (typeof value === 'undefined' || value === '');
    }
}
controller.$inject = [
    '$scope',
    '$location',
    '$route',
    'searchService'
]
angular.module('koolikottApp').controller('searchResultController', controller)
}
