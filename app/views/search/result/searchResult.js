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

        // Get search query and current page
        $scope.searchQuery = searchService.getQuery();
        $scope.paging.thisPage = searchService.getPage();

        // Get search results
        if (!isEmpty($scope.searchQuery)) {
            $scope.searching = true;
            start = RESULTS_PER_PAGE * ($scope.paging.thisPage - 1);
            var params = {
                'q': $scope.searchQuery,
                'start': start
            };
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
                    $scope.goToPage($scope.paging.totalPages);
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

        goToPage = function(page) {
            if (page >= 1 && page <= $scope.paging.totalPages) {
                var params = {
                    'q': $scope.searchQuery,
                    'page': page
                };
                $location.url("search/result").search(params);
            }
        }

    }]);
});