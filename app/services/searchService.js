define(['app'], function(app) {

    app.factory('searchService',['$location', function($location) {
        var searchURLbase = "search/result?q=";
        var subjectURL = "&subject=";
        var resourceTypeURL = "&resource_type=";
        var educationalContextURL = "&educational_context=";
        var licenseTypeURL = "&license_type=";
        var paidURL = "&paid=";
        var typeURL = "&type="

        var searchQuery = "";
        var searchSubject = "";
        var searchResourceType = "";
        var searchEducationalContext = "";
        var searchLicenseType = "";
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
                if (searchPaid && searchPaid === 'false') {
                    searchURL += paidURL + searchPaid;
                }
                if (searchType && this.isValidType(searchType)) {
                    searchURL += typeURL + searchType;
                }

                return searchURL;
            },

            queryExists : function() {
                var searchObject = $location.search();
                if (searchObject.q || searchObject.subject || searchObject.resource_type || searchObject.educational_context || 
                    searchObject.license_type|| (searchObject.paid && searchObject.paid === 'false') || 
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

            isPaid : function() {
                if (searchPaid === "") {
                    var searchObject = $location.search();
                    if (searchObject.paid) {
                        return searchObject.paid;
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

            buildURL : function(query, page, subject, resourceType, educationalContext, licenseType, paid, type) {
                var searchURL = "#/" + searchURLbase + encodeURI(escapeQuery(query)) + "&page=" + page;
                if (subject) {
                    searchURL += subjectURL + subject.toLowerCase();
                }
                if (resourceType) {
                    searchURL += resourceTypeURL + resourceType.toLowerCase();
                }
                if (educationalContext) {
                    searchURL += educationalContextURL + educationalContext.toLowerCase();
                }
                if (licenseType) {
                    searchURL += licenseTypeURL + licenseType.toLowerCase();
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
                if (this.isPaid() && this.isPaid() === 'false') {
                    params.paid = this.isPaid();
                }
                if (this.getType() && this.isValidType(this.getType())) {
                    params.type = this.getType();
                }
                
                $location.url("search/result").search(params);
            },

            clearFieldsNotInSimpleSearch : function() {
                searchPaid = '';
                searchType = '';
            },

            isValidType : function(type) {
                return type === 'material' || type === 'portfolio';
            }
        };
    }]);
});