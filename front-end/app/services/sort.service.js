function SortService(translationService, taxonService) {
    function orderBySubmittedBy(a, b) {
        let aName = a.creator ? a.creator.name + ' ' + a.creator.surname : translationService.instant('UNKNOWN');
        let bName = b.creator ? b.creator.name + ' ' + b.creator.surname : translationService.instant('UNKNOWN');

        if (a.reportCount > 1)
            aName = translationService.instant('REPORTED_BY_MULTIPLE_USERS');
        if (b.reportCount > 1)
            bName = translationService.instant('REPORTED_BY_MULTIPLE_USERS');

        return compareStrings(aName, bName);
    }

    function orderByChanger(a, b) {
        let aName = a.changer.name + ' ' + a.changer.surname;
        let bName = b.changer.name + ' ' + b.changer.surname;

        return compareStrings(aName, bName);
    }

    function orderByFullname(a, b) {
        let aName = `${a.name} ${a.surname}`;
        let bName = `${b.name} ${b.surname}`;

        return compareStrings(aName, bName);
    }

    function orderByTitle(a, b) {
        const getTitle = (o) => Array.isArray(o.titles) && o.titles.length
            ? o.titles[0].text || ''
            : o.title || ''

        return compareStrings(
            getTitle(a.learningObject || a),
            getTitle(b.learningObject || b)
        )
    }

    function orderByTaxon(a, b) {
        let aTaxon = a.userTaxons.map((taxon) => {
            return translationService.instant(taxonService.getTaxonTranslationKey(taxon))
        }).join(", ");

        let bTaxon = b.userTaxons.map((taxon) => {
            return translationService.instant(taxonService.getTaxonTranslationKey(taxon))
        }).join(", ");

        return compareStrings(aTaxon, bTaxon);
    }

    function orderByAddedBy(a, b) {
        const getName = (o) =>
            o.creator
                ? o.creator.name + ' ' + o.creator.surname
                : translationService.instant('UNKNOWN')

        return compareStrings(
            getName(a.learningObject || a),
            getName(b.learningObject || b)
        )
    }

    function orderByAddedAt(a, b) {
        return compareDates(
            (a.learningObject || a).added,
            (b.learningObject || b).added
        )
    }

    function compareStrings(a, b) {
        return a.toLowerCase().localeCompare(b.toLowerCase(), translationService.getLanguageCode());
    }

    function compareDates(a, b) {
        const getDate = (v) => {
            const d = new Date(v)
            return isNaN(d) ? 0 : d
        }
        return getDate(a) - getDate(b)
    }

    return {
        orderItems(data, order) {
            data = data.sort((a, b) => {
                if (!a || !b) return

                switch (order.replace(/^-/, '')) {
                    case "byFullName":
                        return orderByFullname(a, b)
                    case "byUsername":
                        return compareStrings(a.username, b.username)
                    case "byRole":
                        return compareStrings(
                            translationService.instant(a.role),
                            translationService.instant(b.role)
                        )
                    case "byTaxons":
                        return orderByTaxon(a, b)
                    case "bySubmittedAt":
                        compareDates(a.added, b.added)
                    case "byReportCount":
                        return a.reportCount - b.reportCount
                    case "bySubmittedBy":
                        return orderBySubmittedBy(a, b)
                    case "byTitle":
                        return orderByTitle(a, b)
                    case "byChanger":
                        return orderByChanger(a, b)
                    case "byUpdatedAt":
                        return compareDates(a.updated, b.updated)
                    case "byAddedAt":
                        return orderByAddedAt(a, b)
                    case "byAddedBy":
                        return orderByAddedBy(a, b)
                    default:
                        return 0
                }
            })

            // leading “minus” means descending
            if (order.slice(0, 1) === '-')
                data.reverse()
        }
    }
}

angular.module('koolikottApp')
    .service('sortService', ['translationService', 'taxonService', SortService]);
