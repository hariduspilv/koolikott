define(['app'], function(app)
{
    app.directive('dopEditPortfolioModeHeader', ['translationService', '$location', '$mdSidenav', '$mdDialog', '$rootScope', 'serverCallService', 'searchService', 'toastService',
     function(translationService, $location, $mdSidenav, $mdDialog, $rootScope, serverCallService, searchService, toastService) {
        return {
            scope: true,
            templateUrl: 'directives/editPortfolioModeHeader/editPortfolioModeHeader.html',
            controller: function ($scope, $location) {

                $scope.toggleSidenav = function() {
                    $mdSidenav('left').toggle();
                };

                $scope.exitEditPortfolioMode = function() {
                    $location.url("/");
                };

                $scope.makePublic = function() {
                    $rootScope.savedPortfolio.visibility = 'PUBLIC';
                    updatePortfolio();
                    
                    toastService.show('PORTFOLIO_HAS_BEEN_MADE_PUBLIC');
                };

                $scope.makeNotListed = function() {
                    $rootScope.savedPortfolio.visibility = 'NOT_LISTED';
                    updatePortfolio();
                };

                $scope.makePrivate = function() {
                    $rootScope.savedPortfolio.visibility = 'PRIVATE';
                    updatePortfolio();
                };
                
                $scope.getShareUrl = buildShareUrl();
                
                function buildShareUrl() {
                    var protocol = $location.protocol();
                    var host = $location.host();
                    var path = '/#/porftolio'
                    var params = $location.search();

                    return protocol + '://' + host + path + '?id=' + params.id;
                }

                function updatePortfolio() {
                    var url = "rest/portfolio/update";
                    serverCallService.makePost(url, $rootScope.savedPortfolio, updatePortfolioSuccess, updatePortfolioFailed);
                }
                
                function updatePortfolioSuccess(portfolio) {
                    if (isEmpty(portfolio)) {
                        updatePortfolioFailed();
                    } else {
                        log('Portfolio updated.');
                    }
                }
                
                function updatePortfolioFailed() {
                    log('Updating portfolio failed.');
                }

                // Search

                $scope.searchFields = {};
                $scope.searchFields.searchQuery = "";
                $scope.detailedSearch = {};

                $scope.search = function() {
                    if (!isEmpty($scope.searchFields.searchQuery)) {
                        searchService.setSearch($scope.searchFields.searchQuery);
                        searchService.clearFieldsNotInSimpleSearch();
                        searchService.setType('material');
                        $location.url(searchService.getURL());
                    }
                };

                $scope.openDetailedSearch = function() {
                    $scope.detailedSearch.isVisible = true;
                    $scope.detailedSearch.queryIn = $scope.searchFields.searchQuery;
                    $scope.searchFields.searchQuery = $scope.detailedSearch.mainField; 
                }

                $scope.closeDetailedSearch = function() {
                    $scope.detailedSearch.isVisible = false;
                    $scope.searchFields.searchQuery = (($scope.searchFields.searchQuery || "") + " " + $scope.detailedSearch.queryOut).trim();
                    $scope.detailedSearch.queryIn = null;
                };

                $scope.detailedSearch.doSearch = function() {
                    var query = ($scope.searchFields.searchQuery || "") + " " + $scope.detailedSearch.queryOut;
                    searchService.setSearch(query.trim());
                    $location.url(searchService.getURL());
                };

                $scope.searchFieldEnterPressed = function() {
                    if (!$scope.detailedSearch.isVisible) {
                        $scope.search();
                    }
                };

                $scope.clickOutside = function() {
                    if ($scope.detailedSearch.isVisible) {
                        $scope.closeDetailedSearch();
                    }
                };

                $scope.$watch('detailedSearch.mainField', function(newValue, oldValue) {
                    if (newValue != oldValue) {
                        $scope.searchFields.searchQuery = newValue || "";
                    }
                }, true);

            }
        };
    }]);

    return app;
});
 