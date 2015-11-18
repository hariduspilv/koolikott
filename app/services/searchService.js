define(['app'], function(app) {

    app.factory('searchService',['$location', function($location) {
        var searchURLbase = "search/result?q=";
        var educationalContextURL = "&educational_context=";
        var paidURL = "&paid=";
        var typeURL = "&type="

        var searchQuery = "";
        var searchEducationalContext = "";
        var searchPaid = "";
        var searchType = "";

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

            setEducationalContext : function(educationalContext) {
                searchEducationalContext = educationalContext;
            },

            setPaid : function(paid) {
                searchPaid = paid;
            },

            setType : function(type) {
                searchType = type;
            },

            getURL : function() {
                var searchURL;
                if (searchQuery) {
                    searchURL = searchURLbase + escapeQuery(searchQuery)
                } else {
                    searchURL = searchURLbase;
                }

                if (searchEducationalContext) {
                    searchURL += educationalContextURL + searchEducationalContext;
                }
                if (searchPaid === false) {
                    searchURL += paidURL + searchPaid;
                }
                if (searchType && this.isValidType(searchType)) {
                    searchURL += typeURL + searchType;
                }

                return searchURL;
            },

            queryExists : function() {
                var searchObject = $location.search();
                if (searchObject.q || searchObject.educational_context || searchObject.paid === false ||
                    (searchObject.type && this.isValidType(searchObject.type))) {
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

            getEducationalContext: function() {
                if (searchEducationalContext === "") {
                    var searchObject = $location.search();
                    if (searchObject.educational_context) {
                        return searchObject.educational_context;
                    }
                }

                return searchEducationalContext;
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

            buildURL : function(query, page, educationalContext, paid, type) {
                var searchURL = "#/" + searchURLbase + encodeURI(escapeQuery(query)) + "&page=" + page;
                if (educationalContext) {
                    searchURL += educationalContextURL + educationalContext.toLowerCase();
                }
                if (paid && paid === 'false') {
                    searchURL += paidURL + paid;
                }
                if (type && this.isValidType(type)) {
                    searchURL += typeURL + type;
                }
                return searchURL;
            },

            goToPage : function(page) {
                var params = {
                    'q': this.getQuery(),
                    'page': page
                };

                if (this.getEducationalContext()) {
                    params.educational_context = this.getEducationalContext();
                }
                if (this.isPaid() === false) {
                    params.paid = this.isPaid();
                }
                if (this.getType() && this.isValidType(this.getType())) {
                    params.type = this.getType();
                }

                $location.url("search/result").search(params);
            },

            clearFieldsNotInSimpleSearch : function() {
                searchEducationalContext = '';
                searchPaid = '';
                searchType = '';
            },

            isValidType : function(type) {
                return type === 'material' || type === 'portfolio' || type === 'all';
            }
        };
    }]);
});
