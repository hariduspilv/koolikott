define(['app'], function(app)
{
    app.directive('dopEditPortfolioModeHeader', ['translationService', '$location', '$mdSidenav', '$mdDialog', '$rootScope', 'serverCallService', 
     function(translationService, $location, $mdSidenav, $mdDialog, $rootScope, serverCallService) {
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
                
                function updatePortfolioFailed(){
                    log('Updating portfolio failed.');
                }

            }
        };
    }]);

    return app;
});
 