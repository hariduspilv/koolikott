define([
    'angularAMD',
    'services/serverCallService',
    'services/authenticatedUserService',
    'services/storageService'
], function(angularAMD) {
    angularAMD.directive('dopMainFabButton', ['$rootScope', 'serverCallService', '$route', 'storageService', function($rootScope, serverCallService, $route, storageService) {
        return {
            scope: true,
            templateUrl: 'directives/mainFabButton/mainFabButton.html',
            controller: function($scope, $mdDialog, $location, authenticatedUserService) {
                $scope.isOpen = false;

                $scope.showAddPortfolioDialog = function() {                        
                    storageService.setPortfolio(createPortfolio());
                    
                    $mdDialog.show(angularAMD.route({
                        templateUrl: 'views/addPortfolioDialog/addPortfolioDialog.html',
                        controllerUrl: 'views/addPortfolioDialog/addPortfolioDialog'
                    }));
                };

                $scope.showAddMaterialDialog = function() {
                    $mdDialog.show(angularAMD.route({
                        templateUrl: 'views/addMaterialDialog/addMaterialDialog.html',
                        controllerUrl: 'views/addMaterialDialog/addMaterialDialog'
                    }));
                };

                $scope.copyPortfolio = function() {
                    var url = "rest/portfolio/copy";
                    var portfolio = createPortfolio($route.current.params.id);
                    serverCallService.makePost(url, portfolio, createPortfolioSuccess, createPortfolioFailed);
                };

                function createPortfolioSuccess(portfolio) {
                    if (isEmpty(portfolio)) {
                        createPortfolioFailed();
                    } else {
                        $rootScope.savedPortfolio = portfolio;
                        $rootScope.openMetadataDialog = true;
                        $mdDialog.hide();
                        $location.url('/portfolio/edit?id=' + portfolio.id);
                    }
                }

                function createPortfolioFailed() {
                    log('Creating copy of portfolio failed.');
                }

                $scope.hasPermission = function() {
                    return authenticatedUserService.getUser() &&
                        (authenticatedUserService.getUser().role === 'ADMIN' || authenticatedUserService.getUser().role === 'PUBLISHER');
                };
                
                $scope.hasCopyPermission = function() {
                    return $scope.isViewPortforlioPage && $scope.hasPermission();
                };
            }
        };
    }]);
});