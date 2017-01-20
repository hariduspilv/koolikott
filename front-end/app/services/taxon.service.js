'use strict';

angular.module('koolikottApp').factory('taxonService', ['translationService',
    function (translationService) {

        const CHILD_TAXON_KEYS = ['domains', 'subjects', 'topics', 'subtopics', 'modules', 'specializations'];

        const constants = {
            EDUCATIONAL_CONTEXT: '.EducationalContext',
            DOMAIN: '.Domain',
            SUBJECT: '.Subject',
            TOPIC: '.Topic',
            SUBTOPIC: '.Subtopic',
            SPECIALIZATION: '.Specialization',
            MODULE: '.Module'
        };

        let taxonMap;
        let sidenavTaxons;

        function mapTaxon(taxon) {
            taxonMap['t' + taxon.id] = taxon;

            let children = getTaxonChildren(taxon);

            children.forEach(function (child) {
                mapTaxon(child);
            });
        }

        function getTaxonChildren(taxon) {
            for (let i = 0; i < CHILD_TAXON_KEYS.length; i++) {
                let key = CHILD_TAXON_KEYS[i];
                if (taxon[key] && taxon[key].length > 0) {
                    return taxon[key];
                }
            }

            return [];
        }

        function getTaxonByLevel(taxon, level) {
            if (taxon.level === ".TaxonDTO") {
                taxon = taxonMap['t' + taxon.id];
            }

            if (taxon.level === constants.EDUCATIONAL_CONTEXT) {
                return taxon.level === level ? taxon : null;
            }

            if (taxon.level === constants.DOMAIN) {
                return taxon.level === level ? taxon : getTaxonByLevel(taxonMap['t' + taxon.parentId], level);
            }

            if (taxon.level === constants.SUBJECT) {
                return taxon.level === level ? taxon : getTaxonByLevel(taxonMap['t' + taxon.parentId], level);
            }

            if (taxon.level === constants.TOPIC) {
                return taxon.level === level ? taxon : getTaxonByLevel(taxonMap['t' + taxon.parentId], level);
            }

            if (taxon.level === constants.SUBTOPIC) {
                return taxon.level === level ? taxon : getTaxonByLevel(taxonMap['t' + taxon.parentId], level);
            }

            if (taxon.level === constants.SPECIALIZATION) {
                return taxon.level === level ? taxon : getTaxonByLevel(taxonMap['t' + taxon.parentId], level);
            }

            if (taxon.level === constants.MODULE) {
                return taxon.level === level ? taxon : getTaxonByLevel(taxonMap['t' + taxon.parentId], level);
            }
        }

        function getDomainNameHelperTranslation(domainKey) {
            return translationService.instant('HELPER_' + domainKey);
        }

        function getSubjectNameHelperTranslation(subjectKey) {
            return translationService.instant('HELPER_' + subjectKey)
        }

        function getDomainNameTranslation(domain) {
            return translationService.instant('DOMAIN_' + domain.name.toUpperCase())
        }

        function getSubjectNameTranslation(subject) {
            return translationService.instant('SUBJECT_' + subject.name.toUpperCase())
        }

        function getDomainNameTranslationKey(domain) {
            return 'DOMAIN_' + domain.name.toUpperCase()
        }

        function getSubjectNameTranslationKey(subject) {
            return 'SUBJECT_' + subject.name.toUpperCase()
        }

        return {
            constants: constants,

            getFullTaxon: function (id) {
                return taxonMap['t' + id];
            },

            setTaxons: function (educationalContexts) {
                taxonMap = Object.create(null);
                educationalContexts.forEach(function (educationalContext) {
                    mapTaxon(educationalContext);
                });
            },

            getEducationalContext: function (taxon) {
                return this.getTaxon(taxon, constants.EDUCATIONAL_CONTEXT);
            },

            getDomain: function (taxon) {
                return this.getTaxon(taxon, constants.DOMAIN);
            },

            getSubject: function (taxon) {
                return this.getTaxon(taxon, constants.SUBJECT);
            },

            getTopic: function (taxon) {
                return this.getTaxon(taxon, constants.TOPIC);
            },

            getSubtopic: function (taxon) {
                return this.getTaxon(taxon, constants.SUBTOPIC);
            },

            getSpecialization: function (taxon) {
                return this.getTaxon(taxon, constants.SPECIALIZATION);
            },

            getModule: function (taxon) {
                return this.getTaxon(taxon, constants.MODULE);
            },

            getTaxon: function (taxon, level) {
                if (taxon && taxonMap) return getTaxonByLevel(taxon, level);
            },

            setSidenavTaxons: function (taxons) {
                sidenavTaxons = taxons;
            },

            getSidenavTaxons: function () {
                return sidenavTaxons;
            },

            getTaxonTranslationKey: function (taxon) {
                if (taxon.level = ".TaxonDTO") {
                    taxon = this.getFullTaxon(taxon.id);
                }

                if (taxon.level !== '.EducationalContext') {
                    return taxon.level.toUpperCase().substr(1) + "_" + taxon.name.toUpperCase();
                } else {
                    return taxon.name.toUpperCase();
                }
            },

            getDomainSubjectMap: function (taxons) {
                if (_.isEmpty(taxonMap)) return;

                let domains = taxons.map(taxon => this.getDomain(taxon)).filter(item => {return item !== null});
                let subjects = taxons.map(taxon => this.getSubject(taxon)).filter(item => {return item !== null});
                let allSubjects = _.flatten(domains.map(dom => dom.subjects));

                const lang = translationService.getLanguageCode();
                for (let i = 0; i < domains.length; i++) {
                    for (let j = 0; j < allSubjects.length; j++) {

                        // No need to compare parent with its own children
                        if (domains[i].id === allSubjects[j].parentId) continue;

                        // Check if domain and subject have same translation
                        // Estonian translations are compared at the moment
                        if (lang === "et" && getDomainNameTranslation(domains[i]) !== getSubjectNameTranslation(allSubjects[j])) {
                            continue;
                        } else if (lang !== "et" && getDomainNameHelperTranslation(getDomainNameTranslationKey(domains[i])) !== getSubjectNameHelperTranslation(getSubjectNameTranslationKey(allSubjects[j]))) {
                            continue;
                        }

                        // Remove domain and add corresponding subject to subject list
                        if (_.findIndex(subjects, allSubjects[j]) === -1) {
                            domains.splice(i, 1);
                            subjects.push(allSubjects[j]);
                            i--;
                            break;
                        }
                    }
                }

                let result = {};
                domains.forEach(domain => {
                    result[getDomainNameTranslationKey(domain)] = [];
                });

                subjects.forEach(subject => {
                    let domain = this.getDomain(subject);
                    result[getDomainNameTranslationKey(domain)].push(getSubjectNameTranslationKey(subject));
                });

                return result;
            },

            getTaxonsFromDomainSubjectMap: function (map) {
                let result = [];
                let domains = _.keys(map);
                if (!domains || domains.length === 0) return [];

                // Create taxon list that will be shown on card
                domains.forEach((domain) => {
                   if (map[domain].length === 1) result = result.concat(map[domain]);
                   else result.push(domain);
                });

                // Remove remaining duplicates from final list
                return this.removeDuplicatesFromDomainOrSubjectList(result);
            },

            removeDuplicatesFromDomainOrSubjectList: function (list) {
                if (translationService.getLanguageCode() === "et") {
                    return _.uniq(list.map(item => translationService.instant(item)))
                }

                // Workaround for taxons with missing translations
                // "HELPER_" items can be removed once all taxons are translated
                let translatedList = [];
                let cleanedResult = [];

                list.forEach(item => {
                    let translated = translationService.instant("HELPER_" + item);
                    if (translated.startsWith("HELPER_")) translated = translationService.instant(item);

                    if (item && !translatedList.includes(translated)) {
                        translatedList.push(translated);
                        cleanedResult.push(item)
                    }
                });

                return cleanedResult;
            }
        }
    }
]);
