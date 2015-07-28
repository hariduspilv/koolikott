define(['app'], function(app)
{
    app.controller('searchResultController', ['$scope', "serverCallService", 'translationService', '$location', 
             function($scope, serverCallService, translationService, $location) {
    	
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

        var searchObject = $location.search();

        // Get current page
        if (searchObject.page && searchObject.page >= 1) {
            $scope.paging.thisPage = parseInt(searchObject.page);
            start = RESULTS_PER_PAGE * ($scope.paging.thisPage - 1);
        }

        // Get search results
        if (searchObject.q) {
            $scope.searchQuery = searchObject.q;
            $scope.searching = true;
            var params = {
                'q': searchObject.q,
                'start': start
            };
            serverCallService.makeGet("rest/search", params, getAllMaterialSuccess, getAllMaterialFail);
    	} else {
            $location.url('/');
        }
        
        function getAllMaterialSuccess(data) {
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
        
        function getAllMaterialFail(data, status) {
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

        function pushPageNumbers(targetArray, from, to) {
            for (i = from; i < to; i++) {
                targetArray.push(i);
            }
            return to - from;
        }

        $scope.calculatePaging = function() {
            if (!$scope.totalResults) {
                return;
            }

            if ($scope.paging.totalPages <= MAX_PAGES) {
                showAllPages();
            } else if ($scope.paging.totalPages - ($scope.paging.thisPage - PAGES_BEFORE_THIS_PAGE) < MAX_PAGES) {
                showLastPages();
            } else {
                if ($scope.paging.thisPage > PAGES_BEFORE_THIS_PAGE) {
                    showMiddlePages();
                } else {
                    showFirstPages();
                }
            }
        }

        function showAllPages() {
             // Display all page numbers
            pushPageNumbers($scope.paging.before, 1, $scope.paging.thisPage);
            pushPageNumbers($scope.paging.after, $scope.paging.thisPage + 1, $scope.paging.totalPages + 1);
        }

        function showLastPages() {
            // Display the last MAX_PAGES amount of page numbers
            var pagesBeforeThisPage = MAX_PAGES - ($scope.paging.totalPages - $scope.paging.thisPage) - 1;
            var pagesAfterThisPage = MAX_PAGES - pagesBeforeThisPage - 1;

            pushPageNumbers($scope.paging.before, $scope.paging.thisPage - pagesBeforeThisPage, $scope.paging.thisPage);
            pushPageNumbers($scope.paging.after, $scope.paging.thisPage + 1, $scope.paging.thisPage + 1 + pagesAfterThisPage);
        }

        function showFirstPages() {
            var pagesBefore = 0;
            // Display less than PAGES_BEFORE_THIS_PAGE amount of page numbers before this page
            pagesBefore += pushPageNumbers($scope.paging.before, 1, $scope.paging.thisPage);
            pushPageNumbers($scope.paging.after, $scope.paging.thisPage + 1, $scope.paging.thisPage + 1 + PAGES_AFTER_THIS_PAGE + (PAGES_BEFORE_THIS_PAGE - pagesBefore));
        }

        function showMiddlePages() {
            var pagesBefore = 0;
            // Display PAGES_BEFORE_THIS_PAGE amount of page numbers, this page number and PAGES_AFTER_THIS_PAGE amount of page numbers
            pushPageNumbers($scope.paging.before, $scope.paging.thisPage - PAGES_BEFORE_THIS_PAGE, $scope.paging.thisPage);
            pushPageNumbers($scope.paging.after, $scope.paging.thisPage + 1, $scope.paging.thisPage + 1 + PAGES_AFTER_THIS_PAGE);
        }

        $scope.isPreviousButtonDisabled = function() {
            return (start == 0) ? "disabled" : "";
        }

        $scope.isNextButtonDisabled = function() {
            return ($scope.paging.thisPage >= $scope.paging.totalPages) ? "disabled" : "";
        }

        $scope.goToPage = function(page) {
            if (page >= 1 && page <= $scope.paging.totalPages) {
                var params = {
                    'q': searchObject.q,
                    'page': page
                };
                $location.url("search/result").search(params);
            }
        }

    }]);
});