define([
    'app',
    'ngInfiniteScroll',
    'services/serverCallService',
    'services/searchService',
    'directives/materialBox/materialBox',
    'directives/portfolioBox/portfolioBox'
], function (app) {
    return ['$scope', 'serverCallService', 'translationService', '$location', 'searchService', '$rootScope',
        function ($scope, serverCallService, translationService, $location, searchService, $rootScope) {
            $scope.loadingNextPage = false;
            $scope.searching = false;
            $scope.canLoadMore = false;

            // Pagination variables
            $scope.paging = {};
            $scope.paging.thisPage = 0;

            $scope.sortOptions = [{
                option: 'VIEW_COUNT_ASC',
                field: 'views',
                direction: 'asc'
            }, {
                option: 'VIEW_COUNT_DESC',
                field: 'views',
                direction: 'desc'
            }, {
                option: 'ADDED_DATE_ASC',
                field: 'added',
                direction: 'asc'
            }, {
                option: 'ADDED_DATE_DESC',
                field: 'added',
                direction: 'desc'
            }];

            var RESULTS_PER_PAGE = 24;
            var start = 0;

            init();
            search();

            function init() {
                // Redirect to landing page if neither query or filters are present
                if (!searchService.queryExists()) {
                    $location.url('/');
                }

                initSortDropdown();
            }

            function allResultsLoaded() {
                return $scope.paging.thisPage >= $scope.paging.totalPages;
            }

            function search(isNewSearch) {
                if ($scope.searching) {
                    return;
                }
                var isTerminal = allResultsLoaded();

                if (isTerminal && !isNewSearch) return;

                $scope.searching = true;

                start = RESULTS_PER_PAGE * $scope.paging.thisPage;

                var params = {
                    'q': searchService.getQuery(),
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

                if (searchService.isCurriculumLiterature()) {
                    params.curriculumLiterature = searchService.isCurriculumLiterature();
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

                if (searchService.getSort() && searchService.getSortDirection()) {
                    params.sort = searchService.getSort();
                    params.sortDirection = searchService.getSortDirection();
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

            $scope.getNumberOfResults = function () {
                return $scope.totalResults || 0;
            };

            $scope.allResultsLoaded = function () {
                return allResultsLoaded();
            };

            $scope.nextPage = function () {
                if ($scope.canLoadMore) {
                    $scope.loadingNextPage = true;
                    search();
                }

                $scope.canLoadMore = true;
            };

            $scope.scrollContainer = $rootScope.isEditPortfolioMode ? '#scrollable-content' : '';

            $scope.$watch('sortDropdown', function (newValue) {
                if (!newValue)
                    return;

                for (var i = 0; i < $scope.sortOptions.length; i++) {
                    if ($scope.sortOptions[i].option === newValue) {
                        sort($scope.sortOptions[i].field, $scope.sortOptions[i].direction);
                    }
                }
            }, false);

            function sort(field, direction) {
                searchService.setSort(field);
                searchService.setSortDirection(direction);
                $location.url(searchService.getURL());
            }

            function resetAndSearch(newValue) {
                $scope.items = null;
                $scope.paging = {};
                $scope.paging.thisPage = 0;
                searchService.setSearch(newValue.q);
                search(true);
            }

            $scope.$watch(function () {
                return $location.search();
            }, function (newValue, oldValue) {
                if (newValue !== oldValue && (newValue.q || newValue.taxon || newValue.targetGroup || newValue.type )) {
                    resetAndSearch(newValue);
                } else if (newValue !== oldValue && newValue.q === "") {
                    searchService.setType('all');
                    resetAndSearch(newValue);
                }

            }, true);

            /**
             * Set drop down value from url
             */
            function initSortDropdown() {
                if (searchService.getSort() && searchService.getSortDirection()) {
                    for (var i = 0; i < $scope.sortOptions.length; i++) {
                        if ($scope.sortOptions[i].field === searchService.getSort() &&
                            $scope.sortOptions[i].direction === searchService.getSortDirection()) {
                            $scope.sortDropdown = $scope.sortOptions[i].option;
                        }
                    }
                }
            }

        }];
});
