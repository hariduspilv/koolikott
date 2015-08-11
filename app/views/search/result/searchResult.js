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

        // Filters
        $scope.filters = [];

        // Get search query and current page
        $scope.searchQuery = searchService.getQuery();
        $scope.paging.thisPage = searchService.getPage();

        // Expose searchService methods required for the view
        $scope.buildURL = searchService.buildURL;

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

        // Get search results
        if (!isEmpty($scope.searchQuery)) {
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
            
            serverCallService.makeGet("rest/search", params, getSearchedMaterialsSuccess, getSearchedMaterialsFail);
    	} else {
            $location.url('/');
        }
        
        function getSearchedMaterialsSuccess(data) {
            if (isEmpty(data)) {
                log('No material data returned.');
            } else {
                $scope.materials = data.materials;
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
        
        function getSearchedMaterialsFail(data, status) {
            console.log('Failed to get materials. ')
            $scope.searching = false;
        }

        $scope.getNumberOfResults = function() {
            if (!$scope.materials) {
                return 0;
            }
            
            if ($scope.totalResults) {
                return $scope.totalResults;
            }

            return $scope.materials.length;
        }

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
        }

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
            console.log('Failed to get all subjects. ');
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
            console.log('Failed to get all resource types. ');
        }

        // Get all educationalContexts
        serverCallService.makeGet("rest/educationalContext/getAll", {}, getAlleducationalContextsSuccess, getAlleducationalContextsFail);

        function getAlleducationalContextsSuccess(data) {
            $scope.educationalContexts = data;

            // Select current educationalContext in filter box
            for (i = 0; i < $scope.educationalContexts.length; i++) {
                if ($scope.educationalContexts[i].name.toLowerCase() == searchService.getEducationalContext().toLowerCase()) {
                    $scope.filters.educationalContext = $scope.educationalContexts[i];
                    break;
                }
            }
        }

        function getAlleducationalContextsFail() {
            console.log("Getting educational contexts for filter failed.");
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
            $location.url(searchService.getURL());
        }

    }]);

    app.filter('translatableItemFilter', function($filter) {
        return function(items, query, translationPrefix) {
            var out = [];

            if (angular.isArray(items) && query) {
                var translate = $filter('translate');

                items.forEach(function(item) {
                    // Get translation
                    var translatedItem = translate(translationPrefix + item.name.toUpperCase());

                    if (translatedItem.toLowerCase().indexOf(query.toLowerCase()) !== -1) {
                        out.push(item);
                    }
                });
            } else {
                // Output -> input
                out = items;
            }

            return out;
        }
    });

    app.filter('subjectFilter', function($filter) {
        return function(items, query) {
            var translationPrefix = 'MATERIAL_SUBJECT_';
            var translatableItemFilter = $filter('translatableItemFilter');
            return translatableItemFilter(items, query, translationPrefix);
        }
    });

    app.filter('resourceTypeFilter', function($filter) {
        return function(items, query) {
            var translationPrefix = '';
            var translatableItemFilter = $filter('translatableItemFilter');
            return translatableItemFilter(items, query, translationPrefix);
        }
    });

    app.filter('educationalContextFilter', function($filter) {
        return function(items, query) {
            var translationKey = '';
            var genericFilter = $filter('translatableItemFilter');
            return genericFilter(items, query, translationKey);
        }
    });

});