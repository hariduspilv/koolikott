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
                log('No data returned by session search.');
            } else {
                $scope.materials = data.materials;
                $scope.totalResults = data.totalResults;
                $scope.paging.totalPages = Math.ceil($scope.totalResults / RESULTS_PER_PAGE);
                if ($scope.paging.thisPage > $scope.paging.totalPages) {
                    $scope.paging.thisPage = $scope.paging.totalPages;
                }
                $scope.calculatePaging();
            }
            $scope.searching = false;
        }
        
        function getAllMaterialFail(data, status) {
            console.log('Session search failed.')
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

            totalPages = $scope.paging.totalPages;
            thisPage = $scope.paging.thisPage;

            if (totalPages <= MAX_PAGES) {
                // Display all page numbers
                pushPageNumbers($scope.paging.before, 1, thisPage);
                pushPageNumbers($scope.paging.after, thisPage + 1, totalPages + 1);
            } else if (totalPages - (thisPage - PAGES_BEFORE_THIS_PAGE) < MAX_PAGES) {
                // Display the last MAX_PAGES amount of page numbers
                var pagesBeforeThisPage = MAX_PAGES - (totalPages - thisPage) - 1;
                var pagesAfterThisPage = MAX_PAGES - pagesBeforeThisPage - 1;

                pushPageNumbers($scope.paging.before, thisPage - pagesBeforeThisPage, thisPage);
                pushPageNumbers($scope.paging.after, thisPage + 1, thisPage + 1 + pagesAfterThisPage);
            } else {
                var pagesBefore = 0;
                if (thisPage > PAGES_BEFORE_THIS_PAGE) {
                    // Display PAGES_BEFORE_THIS_PAGE amount of page numbers, this page number and PAGES_AFTER_THIS_PAGE amount of page numbers
                    pagesBefore += pushPageNumbers($scope.paging.before, thisPage - PAGES_BEFORE_THIS_PAGE, thisPage);
                } else {
                    // Display less than PAGES_BEFORE_THIS_PAGE amount of page numbers before this page
                    pagesBefore += pushPageNumbers($scope.paging.before, 1, thisPage);
                }
                pushPageNumbers($scope.paging.after, thisPage + 1, thisPage + 1 + PAGES_AFTER_THIS_PAGE + (PAGES_BEFORE_THIS_PAGE - pagesBefore));
            }
        }

        $scope.getPageLink = function(page) {
            if (page >= 1 && page <= $scope.paging.totalPages) {
                return "/#/search/result?q=" + searchObject.q + "&page=" + page;
            }
            return "";
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
                $location.url("/search/result").search(params);
            }
        }

    }]);
});