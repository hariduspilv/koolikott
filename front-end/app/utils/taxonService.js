define(function () {

    var CHILD_TAXON_KEYS = ['domains', 'subjects', 'topics', 'subtopics', 'modules', 'specializations'];
    var TAXON_LEVELS = ['.EducationalContext', '.Domain', '.Subject', '.Topic', 'Subtopic', '.Module', '.Specialization'];
    var constants = {
        EDUCATIONAL_CONTEXT: '.EducationalContext',
        DOMAIN: '.Domain',
        SUBJECT: '.Subject',
        TOPIC: '.Topic',
        SUBTOPIC: '.Subtopic',
        SPECIALIZATION: '.Specialization',
        MODULE: '.Module'
    };
    var taxonMap;
    var taxonMapCallbacks = [];

    var learningObjectsToParse = [];

    function mapTaxon(taxon) {
        taxonMap['t' + taxon.id] = taxon;

        var children = getTaxonChildren(taxon);

        children.forEach(function (child) {
            mapTaxon(child);
        });
    }

    function getTaxonChildren(taxon) {
        for (var i = 0; i < CHILD_TAXON_KEYS.length; i++) {
            var key = CHILD_TAXON_KEYS[i];
            if (taxon[key] && taxon[key].length > 0) {
                return taxon[key];
            }
        }

        return [];
    }

    function getObjectsWithTaxonsFrom(obj, analyzedObjects) {
        var objects = [];

        if (analyzedObjects.indexOf(obj) !== -1 || objectIsTaxon(obj)) {
            return [];
        }

        for (var property in obj) {
            if (obj.hasOwnProperty(property) && obj[property] != null) {
                if (property === 'taxons' || property === 'taxon') {
                    objects.push(obj);
                } else if (obj[property].constructor == Object) {
                    objects = objects.concat(getObjectsWithTaxonsFrom(obj[property], analyzedObjects));
                } else if (obj[property].constructor == Array) {
                    for (var i = 0; i < obj[property].length; i++) {
                        objects = objects.concat(getObjectsWithTaxonsFrom(obj[property][i], analyzedObjects));
                    }
                }
            }
        }

        analyzedObjects.push(obj);

        return objects;
    }

    function objectIsTaxon(obj) {
        return obj && obj.level && TAXON_LEVELS.indexOf(obj.level) !== -1;
    }

    function getFullTaxon(taxon) {
        if (!taxon.level && taxon.length >= 2) {
            return taxonMap['t' + taxon[2]];
        }
    }

    function replaceTaxons(learningObject, replacementFunction) {
        if (learningObject.taxons) {
            learningObject.taxons.forEach(function (taxon, taxonIndex) {
                var replacementTaxon = replacementFunction(taxon);
                if (replacementTaxon) {
                    learningObject.taxons[taxonIndex] = replacementTaxon;
                }
                if (!taxon || isObjectEmpty(taxon)) {
                    learningObject.taxons.splice(taxonIndex, 1);
                }
            });
        }

        if (learningObject.taxon) {
            var replacementTaxon = replacementFunction(learningObject.taxon);
            if (replacementTaxon) {
                learningObject.taxon = replacementTaxon;
            }
            if (!learningObject.taxon || isObjectEmpty(learningObject.taxon)) {
                learningObject.taxon = null;
            }
        }
    }

    return {
        constants: constants,

        setTaxons: function (educationalContexts) {
            taxonMap = Object.create(null);
            educationalContexts.forEach(function (educationalContext) {
                mapTaxon(educationalContext);
            });

            learningObjectsToParse.forEach(function (learningObject) {
                replaceTaxons(learningObject, getFullTaxon);
            });

            taxonMapCallbacks.forEach(function (callback) {
                callback(taxonMap);
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
            if (!taxon) {
                return;
            }

            if (taxon.level === constants.EDUCATIONAL_CONTEXT) {
                return taxon.level === level ? taxon : null;
            }

            if (taxon.level === constants.DOMAIN) {
                return taxon.level === level ? taxon : this.getTaxon(taxonMap['t' + taxon.parentId], level);
            }

            if (taxon.level === constants.SUBJECT) {
                return taxon.level === level ? taxon : this.getTaxon(taxonMap['t' + taxon.parentId], level);
            }

            if (taxon.level === constants.TOPIC) {
                if (taxon.level === level) return taxon;

                var parent = taxonMap['t' + taxon.parentId];
                return this.getTaxon(parent, level);
            }

            if (taxon.level === constants.SUBTOPIC) {
                return taxon.level === level ? taxon : this.getTaxon(taxonMap['t' + taxon.parentId], level);
            }

            if (taxon.level === constants.SPECIALIZATION) {
                return taxon.level === level ? taxon : this.getTaxon(taxonMap['t' + taxon.parentId], level);
            }

            if (taxon.level === constants.MODULE) {
                return taxon.level === level ? taxon : this.getTaxon(taxonMap['t' + taxon.parentId], level);
            }
        }
    }
});
