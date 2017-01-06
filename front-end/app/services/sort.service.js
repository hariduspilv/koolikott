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
        let aTitle = (a.learningObject.titles ? a.learningObject.titles[0].text : undefined) || a.learningObject.title;
        let bTitle = (b.learningObject.titles ? b.learningObject.titles[0].text : undefined)  || b.learningObject.title;

        return compareStrings(aTitle, bTitle);
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

    function compareStrings(a, b) {
        return a.toLowerCase().localeCompare(b.toLowerCase(), translationService.getLanguageCode());
    }

    return {
        orderItems(data, order) {
            data = data.sort((a, b) => {
                if (!a || !b) return;

                switch (order) {
                    case "byFullName": case "-byFullName":
                        return orderByFullname(a, b);
                    case "byUsername": case "-byUsername":
                        return compareStrings(a.username, b.username);
                    case "byRole": case "-byRole":
                        return translationService.instant(a.role).localeCompare(translationService.instant(b.role));
                    case "byTaxons": case "-byTaxons":
                        return orderByTaxon(a, b);
                    case "bySubmittedAt": case "-bySubmittedAt":
                        return new Date(b.added) - new Date(a.added);
                    case "byReportCount": case "-byReportCount":
                        return b.reportCount - a.reportCount;
                    case "bySubmittedBy": case "-bySubmittedBy":
                        return orderBySubmittedBy(a, b);
                    case "byTitle": case "-byTitle":
                        return orderByTitle(a, b);
                    case "byChanger": case "-byChanger":
                        return orderByChanger(a, b);
                    case "byUpdatedAt": case "-byUpdatedAt":
                        return new Date(b.updated) - new Date(a.updated);
                    case "byAddedAt": case "-byAddedAt":
                        return new Date(b.added) - new Date(a.added);
                    default:
                        return 0;
                }
            });

            if (order.slice(0, 1) === '-')
                data.reverse();
        }
    }
}

angular.module('koolikottApp')
    .service('sortService', ['translationService', 'taxonService', SortService]);
