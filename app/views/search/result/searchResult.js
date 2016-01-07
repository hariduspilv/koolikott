define(['app'], function(app)
{
    app.controller('searchResultController', ['$scope', "serverCallService", 'translationService', '$location', 'searchService',
       function($scope, serverCallService, translationService, $location, searchService) {
        $scope.loadingNextPage = false;
        $scope.searching = false;
        
        // Pagination variables
        $scope.paging = {};
        $scope.paging.thisPage = 0;

        var RESULTS_PER_PAGE = 24;
        var start = 0;

        init();
        search();

        function init() {
            // Redirect to landing page if neither query or filters are present
            if (!searchService.queryExists()) {
                $location.url('/');
            }

            // Get search query and current page
            $scope.searchQuery = searchService.getQuery();
        }
        
        function allResultsLoaded() {
            return $scope.paging.thisPage >= $scope.paging.totalPages;
        }

        function search() {
            var isTerminal = allResultsLoaded();
            
            if (isTerminal) return;

            if (!$scope.loadingNextPage)
                $scope.searching = true;

            start = RESULTS_PER_PAGE * $scope.paging.thisPage;

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

            if (searchService.getTargetGroups()) {
                params.targetGroup = searchService.getTargetGroups();
            }

            if (searchService.getResourceType()) {
                params.resourceType = searchService.getResourceType();
            }

            if (searchService.isSpecialEducation() === true) {
                params.specialEducation = searchService.isSpecialEducation();
            }

            if (searchService.getIssuedFrom()) {
                params.issuedFrom = searchService.getIssuedFrom();
            }

            if (searchService.getCrossCurricularTheme()) {
                params.crossCurricularTheme = searchService.getCrossCurricularTheme();
            }

            if (searchService.getKeyCompetence()) {
                params.keyCompetence = searchService.getKeyCompetence();
            }

            serverCallService.makeGet("rest/search", params, searchSuccess, searchFail);
        }

        function searchSuccess(data) {
            if (isEmpty(data)) {
                searchFail();
            } else {
                $scope.items = $scope.items || [];
                $scope.items.push.apply($scope.items, data.items);

                $scope.paging.thisPage++;
                $scope.totalResults = data.totalResults;
                $scope.paging.totalPages = Math.ceil($scope.totalResults / RESULTS_PER_PAGE);
            }

            $scope.searching = false;
            $scope.loadingNextPage = false;
        }

        function searchFail() {
            console.log('Search failed.');
            $scope.searching = false;
            $scope.loadingNextPage = false;
        }

        $scope.getNumberOfResults = function() {
            return $scope.totalResults || 0;
        };
        
        $scope.allResultsLoaded = function() {
            return allResultsLoaded();
        }

        $scope.nextPage = function() {
            $scope.loadingNextPage = true;
            
            search();
        }
    }]);
});
