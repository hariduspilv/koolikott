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
        return a.materialCount - b.materialCount
    }
    compareNullIsNegative(ac, bc) {
        if (ac === undefined) ac = -1;
        if (bc === undefined) bc = -1;
        return ac - bc
    }
}
controller.$inject = [
    '$translate',
    'translationService',
    'taxonService'
]
service('sortService', controller)
}
