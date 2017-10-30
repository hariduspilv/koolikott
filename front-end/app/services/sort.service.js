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
    byType(a, b) {
        const getTypeLabel = (o) => this.$translate.instant(
            this.isPortfolio(o)
                ? 'PORTFOLIO_RESOURCE'
                : o.resourceTypes.length
                    ? o.resourceTypes[0].name
                    : 'UNKNOWN'
        )
        return this.compareStrings(
            getTypeLabel(a.learningObject || a),
            getTypeLabel(b.learningObject || b),
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
    byUpdatedAt(a, b) {
        return this.compareDates(
            a.updatedAt || a.updated,
            b.updatedAt || b.updated
        )
    }
    byReportCount(a, b) {
        return a.__reportCount - b.__reportCount
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
}
controller.$inject = [
    '$translate',
    'translationService',
    'taxonService'
]
service('sortService', controller)
}
