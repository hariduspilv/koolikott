define(['app'], function(app)
{
    app.controller('searchResultController', ['$scope', "serverCallService", 'translationService', '$location', 'searchService',
       function($scope, serverCallService, translationService, $location, searchService) {

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

            if (searchService.getTaxon()) {
                params.taxon = searchService.getTaxon();
            }

            if (searchService.isPaid() === false) {
                params.paid = searchService.isPaid();
            }

            if (searchService.getType() && searchService.isValidType(searchService.getType())) {
                params.type = searchService.getType();
            }

            if (searchService.getLanguage()) {
                params.language = searchService.getLanguage();
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

        $scope.buildPageURL = function(page) {
            return searchService.buildURL($scope.searchQuery, page, searchService.getTaxon(), searchService.isPaid().toString(), 
                searchService.getType(), searchService.getLanguage());
        }

    }]);
});
