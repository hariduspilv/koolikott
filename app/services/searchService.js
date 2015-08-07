define(['app'], function(app) {

	app.factory('searchService',['$location', '$rootScope', function($location, $rootScope) {
		var searchURLbase = "search/result?q=";
        var subjectURL = "&subject=";
		var searchQuery = "";
        var searchSubject = "";

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
				$rootScope.searchFields.searchQuery = query;
			},
			
            setSubject : function(subject) {
                searchSubject = subject;
            },

	        getURL : function() {
	        	var query = escapeQuery(searchQuery);

                var searchURL = searchURLbase + query
                if (searchSubject) {
                    searchURL += subjectURL + searchSubject;
                } 
	        	return searchURL;
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

            getSubject : function() {
                if (searchSubject === "") {
                    var searchObject = $location.search();
                    if (searchObject.subject) {
                        return searchObject.subject;
                    }
                }

                return searchSubject;
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

            buildURL : function(query, page, subject) {
            	var searchURL = "#/" + searchURLbase + encodeURI(escapeQuery(query)) + "&page=" + page;
                if (subject) {
                    searchURL += subjectURL + subject;
                }
                return searchURL;
            }, 

            goToPage : function(page) {
                var params = {
                    'q': this.getQuery(),
                    'page': page
                };
                if (this.getSubject()) {
                    params.subject = this.getSubject();
                }
                $location.url("search/result").search(params);
       	    }
	    };
	}]);
});