define(['app'], function(app)
{
    app.controller('searchResultController', ['$scope', "serverCallService", 'translationService', '$location', '$rootScope',
             function($scope, serverCallService, translationService, $location, $rootScope) {
    	var searchObject = $location.search();
        $scope.paging = [];
        var RESULTS_PER_PAGE = 24;
        var start = 0;

        if (searchObject.q) {
            $scope.searching = true;
            $scope.searchQuery = searchObject.q;
            if (searchObject.start) {
            	start = searchObject.start;
            }
	    	
	    	doSearch($scope.searchQuery, start);
	    	$rootScope.searchFields.searchQuery = searchObject.q;
    	} else {
            $location.url('/');
        }

        function doSearch(q, startFrom) {
            start = startFrom;
            var params = {
                'q': q,
                'start': startFrom
            };
            serverCallService.makeGet("rest/search", params, getAllMaterialSuccess, getAllMaterialFail);
            serverCallService.makeGet("rest/search/countResults", params, getResultCountSuccess, getResultCountFail);
        }
    	
    	function getAllMaterialSuccess(data) {
            if (isEmpty(data)) {
                log('No data returned by session search.');
            } else {
                $scope.materials = data;
            }
            $scope.searching = false;
    	}
    	
    	function getAllMaterialFail(data, status) {
            console.log('Session search failed.')
            $scope.searching = false;
    	}
    	
    	function getResultCountSuccess(data) {
            if (isEmpty(data)) {
                log('No result count returned.');
            } else {
                $scope.resultCount = data;
                $scope.calculatePaging();
            }
    	}
    	
    	function getResultCountFail(data, status) {
            console.log('Failed to get result count');
    	}

        $scope.getNumberOfResults = function() {
            if (!$scope.materials) {
                return 0;
            }
            
            if ($scope.resultCount) {
            	return $scope.resultCount;
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
            $scope.paging = [];
            $scope.paging.before = [];
            $scope.paging.thisPage = 1;
            $scope.paging.after = [];
            $scope.paging.last = [];

            if (!$scope.resultCount) {
                return;
            }

            var pageCount = Math.ceil($scope.resultCount / RESULTS_PER_PAGE);
            $scope.paging.pageCount = pageCount;

            var thisPage = (start / RESULTS_PER_PAGE) + 1;
            if (thisPage > pageCount) {
                thisPage = pageCount;
            }
            $scope.paging.thisPage = thisPage;

            var MAX_PAGES_BEFORE_DOTS = 6;
            var MAX_PAGES_AFTER_DOTS = 3;
            var OPTIMAL_PAGES_BEFORE_THIS_PAGE = 2;
            var MAX_PAGES = MAX_PAGES_BEFORE_DOTS + MAX_PAGES_AFTER_DOTS + 1;

            var pagesBeforeDots = 0;

            if (pageCount <= MAX_PAGES) {
                // Display all page numbers
                pushPageNumbers($scope.paging.before, 1, thisPage);
                pushPageNumbers($scope.paging.after, thisPage + 1, pageCount + 1);

            } else if (pageCount - (thisPage - OPTIMAL_PAGES_BEFORE_THIS_PAGE) < MAX_PAGES) {
                // Display the last MAX_PAGES amount of pages
                var pagesBeforeThisPage = MAX_PAGES - (pageCount - thisPage) - 1;
                var pagesAfterThisPage = MAX_PAGES - pagesBeforeThisPage - 1;

                pushPageNumbers($scope.paging.before, thisPage - pagesBeforeThisPage, thisPage);
                pushPageNumbers($scope.paging.after, thisPage + 1, thisPage + 1 + pagesAfterThisPage);

            } else {
                // Display pages, dots and more pages
                if (thisPage <= OPTIMAL_PAGES_BEFORE_THIS_PAGE) {
                    pagesBeforeDots += pushPageNumbers($scope.paging.before, 1, thisPage);
                } else {
                    pagesBeforeDots += pushPageNumbers($scope.paging.before, thisPage - OPTIMAL_PAGES_BEFORE_THIS_PAGE, thisPage);
                }

                var pagesAfterThisPage = MAX_PAGES_BEFORE_DOTS - (pagesBeforeDots + 1);

                pushPageNumbers($scope.paging.after, thisPage + 1, thisPage + 1 + pagesAfterThisPage);
                pushPageNumbers($scope.paging.last,  pageCount + 1 - MAX_PAGES_AFTER_DOTS, pageCount + 1);

            }
        }

        $scope.isPreviousButtonDisabled = function() {
            if (start == 0) {
                return "disabled";
            }
            return "";
        }

        $scope.isNextButtonDisabled = function() {
            var thisPage = (start / RESULTS_PER_PAGE) + 1;

            if (thisPage >= $scope.paging.pageCount) {
                return "disabled";
            } 
            return "";
        }

        $scope.getPage = function(pageNumber) {
            if (pageNumber >= 1 && pageNumber <= $scope.paging.pageCount) {
                doSearch($scope.searchQuery, (pageNumber - 1) * RESULTS_PER_PAGE); 
            }
        }

    	$scope.$on("$destroy", function() {
    		$rootScope.searchFields.searchQuery = "";
        });
    	
    }]);
});