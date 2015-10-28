define(['app'], function(app)
{
    app.controller('searchResultController', ['$scope', "serverCallService", 'translationService', '$location', 'searchService', '$filter', 
       function($scope, serverCallService, translationService, $location, searchService, $filter) {

        // Pagination variables
        $scope.paging = [];
        $scope.paging.before = [];
        $scope.paging.thisPage = 1;
        $scope.paging.after = [];

        var RESULTS_PER_PAGE = 24;
        var PAGES_BEFORE_THIS_PAGE = 5;
        var PAGES_AFTER_THIS_PAGE = 4;
        var MAX_PAGES = PAGES_BEFORE_THIS_PAGE + 1 + PAGES_AFTER_THIS_PAGE;
        var start = 0;

        init();
        validatePageNumber();
        search();

        function init() {
            // Redirect to landing page if neither query or filters are present
            if (!searchService.queryExists()) {
                $location.url('/');
            }

            // Filters
            $scope.filters = [];

            // Get search query and current page
            $scope.searchQuery = searchService.getQuery();
            $scope.paging.thisPage = searchService.getPage();
        }

        function validatePageNumber() {
            // If page is negative, redirect to page 1
            if ($scope.paging.thisPage < 1) {
                searchService.goToPage(1);
                return;
            }

            // If page number is not an integer, redirect to correct page
            if ($scope.paging.thisPage != searchService.getActualPage()) {
                searchService.goToPage($scope.paging.thisPage);
                return;
            }
        }

        function search() {
            $scope.searching = true;
            start = RESULTS_PER_PAGE * ($scope.paging.thisPage - 1);

            var params = {
                'q': $scope.searchQuery,
                'start': start
            };

            if (searchService.getSubject()) {
                params.subject = searchService.getSubject();
            }

            if (searchService.getResourceType()) {
                params.resource_type = searchService.getResourceType();
            }

            if (searchService.getEducationalContext()) {
                params.educational_context = searchService.getEducationalContext();
            }

            if (searchService.getLicenseType()) {
                params.license_type = searchService.getLicenseType();
            }

            if (searchService.isPaid() && searchService.isPaid() === 'false') {
                params.paid = searchService.isPaid();
            }

            if (searchService.getType() && searchService.isValidType(searchService.getType())) {
                params.type = searchService.getType();
            }
            
            serverCallService.makeGet("rest/search", params, searchSuccess, searchFail);
        }
        
        function searchSuccess(data) {
            if (isEmpty(data)) {
                searchFail();
            } else {
                $scope.items = data.items;
                $scope.totalResults = data.totalResults;
                $scope.paging.totalPages = Math.ceil($scope.totalResults / RESULTS_PER_PAGE);
                if ($scope.paging.thisPage > $scope.paging.totalPages) {
                    if ($scope.paging.totalPages != 0) {
                        searchService.goToPage($scope.paging.totalPages);
                    }
                } else {
                    $scope.calculatePaging();
                }
            }
            $scope.searching = false;
        }
        
        function searchFail() {
            console.log('Search failed.');
            $scope.searching = false;
        }

        $scope.getNumberOfResults = function() {
            return $scope.totalResults || 0;
        };

        function addNumbersToArray(targetArray, from, to) {
            for (i = from; i < to; i++) {
                targetArray.push(i);
            }
        }

        $scope.calculatePaging = function() {
            if (!$scope.totalResults) {
                return;
            }

            if ($scope.paging.totalPages <= MAX_PAGES) {
                addAllPageNumbers();
            } else if ($scope.paging.totalPages - ($scope.paging.thisPage - PAGES_BEFORE_THIS_PAGE) < MAX_PAGES) {
                addLastPageNumbers();
            } else if ($scope.paging.thisPage > PAGES_BEFORE_THIS_PAGE) {
                addMiddlePageNumbers();
            } else {
                addFirstPageNumbers();
            }
        };

        function addAllPageNumbers() {
             // Add all page numbers
             addNumbersToArray($scope.paging.before, 1, $scope.paging.thisPage);
             addNumbersToArray($scope.paging.after, $scope.paging.thisPage + 1, $scope.paging.totalPages + 1);
         }

         function addLastPageNumbers() {
            // Add the last MAX_PAGES amount of page numbers
            addNumbersToArray($scope.paging.after, $scope.paging.thisPage + 1, $scope.paging.totalPages + 1);
            addNumbersToArray($scope.paging.before, $scope.paging.totalPages + 1 - MAX_PAGES, $scope.paging.thisPage);
        }

        function addFirstPageNumbers() {
            // Add less than PAGES_BEFORE_THIS_PAGE amount of page numbers before this page
            addNumbersToArray($scope.paging.before, 1, $scope.paging.thisPage);
            addNumbersToArray($scope.paging.after, $scope.paging.thisPage + 1, MAX_PAGES + 1);
        }

        function addMiddlePageNumbers() {
            // Add PAGES_BEFORE_THIS_PAGE amount of page numbers, this page number and PAGES_AFTER_THIS_PAGE amount of page numbers
            addNumbersToArray($scope.paging.before, $scope.paging.thisPage - PAGES_BEFORE_THIS_PAGE, $scope.paging.thisPage);
            addNumbersToArray($scope.paging.after, $scope.paging.thisPage + 1, $scope.paging.thisPage + 1 + PAGES_AFTER_THIS_PAGE);
        }

        // Get all subjects
        serverCallService.makeGet("rest/subject/getAll", {}, getAllSubjectsSuccess, getAllSubjectsFail);

        function getAllSubjectsSuccess(data) {
            if (isEmpty(data)) {
                log('No subjects returned.');
            } else {
                $scope.subjects = data;

                // Select current subject in filter box
                for (i = 0; i < $scope.subjects.length; i++) {
                    if ($scope.subjects[i].name.toLowerCase() == searchService.getSubject().toLowerCase()) {
                        $scope.filters.subject = $scope.subjects[i];
                        break;
                    }
                }
            }          
        }

        function getAllSubjectsFail(data, status) { 
            console.log('Failed to get all subjects.');
        }

        // Get all resourceTypes
        serverCallService.makeGet("rest/resourceType/getAll", {}, getAllResourceTypesSuccess, getAllResourceTypesFail);

        function getAllResourceTypesSuccess(data) {
            if (isEmpty(data)) {
                log('No resource types returned.');
            } else {
                $scope.resourceTypes = data;

                // Select current resourceType in filter box
                for (i = 0; i < $scope.resourceTypes.length; i++) {
                    if ($scope.resourceTypes[i].name.toLowerCase() == searchService.getResourceType().toLowerCase()) {
                        $scope.filters.resourceType = $scope.resourceTypes[i];
                        break;
                    }
                }
            }
        }

        function getAllResourceTypesFail(data, status) { 
            console.log('Failed to get all resource types.');
        }

        // Get all educationalContexts
        serverCallService.makeGet("rest/educationalContext/getAll", {}, getAlleducationalContextsSuccess, getAlleducationalContextsFail);

        function getAlleducationalContextsSuccess(data) {
            if (isEmpty(data)) {
                log('No educational contexts returned.');
            } else {
                $scope.educationalContexts = data;

                // Select current educationalContext in filter box
                for (i = 0; i < $scope.educationalContexts.length; i++) {
                    if ($scope.educationalContexts[i].name.toLowerCase() == searchService.getEducationalContext().toLowerCase()) {
                        $scope.filters.educationalContext = $scope.educationalContexts[i];
                        break;
                    }
                }
            }
        }

        function getAlleducationalContextsFail() {
            console.log("Getting educational contexts for filter failed.");
        }

        // Get all licenseTypes
        serverCallService.makeGet("rest/licenseType/getAll", {}, getAllLicenseTypesSuccess, getAllLicenseTypesFail);

        function getAllLicenseTypesSuccess(data) {
            if (isEmpty(data)) {
                log('No license types returned.');
            } else {
                $scope.licenseTypes = data;

                // Select current licenseType in filter box
                for (i = 0; i < $scope.licenseTypes.length; i++) {
                    if ($scope.licenseTypes[i].name.toLowerCase() == searchService.getLicenseType().toLowerCase()) {
                        $scope.filters.licenseType = $scope.licenseTypes[i];
                        break;
                    }
                }
            }
        }

        function getAllLicenseTypesFail(data, status) { 
            console.log('Failed to get all license types.');
        }

        $scope.filter = function() {
            searchService.setSearch(searchService.getQuery());

            if ($scope.filters.subject) {
                searchService.setSubject($scope.filters.subject.name.toLowerCase());
            } else {
                searchService.setSubject('');
            }

            if ($scope.filters.resourceType) {
                searchService.setResourceType($scope.filters.resourceType.name.toLowerCase());
            } else {
                searchService.setResourceType('');
            }

            if ($scope.filters.educationalContext) {
                searchService.setEducationalContext($scope.filters.educationalContext.name.toLowerCase());
            } else {
                searchService.setEducationalContext('');
            }

            if ($scope.filters.licenseType) {
                searchService.setLicenseType($scope.filters.licenseType.name.toLowerCase());
            } else {
                searchService.setLicenseType('');
            }

            $location.url(searchService.getURL());
        }

        $scope.reset = function() {
           $scope.filters.subject = null;
           searchService.setSubject('');
           $scope.filters.resourceType = null;
           searchService.setResourceType('');
           $scope.filters.educationalContext = null;
           searchService.setEducationalContext('');
           $scope.filters.licenseType = null;
           searchService.setLicenseType('');
        }

        $scope.buildPageURL = function(page) {
            var subject = $scope.filters.subject ? $scope.filters.subject.name : null;
            var resourceType = $scope.filters.resourceType ? $scope.filters.resourceType.name : null;
            var educationalContext = $scope.filters.educationalContext ? $scope.filters.educationalContext.name : null;
            var licenseType = $scope.filters.licenseType ? $scope.filters.licenseType.name : null;

            return searchService.buildURL($scope.searchQuery, page, subject, resourceType, educationalContext, licenseType, 
                searchService.isPaid(), searchService.getType());
        }


    }]);

app.filter('translatableItemFilter', function($filter) {
    return function(items, query, translationPrefix) {
        var out = [];

        if (angular.isArray(items) && query) {
            items.forEach(function(item) {
                // Get translation
                var translatedItem = $filter('translate')(translationPrefix + item.name.toUpperCase());

                if (translatedItem.toLowerCase().indexOf(query.toLowerCase()) !== -1) {
                    out.push(item);
                }
            });
        } else {
            out = items;
        }

        return out;
    }
});

app.filter('orderByTranslation', function($filter) {
    return function(items, translationPrefix) {

        if (angular.isArray(items)) {
            for (i = 0; i < items.length; i++) {
                // Get translation
                var translatedItem = $filter('translate')(translationPrefix + items[i].name.toUpperCase());

                // Create temporary property
                items[i].translation = translatedItem.toLowerCase();
            }

            // Sort alphabetically
            items = $filter('orderBy')(items, '-translation', true);

            // Remove translation property
            for (i = 0; i < items.length; i++) {
                items[i].translation = null;
            }

        } 

        return items;
    }
});

app.filter('subjectFilter', function($filter) {
    return function(items, query) {
        var translationPrefix = 'SUBJECT_';
        items = $filter('translatableItemFilter')(items, query, translationPrefix);
        items = $filter('orderByTranslation')(items, translationPrefix);
        return items;
    }
});

app.filter('resourceTypeFilter', function($filter) {
    return function(items, query) {
        var translationPrefix = '';
        items = $filter('translatableItemFilter')(items, query, translationPrefix);
        items = $filter('orderByTranslation')(items, translationPrefix);
        return items;
    }
});

app.filter('educationalContextFilter', function($filter) {
    return function(items, query) {
        var translationPrefix = '';
        items = $filter('translatableItemFilter')(items, query, translationPrefix);
        return items;
    }
});

app.filter('licenseTypeFilter', function($filter) {
    return function(items, query) {
        var translationPrefix = 'LICENSETYPE_';
        items = $filter('translatableItemFilter')(items, query, translationPrefix);
        items = $filter('orderByTranslation')(items, translationPrefix);
        return items;
    }
});

});