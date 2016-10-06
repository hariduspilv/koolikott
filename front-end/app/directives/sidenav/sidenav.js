define([
    'angularAMD',
    'services/serverCallService',
    'services/searchService',
    'directives/learningObjectRow/learningObjectRow'
], function (angularAMD) {
    angularAMD.directive('dopSidenav', ['serverCallService', '$location', 'searchService', 'ivhTreeviewMgr', function () {
        return {
            scope: true,
            templateUrl: 'directives/sidenav/sidenav.html',
            controller: function ($rootScope, $scope, serverCallService, $location, searchService, $timeout) {

              if(window.innerWidth > 1280) {
                $scope.sideNavOpen = true;
              }

              var SIDE_ITEMS_AMOUNT = 5;

                var params = {
                    q: 'recommended:true',
                    start: 0,
                    sort: 'recommendation_timestamp',
                    sortDirection: 'desc',
                    limit: SIDE_ITEMS_AMOUNT
                };

                serverCallService.makeGet("rest/search", params, getRecommendationsSuccess, getRecommendationsFail);


                function isSearchResultPage() {
                    return $location.url().startsWith('/' + searchService.getSearchURLbase());
                }

                if (isSearchResultPage()) {
                    var params = {
                        limit: SIDE_ITEMS_AMOUNT
                    };

                    var originalSort = searchService.getSort();
                    var originalSortDirection = searchService.getSortDirection();
                    searchService.setSort('like_score');
                    searchService.setSortDirection('desc');
                    var searchUrl = searchService.getQueryURL(true);
                    searchService.setSort(originalSort);
                    searchService.setSortDirection(originalSortDirection);

                    serverCallService.makeGet("rest/search?" + searchUrl, params, searchMostLikedSuccess, getMostLikedFail);
                } else {
                    var params = {
                        maxResults: SIDE_ITEMS_AMOUNT
                    };

                    serverCallService.makeGet("rest/search/mostLiked", params, getMostLikedSuccess, getMostLikedFail);
                }

                function getRecommendationsSuccess(data) {
                    if (isEmpty(data)) {
                        log('No data returned by recommended item search.');
                    } else {
                        $scope.recommendations = data.items;
                    }
                }

                function getRecommendationsFail(data, status) {
                    console.log('Session search failed.')
                }

                function getMostLikedSuccess(data) {
                    if (isEmpty(data)) {
                        getMostLikedFail();
                    } else {
                        $scope.mostLikedList = data;
                    }
                }

                function getMostLikedFail() {
                    console.log('Most liked search failed.')
                }

                function searchMostLikedSuccess(data) {
                    if (isEmpty(data)) {
                        getMostLikedFail();
                    } else {
                        $scope.mostLikedList = data.items;
                    }
                }

                $scope.showMoreRecommendations = function() {
                    searchService.setSearch('recommended:true');
                    searchService.clearFieldsNotInSimpleSearch();
                    searchService.setSort('recommendation_timestamp');
                    searchService.setSortDirection('desc');

                    $location.url('/' + searchService.getSearchURLbase() + searchService.getQueryURL());
                }

                // Taxon dummy part
                // ================
                $rootScope.taxonomy = {};
                $rootScope.taxonomy.isVisible = false;
                if ($location.$$path == '/material' || $location.$$path == '/search/result') {
                    $rootScope.taxonomy.isVisible = true;
                }
                $rootScope.taxonomy.isPrimaryVisible = false;
                $rootScope.taxonomy.isHighVisible = false;
                $scope.category = 'high';
                $scope.triggerPlace = 'sidebar';
                $scope.taxonomy.isHighTaxonsOpen = false;
                $scope.taxonomy.isPrimaryTaxonsOpen = false;

                $rootScope.changeTaxonomyDataAndVisibility = function(triggerPlace, category) {
                    $scope.category = category;
                    $scope.triggerPlace = triggerPlace;

                    if ($location.$$path == '/material' && category == 'high') {
                        serverCallService.makeGet("utils/taxon_selected.json", params, getTaxonJsonSuccess, getTaxonJsonFail);
                    } else {
                        serverCallService.makeGet("utils/taxon.json", params, getTaxonJsonSuccess, getTaxonJsonFail);
                    }


                    if (category == 'high') {
                        if (triggerPlace == 'material') {
                            $rootScope.taxonomy.isHighVisible = true;
                        } else {
                            $rootScope.taxonomy.isHighVisible = $rootScope.taxonomy.isHighVisible === false ? true : false;
                        }
                    } else if (category == 'primary') {
                        $rootScope.taxonomy.isPrimaryVisible = $rootScope.taxonomy.isPrimaryVisible === false ? true : false;
                    }
                }

                function getTaxonJsonSuccess (data) {
                    if ($location.$$path == '/material' && $scope.category == 'high') {
                        $rootScope.taxonomy.taxonHighData = data;
                        ivhTreeviewMgr.expandTo($rootScope.taxonomy.taxonHighData, 'language_selected');
                    } else if ($scope.triggerPlace == 'sidebar' && $scope.category == 'high') {
                        $rootScope.taxonomy.taxonHighData = data;
                    } else {
                        $rootScope.taxonomy.taxonPrimaryData = data;
                    }
                }

                function getTaxonJsonFail (data, status) {
                    console.log(data);
                    console.log(status);
                }

                $scope.toggleAllTaxons = function (category) {
                    if (category == 'high') {
                        $scope.taxonomy.isHighTaxonsOpen = $scope.taxonomy.isHighTaxonsOpen === false ? true : false;
                        if ($scope.taxonomy.isHighTaxonsOpen) {
                            ivhTreeviewMgr.expandRecursive($rootScope.taxonomy.taxonHighData);
                        } else {
                            ivhTreeviewMgr.collapseRecursive($rootScope.taxonomy.taxonHighData);
                        }
                    } else {
                        $scope.taxonomy.isPrimaryTaxonsOpen = $scope.taxonomy.isPrimaryTaxonsOpen === false ? true : false;
                        if ($scope.taxonomy.isPrimaryTaxonsOpen) {
                            ivhTreeviewMgr.expandRecursive($rootScope.taxonomy.taxonPrimaryData);
                        } else {
                            ivhTreeviewMgr.collapseRecursive($rootScope.taxonomy.taxonPrimaryData);
                        }
                    }
                }

            }
        }
    }]);
});
