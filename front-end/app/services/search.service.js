'use strict'

{
class controller extends Controller {
    constructor(...args) {
        super(...args)

        this.searchURLbase = 'search/result?'
        this.taxonURL = '&taxon='
        this.paidURL = '&paid='
        this.typeURL = '&type='
        this.languageURL = '&language='
        this.targetGroupsURL = '&targetGroup='
        this.resourceTypeURL = '&resourceType='
        this.isSpecialEducationURL = '&specialEducation='
        this.issuedFromURL = '&issuedFrom='
        this.crossCurricularThemeURL = '&crossCurricularTheme='
        this.keyCompetenceURL = '&keyCompetence='
        this.isCurriculumLiteratureURL = '&curriculumLiterature='
        this.isFavoritesURL = '&favorites='
        this.isRecommendedURL = '&recommended='
        this.sortURL = '&sort='
        this.sortDirectionURL = '&sortDirection='
        this.isGroupedURL = '&isGrouped='
        this.search = {
            query: '',
            taxons: [],
            paid: '',
            type: '',
            language: '',
            targetGroups: [],
            resourceType: '',
            isSpecialEducation: '',
            issuedFrom: '',
            crossCurricularTheme: '',
            keyCompetence: '',
            isCurriculumLiterature: '',
            isPreferred: '',
            sort: '',
            sortDirection: '',
            isGrouped: ''
        }

        const searchObject = this.$location.search()
        for (let prop in searchObject)
            if (searchObject.hasOwnProperty(prop) && searchObject[prop] !== null)
                switch (prop) {
                    case 'targetGroup':
                        this.search[prop] = this.asArray(searchObject[prop])
                        break
                    case 'taxon':
                        this.search[prop] = this.asArray(searchObject[prop])
                        break
                    default:
                        this.search[prop] = searchObject[prop]
                }
    }
    escapeQuery(query) {
        return query.replace(/\+/g, "%2B")
    }
    arrayToLowerCase(upperCaseArray) {
        const lowerCaseArray = []

        for (let i = 0; i < upperCaseArray.length; i++)
            if (upperCaseArray[i] && this.isString(upperCaseArray[i]))
                lowerCaseArray.push(upperCaseArray[i].toLowerCase())

        return lowerCaseArray
    }
    arrayToUpperCase(lowerCaseArray) {
        const upperCaseArray = []

        for (let i = 0; i < lowerCaseArray.length; i++)
            if (lowerCaseArray[i] && this.isString(lowerCaseArray[i]))
                upperCaseArray.push(lowerCaseArray[i].toUpperCase())

        return upperCaseArray
    }
    isString(value) {
        return typeof value === 'string' || value instanceof String
    }
    asArray(value) {
        return Array.isArray(value) ? value : [value]
    }
    setQuery(query) {
        this.search.query = query
    }
    setTaxon(taxon) {
        this.search.taxons = taxon
    }
    setPaid(paid) {
        this.search.paid = paid
    }
    setType(type) {
        this.search.type = type
    }
    setLanguage(language) {
        this.search.language = language
    }
    setTargetGroups(targetGroups) {
        this.search.targetGroups = this.arrayToLowerCase(this.asArray(targetGroups))
    }
    setResourceType(resourceType) {
        this.search.resourceType = resourceType
    }
    setIsSpecialEducation(isSpecialEducation) {
        this.search.isSpecialEducation = isSpecialEducation
    }
    setIssuedFrom(issuedFrom) {
        this.search.issuedFrom = issuedFrom
    }
    setCrossCurricularTheme(crossCurricularTheme) {
        this.search.crossCurricularTheme = crossCurricularTheme
    }
    setKeyCompetence(keyCompetence) {
        this.search.keyCompetence = keyCompetence
    }
    setIsCurriculumLiterature(isCurriculumLiterature) {
        this.search.isCurriculumLiterature = isCurriculumLiterature
    }
    setIsFavorites(isFavorites) {
        this.search.isFavorites = isFavorites
    }
    setIsRecommended(isRecommended) {
        this.search.isRecommended = isRecommended
    }
    setSort(sort) {
        this.search.sort = sort
    }
    setSortDirection(sortDirection) {
        this.search.sortDirection = sortDirection
    }
    setIsGrouped(isGrouped) {
        this.search.isGrouped = isGrouped
    }
    queryExists() {
        const searchObject = this.$location.search()
        return !!(
            searchObject.q ||
            searchObject.taxon ||
            searchObject.paid === 'false' ||
            (searchObject.type && this.isValidType(searchObject.type)) ||
            searchObject.language ||
            searchObject.targetGroup ||
            searchObject.resourceType ||
            searchObject.specialEducation ||
            searchObject.issuedFrom ||
            searchObject.crossCurricularTheme ||
            searchObject.keyCompetence ||
            searchObject.curriculumLiterature ||
            (searchObject.sort && searchObject.sortDirection)
        )
    }
    getQuery() {
        const { q } = this.$location.search()
        if (q) this.setQuery(q)
        return this.search.query
    }
    getTaxon() {
        const { taxon } = this.$location.search()
        if (taxon) this.setTaxon(this.asArray(taxon))
        return this.search.taxons
    }
    isPaid() {
        const { paid } = this.$location.search()
        if (paid) this.setPaid(paid.toString() === 'true')
        return this.search.paid
    }
    getType() {
        const { type } = this.$location.search()
        if (type) this.setType(type)
        return this.search.type
    }
    getLanguage() {
        const { language } = this.$location.search()
        if (language) this.setLanguage(language)
        return this.search.language
    }
    getTargetGroups() {
        const { targetGroup } = this.$location.search()
        if (targetGroup) this.setTargetGroups(targetGroup)
        return this.arrayToUpperCase(this.search.targetGroups)
    }
    getResourceType() {
        const { resourceType } = this.$location.search()
        if (resourceType) this.setResourceType(resourceType)
        return this.search.resourceType
    }
    isSpecialEducation() {
        const { specialEducation } = this.$location.search()
        if (specialEducation) this.setIsSpecialEducation(specialEducation === 'true')
        return this.search.isSpecialEducation
    }
    getIssuedFrom() {
        const { issuedFrom } = this.$location.search()
        if (issuedFrom) this.setIssuedFrom(issuedFrom)
        return this.search.issuedFrom
    }
    getCrossCurricularTheme() {
        const { crossCurricularTheme } = this.$location.search()
        if (crossCurricularTheme) this.setCrossCurricularTheme(crossCurricularTheme)
        return this.search.crossCurricularTheme
    }
    getKeyCompetence() {
        const { keyCompetence } = this.$location.search()

        if (keyCompetence)
            this.setKeyCompetence(keyCompetence)

        return this.search.keyCompetence
    }
    getSort() {
        const { sort } = this.$location.search()

        if (sort)
            this.setSort(sort)

        return this.search.sort
    }
    getSortDirection() {
        const { sortDirection } = this.$location.search()

        if (sortDirection)
            this.setSortDirection(sortDirection)

        return this.search.sortDirection
    }
    isCurriculumLiterature() {
        const { curriculumLiterature } = this.$location.search()

        if (curriculumLiterature)
            this.setIsCurriculumLiterature(curriculumLiterature === 'true')

        return this.search.isCurriculumLiterature
    }
    isFavorites() {
        const { favorites } = this.$location.search()

        if (favorites)
            this.setIsFavorites(favorites === 'true')

        return this.search.isFavorites
    }
    isRecommended() {
        const { recommended } = this.$location.search()

        if (recommended)
            this.setIsRecommended(recommended === 'true')

        return this.search.isRecommended
    }
    isGrouped() {
        const { isGrouped } = this.$location.search()
        if (isGrouped) this.setIsGrouped(isGrouped === 'true')
        return this.search.isGrouped
    }
    clearFieldsNotInSimpleSearch() {
        this.search.taxons = []
        this.search.paid = ''
        this.search.type = ''
        this.search.language = ''
        this.search.targetGroups = []
        this.search.resourceType = ''
        this.search.isSpecialEducation = ''
        this.search.issuedFrom = ''
        this.search.crossCurricularTheme = ''
        this.search.keyCompetence = ''
        this.search.isCurriculumLiterature = ''
        this.search.sort = ''
        this.search.sortDirection = ''
    }
    isValidType(type) {
        return type === 'material' || type === 'portfolio' || type === 'all'
    }
    getSearchURLbase() {
        return this.searchURLbase
    }
    getURL(isBackendQuery) {
        return this.searchURLbase + this.getQueryURL(isBackendQuery)
    }
    getQueryURL(isBackendQuery) {
        let searchURL = 'q='

        if (this.search.query) searchURL += this.escapeQuery(this.search.query)

        if (this.search.taxons)
            for (let i = 0; i < this.search.taxons.length; i++)
                if (this.search.taxons[i]) searchURL += this.taxonURL + this.search.taxons[i]

        if (this.search.paid.toString() === 'false')
            searchURL += this.paidURL + this.search.paid;

        if (this.search.type && this.isValidType(this.search.type))
            searchURL += this.typeURL + this.search.type

        if (this.search.language)
            searchURL += this.languageURL + this.search.language

        if (this.search.targetGroups)
            for (let i = 0; i < this.search.targetGroups.length; i++)
                searchURL += this.targetGroupsURL + (
                    // Enums are case sensitive, so they must be uppercase for the back-end query
                    isBackendQuery && this.search.targetGroups[i]
                        ? this.search.targetGroups[i].toUpperCase()
                        : this.search.targetGroups[i]
                )

        if (this.search.resourceType)
            searchURL += this.resourceTypeURL + this.search.resourceType

        if (this.search.isSpecialEducation.toString() === 'true')
            searchURL += this.isSpecialEducationURL + this.search.isSpecialEducation

        if (this.search.issuedFrom)
            searchURL += this.issuedFromURL + this.search.issuedFrom

        if (this.search.crossCurricularTheme)
            searchURL += this.crossCurricularThemeURL + this.search.crossCurricularTheme

        if (this.search.keyCompetence)
            searchURL += this.keyCompetenceURL + this.search.keyCompetence

        if (this.search.isCurriculumLiterature)
            searchURL += this.isCurriculumLiteratureURL + this.search.isCurriculumLiterature

        if (typeof this.search.isFavorites === 'boolean')
            searchURL += this.isFavoritesURL + this.search.isFavorites

        if (typeof this.search.isRecommended === 'boolean')
            searchURL += this.isRecommendedURL + this.search.isRecommended

        if (this.search.sort && this.search.sortDirection)
            searchURL += this.sortURL + this.search.sort + this.sortDirectionURL + this.search.sortDirection

        if (this.search.isGrouped) searchURL += this.isGroupedURL + this.search.isGrouped

        return searchURL
    }
}
controller.$inject = [
    '$location'
]
factory('searchService', controller)
}
