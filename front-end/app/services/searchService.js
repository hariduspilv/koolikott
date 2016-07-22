define(['angularAMD'], function(angularAMD) {

    angularAMD.factory('searchService', ['$location', function($location) {
        var searchURLbase = "search/result?";
        var taxonURL = "&taxon=";
        var paidURL = "&paid=";
        var typeURL = "&type=";
        var languageURL = "&language=";
        var targetGroupsURL = "&targetGroup=";
        var resourceTypeURL = "&resourceType=";
        var isSpecialEducationURL = "&specialEducation=";
        var issuedFromURL = "&issuedFrom=";
        var crossCurricularThemeURL = "&crossCurricularTheme=";
        var keyCompetenceURL = "&keyCompetence="
        var isCurriculumLiteratureURL = "&curriculumLiterature=";
        var sortURL = "&sort=";
        var sortDirectionURL = "&sortDirection=";

        var search = {
            query: '',
            taxon: '',
            paid: '',
            type: '',
            language: '',
            targetGroups: [],
            resourceType: '',
            isSpecialEducation: '',
            issuedFrom: '',
            crossCurricularTheme: '',
            keyCompetence: '',
            isCurriculumLiterature: '',
            sort: '',
            sortDirection: ''
        };

        function init() {
            var searchObject = $location.search();
            for (var property in searchObject) {
                if (searchObject.hasOwnProperty(property) && searchObject[property] != null) {
                    if (property === 'targetGroup') {
                        search['targetGroups'] = arrayToLowerCase(asArray(searchObject[property]));
                    } else {
                        search[property] = searchObject[property];
                    }
                }
            }
        }

        function escapeQuery(query) {
            //replace backslashes
            query = query.replace(/\\/g, "\\\\");

            //make plus signs into \+
            query = query.replace(/\+/g, "\\+");

            return query;
        }

        function unescapeQuery(query) {
            //get back + sign
            query = query.replace(/\\ /g, "+");

            //make backslashes singular
            query = query.replace(/\\\\/g, "\\");

            return query;
        }

        function arrayToLowerCase(upperCaseArray) {
            var lowerCaseArray = [];
            for (i = 0; i < upperCaseArray.length; i++) {
                if (upperCaseArray[i] && isString(upperCaseArray[i])) {
                    lowerCaseArray.push(upperCaseArray[i].toLowerCase());
                }
            }
            return lowerCaseArray;
        }

        function arrayToUpperCase(lowerCaseArray) {
            var upperCaseArray = [];
            for (i = 0; i < lowerCaseArray.length; i++) {
                if (lowerCaseArray[i] && isString(lowerCaseArray[i])) {
                    upperCaseArray.push(lowerCaseArray[i].toUpperCase());
                }
            }
            return upperCaseArray;
        }

        function isString(value) {
            return typeof value === 'string' || value instanceof String;
        }

        // Get value as array
        function asArray(value) {
            if (!Array.isArray(value)) {
                var valueArray = [];
                valueArray.push(value);
                return valueArray;
            } else {
                return value;
            }
        }

        init();

        return {

            setSearch: function(query) {
                search.query = query;
            },

            setTaxon: function(taxon) {
                search.taxon = taxon;
            },

            setPaid: function(paid) {
                search.paid = paid;
            },

            setType: function(type) {
                search.type = type;
            },

            setLanguage: function(language) {
                search.language = language;
            },

            setTargetGroups: function(targetGroups) {
                search.targetGroups = arrayToLowerCase(asArray(targetGroups));
            },

            setResourceType: function(resourceType) {
                search.resourceType = resourceType;
            },

            setIsSpecialEducation: function(isSpecialEducation) {
                search.isSpecialEducation = isSpecialEducation;
            },

            setIssuedFrom: function(issuedFrom) {
                search.issuedFrom = issuedFrom;
            },

            setCrossCurricularTheme: function(crossCurricularTheme) {
                search.crossCurricularTheme = crossCurricularTheme;
            },

            setKeyCompetence: function(keyCompetence) {
                search.keyCompetence = keyCompetence;
            },

            setCurriculumLiterature: function(isCurriculumLiterature) {
                search.isCurriculumLiterature = isCurriculumLiterature;
            },

            setSort: function(sort) {
                search.sort = sort;
            },

            setSortDirection: function(sortDirection) {
                search.sortDirection = sortDirection;
            },

            getURL: function() {
                return searchURLbase + this.getQueryURL();;
            },

            getQueryURL: function(isBackendQuery) {
                var searchURL = 'q=';
                if (search.query) {
                    searchURL += escapeQuery(search.query)
                }

                if (search.taxon) {
                    searchURL += taxonURL + search.taxon;
                }
                if (search.paid === false) {
                    searchURL += paidURL + search.paid;
                }
                if (search.type && this.isValidType(search.type)) {
                    searchURL += typeURL + search.type;
                }
                if (search.language) {
                    searchURL += languageURL + search.language;
                }
                if (search.targetGroups) {
                    for (var i = 0; i < search.targetGroups.length; i++) {
                        if (isBackendQuery && search.targetGroups[i]) {
                            // Enums are case sensitive, so they must be uppercase for the back-end query
                            searchURL += targetGroupsURL + search.targetGroups[i].toUpperCase();
                        } else {
                            searchURL += targetGroupsURL + search.targetGroups[i];
                        }
                    }
                }
                if (search.resourceType) {
                    searchURL += resourceTypeURL + search.resourceType;
                }
                if (search.isSpecialEducation === true) {
                    searchURL += isSpecialEducationURL + search.isSpecialEducation;
                }
                if (search.issuedFrom) {
                    searchURL += issuedFromURL + search.issuedFrom;
                }
                if (search.crossCurricularTheme) {
                    searchURL += crossCurricularThemeURL + search.crossCurricularTheme;
                }
                if (search.keyCompetence) {
                    searchURL += keyCompetenceURL + search.keyCompetence;
                }
                if (search.isCurriculumLiterature) {
                    searchURL += isCurriculumLiteratureURL + search.isCurriculumLiterature;
                }
                if (search.sort && search.sortDirection) {
                    searchURL += sortURL + search.sort + sortDirectionURL + search.sortDirection;
                }

                return searchURL;
            },

            queryExists: function() {
                var searchObject = $location.search();
                if (searchObject.q || searchObject.taxon || searchObject.paid === false ||
                    (searchObject.type && this.isValidType(searchObject.type)) || searchObject.language || searchObject.targetGroup ||
                    searchObject.resourceType || searchObject.specialEducation || searchObject.issuedFrom || searchObject.crossCurricularTheme ||
                    searchObject.keyCompetence || searchObject.curriculumLiterature || (searchObject.sort && searchObject.sortDirection)) {
                    return true;
                } else {
                    return false;
                }
            },

            getQuery: function() {
                if (search.query === "") {
                    var searchObject = $location.search();
                    if (searchObject.q) {
                        search.query = unescapeQuery(searchObject.q);
                    }
                }

                return search.query;
            },

            getTaxon: function() {
                if (search.taxon === "") {
                    var searchObject = $location.search();
                    if (searchObject.taxon) {
                        return searchObject.taxon;
                    }
                }

                return search.taxon;
            },

            isPaid: function() {
                if (search.paid === "") {
                    var searchObject = $location.search();
                    if (searchObject.paid) {
                        return searchObject.paid === 'true';
                    }
                }

                return search.paid;
            },

            getType: function() {
                if (search.type === "") {
                    var searchObject = $location.search();
                    if (searchObject.type) {
                        return searchObject.type;
                    }
                }

                return search.type;
            },

            getLanguage: function() {
                if (search.language === "") {
                    var searchObject = $location.search();
                    if (searchObject.language) {
                        return searchObject.language;
                    }
                }

                return search.language;
            },

            getTargetGroups: function() {
                if (!search.targetGroups || search.targetGroups.length === 0) {
                    var searchObject = $location.search();
                    if (searchObject.targetGroup) {
                        return arrayToUpperCase(asArray(searchObject.targetGroup));
                    }
                }

                return arrayToUpperCase(search.targetGroups);
            },

            getResourceType: function() {
                if (search.resourceType === "") {
                    var searchObject = $location.search();
                    if (searchObject.resourceType) {
                        return searchObject.resourceType;
                    }
                }

                return search.resourceType;
            },

            isSpecialEducation: function() {
                if (search.isSpecialEducation === "") {
                    var searchObject = $location.search();
                    if (searchObject.specialEducation) {
                        return searchObject.specialEducation === 'true';
                    }
                }

                return search.isSpecialEducation;
            },

            getIssuedFrom: function() {
                if (search.issuedFrom === "") {
                    var searchObject = $location.search();
                    if (searchObject.issuedFrom) {
                        return searchObject.issuedFrom;
                    }
                }

                return search.issuedFrom;
            },

            getCrossCurricularTheme: function() {
                if (search.crossCurricularTheme === "") {
                    var searchObject = $location.search();
                    if (searchObject.crossCurricularTheme) {
                        return searchObject.crossCurricularTheme;
                    }
                }

                return search.crossCurricularTheme;
            },

            getKeyCompetence: function() {
                if (search.keyCompetence === "") {
                    var searchObject = $location.search();
                    if (searchObject.keyCompetence) {
                        return searchObject.keyCompetence;
                    }
                }

                return search.keyCompetence;
            },

            getSort: function() {
                if (search.sort === "") {
                    var searchObject = $location.search();
                    if (searchObject.sort) {
                        return searchObject.sort;
                    }
                }

                return search.sort;
            },

            getSortDirection: function() {
                if (search.sortDirection === "") {
                    var searchObject = $location.search();
                    if (searchObject.sortDirection) {
                        return searchObject.sortDirection;
                    }
                }

                return search.sortDirection;
            },

            isCurriculumLiterature: function() {
                if (search.isCurriculumLiterature === "") {
                    var searchObject = $location.search();
                    if (searchObject.curriculumLiterature) {
                        return searchObject.curriculumLiterature === 'true';
                    }
                }

                return search.isCurriculumLiterature;
            },

            clearFieldsNotInSimpleSearch: function() {
                search.taxon = '';
                search.paid = '';
                search.type = '';
                search.language = '';
                search.targetGroups = '';
                search.resourceType = '';
                search.isSpecialEducation = '';
                search.issuedFrom = '';
                search.crossCurricularTheme = '';
                search.keyCompetence = '';
                search.isCurriculumLiterature = '';
                search.sort = '';
                search.sortDirection = '';
            },

            isValidType: function(type) {
                return type === 'material' || type === 'portfolio' || type === 'all';
            },

            getSearchURLbase: function() {
                return searchURLbase;
            }
        };
    }]);
});
