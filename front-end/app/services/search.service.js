'use strict'

{
class controller extends Controller {
    constructor(...args) {
        super(...args)

        this.groups = ['title', 'tag', 'description', 'author', 'publisher', 'recommended', 'portfolioTitle', 'summary']
        this.searchURLbase = 'search/result?'
        this.types = ['portfolio', 'material', 'all']
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
        this.filterURL = '&filter='
        this.materialTitleURL = '&materialTitle='
        this.materialDescriptionURL = '&materialDescription='
        this.materialTagURL = '&materialTag='
        this.materialAuthorURL = '&materialAuthor='
        this.materialPublisherURL = '&materialPublisher='
        this.materialAllURL = '&materialAll='
        this.portfolioTitleURL = '&portfolioTitle='
        this.portfolioDescriptionURL = '&portfolioDescription='
        this.portfolioTagURL = '&portfolioTag='
        this.portfolioAuthorURL = '&portfolioAuthor='
        this.portfolioPublisherURL = '&portfolioPublisher='
        this.portfolioAllURL = '&portfolioAll='
        this.isExactURL = '&isExact='
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
            isGrouped: '',
            filter: '',
            materialTitle: '',
            materialDescription: '',
            materialTag: '',
            materialAuthor: '',
            materialPublisher: '',
            materialAll: '',
            portfolioTitle: '',
            portfolioDescription: '',
            portfolioTag: '',
            portfolioAuthor: '',
            portfolioPublisher: '',
            portfolioAll: '',
            isExact: ''
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
        return query && query.replace(/[<>/]/g, "");
    }
    escapeAllQuery(query) {
        return query && query.replace(/[<>/]/g, "").replace(/\+/g, "%2B");
    }
    asArray(value) {
        return Array.isArray(value) ? value : [value]
    }
    setQuery(query) {
        this.search.query = this.escapeQuery(query)
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
    setIsExact(isExact) {
        this.search.isExact = isExact
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
    setMaterialTitle(title){
        this.search.materialTitle = title
    }
    setMaterialDescription(description){
        this.search.materialDescription = description
    }
    setMaterialTag(tag){
        this.search.materialTag = tag
    }
    setMaterialAuthor(author){
        this.search.materialAuthor = author
    }
    setMaterialPublisher(publisher){
        this.search.materialPublisher = publisher
    }
    setMaterialAll(all){
        this.search.materialAll = all
    }
    setPortfolioTitle(title){
        this.search.portfolioTitle = title
    }
    setPortfolioDescription(description){
        this.search.portfolioDescription = description
    }
    setPortfolioTag(tag){
        this.search.portfolioTag = tag
    }
    setPortfolioAuthor(author){
        this.search.portfolioAuthor = author
    }
    setPortfolioPublisher(publisher){
        this.search.portfolioPublisher = publisher
    }
    setPortfolioAll(all){
        this.search.portfolioAll = all
    }
    disableAllMaterial(){
        this.search.materialTitle = ''
    }
    setFilter(filter) {
        this.search.filter = filter
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
        if (keyCompetence) this.setKeyCompetence(keyCompetence)
        return this.search.keyCompetence
    }
    getSort() {
        const { sort } = this.$location.search()
        if (sort) this.setSort(sort)
        return this.search.sort
    }
    getSortDirection() {
        const { sortDirection } = this.$location.search()
        if (sortDirection) this.setSortDirection(sortDirection)
        return this.search.sortDirection
    }
    getMaterialTitle() {
        const { materialTitle } = this.$location.search()
        if (materialTitle) this.setMaterialTitle(materialTitle)
        return this.search.materialTitle
    }
    getMaterialDescription() {
        const { materialDescription } = this.$location.search()
        if (materialDescription) this.setMaterialDescription(materialDescription)
        return this.search.materialDescription
    }
    getMaterialTag() {
        const { materialTag } = this.$location.search()
        if (materialTag) this.setMaterialTag(materialTag)
        return this.search.materialTag
    }
    getMaterialAuthor() {
        const { materialAuthor } = this.$location.search()
        if (materialAuthor) this.setMaterialAuthor(materialAuthor)
        return this.search.materialAuthor
    }
    getMaterialPublisher() {
        const { materialPublisher } = this.$location.search()
        if (materialPublisher) this.setMaterialPublisher(materialPublisher)
        return this.search.materialPublisher
    }
    getMaterialAll() {
        const { materialAll } = this.$location.search()
        if (materialAll) this.setMaterialAll(materialAll)
        return this.search.materialAll
    }
    getPortfolioTitle() {
        const { portfolioTitle } = this.$location.search()
        if (portfolioTitle) this.setPortfolioTitle(portfolioTitle)
        return this.search.portfolioTitle
    }
    getPortfolioDescription() {
        const { portfolioDescription } = this.$location.search()
        if (portfolioDescription) this.setPortfolioDescription(portfolioDescription)
        return this.search.portfolioDescription
    }
    getPortfolioTag() {
        const { portfolioTag } = this.$location.search()
        if (portfolioTag) this.setPortfolioTag(portfolioTag)
        return this.search.portfolioTag
    }
    getPortfolioAuthor() {
        const { portfolioAuthor } = this.$location.search()
        if (portfolioAuthor) this.setPortfolioAuthor(portfolioAuthor)
        return this.search.portfolioAuthor
    }
    getPortfolioPublisher() {
        const { portfolioPublisher } = this.$location.search()
        if (portfolioPublisher) this.setPortfolioPublisher(portfolioPublisher)
        return this.search.portfolioPublisher
    }
    getPortfolioAll() {
        const { portfolioAll } = this.$location.search()
        if (portfolioAll) this.setPortfolioAll(portfolioAll)
        return this.search.portfolioAll
    }
    isCurriculumLiterature() {
        const { curriculumLiterature } = this.$location.search()
        if (curriculumLiterature) this.setIsCurriculumLiterature(curriculumLiterature === 'true')
        return this.search.isCurriculumLiterature
    }
    isFavorites() {
        const { favorites } = this.$location.search()
        if (favorites) this.setIsFavorites(favorites === 'true')
        return this.search.isFavorites
    }
    isRecommended() {
        const { recommended } = this.$location.search()
        if (recommended) this.setIsRecommended(recommended === 'true')
        return this.search.isRecommended
    }
    isExact() {
        const { isExact } = this.$location.search()
        if (isExact) this.setIsExact(isExact === 'true')
        return this.search.isExact
    }
    isGrouped() {
        const { isGrouped } = this.$location.search()
        if (isGrouped) this.setIsGrouped(isGrouped === 'true')
        if (!this.getQuery()) this.setIsGrouped(false)
        else if (this.groups.some((group) => this.getQuery().startsWith(group + ':'))) this.setIsGrouped(false)
        return this.search.isGrouped
    }
    isFilter() {
        const { filter } = this.$location.search()
        if (filter) this.setFilter(filter === 'true')
        return this.search.filter
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
        return this.types.includes(type)
    }
    shouldBeGrouped() {
        const query = this.search.query
        return Boolean(query) && !this.groups.some((group) => query.startsWith(group + ':'))
    }
    getSearchURLbase() {
        return this.searchURLbase
    }
    getURL(isBackendQuery) {
        return this.searchURLbase + this.getQueryURL(isBackendQuery)
    }
    getQueryURL(isBackendQuery) {
        let searchURL = 'q='

        if (this.search.query) searchURL += this.escapeAllQuery(this.search.query)

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

        if (typeof this.search.isFavorites === 'boolean' && this.search.isFavorites)
            searchURL += this.isFavoritesURL + this.search.isFavorites

        if (typeof this.search.isRecommended === 'boolean' && this.search.isRecommended)
            searchURL += this.isRecommendedURL + this.search.isRecommended

        if (this.search.sort && this.search.sortDirection)
            searchURL += this.sortURL + this.search.sort + this.sortDirectionURL + this.search.sortDirection

        if (this.search.isGrouped) searchURL += this.isGroupedURL + this.search.isGrouped

        if (this.search.filter) searchURL += this.filterURL + this.search.filter

        if (this.search.materialTitle) searchURL += this.materialTitleURL + this.search.materialTitle
        if (this.search.materialDescription) searchURL += this.materialDescriptionURL + this.search.materialDescription
        if (this.search.materialTag) searchURL += this.materialTagURL + this.search.materialTag
        if (this.search.materialAuthor) searchURL += this.materialAuthorURL + this.search.materialAuthor
        if (this.search.materialPublisher) searchURL += this.materialPublisherURL + this.search.materialPublisher
        if (this.search.materialAll) searchURL += this.materialAllURL + this.search.materialAll
        if (this.search.portfolioTitle) searchURL += this.portfolioTitleURL + this.search.portfolioTitle
        if (this.search.portfolioDescription) searchURL += this.portfolioDescriptionURL + this.search.portfolioDescription
        if (this.search.portfolioTag) searchURL += this.portfolioTagURL + this.search.portfolioTag
        if (this.search.portfolioAuthor) searchURL += this.portfolioAuthorURL + this.search.portfolioAuthor
        if (this.search.portfolioPublisher) searchURL += this.portfolioPublisherURL + this.search.portfolioPublisher
        if (this.search.portfolioAll) searchURL += this.portfolioAllURL + this.search.portfolioAll
        if (this.search.isExact) searchURL += this.isExactURL + this.search.isExact

        return searchURL
    }
}
controller.$inject = [
    '$location'
]
factory('searchService', controller)
}
