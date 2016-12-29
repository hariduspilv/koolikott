'use strict'

angular.module('koolikottApp')
.directive('dopMainFabButton',
[
    'serverCallService', 'authenticatedUserService', 'storageService', 'toastService',
    function(serverCallService, authenticatedUserService, storageService, toastService) {
        return {
            scope: true,
            templateUrl: 'directives/mainFabButton/mainFabButton.html',
            controller: ['$scope', '$location', '$rootScope', '$route', '$filter', '$mdDialog', function($scope, $location, $rootScope, $route, $filter, $mdDialog) {
                $scope.isOpen = false;
                $scope.userHasSelectedMaterials = false;

                $rootScope.$watch('selectedMaterials.length', function (newValue) {
                    $scope.userHasSelectedMaterials = newValue > 0;
                },false);

                $rootScope.$watch('selectedSingleMaterial', function (newValue) {
                    $scope.userHasSelectedMaterials = newValue !== null && newValue !== undefined;
                },false);

                $scope.showAddPortfolioDialog = function(e) {
                    e.preventDefault();
                    var emptyPortfolio = createPortfolio();

                    if($scope.userHasSelectedMaterials || $rootScope.selectedSingleMaterial) {
                        emptyPortfolio.chapters = [];

                        emptyPortfolio.chapters.push({
                            title: '',
                            subchapters: [],
                            materials: []
                        });

                        if ($rootScope.selectedMaterials && $rootScope.selectedMaterials.length > 0) {
                            for (var i = 0; i < $rootScope.selectedMaterials.length; i++) {
                                var selectedMaterial = $rootScope.selectedMaterials[i];
                                emptyPortfolio.chapters[0].contentRows.push({learningObjects: [selectedMaterial]});
                            }
                        } else if($rootScope.selectedSingleMaterial != null) {
                            if (emptyPortfolio.chapters[0].contentRows) {
                                emptyPortfolio.chapters[0].contentRows.push({learningObjects: [$rootScope.selectedSingleMaterial]})
                            } else {
                                emptyPortfolio.chapters[0].contentRows = [{learningObjects: [$rootScope.selectedSingleMaterial]}]
                            }
                        }

                        toastService.showOnRouteChange('PORTFOLIO_ADD_MATERIAL_SUCCESS');
                    }

                    storageService.setEmptyPortfolio(emptyPortfolio);

                    $rootScope.newPortfolioCreated = true;

                    $mdDialog.show({
                        templateUrl: 'views/addPortfolioDialog/addPortfolioDialog.html',
                        controller: 'addPortfolioDialogController'
                    });
                };

                $scope.showAddMaterialDialog = function() {
                    $mdDialog.show({
                        templateUrl: 'addMaterialDialog.html',
                        controller: 'addMaterialDialogController'
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
                        storageService.setPortfolio(portfolio);
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

                $scope.setFabState = function(state) {
                    if(!isTouchDevice()) {
                        $scope.isOpen = state;
                    }
                };

                function isTouchDevice() {
                    return true == ("ontouchstart" in window || window.DocumentTouch && document instanceof DocumentTouch);
                }
            }]
        }
    }
]);
