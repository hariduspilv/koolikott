define(['app'], function(app) {

    app.factory('searchService',['$location', function($location) {
        var searchURLbase = "search/result?q=";
        var taxonURL = "&taxon=";
        var paidURL = "&paid=";
        var typeURL = "&type="
        var languageURL = "&language=";
        var targetGroupsURL = "&targetGroup=";
        var resourceTypeURL = "&resourceType=";

        var searchQuery = "";
        var searchTaxon = "";
        var searchPaid = "";
        var searchType = "";
        var searchLanguage = "";
        var searchTargetGroups = [];
        var searchResourceType = "";

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

        return {

            setSearch : function(query) {
                searchQuery = query;
            },

            setTaxon : function(taxon) {
                searchTaxon = taxon;
            },

            setPaid : function(paid) {
                searchPaid = paid;
            },

            setType : function(type) {
                searchType = type;
            },

            setLanguage : function(language) {
                searchLanguage = language;
            },

            setTargetGroups : function(targetGroups) {
                if (!Array.isArray(targetGroups)) {
                    searchTargetGroups = [];
                    searchTargetGroups.push(targetGroups);
                    searchTargetGroups = this.arrayToLowerCase(searchTargetGroups);
                } else {
                    searchTargetGroups = this.arrayToLowerCase(targetGroups);
                }
            },

            setResourceType : function(resourceType) {
                searchResourceType = resourceType;
            },

            getURL : function() {
                var searchURL;
                if (searchQuery) {
                    searchURL = searchURLbase + escapeQuery(searchQuery)
                } else {
                    searchURL = searchURLbase;
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

                return searchURL;
            },

            queryExists : function() {
                var searchObject = $location.search();
                if (searchObject.q || searchObject.taxon || searchObject.paid === false ||
                    (searchObject.type && this.isValidType(searchObject.type)) || searchObject.language || searchObject.targetGroup ||
                    searchObject.resourceType) {
                    return true;
                } else {
                    return false;
                }
            },

            getQuery : function() {
                if(searchQuery === ""){
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

            isPaid : function() {
                if (searchPaid === "") {
                    var searchObject = $location.search();
                    if (searchObject.paid) {
                        return searchObject.paid === 'true' ? true : false;
                    }
                }

                return searchPaid;
            },

            getType : function() {
                if (searchType === "") {
                    var searchObject = $location.search();
                    if (searchObject.type) {
                        return searchObject.type;
                    }
                }

                return searchType;
            },

            getLanguage : function() {
                if (searchLanguage === "") {
                    var searchObject = $location.search();
                    if (searchObject.language) {
                        return searchObject.language;
                    }
                }

                return searchLanguage;
            },

            getTargetGroups : function() {
                if (!searchTargetGroups || searchTargetGroups.length === 0) {
                    var searchObject = $location.search();
                    if (searchObject.targetGroup) {
                        if (!Array.isArray(searchObject.targetGroup)) {
                            var targetGroupsArray = [];
                            targetGroupsArray.push(searchObject.targetGroup);
                            return this.arrayToUpperCase(targetGroupsArray);
                        } else {
                            return this.arrayToUpperCase(searchObject.targetGroup);
                        }
                    }
                } 

                return this.arrayToUpperCase(searchTargetGroups);
            },

            getResourceType : function() {
                if (searchResourceType === "") {
                    var searchObject = $location.search();
                    if (searchObject.resourceType) {
                        return searchObject.resourceType;
                    }
                }

                return searchResourceType;
            },

            clearFieldsNotInSimpleSearch : function() {
                searchTaxon = '';
                searchPaid = '';
                searchType = '';
                searchLanguage = '';
                searchTargetGroups = '';
                searchResourceType = '';
            },

            isValidType : function(type) {
                return type === 'material' || type === 'portfolio' || type === 'all';
            },

            arrayToLowerCase : function(upperCaseArray) {
                var lowerCaseArray = [];
                for (i = 0; i < upperCaseArray.length; i++) {
                    lowerCaseArray.push(upperCaseArray[i].toLowerCase());
                }
                return lowerCaseArray;
            },

            arrayToUpperCase : function(lowerCaseArray) {
                var upperCaseArray = [];
                for (i = 0; i < lowerCaseArray.length; i++) {
                    upperCaseArray.push(lowerCaseArray[i].toUpperCase());
                }
                return upperCaseArray;
            }
        };
    }]);
});
