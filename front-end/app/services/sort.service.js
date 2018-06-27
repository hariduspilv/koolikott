'use strict'

{
class controller extends Controller {
    orderItems(data, sortBy) {
        this.language = this.translationService.getLanguage()

        const isDescending = sortBy.startsWith('-')
        const methodName = sortBy.replace(/^-/, '')
        const canSort = typeof this[methodName] === 'function'

        data = data.sort((a, b) =>
            a && b && canSort
                ? this[methodName].apply(this, isDescending ? [b, a] : [a, b])
                : 0
        )
    }
    compareStrings(a, b) {
        return a.toLowerCase().localeCompare(b.toLowerCase(), this.translationService.getLanguageCode())
    }
    compareDates(a, b) {
        const getDate = (v) => {
            const d = new Date(v)
            return isNaN(d) ? 0 : d
        }
        return getDate(a) - getDate(b)
    }
    byType(a, b) {
        const getTypeLabel = (o) => this.$translate.instant(
            this.isPortfolio(o)
                ? 'PORTFOLIO_RESOURCE'
                : 'MATERIAL'
        )
        return this.compareStrings(
            getTypeLabel(a.learningObject || a),
            getTypeLabel(b.learningObject || b)
        )
    }
    byTitle(a, b) {
        const getTitle = (o) => Array.isArray(o.titles) && o.titles.length
            ? (o.titles.filter(t => t.language == this.language)[0] || o.titles[0]).text || ''
            : o.title || ''

        return this.compareStrings(
            getTitle(a.learningObject || a.material || a.portfolio || a),
            getTitle(b.learningObject || b.material || b.portfolio || b)
        )
    }
    byCreatedAt(a, b) {
        return this.compareDates(
            a.createdAt || a.added,
            b.createdAt || b.added
        )
    }
    byLastReportedAt(a, b) {
        return this.compareDates(
            this.getMostRecentReportDate(a),
            this.getMostRecentReportDate(b)
        )
    }
    byLastChangedAt(a, b) {
        return this.compareDates(
            this.getMostRecentChangeDate(a),
            this.getMostRecentChangeDate(b)
        )
    }
    byCreatedBy(a, b) {
        const getName = (o) => {
            if (!o.createdBy && !o.creator)
                return this.translationService.instant('UNKNOWN')

            const { name, surname } = o.createdBy || o.creator
            return name +' '+ surname
        }
        return this.compareStrings(
            getName(a),
            getName(b)
        )
    }
    byReportedBy(a, b) {
        const getLabel = (o) =>
            o.__reporters.length > 1
                ? this.getCommaSeparatedReporters(o)
                : this.getReportedByLabel(o)

        return this.compareStrings(
            getLabel(a),
            getLabel(b)
        )
    }
    byChangedBy(a, b) {
        const getLabel = (o) =>
            o.__changers.length > 1
                ? this.getCommaSeparatedChangers(o)
                : this.getChangedByLabel(o)

        return this.compareStrings(
            getLabel(a),
            getLabel(b)
        )
    }
    byUpdatedAt(a, b) {
        return this.compareDates(
            a.updatedAt || a.updated,
            b.updatedAt || b.updated
        )
    }
    byReportCount(a, b) {
        return a.__reports.length - b.__reports.length
    }
    byChangeCount(a, b) {
        return a.__numChanges - b.__numChanges
    }
    byReason(a, b) {
        return this.compareStrings(
            this.$translate.instant(a.__reportLabelKey),
            this.$translate.instant(b.__reportLabelKey)
        )
    }
    byUsername(a, b) {
        return this.compareStrings(a.username, b.username)
    }
    byFullName(a, b) {
        return this.compareStrings(
            `${a.name} ${a.surname}`,
            `${b.name} ${b.surname}`
        )
    }
    byRole(a, b) {
        return this.compareStrings(
            this.$translate.instant(a.role),
            this.$translate.instant(b.role)
        )
    }
    byTaxons(a, b) {
        const getTaxonStr = (o) =>
            o.userTaxons.reduce(
                (str, taxon) => `${str}${str ? ', ' : ''}${
                    this.$translate.instant(
                        this.taxonService.getTaxonTranslationKey(taxon)
                    )
                }`,
                ''
            )
        return this.compareStrings(
            getTaxonStr(a),
            getTaxonStr(b)
        )
    }
    // Statistics table
    byEducationalContext(a, b) {
        return this.compareStrings(
            `${this.$translate.instant(a.educationalContext.name)}`,
            `${this.$translate.instant(b.educationalContext.name)}`
        )
    }
    byDomainOrSubject(a, b) {
        const getTranslation = (o, prop) => this.$translate.instant(prop.toUpperCase() + '_' + o[prop].name.toUpperCase())

        return this.compareStrings(
            a.subject ? `${getTranslation(a, 'domain')} › ${getTranslation(a, 'subject')}` : getTranslation(a, 'domain'),
            b.subject ? `${getTranslation(b, 'domain')} › ${getTranslation(b, 'subject')}` : getTranslation(b, 'domain')
        )
    }
    byExpert(a, b) {
        // if there is no userin the domain/subject row then make sure those rows fall to the end (after Z in A-Z)
        if (!a.user && b.user)
            return 1
        else if (a.user && !b.user)
            return -1
        else if (!a.user && !b.user)
            return 0

        return this.compareStrings(
            `${a.user ? a.user.name : ''} ${a.user ? a.user.surname : ''}`,
            `${b.user ? b.user.name : ''} ${b.user ? b.user.surname : ''}`
        )
    }
    byNewReviewed(a, b) {
        return this.compareNullIsNegative(a.reviewedLOCount, b.reviewedLOCount);
    }
    byImproperApproved(a, b) {
        return this.compareNullIsNegative(a.approvedReportedLOCount, b.approvedReportedLOCount)
    }
    byImproperDeleted(a, b) {
        return this.compareNullIsNegative(a.deletedReportedLOCount, b.deletedReportedLOCount)
    }
    byChangesAccepted(a, b) {
        return this.compareNullIsNegative(a.acceptedChangedLOCount, b.acceptedChangedLOCount)
    }
    byChangesRejected(a, b) {
        return this.compareNullIsNegative(a.rejectedChangedLOCount, b.rejectedChangedLOCount)
    }
    byPortfolioPublicized(a, b) {
        return this.compareNullIsNegative(a.publicPortfolioCount, b.publicPortfolioCount)
    }
    byPortfolioCreated(a, b) {
        return this.compareNullIsNegative(a.portfolioCount, b.portfolioCount)
    }
    byMaterialCreated(a, b) {
        return this.compareNullIsNegative(a.materialCount, b.materialCount)
    }
    compareNullIsNegative(a, b) {
        if (a === undefined) a = -1;
        if (b === undefined) b = -1;
        return a - b
    }
    byVersion(a, b) {
        return this.compareStrings(a.version, b.version)
    }
    byUrl(a, b) {
        return this.compareStrings(a.url, b.url)
    }
    byValidFrom(a, b) {
        return this.compareDates(a.validFrom, b.validFrom)
    }
    orderCardsByDate(a, b){
        const compareDates = this.compareDates(a.added, b.added);
        return compareDates !== 0 ? compareDates : a.id - b.id;
    }
    orderPortfoliosFirst(a, b){
        const aPort = this.isPortfolio(a);
        const bPort = this.isPortfolio(b);
        if (aPort && !bPort){
            return -1;
        }
        if (!aPort && bPort){
            return 1;
        }
        if (aPort && bPort){
            return this.compareDates(b.added, a.added);
        }
        return this.compareMaterials(a, b);
    }
    orderMaterialsFirst(a, b){
        const aMat = this.isMaterial(a);
        const bMat = this.isMaterial(b);
        if (aMat && !bMat){
            return -1;
        }
        if (!aMat && bMat){
            return 1;
        }
        if (!aMat && !bMat){
            return this.compareDates(b.added, a.added);
        }
        return this.compareMaterials(a, b);
    }

    compareMaterials(a, b){
        const aResources = a.resourceTypes;
        const bResources = b.resourceTypes;
        if (aResources.length && !bResources.length){
            return -1;
        }
        if (!aResources.length && bResources.length){
            return 1;
        }
        if (!aResources.length && !bResources.length){
            return this.compareDates(b.added, a.added);
        }
        const bResource = bResources.sort((a, b) => this.compareStrings(a.name, b.name))[0];
        const aResource = aResources.sort((a, b) => this.compareStrings(a.name, b.name))[0];
        const result = this.compareStrings(aResource.name, bResource.name);
        return result !== 0 ? result : this.orderCardsByDate(b, a);
    }
}
controller.$inject = [
    '$translate',
    'translationService',
    'taxonService'
]
service('sortService', controller)
}
