define(['angularAMD'], function(angularAMD) {

    angularAMD.factory('searchService', ['$location', function($location) {
        var searchURLbase = "search/result?";
        var taxonURL = "&taxon=";
        var paidURL = "&paid=";
        var typeURL = "&type="
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

        var searchQuery = "";
        var searchTaxon = "";
        var searchPaid = "";
        var searchType = "";
        var searchLanguage = "";
        var searchTargetGroups = [];
        var searchResourceType = "";
        var searchIsSpecialEducation = "";
        var searchIssuedFrom = "";
        var searchCrossCurricularTheme = "";
        var searchKeyCompetence = "";
        var searchIsCurriculumLiterature = "";
        var searchSort = "";
        var searchSortDirection = "";

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

        return {

            setSearch: function(query) {
                searchQuery = query;
            },

            setTaxon: function(taxon) {
                searchTaxon = taxon;
            },

            setPaid: function(paid) {
                searchPaid = paid;
            },

            setType: function(type) {
                searchType = type;
            },

            setLanguage: function(language) {
                searchLanguage = language;
            },

            setTargetGroups: function(targetGroups) {
                searchTargetGroups = arrayToLowerCase(asArray(targetGroups));
            },

            setResourceType: function(resourceType) {
                searchResourceType = resourceType;
            },

            setIsSpecialEducation: function(isSpecialEducation) {
                searchIsSpecialEducation = isSpecialEducation;
            },

            setIssuedFrom: function(issuedFrom) {
                searchIssuedFrom = issuedFrom;
            },

            setCrossCurricularTheme: function(crossCurricularTheme) {
                searchCrossCurricularTheme = crossCurricularTheme;
            },

            setKeyCompetence: function(keyCompetence) {
                searchKeyCompetence = keyCompetence;
            },

            setCurriculumLiterature: function(isCurriculumLiterature) {
                searchIsCurriculumLiterature = isCurriculumLiterature;
            },

            setSort: function(sort) {
                searchSort = sort;
            },

            setSortDirection: function(sortDirection) {
                searchSortDirection = sortDirection;
            },

            getURL: function() {
                return searchURLbase + this.getQueryURL();;
            },

            getQueryURL: function() {
                var searchURL = 'q=';
                if (searchQuery) {
                    searchURL += escapeQuery(searchQuery)
                }

                if (searchTaxon) {
                    searchURL += taxonURL + searchTaxon;
                }
                if (searchPaid === false) {
                    searchURL += paidURL + searchPaid;
                }
                if (searchType && this.isValidType(searchType)) {
                    searchURL += typeURL + searchType;
                }
                if (searchLanguage) {
                    searchURL += languageURL + searchLanguage;
                }
                if (searchTargetGroups) {
                    for (i = 0; i < searchTargetGroups.length; i++) {
                        searchURL += targetGroupsURL + searchTargetGroups[i];
                    }
                }
                if (searchResourceType) {
                    searchURL += resourceTypeURL + searchResourceType;
                }
                if (searchIsSpecialEducation === true) {
                    searchURL += isSpecialEducationURL + searchIsSpecialEducation;
                }
                if (searchIssuedFrom) {
                    searchURL += issuedFromURL + searchIssuedFrom;
                }
                if (searchCrossCurricularTheme) {
                    searchURL += crossCurricularThemeURL + searchCrossCurricularTheme;
                }
                if (searchKeyCompetence) {
                    searchURL += keyCompetenceURL + searchKeyCompetence;
                }
                if (searchIsCurriculumLiterature) {
                    searchURL += isCurriculumLiteratureURL + searchIsCurriculumLiterature;
                }
                if (searchSort && searchSortDirection) {
                    searchURL += sortURL + searchSort + sortDirectionURL + searchSortDirection;
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
                if (searchQuery === "") {
                    var searchObject = $location.search();
                    if (searchObject.q) {
                        searchQuery = unescapeQuery(searchObject.q);
                    }
                }

                return searchQuery;
            },

            getTaxon: function() {
                if (searchTaxon === "") {
                    var searchObject = $location.search();
                    if (searchObject.taxon) {
                        return searchObject.taxon;
                    }
                }

                return searchTaxon;
            },

            isPaid: function() {
                if (searchPaid === "") {
                    var searchObject = $location.search();
                    if (searchObject.paid) {
                        return searchObject.paid === 'true' ? true : false;
                    }
                }

                return searchPaid;
            },

            getType: function() {
                if (searchType === "") {
                    var searchObject = $location.search();
                    if (searchObject.type) {
                        return searchObject.type;
                    }
                }

                return searchType;
            },

            getLanguage: function() {
                if (searchLanguage === "") {
                    var searchObject = $location.search();
                    if (searchObject.language) {
                        return searchObject.language;
                    }
                }

                return searchLanguage;
            },

            getTargetGroups: function() {
                if (!searchTargetGroups || searchTargetGroups.length === 0) {
                    var searchObject = $location.search();
                    if (searchObject.targetGroup) {
                        return arrayToUpperCase(asArray(searchObject.targetGroup));
                    }
                }

                return arrayToUpperCase(searchTargetGroups);
            },

            getResourceType: function() {
                if (searchResourceType === "") {
                    var searchObject = $location.search();
                    if (searchObject.resourceType) {
                        return searchObject.resourceType;
                    }
                }

                return searchResourceType;
            },

            isSpecialEducation: function() {
                if (searchIsSpecialEducation === "") {
                    var searchObject = $location.search();
                    if (searchObject.specialEducation) {
                        return searchObject.specialEducation === 'true' ? true : false;
                    }
                }

                return searchIsSpecialEducation;
            },

            getIssuedFrom: function() {
                if (searchIssuedFrom === "") {
                    var searchObject = $location.search();
                    if (searchObject.issuedFrom) {
                        return searchObject.issuedFrom;
                    }
                }

                return searchIssuedFrom;
            },

            getCrossCurricularTheme: function() {
                if (searchCrossCurricularTheme === "") {
                    var searchObject = $location.search();
                    if (searchObject.crossCurricularTheme) {
                        return searchObject.crossCurricularTheme;
                    }
                }

                return searchCrossCurricularTheme;
            },

            getKeyCompetence: function() {
                if (searchKeyCompetence === "") {
                    var searchObject = $location.search();
                    if (searchObject.keyCompetence) {
                        return searchObject.keyCompetence;
                    }
                }

                return searchKeyCompetence;
            },

            getSort: function() {
                if (searchSort === "") {
                    var searchObject = $location.search();
                    if (searchObject.sort) {
                        return searchObject.sort;
                    }
                }

                return searchSort;
            },

            getSortDirection: function() {
                if (searchSortDirection === "") {
                    var searchObject = $location.search();
                    if (searchObject.sortDirection) {
                        return searchObject.sortDirection;
                    }
                }

                return searchSortDirection;
            },

            isCurriculumLiterature: function() {
                if (searchIsCurriculumLiterature === "") {
                    var searchObject = $location.search();
                    if (searchObject.curriculumLiterature) {
                        return searchObject.curriculumLiterature === 'true' ? true : false;
                    }
                }

                return searchIsCurriculumLiterature;
            },

            clearFieldsNotInSimpleSearch: function() {
                searchTaxon = '';
                searchPaid = '';
                searchType = '';
                searchLanguage = '';
                searchTargetGroups = '';
                searchResourceType = '';
                searchIsSpecialEducation = '';
                searchIssuedFrom = '';
                searchCrossCurricularTheme = '';
                searchKeyCompetence = '';
                searchIsCurriculumLiterature = '';
                searchSort = '';
                searchSortDirection = '';
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