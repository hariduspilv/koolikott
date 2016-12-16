'use strict';

angular.module('koolikottApp')
.controller('addMaterialToExistingPortfolioController',
[
    '$scope', '$mdDialog', 'serverCallService', 'translationService', 'metadataService', '$rootScope', 'authenticatedUserService', 'toastService',
    function ($scope, $mdDialog, serverCallService, translationService, metadataService, $rootScope, authenticatedUserService, toastService) {
        $scope.step = {};
        $scope.step.currentStep = 0;
        getUsersPortfolios();

        $scope.cancel = function () {
            $mdDialog.hide();
        };

        $scope.step.previousStep = function () {
            $scope.step.currentStep -= 1;
        };

        $scope.chooseChapter = function (selectedPortfolio) {
            $scope.selectedPortfolio = selectedPortfolio;
            $scope.step.currentStep = 1;
        };

        $scope.addMaterialsToChapter = function (chapter, portfolio) {
            if (chapter && chapter.materials) {

                if ($rootScope.selectedSingleMaterial) {
                    if (!containsMaterial(chapter.materials, $rootScope.selectedSingleMaterial)) {
                        chapter.materials.push($rootScope.selectedSingleMaterial);
                    }
                } else {
                    for (var i = 0; i < $rootScope.selectedMaterials.length; i++) {
                        var selectedMaterial = $rootScope.selectedMaterials[i];
                        if (!containsMaterial(chapter.materials, selectedMaterial)) {
                            chapter.materials.push(selectedMaterial);
                        }
                    }

                }
            }
            $rootScope.selectedMaterials = [];

            serverCallService.makePost("rest/portfolio/update", portfolio, updatePortfolioSuccess, updatePortfolioFailed);
        };

        function updatePortfolioSuccess(portfolio) {
            if (isEmpty(portfolio)) {
                updatePortfolioFailed();
            } else {
                $mdDialog.hide();
                toastService.show('PORTFOLIO_ADD_MATERIAL_SUCCESS');
            }
        }

        function updatePortfolioFailed() {
            toastService.show('PORTFOLIO_ADD_MATERIAL_FAIL');
        }

        function getUsersPortfolios() {
            var user = authenticatedUserService.getUser();
            var params = {
                'username': user.username
            };
            var url = "rest/portfolio/getByCreator";
            serverCallService.makeGet(url, params, getUsersPortfoliosSuccess, getUsersPortfoliosFail);
        }

        function getUsersPortfoliosSuccess(data) {
            if (isEmpty(data)) {
                getUsersPortfoliosFail();
            } else {
                $scope.portfolios = data.items;
            }
        }

        function getUsersPortfoliosFail() {
            console.log('Failed to get portfolios.');
        }
    }
]);
