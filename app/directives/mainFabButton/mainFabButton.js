define(['app'], function(app) {
    app.directive('dopMainFabButton', ['$rootScope', 'serverCallService', '$route',
        function($rootScope, serverCallService, $route) {
            return {
                scope: true,
                templateUrl: 'directives/mainFabButton/mainFabButton.html',
                controller: function($scope, $mdDialog, $location, authenticatedUserService) {
                    $scope.isOpen = false;

                    $scope.showAddPortfolioDialog = function() {
                        var portfolio = createPortfolio();
                        $mdDialog.show({
                            controller: 'addPortfolioDialog',
                            templateUrl: 'views/addPortfolioDialog/addPortfolioDialog.html',
                            locals: {
                                portfolio: portfolio
                            }
                        });
                    };

                    $scope.showAddMaterialDialog = function() {
                        $mdDialog.show({
                            controller: 'addMaterialDialog',
                            templateUrl: 'views/addMaterialDialog/addMaterialDialog.html'
                        });
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
                }
            };
        }
    ]);

    return app;
});