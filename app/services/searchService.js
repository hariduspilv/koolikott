define(['app'], function(app) {

    app.factory('searchService',['$location', function($location) {
        var searchURLbase = "search/result?q=";
        var taxonURL = "&taxon=";
        var paidURL = "&paid=";
        var typeURL = "&type="
        var languageURL = "&language=";

        var searchQuery = "";
        var searchTaxon = "";
        var searchPaid = "";
        var searchType = "";
        var searchLanguage = "";

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

                return searchURL;
            },

            queryExists : function() {
                var searchObject = $location.search();
                if (searchObject.q || searchObject.taxon || searchObject.paid === false ||
                    (searchObject.type && this.isValidType(searchObject.type)) || searchObject.language) {
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

            getPage : function() {
                var searchObject = $location.search();
                if (searchObject.page) {
                    return parseInt(searchObject.page);
                }
                return 1;
            },

            getActualPage : function() {
                var searchObject = $location.search();
                if (searchObject.page) {
                    return searchObject.page;
                }
                return 1;
            },

            buildURL : function(query, page, taxon, paid, type, language) {
                var searchURL = "#/" + searchURLbase + encodeURI(escapeQuery(query)) + "&page=" + page;
                if (taxon) {
                    searchURL += taxonURL + taxon;
                }
                if (paid && paid === 'false') {
                    searchURL += paidURL + paid;
                }
                if (type && this.isValidType(type)) {
                    searchURL += typeURL + type;
                }
                if (language) {
                    searchURL += languageURL + language;
                }
                return searchURL;
            },

            goToPage : function(page) {
                var params = {
                    'q': this.getQuery(),
                    'page': page
                };

                if (this.getTaxon()) {
                    params.taxon = this.getTaxon();
                }
                if (this.isPaid() === false) {
                    params.paid = this.isPaid();
                }
                if (this.getType() && this.isValidType(this.getType())) {
                    params.type = this.getType();
                }
                if (this.getLanguage()) {
                    params.language = this.getLanguage();
                }

                $location.url("search/result").search(params);
            },

            clearFieldsNotInSimpleSearch : function() {
                searchTaxon = '';
                searchPaid = '';
                searchType = '';
                searchLanguage = '';
            },

            isValidType : function(type) {
                return type === 'material' || type === 'portfolio' || type === 'all';
            }
        };
    }]);
});
