define(function () {

    var taxons;

    function mapTaxon(taxon) {
        taxons['t' + taxon.id] = taxon;

        var children = getTaxonChildren(taxon);

        children.forEach(function(child) {
            mapTaxon(child);
        });
    }

    function getTaxonChildren(taxon) {
        var children = [];
        var keys = ['domains', 'subjects', 'topics', 'subtopics', 'modules', 'specializations'];

        keys.forEach(function(key) {
            if (taxon[key] && taxon[key].length > 0) {
                taxon[key].forEach(function(childTaxon) {
                   children.push(childTaxon);
                });
            }
        });

        return children;
    }

    function getObjectsWithTaxons(obj) {
        var objects = [];

        if (obj.taxons || obj.taxon) {
            return obj;
        }

        if (obj.level) {
            return;
        }

        for (var property in obj) {
            if (obj.hasOwnProperty(property) && obj[property] != null) {
                if (obj[property].taxons || obj[property].taxon) {
                    objects.push(obj[property]);
                } else if (obj[property].constructor == Object) {
                    getObjectsWithTaxonsAndPushToArray(obj[property]);
                } else if (obj[property].constructor == Array) {
                    for (var i = 0; i < obj[property].length; i++) {
                        getObjectsWithTaxonsAndPushToArray(obj[property][i]);
                    }
                }
            }
        }

        function getObjectsWithTaxonsAndPushToArray(prop) {
            var foundObject = getObjectsWithTaxons(prop);
            if (foundObject) {
                objects.push(foundObject);
            }
        }

        return objects;
    }

    function getFullTaxon(taxon) {
        if (!taxon.level && taxon.length >= 2) {
            return taxons['t' + taxon[2]];
        }
    }

    function getMinimalTaxon(taxon) {
        if (taxon && taxon.level && taxon.id) {
            return [taxon.level, 'id', taxon.id];
        }
    }

    function replaceWithFullTaxons(learningObject) {
        return replaceTaxons(learningObject, getFullTaxon);
    }

    function replaceWithMinimalTaxons(learningObject) {
        return replaceTaxons(learningObject, getMinimalTaxon);
    }

    function replaceTaxons(learningObject, replacementFunction) {
        if (learningObject.taxons) {
            learningObject.taxons.forEach(function(taxon, taxonIndex) {
                var replacementTaxon = replacementFunction(taxon);
                if (replacementTaxon) {
                    learningObject.taxons[taxonIndex] = replacementTaxon;
                }
            });
        }

        if (learningObject.taxon) {
            var replacementTaxon = replacementFunction(learningObject.taxon);
            if (replacementTaxon) {
                learningObject.taxon = replacementTaxon;
            }
        }

        if (learningObject.chapters) {
            replaceTaxonsInChapters(learningObject.chapters, replacementFunction);
        }

        return learningObject;
    }

    function replaceTaxonsInChapters(chapters, replacementFunction) {
        chapters.forEach(function(chapter) {
            chapter.materials.forEach(function(material) {
                material.taxons.forEach(function(taxon, taxonIndex) {
                    var replacementTaxon = replacementFunction(taxon);
                    if (replacementTaxon) {
                        material.taxons[taxonIndex] = replacementTaxon;
                    }
                });
            });

            if (chapter.subchapters) {
                replaceTaxonsInChapters(chapter.subchapters, replacementFunction);
            }
        });
    }

    function transform(objects, transformFunction) {
        if (taxons) {
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
        transformToFullTaxons: function(objects) {
            var learningObjects = getObjectsWithTaxons(objects);
            transform(learningObjects, replaceWithFullTaxons);
        },

        transformToMinimalTaxons: function(objects) {
            var learningObjects = getObjectsWithTaxons(objects);
            transform(learningObjects, replaceWithMinimalTaxons);
        },

        setTaxons: function(educationalContexts) {
            taxons = Object.create(null);
            educationalContexts.forEach(function(context) {
                mapTaxon(context);
            });
        }
    }
});
