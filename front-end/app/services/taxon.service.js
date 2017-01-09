'use strict';

angular.module('koolikottApp').factory('taxonService', [
    function () {

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
                let resultMap = {};

                const self = this;
                taxons.forEach(function (taxon) {
                    const domain = self.getDomain(taxon);
                    const subject = self.getSubject(taxon);

                    let domainName = domain ? "DOMAIN_" + domain.name.toUpperCase() : null;
                    let subjectName = subject ? "SUBJECT_" + subject.name.toUpperCase() : null;

                    if (_.has(resultMap, domainName)) {
                        if (subjectName && !resultMap[domainName].includes(subjectName)) {
                            resultMap[domainName].push(subjectName);
                        }
                    } else {
                        subjectName ? resultMap[domainName] = [subjectName] : resultMap[domainName] = [];
                    }
                });

                return resultMap;
            },

            getTaxonFromDomainSubjectMap: function (map) {
                const domains = _.keys(map);
                if (!domains || domains.length === 0) return [];

                if (domains.length > 1) return domains;
                if (domains[0].length > 1) return domains;

                return _.flatten(_.values(map));
            }
        }
    }
]);
