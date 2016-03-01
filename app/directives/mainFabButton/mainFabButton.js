define([
    'angularAMD',
    'services/serverCallService',
    'services/authenticatedUserService',
    'services/storageService'
], function(angularAMD) {
    angularAMD.directive('dopMainFabButton', ['$rootScope', 'serverCallService', '$route', 'storageService', '$filter',
        function($rootScope, serverCallService, $route, storageService, $filter) {
        return {
            scope: true,
            templateUrl: 'directives/mainFabButton/mainFabButton.html',
            controller: function($scope, $mdDialog, $location, authenticatedUserService, $rootScope, $filter) {
                $scope.isOpen = false;
                $scope.userHasSelectedMaterials = false;

                $rootScope.$watch('selectedMaterials.length', function (newValue) {
                    $scope.userHasSelectedMaterials = newValue > 0;
                },true);

                $rootScope.$watch('selectedSingleMaterial', function (newValue) {
                        $scope.userHasSelectedMaterials = newValue !== null;
                },true);

                $scope.showAddPortfolioDialog = function() {
                    var emptyPortfolio = createPortfolio();

                    if($scope.userHasSelectedMaterials || $rootScope.selectedSingleMaterial) {
                        emptyPortfolio.chapters = [];

                        emptyPortfolio.chapters.push({
                            title: $filter('translate')('PORTFOLIO_DEFAULT_NEW_CHAPTER_TITLE'),
                            subchapters: [],
                            materials: []
                        });

                        if ($rootScope.selectedMaterials && $rootScope.selectedMaterials.length > 0) {
                            for (var i = 0; i < $rootScope.selectedMaterials.length; i++) {
                                var selectedMaterial = $rootScope.selectedMaterials[i];
                                emptyPortfolio.chapters[0].materials.push(selectedMaterial);
                            }
                        } else if($rootScope.selectedSingleMaterial != null) {
                            emptyPortfolio.chapters[0].materials.push($rootScope.selectedSingleMaterial);
                        }
                    }

                    storageService.setPortfolio(emptyPortfolio);

                    $mdDialog.show(angularAMD.route({
                        templateUrl: 'views/addPortfolioDialog/addPortfolioDialog.html',
                        controllerUrl: 'views/addPortfolioDialog/addPortfolioDialog'
                    }));
                };

                $scope.showAddMaterialDialog = function() {
                    storageService.setMaterial(null);
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

                $scope.hasAddMaterialPermission = function() {
                    return authenticatedUserService.getUser() &&
                        (authenticatedUserService.getUser().role === 'ADMIN' || authenticatedUserService.getUser().role === 'PUBLISHER');
                };

                $scope.hasPermission = function() {
                    return authenticatedUserService.getUser() && !authenticatedUserService.isRestricted();
                };
            }
        };
    }]);
});
