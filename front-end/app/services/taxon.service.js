'use strict';

angular.module('koolikottApp').factory('taxonService', ['eventService',
    function (eventService) {

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

        function getFullTaxon(id) {
            return taxonMap && taxonMap['t' + id];
        }

        function setTaxons(educationalContexts) {
            taxonMap = Object.create(null);
            educationalContexts.forEach(function (educationalContext) {
                mapTaxon(educationalContext);
            });

            eventService.notify('taxonService:mapInitialized');
        }

        function getEducationalContext(taxon) {
            return getTaxon(taxon, constants.EDUCATIONAL_CONTEXT);
        }

        function getDomain(taxon) {
            return getTaxon(taxon, constants.DOMAIN);
        }

        function getSubject(taxon) {
            return getTaxon(taxon, constants.SUBJECT);
        }

        function getTopic(taxon) {
            return getTaxon(taxon, constants.TOPIC);
        }

        function getSubtopic(taxon) {
            return getTaxon(taxon, constants.SUBTOPIC);
        }

        function getSpecialization(taxon) {
            return getTaxon(taxon, constants.SPECIALIZATION);
        }

        function getModule(taxon) {
            return getTaxon(taxon, constants.MODULE);
        }

        function getTaxon(taxon, level) {
            if (taxon && taxonMap) return getTaxonByLevel(taxon, level);
        }

        function setSidenavTaxons(taxons) {
            sidenavTaxons = taxons;
        }

        function getSidenavTaxons() {
            return sidenavTaxons;
        }

        function getTaxonTranslationKey(taxon) {
            if (taxon.level = ".TaxonDTO") {
                taxon = getFullTaxon(taxon.id);
            }

            if (taxon.level !== '.EducationalContext') {
                return taxon.level.toUpperCase().substr(1) + "_" + taxon.name.toUpperCase();
            } else {
                return taxon.name.toUpperCase();
            }
        }

        function isTaxonMapLoaded() {
            return !_.isEmpty(taxonMap);
        }

        return {
            constants: constants,

            getFullTaxon: getFullTaxon,
            setTaxons: setTaxons,
            getEducationalContext: getEducationalContext,
            getDomain: getDomain,
            getSubject: getSubject,
            getTopic: getTopic,
            getSubtopic: getSubtopic,
            getSpecialization: getSpecialization,
            getModule: getModule,
            getTaxon: getTaxon,
            setSidenavTaxons: setSidenavTaxons,
            getSidenavTaxons: getSidenavTaxons,
            getTaxonTranslationKey: getTaxonTranslationKey,
            isTaxonMapLoaded: isTaxonMapLoaded
        }
    }
]);
