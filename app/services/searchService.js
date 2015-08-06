define(['app'], function(app) {

	app.factory('searchService',['$location', '$rootScope', function($location, $rootScope) {
		var searchURLbase = "search/result?q=";
		var searchQuery = "";
		
		return {
			
			setSearch : function(query) {
				searchQuery = query;
				$rootScope.searchFields.searchQuery = query;
			},
			
	        getURL : function() {
	        	var query = searchQuery
	        	//replace backslashes
                query = query.replace(/\\/g, "\\\\");

                //make plus signs into \+
                query = query.replace(/\+/g, "\\+");
	        	return searchURLbase + query;
	        },

	        getQuery : function() {
				if(searchQuery === ""){
					var searchObject = $location.search();
					if (searchObject.q) {
						//get back + sign
               			query = searchObject.q.replace(/\\ /g, "+");
                		//make backslashes singular
                		query = query.replace(/\\\\/g, "\\");
						searchQuery = query;
					}
				}

				return searchQuery;
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

            buildURL : function(query, page) {
            	return "#/" + searchURLbase + query + "&page=" + page;
            }, 

            goToPage : function(page) {
                var params = {
                    'q': this.getQuery(),
                    'page': page
                };
                $location.url("search/result").search(params);
       	    }
	    };
	}]);
});