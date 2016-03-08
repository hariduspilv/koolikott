define(function () {

    var CHILD_TAXON_KEYS = ['domains', 'subjects', 'topics', 'subtopics', 'modules', 'specializations'];
    var TAXON_LEVELS = ['.EducationalContext', '.Domain', '.Subject', '.Topic', 'Subtopic', '.Module', '.Specialization'];

    var taxonMap;

    function mapTaxon(taxon) {
        taxonMap['t' + taxon.id] = taxon;

        var children = getTaxonChildren(taxon);

        children.forEach(function(child) {
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

    function getObjectsWithTaxons(obj) {
        var analyzedObjects = [];

        return getObjectsWithTaxonsFrom(obj, analyzedObjects);
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

    function getMinimalTaxon(taxon) {
        if (taxon && taxon.level && taxon.id) {
            return [taxon.level, 'id', taxon.id];
        }
    }

    function parse(learningObject) {
        replaceTaxons(learningObject, getFullTaxon);
    }

    function serialize(learningObject) {
        replaceTaxons(learningObject, getMinimalTaxon);
    }

    function replaceTaxons(learningObject, replacementFunction) {
        if (learningObject.taxons) {
            learningObject.taxons.forEach(function(taxon, taxonIndex) {
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

    function transform(objects, transformFunction) {
        if (taxonMap) {
            if (Array.isArray(objects)) {
                objects.forEach(function(obj, index) {
                    objects[index] = transformFunction(obj);
                });
            } else {
                objects = transformFunction(objects);
            }
        }

        return objects;
    }

    return {
        parse: function(objects) {
            var objectsWithTaxons = getObjectsWithTaxons(objects);
            transform(objectsWithTaxons, parse);
        },

        serialize: function(objects) {
            var objectsWithTaxons = getObjectsWithTaxons(objects);
            transform(objectsWithTaxons, serialize);
        },

        setTaxons: function(educationalContexts) {
            taxonMap = Object.create(null);
            educationalContexts.forEach(function(educationalContext) {
                mapTaxon(educationalContext);
            });
        }
    }
});
