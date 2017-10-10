'use strict'

{
const BASIC_EDUCATION_ID = 2
const SECONDARY_EDUCATION_ID = 3
const VOCATIONAL_EDUCATION_ID = 4

class controller extends Controller {
    $onInit() {
        this.prefilling = false
        this.queryIn = this.queryIn || ''

        // Detailed search fields
        this.$scope.detailedSearch = {}

        // Languages
        this.metadataService.loadUsedLanguages(languages => this.$scope.languages = languages)
        this.$scope.detailedSearch.language = this.searchService.getLanguage()
        
        if (!this.searchService.getLanguage())
            this.$scope.detailedSearch.language = null

        // Used resource types
        this.metadataService.loadUsedResourceTypes(this.setUsedResourceTypes.bind(this))

        // Target groups
        this.$scope.detailedSearch.targetGroups = this.searchService.getTargetGroups()

        // Paid
        const isPaid = this.searchService.isPaid()
        this.$scope.detailedSearch.paid = typeof isPaid === 'boolean'
            ? !isPaid
            : false

        // Type
        this.$scope.detailedSearch.type = this.searchService.getType() && this.searchService.isValidType(this.searchService.getType())
            ? this.searchService.getType()
            : 'all'

        // Curriculum literature
        this.$scope.detailedSearch.onlyCurriculumLiterature = this.searchService.isCurriculumLiterature()

        // Special education
        this.$scope.detailedSearch.specialEducation = !!this.searchService.isSpecialEducation()

        // Issue date
        this.$scope.issueDateFirstYear = 2009
        this.$scope.issueDateLastYear = new Date().getFullYear()
        
        const issuedFrom = this.searchService.getIssuedFrom()
        if (
            issuedFrom &&
            issuedFrom >= this.$scope.issueDateFirstYear &&
            issuedFrom <= this.$scope.issueDateLastYear
        )   
            this.$scope.detailedSearch.issueDate = issuedFrom

        // Cross-curricular themes
        this.metadataService.loadCrossCurricularThemes(crossCurricularThemes =>
            this.$scope.crossCurricularThemes = crossCurricularThemes
        )
        this.$scope.detailedSearch.crossCurricularTheme = this.searchService.getCrossCurricularTheme()

        // Key competences
        this.metadataService.loadKeyCompetences(keyCompetences =>
            this.$scope.keyCompetences = keyCompetences
        )
        this.$scope.detailedSearch.keyCompetence = this.searchService.getKeyCompetence()

        if (this.$rootScope.isEditPortfolioMode && this.storageService.getPortfolio())
            this.setEditModePrefill()

        this.$scope.clear = this.clear.bind(this)

        if (this.accessor)
            this.accessor.clear = this.clear.bind(this)

        this.$scope.getLanguageTranslationKey = (languageCode) => 'LANGUAGE_' + languageCode.toUpperCase()
        this.$scope.getEffectiveIssueDate = () =>
            this.$scope.detailedSearch.issueDate && this.$scope.detailedSearch.issueDate != this.$scope.issueDateFirstYear
                ? this.$scope.detailedSearch.issueDate
                : undefined

        this.$scope.$on('detailedSearch:open', () =>
            this.$timeout(() =>
                this.metadataService.updateUsedResourceTypes(this.setUsedResourceTypes.bind(this))
            )
        )

        this.$scope.$watch(() => this.storageService.getPortfolio(), (newValue, oldValue) => {
            if (newValue && oldValue && (newValue !== oldValue))
                this.setEditModePrefill()
        }, true)

        this.$scope.$watch('detailedSearch', (newValue, oldValue) => {
            if (!this.$scope.detailedSearch.taxon)
                this.$scope.detailedSearch.educationalContext = null

            if (
                this.isVisible &&
                (!this.prefilling || newValue.main) &&
                this.hasSearchChanged(newValue, oldValue)
            ) {
                this.filterTypeSearch()
                this.search()
            } else if (this.prefilling)
                this.prefilling = false
        }, true)

        this.$rootScope.$watch('isEditPortfolioMode', (newValue, oldValue) => {
            if (newValue && (newValue !== oldValue))
                this.setEditModePrefill()
            else if (!newValue && (newValue !== oldValue))
                this.clear()
        }, true)
    }
    $onChanges({ queryIn }) {
        if (queryIn &&
            queryIn.currentValue !== queryIn.previousValue &&
            this.isVisible
        )
            this.parseSimpleSearchQuery(queryIn.currentValue)
    }
    hasSearchChanged(newValue, oldValue) {
        if (newValue.main !== oldValue.main) return true
        if (newValue.title !== oldValue.title) return true
        if (newValue.language !== oldValue.language) return true
        if (newValue.resourceType !== oldValue.resourceType) return true
        if (!_.isEqual(newValue.targetGroups, oldValue.targetGroups)) return true
        if (newValue.onlyCurriculumLiterature !== oldValue.onlyCurriculumLiterature) return true
        if (newValue.specialEducation !== oldValue.specialEducation) return true
        if (newValue.paid !== oldValue.paid) return true
        if (newValue.type !== oldValue.type) return true
        if (newValue.issueDate !== oldValue.issueDate) return true
        if (newValue.crossCurricularTheme !== oldValue.crossCurricularTheme) return true
        if (newValue.keyCompetence !== oldValue.keyCompetence) return true
        if (newValue.specialEducationalNeed !== oldValue.specialEducationalNeed) return true
        if (newValue.CLIL !== oldValue.CLIL) return true
        if (newValue.taxon !== oldValue.taxon && this.$scope.detailedSearch.taxon) {
            this.$scope.detailedSearch.educationalContext = this.taxonService.getEducationalContext(this.$scope.detailedSearch.taxon)
            
            if (this.$rootScope.isEditPortfolioMode)
                this.$scope.$broadcast('detailedSearch:taxonChange', {
                    newValue: newValue.taxon,
                    oldValue: oldValue.taxon
                })

            this.clearHiddenFields()
            return true
        }
    }
    clearHiddenFields() {
        var educationalContext = this.$scope.detailedSearch.educationalContext;

        // Only curriculum literature checkbox
        if (!educationalContext || (
                educationalContext.id != BASIC_EDUCATION_ID &&
                educationalContext.id != SECONDARY_EDUCATION_ID &&
                educationalContext.id != VOCATIONAL_EDUCATION_ID
            )
        )
            this.$scope.detailedSearch.onlyCurriculumLiterature = false

        // Special education checkbox
        if (!educationalContext || educationalContext.id != BASIC_EDUCATION_ID)
            this.$scope.detailedSearch.specialEducation = false

        // Cross-curricular themes and key competences
        if (!educationalContext || (
                educationalContext.id != BASIC_EDUCATION_ID &&
                educationalContext.id != SECONDARY_EDUCATION_ID
            )
        )
            this.$scope.detailedSearch.crossCurricularTheme = null
            this.$scope.detailedSearch.keyCompetence = null

        // Target groups
        if (educationalContext && educationalContext.id === VOCATIONAL_EDUCATION_ID)
            this.$scope.detailedSearch.targetGroups = []
    }
    clear() {
        this.$scope.detailedSearch = {
            'main': '',
            'paid': false,
            'onlyCurriculumLiterature': false,
            'CLIL': false,
            'targetGroups': [],
            'specialEducation': false,
            'specialEducationalNeed': false,
            'issueDate': this.$scope.issueDateFirstYear,
            'type': 'all',
            'taxon': {},
            'language': null
        }

        this.$scope.$broadcast('targetGroupSelector:clear')

        if (this.$rootScope.isEditPortfolioMode)
            this.$scope.detailedSearch.type = 'material'

        this.$scope.$parent.clearTaxonSelector()

        if (this.accessor && typeof this.accessor.clearSimpleSearch === 'function')
            this.accessor.clearSimpleSearch()
    }
    setUsedResourceTypes(resourceTypes) {
        this.$scope.usedResourceTypes = resourceTypes

        if (!resourceTypes.filter(t => t.name === 'PORTFOLIO_RESOURCE').length)
            this.$scope.usedResourceTypes.push({
                name: 'PORTFOLIO_RESOURCE'
            })
    }
    setEditModePrefill() {
        if (this.$rootScope.isEditPortfolioMode && this.storageService.getPortfolio()) {
            this.$scope.detailedSearch.taxon = this.storageService.getPortfolio().taxons[0]
            this.$scope.detailedSearch.targetGroups = this.storageService.getPortfolio().targetGroups

            this.$scope.$broadcast('detailedSearch:prefillTaxon', this.storageService.getPortfolio().taxons[0])
            this.$scope.$broadcast('detailedSearch:prefillTargetGroup', this.storageService.getPortfolio().targetGroups)

            this.prefilling = true

            this.$scope.detailedSearch.type = 'material'
        }
    }
    search() {
        this.searchService.setSearch(this.createSimpleSearchQuery())
        this.searchService.setPaid(!this.$scope.detailedSearch.paid)
        this.searchService.setType(this.$scope.detailedSearch.type)
        this.searchService.setLanguage(this.$scope.detailedSearch.language)
        this.searchService.setTaxon(this.$scope.detailedSearch.taxon ? [this.$scope.detailedSearch.taxon.id] : null)
        this.searchService.setTargetGroups(this.$scope.detailedSearch.targetGroups || null)
        this.searchService.setResourceType(this.$scope.detailedSearch.resourceType || null)
        this.searchService.setCurriculumLiterature(this.$scope.detailedSearch.onlyCurriculumLiterature)
        this.searchService.setIsSpecialEducation(this.$scope.detailedSearch.specialEducation)
        this.searchService.setIssuedFrom(this.$scope.getEffectiveIssueDate() || null)
        this.searchService.setCrossCurricularTheme(this.$scope.detailedSearch.crossCurricularTheme || null)
        this.searchService.setKeyCompetence(this.$scope.detailedSearch.keyCompetence || null)

        this.$location.url(this.searchService.getURL())
    }
    createSimpleSearchQuery() {
        return (
            (this.$scope.detailedSearch.main || '') + ' ' +
            this.getTextFieldsAsQuery() + ' ' +
            this.getCheckboxesAsQuery()
        ).trim()
    }
    getTextFieldsAsQuery() {
        const addQuotesIfNecessary = (str) => /\s/g.test(str) ? `"${str}"` : str
        const { title, combinedDescription, author } = this.$scope.detailedSearch
        let query = ''

        if (title)
            query += 'title:' + addQuotesIfNecessary(title)

        if (combinedDescription)
            query += ' description:' + addQuotesIfNecessary(combinedDescription) + ' summary:' + addQuotesIfNecessary(combinedDescription)

        if (author)
            query += ' author:' + addQuotesIfNecessary(author)

        return query.trim()
    }
    getCheckboxesAsQuery() {
        return this.$scope.detailedSearch.CLIL === true && this.$scope.detailedSearch.educationalContext.id === 2
            ? 'LAK "Lõimitud aine- ja keeleõpe"'
            : this.$scope.detailedSearch.specialEducationalNeed === true && this.$scope.detailedSearch.educationalContext.id === 4
                ? 'HEV "Hariduslik erivajadus"'
                : ''
    }
    parseSimpleSearchQuery(query) {
        const titleRegex = /(^|\s)(title:([^\s\"]\S*)|title:\"(.*?)\"|title:)/g
        const descriptionRegex = /(^|\s)(description:([^\s\"]\S*)|description:\"(.*?)\"|description:|summary:([^\s\"]\S*)|summary:\"(.*?)\"|summary:)/g
        const authorRegex = /(^|\s)(author:([^\s\"]\S*)|author:\"(.*?)\"|author:)/g
        const clilRegex = /(^|\s)(LAK|"Lõimitud aine- ja keeleõpe")(?=\s|$)/g
        const specialEducationalNeedRegex = /(^|\s)(HEV|"Hariduslik erivajadus")(?=\s|$)/g

        let firstTitle
        let firstDescription
        let firstAuthor
        let main = query

        let title
        while (title = titleRegex.exec(query)) {
            // Remove token from main query
            main = main.replace(title[2], '')

            // Get token content
            if (!firstTitle)
                firstTitle = title[3] || title[4]
        }

        let description
        while (description = descriptionRegex.exec(query)) {
            main = main.replace(description[2], '')

            if (!firstDescription)
                firstDescription = description[3] || description[4] || description[5] || description[6]
        }

        let author
        while (author = authorRegex.exec(query)) {
            main = main.replace(author[2], '')

            if (!firstAuthor)
                firstAuthor = author[3] || author[4]
        }

        let keyword
        while (keyword = clilRegex.exec(query)) {
            main = main.replace(keyword[2], '')
            this.$scope.detailedSearch.CLIL = true
        }
        while (keyword = specialEducationalNeedRegex.exec(query)) {
            main = main.replace(keyword[2], '')
            this.$scope.detailedSearch.specialEducationalNeed = true
        }

        this.$scope.detailedSearch.main = main.replace(/\s{2,}/g, ' ').trim() || ''
        this.$scope.detailedSearch.title = firstTitle || this.$scope.detailedSearch.title || ''
        this.$scope.detailedSearch.combinedDescription = firstDescription || this.$scope.detailedSearch.combinedDescription || ''
        this.$scope.detailedSearch.author = firstAuthor || this.$scope.detailedSearch.author || ''

        this.mainField = this.$scope.detailedSearch.main
    }
    filterTypeSearch() {
        if (this.$scope.detailedSearch.resourceType === 'all')
            this.$scope.detailedSearch.type = 'all'
        else
        if (this.$scope.detailedSearch.resourceType === 'PORTFOLIO_RESOURCE')
            this.$scope.detailedSearch.type = 'portfolio'
        else
        if (this.$rootScope.isEditPortfolioMode)
            this.$scope.detailedSearch.type = 'material'
        else
            this.$scope.detailedSearch.type = 'all'
    }
}
controller.$inject = [
    '$scope',
    '$rootScope',
    '$location',
    '$timeout',
    'metadataService',
    'searchService',
    'storageService',
    'taxonService'
]

angular.module('koolikottApp').component('dopDetailedSearch', {
    bindings: {
        queryIn: '<',
        mainField: '=',
        accessor: '=',
        isVisible: '='
    },
    templateUrl: 'directives/detailedSearch/detailedSearch.html',
    controller
})
}
