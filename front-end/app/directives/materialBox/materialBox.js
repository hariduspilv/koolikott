'use strict'

angular.module('koolikottApp')
.directive('dopMaterialBox',
[
    'translationService', 'serverCallService', '$rootScope', 'iconService', 'authenticatedUserService',
    function (translationService, serverCallService, $rootScope, iconService, authenticatedUserService) {
        return {
            scope: {
                material: '=',
                chapter: '='
            },
            templateUrl: 'directives/materialBox/materialBox.html',
            controller: function ($scope, $location) {

                $scope.selected = false;
                $scope.isEditPortfolioPage = $rootScope.isEditPortfolioPage;
                $scope.isEditPortfolioMode = $rootScope.isEditPortfolioMode;

                $scope.navigateTo = function (material, $event) {
                    $event.preventDefault();
                    $rootScope.savedMaterial = material;

                    $location.path('/material').search({
                        materialId: material.id
                    });
                };

                $scope.getCorrectLanguageTitle = function (material) {
                    if (material) {
                        return getCorrectLanguageString(material.titles, material.language);
                    }
                };

                function getCorrectLanguageString(languageStringList, materialLanguage) {
                    if (languageStringList) {
                        return getUserDefinedLanguageString(languageStringList, translationService.getLanguage(), materialLanguage);
                    }
                }

                $scope.formatMaterialIssueDate = function (issueDate) {
                    return formatIssueDate(issueDate);

                };

                $scope.formatName = function (name) {
                    if (name) {
                        return formatNameToInitials(name.trim());
                    }
                };

                $scope.formatSurname = function (surname) {
                    if (surname) {
                        return formatSurnameToInitialsButLast(surname.trim());
                    }
                };

                $scope.materialType = iconService.getMaterialIcon($scope.material.resourceTypes);

                $scope.pickMaterial = function ($event, material) {
                    $event.preventDefault();
                    $event.stopPropagation();

                    var index = $rootScope.selectedMaterials.indexOf(material);
                    if (index == -1) {
                        $rootScope.selectedMaterials.push(material);
                        material.selected = true;
                    } else {
                        $rootScope.selectedMaterials.splice(index, 1);
                        material.selected = false;
                    }
                };

                $scope.isAuthenticated = function () {
                    var authenticated = authenticatedUserService.getUser() && !authenticatedUserService.isRestricted() && !$rootScope.isEditPortfolioPage;
                    if (!authenticated) {
                        $scope.material.selected = false;
                    }

                    return authenticated;
                };

                $rootScope.$watch('selectedMaterials', function (newValue) {
                    if (newValue && newValue.length == 0) {
                        $scope.material.selected = false;
                    }
                });
            }
        };
    }
]);
