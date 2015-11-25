define(['app'], function(app) {

    app.factory('searchService',['$location', function($location) {
        var searchURLbase = "search/result?q=";
        var taxonURL = "&taxon=";
        var paidURL = "&paid=";
        var typeURL = "&type="
        var languageURL = "&language=";
        var targetGroupURL = "&targetGroup=";

        var searchQuery = "";
        var searchTaxon = "";
        var searchPaid = "";
        var searchType = "";
        var searchLanguage = "";
        var searchTargetGroup = "";

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

            setTargetGroup : function(targetGroup) {
                searchTargetGroup = targetGroup;
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
                if (searchTargetGroup) {
                    searchURL += targetGroupURL + searchTargetGroup;
                }

                return searchURL;
            },

            queryExists : function() {
                var searchObject = $location.search();
                if (searchObject.q || searchObject.taxon || searchObject.paid === false ||
                    (searchObject.type && this.isValidType(searchObject.type)) || searchObject.language || searchObject.targetGroup) {
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

            getTargetGroup : function() {
                if (searchTargetGroup === "") {
                    var searchObject = $location.search();
                    if (searchObject.targetGroup) {
                        return searchObject.targetGroup;
                    }
                }

                return searchTargetGroup;
            },

            clearFieldsNotInSimpleSearch : function() {
                searchTaxon = '';
                searchPaid = '';
                searchType = '';
                searchLanguage = '';
                searchTargetGroup = '';
            },

            isValidType : function(type) {
                return type === 'material' || type === 'portfolio' || type === 'all';
            }
        };
    }]);
});
