define(['app'], function(app) {

    app.directive('dopMaterialBox', ['translationService', 'serverCallService', '$rootScope', 'iconService', function(translationService, serverCallService, $rootScope, iconService) {
        return {
            scope: {
                material: '=',
                chapter: '='
            },
            templateUrl: 'directives/materialBox/materialBox.html',
            controller: function($scope, $location) {

                $scope.selected = false;
                $scope.isEditPortfolioPage = $rootScope.isEditPortfolioPage;
                $scope.isEditPortfolioMode = $rootScope.isEditPortfolioMode;

                $scope.navigateTo = function(material) {
                    $rootScope.savedMaterial = material;

                    $location.path('/material').search({
                        materialId: material.id
                    });
                };

                $scope.getCorrectLanguageTitle = function(material) {
                    if (material) {
                        return getCorrectLanguageString(material.titles, material.language);
                    }
                };

                function getCorrectLanguageString(languageStringList, materialLanguage) {
                    if (languageStringList) {
                        return getUserDefinedLanguageString(languageStringList, translationService.getLanguage(), materialLanguage);
                    }
                }

                $scope.formatMaterialIssueDate = function(issueDate) {
                    return formatIssueDate(issueDate);

                };

                $scope.formatName = function(name) {
                    if (name) {
                        return formatNameToInitials(name);
                    }
                };

                $scope.formatSurname = function(surname) {
                    if (surname) {
                        return formatSurnameToInitialsButLast(surname);
                    }
                };
              
                $scope.materialType = iconService.getMaterialIcon($scope.material.resourceTypes);

                $scope.pickMaterial = function($event, material) {
                    $event.stopPropagation();
                    var index = $rootScope.selectedMaterials.indexOf(material);
                    if (index == -1) {
                        $rootScope.selectedMaterials.push(material);
                        $scope.selected = true;
                    } else {
                        $rootScope.selectedMaterials.splice(index, 1);
                        $scope.selected = false;
                    }
                };

                $scope.removeMaterial = function($event, material) {
                    $event.stopPropagation();
                    var index = $scope.chapter.materials.indexOf(material);
                    $scope.chapter.materials.splice(index, 1);
                }
                
                $rootScope.$watch('selectedMaterials', function (newValue, oldValue) {
                    if(newValue && newValue.length == 0) {
                    	$scope.selected = false;
                    }
                });
                
            }
        };
    }]);

    return app;
});