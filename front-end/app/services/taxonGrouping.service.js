
angular.module('koolikottApp')
    .service('taxonGroupingService', ['taxonService', 'translationService', TaxonGroupingService]);

function TaxonGroupingService(taxonService, translationService) {

    function getDomainNameTranslationKey(domain) {
        return 'DOMAIN_' + domain.name.toUpperCase()
    }

    function getSubjectNameTranslationKey(subject) {
        return 'SUBJECT_' + subject.name.toUpperCase()
    }

    function getUniqueEducationalContexts(taxons) {
        let educationalContexts = [];
        taxons.forEach(function (taxon) {
            let edCtx = taxonService.getEducationalContext(taxon);
            if (edCtx && !educationalContexts.includes(edCtx.name)) educationalContexts.push(edCtx.name);
        });

        return educationalContexts;
    }

    function getUniqueTopics(taxons) {
        let topics = [];
        taxons.forEach(function (taxon) {
            let topic = taxonService.getTopic(taxon);
            if (topic) {
                let topicName = 'TOPIC_' + topic.name.toUpperCase();

                if (!topics.includes(topicName)) topics.push(topicName);
            } else {
                let specialization = taxonService.getSpecialization(taxon);
                if (specialization) {
                    let specializationName = 'SPECIALIZATION_' + specialization.name.toUpperCase();
                    if (!topics.includes(specializationName)) topics.push(specializationName);
                }
            }
        });

        return topics;
    }

    function getUniqueSubtopics(taxons) {
        let subtopics = [];
        taxons.forEach(function (taxon) {
            let subtopic = taxonService.getSubtopic(taxon);
            if (subtopic) {
                let subtopicName = 'SUBTOPIC_' + subtopic.name.toUpperCase();

                if (!subtopics.includes(subtopicName)) subtopics.push(subtopicName);
            } else {
                let module = taxonService.getModule(taxon);
                if (module) {
                    let moduleName = 'MODULE_' + module.name.toUpperCase();
                    if (!subtopics.includes(moduleName)) subtopics.push(moduleName);
                }
            }
        });

        return subtopics;
    }

    function getTaxonObject(taxons) {
        let taxonObject = {};

        taxonObject.educationalContexts = getUniqueEducationalContexts(taxons);
        taxonObject.domainGroup = getTaxonsFromDomainSubjectMap(getDomainSubjectMap(taxons));
        taxonObject.topics = getUniqueTopics(taxons);
        taxonObject.subtopics = getUniqueSubtopics(taxons);

        return taxonObject;
    }

    function getDomainSubjectMap(taxons) {
        if (!taxonService.isTaxonMapLoaded()) return;

        let domains = taxons.map(taxon => taxonService.getDomain(taxon)).filter(item => {return item !== null});
        let subjects = taxons.map(taxon => taxonService.getSubject(taxon)).filter(item => {return item !== null});

        subjects.forEach(subject => {
            let tmp = domains.filter(domain => domain.id === taxonService.getDomain(subject).id);
            if (tmp.length > 1) {
                subjects.push(...tmp[0].subjects);
            }
        });

        let result = {};
        domains.forEach(domain => {
            result[getDomainNameTranslationKey(domain)] = [];
        });

        subjects.forEach(subject => {
            let domain = taxonService.getDomain(subject);
            if (result[getDomainNameTranslationKey(domain)]) {
                result[getDomainNameTranslationKey(domain)].push(getSubjectNameTranslationKey(subject));
            } else {
                result[getDomainNameTranslationKey(domain)] = [getSubjectNameTranslationKey(subject)];
            }
        });

        return result;
    }

    function getTaxonsFromDomainSubjectMap(map) {
        let result = [];
        let domains = _.keys(map);
        if (!domains || domains.length === 0) return [];

        // Create taxon list that will be shown on card
        domains.forEach((domain) => {
            if (map[domain].length === 1) result = result.concat(map[domain]);
            else result.push(domain);
        });

        // Remove remaining duplicates from final list
        return removeDuplicatesFromDomainOrSubjectList(result);
    }

    function removeDuplicatesFromDomainOrSubjectList(list) {
        if (translationService.getLanguageCode() === "et") {
            return _.uniq(list.map(item => translationService.instant(item)))
        }
    }

    function getDomainSubjectList(taxons) {
        return getTaxonsFromDomainSubjectMap(getDomainSubjectMap(taxons))
    }

    return {
        getTaxonObject: getTaxonObject,
        getDomainSubjectList: getDomainSubjectList
    }
}
