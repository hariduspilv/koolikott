define([
    'angularAMD',
    'services/serverCallService',
    'services/searchService',
    'directives/learningObjectRow/learningObjectRow',
    'directives/sidebarTaxon/sidebarTaxon'
], function (angularAMD) {
    angularAMD.directive('dopSidenav', ['serverCallService', '$location', '$sce','searchService', 'authenticatedUserService', '$mdDialog', function () {
        return {
            scope: true,
            templateUrl: 'directives/sidenav/sidenav.html',
            controller: function ($rootScope, $scope, $location,serverCallService, $location, searchService, $timeout, metadataService, authenticatedUserService, $sce, $mdDialog) {

                $scope.oneAtATime = true;

                // List of taxon icons
                $scope.taxonIcons = [
                    'extension',
                    'accessibility',
                    'school',
                    'palette'
                ]

                $scope.$watch(function () {
                    return authenticatedUserService.getUser();
                }, function (user) {
                    $scope.user = user;
                }, true);

                $scope.isAdmin = function () {
                    return authenticatedUserService.isAdmin();
                };

                $scope.isModerator = function () {
                    return authenticatedUserService.isModerator();
                };

                $scope.checkUser = function(e, redirectURL) {
                    if ($scope.user) {
                        $location.url('/' + $scope.user.username + redirectURL);
                    } else {
                        $rootScope.afterAuthRedirectURL = redirectURL;
                        $rootScope.sidenavLogin = redirectURL;
                        openLoginDialog(e);
                    }
                };

                $scope.modUser = function() {
                    if (authenticatedUserService.isModerator() || authenticatedUserService.isAdmin()) {
                        return true;
                    } else {
                        return false;
                    }
                }


                //Checks the location
                $scope.isLocation = function (location) {
                    var isLocation = location === $location.path();
                    return isLocation;
                }

                metadataService.loadReducedTaxon(function(callback) {
                    $scope.reducedTaxon = callback;
                });

                if(window.innerWidth > 1280) {$scope.sideNavOpen = true;}



                // TODO: Taxonomy logic
                // DATA: $scope.reducedTaxon

                $scope.test = function(data) {
                    if(data.children) {
                        $scope.asd = data.children;
                        var html = $sce.trustAsHtml('<li ng-repeat="item in asd">' +
                            '<div ng-bind-html="test(item)">{{asd}}</div>' +
                            '</li>')
                        return html;
                    }
                }

                // TAXONOMY LOGIC END



              $scope.status = true;

              $scope.swapState = function() {
                  $scope.status = !$scope.status;
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

                function openLoginDialog(e) {
                    $mdDialog.show(angularAMD.route({
                        templateUrl: 'views/loginDialog/loginDialog.html',
                        controllerUrl: 'views/loginDialog/loginDialog',
                        targetEvent: e
                    }));
                }

                $scope.showMoreRecommendations = function() {
                    searchService.setSearch('recommended:true');
                    searchService.clearFieldsNotInSimpleSearch();
                    searchService.setSort('recommendation_timestamp');
                    searchService.setSortDirection('desc');

                    $location.url('/' + searchService.getSearchURLbase() + searchService.getQueryURL());
                }



            }
        }
    }]);
});
