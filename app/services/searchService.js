define(['app'], function(app) {

    app.factory('searchService',['$location', function($location) {
        var searchURLbase = "search/result?q=";
        var subjectURL = "&subject=";
        var resourceTypeURL = "&resource_type=";
        var educationalContextURL = "&educational_context=";
        var licenseTypeURL = "&license_type=";
        var titleURL = "&title=";

        var searchQuery = "";
        var searchSubject = "";
        var searchResourceType = "";
        var searchEducationalContext = "";
        var searchLicenseType = "";
        var searchTitle = "";

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
            
            setSubject : function(subject) {
                searchSubject = subject;
            },

            setResourceType : function(resourceType) {
                searchResourceType = resourceType;
            },

            setEducationalContext : function(educationalContext) {
                searchEducationalContext = educationalContext;
            },

            setLicenseType : function(licenseType) {
                searchLicenseType = licenseType;
            },

            setTitle : function(title) {
                searchTitle = title;
            },

            getURL : function() {
                var searchURL;
                if (searchQuery) {
                    searchURL = searchURLbase + escapeQuery(searchQuery)
                } else {
                    searchURL = searchURLbase;
                }

                if (searchSubject) {
                    searchURL += subjectURL + searchSubject;
                } 
                if (searchResourceType) {
                    searchURL += resourceTypeURL + searchResourceType;
                }
                if (searchEducationalContext) {
                    searchURL += educationalContextURL + searchEducationalContext;
                }
                if (searchLicenseType) {
                    searchURL += licenseTypeURL + searchLicenseType;
                }
                if (searchTitle) {
                    searchURL += titleURL + searchTitle;
                }

                return searchURL;
            },

            queryExists : function() {
                var searchObject = $location.search();
                if (searchObject.q || searchObject.subject || searchObject.resource_type || searchObject.educational_context || searchObject.license_type || searchObject.title) {
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

            getSubject : function() {
                if (searchSubject === "") {
                    var searchObject = $location.search();
                    if (searchObject.subject) {
                        return searchObject.subject;
                    }
                }

                return searchSubject;
            },

            getResourceType : function() {
                if (searchResourceType === "") {
                    var searchObject = $location.search();
                    if (searchObject.resource_type) {
                        return searchObject.resource_type;
                    }
                }

                return searchResourceType;
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

            getLicenseType : function() {
                if (searchLicenseType === "") {
                    var searchObject = $location.search();
                    if (searchObject.license_type) {
                        return searchObject.license_type;
                    }
                }

                return searchLicenseType;
            },

            getTitle : function() {
                if (searchTitle === "") {
                    var searchObject = $location.search();
                    if (searchObject.title) {
                        return searchObject.title;
                    }
                }

                return searchTitle;
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

            buildURL : function(query, page, subject, resourceType, educationalContext, licenseType, title) {
                var searchURL = "#/" + searchURLbase + encodeURI(escapeQuery(query)) + "&page=" + page;
                if (subject) {
                    searchURL += subjectURL + subject;
                }
                if (resourceType) {
                    searchURL += resourceTypeURL + resourceType;
                }
                if (educationalContext) {
                    searchURL += educationalContextURL + educationalContext;
                }
                if (licenseType) {
                    searchURL += licenseTypeURL + licenseType;
                }
                if (title) {
                    searchURL += titleURL + title;
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
                if (this.getResourceType()) {
                    params.resource_type = this.getResourceType();
                }
                if (this.getEducationalContext()) {
                    params.educational_context = this.getEducationalContext();
                }
                if (this.getLicenseType()) {
                    params.license_type = this.getLicenseType();
                }
                if (this.getTitle()) {
                    params.title = this.getTitle();
                }
                
                $location.url("search/result").search(params);
            },

            clearFieldsNotInSimpleSearch : function() {
                searchTitle = '';
            }
        };
    }]);
});