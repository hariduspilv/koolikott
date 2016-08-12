define([
    'angularAMD',
    'services/serverCallService',
    'services/authenticatedUserService',
    'services/storageService'
], function(angularAMD) {
    angularAMD.directive('dopMainFabButton', ['$rootScope', 'serverCallService', '$route', 'storageService', '$filter',  'toastService',
        function($rootScope, serverCallService, $route, storageService, $filter, toastService) {
        return {
            scope: true,
            templateUrl: 'directives/mainFabButton/mainFabButton.html',
            controller: function($scope, $mdDialog, $location, authenticatedUserService, $rootScope) {
                $scope.isOpen = false;
                $scope.userHasSelectedMaterials = false;

                $rootScope.$watch('selectedMaterials.length', function (newValue) {
                    $scope.userHasSelectedMaterials = newValue > 0;
                },false);

                $rootScope.$watch('selectedSingleMaterial', function (newValue) {
                        $scope.userHasSelectedMaterials = newValue !== null;
                },false);

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
                        toastService.showOnRouteChange('PORTFOLIO_ADD_MATERIAL_SUCCESS');
                    }

                    storageService.setPortfolio(emptyPortfolio);

                    $mdDialog.show(angularAMD.route({
                        templateUrl: 'views/addPortfolioDialog/addPortfolioDialog.html',
                        controllerUrl: 'views/addPortfolioDialog/addPortfolioDialog'
                    }));
                };

                $scope.showAddMaterialsToPortfolioDialog = function() {
                    $mdDialog.show(angularAMD.route({
                        templateUrl: 'views/addMaterialToExistingPortfolio/addMaterialToExistingPortfolio.html',
                        controllerUrl: 'views/addMaterialToExistingPortfolio/addMaterialToExistingPortfolio'
                    }));
                };

                $scope.showAddMaterialDialog = function() {
                    $mdDialog.show(angularAMD.route({
                        templateUrl: 'addMaterialDialog.html',
                        controllerUrl: 'views/addMaterialDialog/addMaterialDialog'
                    }))
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
                    return authenticatedUserService.getUser() && !authenticatedUserService.isRestricted();
                };
            }
        };
    }]);
});
