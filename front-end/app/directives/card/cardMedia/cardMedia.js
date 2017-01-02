'use strict'

angular.module('koolikottApp')
.directive('dopCardMedia',
[
    'iconService',
    function (iconService) {
        return {
            scope: {
                learningObject: '=',
                isAuthenticated: '='
            },
            templateUrl: 'directives/card/cardMedia/cardMedia.html',
            controller: ['$scope', '$rootScope', function ($scope, $rootScope) {

                function init () {
                    if (isMaterial($scope.learningObject.type)) {
                        $scope.learningObjectType = 'material';
                        $scope.materialType = iconService.getMaterialIcon($scope.learningObject.resourceTypes);
                    } else if (isPortfolio($scope.learningObject.type)) {
                        $scope.learningObjectType = 'portfolio';
                    }
                }

                $scope.isMaterial = function (type) {
                    return isMaterial(type);
                };

                $scope.isPortfolio = function (type) {
                    return isPortfolio(type);
                };

                $scope.pickMaterial = function ($event, material) {
                    $event.preventDefault();
                    $event.stopPropagation();

                    if ($rootScope.selectedMaterials) {
                        let index = $rootScope.selectedMaterials.indexOf(material);
                        if (index == -1) {
                            $rootScope.selectedMaterials.push(material);
                            material.selected = true;
                        } else {
                            $rootScope.selectedMaterials.splice(index, 1);
                            material.selected = false;
                        }
                    } else {
                        $rootScope.selectedMaterials = [];
                        $rootScope.selectedMaterials.push(material);
                        material.selected = true;
                    }

                    $rootScope.$broadcast("detailedSearch:close");
                };

                init();
            }]
        }
    }
]);
